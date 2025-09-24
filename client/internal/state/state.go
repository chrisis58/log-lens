package state

import (
	"io"

	"github.com/chrisis58/log-lens/client/pkg/registry"
	"gopkg.in/yaml.v3"
)

type BookmarkState interface {
	Load(key string) (string, error)
	Save(key string, bookmark string) error
}

type Batch struct {
	Id   int64
	Data []byte
}

type ShipperState interface {
	EnqueueFailedBatch(data []byte) error

	LeaseBatch() (*Batch, error)
	Ack(batchId int64) error
	Nack(batchId int64) error
}

type StateManager interface {
	BookmarkState
	ShipperState
	io.Closer
}

type StateDeps struct {
}

var bookmarkStateRegistry = registry.NewRegistry[StateManager, StateDeps]()

func RegisterStateManager[C any](name string, constructor registry.FactoryFuncWithConfig[StateManager, C, StateDeps]) {
	registry.Register(bookmarkStateRegistry, name, constructor)
}

func GetStateManager(stateType string, stateConf *yaml.Node, deps StateDeps) (StateManager, error) {
	if stateType == "" {
		// default to sqlite
		stateType = SQLITE_STATE_MANAGER
	}

	stateManager, err := bookmarkStateRegistry.Get(stateType, stateConf, deps)
	if err != nil {
		return nil, err
	}
	return stateManager, nil
}
