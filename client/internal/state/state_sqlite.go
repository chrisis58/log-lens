package state

import (
	"database/sql"

	_ "modernc.org/sqlite" // sqlite driver
)

const (
	SQLITE_STATE_MANAGER = "sqlite"
)

func init() {
	RegisterStateManager(SQLITE_STATE_MANAGER, NewSQLiteState)
}

type SQLiteStateManager struct {
	db *sql.DB
}

type SQLiteConfig struct {
	DBPath string `yaml:"dbPath"`
}

func resolveSQLiteConfigDefaultValues(conf *SQLiteConfig) {
	if conf.DBPath == "" {
		conf.DBPath = "llc.db"
	}
}

func NewSQLiteState(config SQLiteConfig, deps StateDeps) (StateManager, error) {
	resolveSQLiteConfigDefaultValues(&config)

	db, err := sql.Open("sqlite", config.DBPath)
	if err != nil {
		return nil, err
	}

	// Create the bookmarks table if it doesn't exist.
	_, err = db.Exec(`
	CREATE TABLE IF NOT EXISTS bookmarks (
		key TEXT PRIMARY KEY,
		bookmark TEXT
	);
	`)
	if err != nil {
		return nil, err
	}

	_, err = db.Exec(`
	CREATE TABLE IF NOT EXISTS failed_batches (
		id INTEGER PRIMARY KEY AUTOINCREMENT,
		data BLOB,
		created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
		leased_at TIMESTAMP DEFAULT NULL
	);
	`)
	if err != nil {
		return nil, err
	}

	return &SQLiteStateManager{
		db: db,
	}, nil
}

func (s *SQLiteStateManager) Load(key string) (string, error) {
	var bookmark string
	err := s.db.QueryRow("SELECT bookmark FROM bookmarks WHERE key = ?", key).Scan(&bookmark)
	if err == sql.ErrNoRows {
		return "", nil // No bookmark found
	} else if err != nil {
		return "", err
	}
	return bookmark, nil
}

func (s *SQLiteStateManager) Save(key string, bookmark string) error {
	_, err := s.db.Exec(`
	INSERT INTO bookmarks (key, bookmark) VALUES (?, ?)
	ON CONFLICT(key) DO UPDATE SET bookmark=excluded.bookmark;
	`, key, bookmark)
	return err
}

func (s *SQLiteStateManager) EnqueueFailedBatch(data []byte) error {
	_, err := s.db.Exec("INSERT INTO failed_batches (data) VALUES (?)", data)
	return err
}

func (s *SQLiteStateManager) LeaseBatch() (*Batch, error) {
	var batch Batch
	err := s.db.QueryRow(`
	SELECT id, data FROM failed_batches
	WHERE leased_at IS NULL
	ORDER BY created_at ASC
	LIMIT 1
	`).Scan(&batch.Id, &batch.Data)
	if err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}
	_, err = s.db.Exec("UPDATE failed_batches SET leased_at = CURRENT_TIMESTAMP WHERE id = ?", batch.Id)
	if err != nil {
		return nil, err
	}

	return &batch, nil
}

func (s *SQLiteStateManager) Ack(batchId int64) error {
	_, err := s.db.Exec("DELETE FROM failed_batches WHERE id = ?", batchId)
	return err
}

func (s *SQLiteStateManager) Nack(batchId int64) error {
	_, err := s.db.Exec("UPDATE failed_batches SET leased_at = NULL WHERE id = ?", batchId)
	return err
}

func (s *SQLiteStateManager) Close() error {
	return s.db.Close()
}
