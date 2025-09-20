//go:build windows

package tailer

import (
	"context"
	"fmt"
	"log"
	"syscall"

	"github.com/google/winops/winlog"
	"github.com/google/winops/winlog/wevtapi"
	"golang.org/x/sys/windows"
	"golang.org/x/sys/windows/registry"
)

func init() {
	// ensure the registry path exists
	k, _, err := registry.CreateKey(registry.CURRENT_USER, BOOKMARK_REGISTRY_PATH, registry.ALL_ACCESS)
	if err != nil {
		panic(fmt.Errorf("fail to create bookmark from registry '%s': %w", BOOKMARK_REGISTRY_PATH, err))
	}
	k.Close()

	RegisterTailer("windows", NewWindowsEventsTailer)
}

const (
	BOOKMARK_REGISTRY_PATH = `SOFTWARE\LogLensClient\WindowsEventLogBookmarks`
	BOOKMARK_KEY_PREFIX    = "llc:" // llc: means Log Lens Client
)

type WindowsEventsTailer struct {
	bookmark string

	maxEvents      int
	cfg            *winlog.SubscribeConfig
	subscription   windows.Handle
	publisherCache map[string]windows.Handle

	ctx    context.Context
	cancel context.CancelFunc

	lines  chan string
	errors chan error
}

type WindowsConfig struct {
	Name      string            `yaml:"name"`
	XPaths    map[string]string `yaml:"xpaths"`
	MaxEvents int               `yaml:"maxEvents"`
}

func NewWindowsEventsTailer(conf WindowsConfig) (Tailer, error) {
	config, err := winlog.DefaultSubscribeConfig()
	if err != nil {
		return nil, fmt.Errorf("fail to get default subscribe config: %w", err)
	}

	resolveDefaultValues(&conf)

	config.SignalEvent, err = windows.CreateEvent(nil, 1, 1, nil)
	if err != nil {
		return nil, fmt.Errorf("fail to create event: %w", err)
	}

	xmlQuery, err := winlog.BuildStructuredXMLQuery(conf.XPaths)
	if err != nil {
		config.Close()
		return nil, fmt.Errorf("fail to build XML query: %w", err)
	}

	config.Flags = wevtapi.EvtSubscribeStartAfterBookmark
	config.Query, err = syscall.UTF16PtrFromString(string(xmlQuery))
	if err != nil {
		config.Close()
		return nil, fmt.Errorf("fail to convert query string: %w", err)
	}

	err = winlog.GetBookmarkRegistry(config, registry.CURRENT_USER, BOOKMARK_REGISTRY_PATH, BOOKMARK_KEY_PREFIX+conf.Name)
	if err != nil {
		log.Printf("fail to load bookmark from registry: %v", err)
	}

	return &WindowsEventsTailer{
		bookmark:       BOOKMARK_KEY_PREFIX + conf.Name,
		maxEvents:      conf.MaxEvents,
		cfg:            config,
		publisherCache: make(map[string]windows.Handle),
		lines:          make(chan string),
		errors:         make(chan error),
	}, nil
}

func resolveDefaultValues(conf *WindowsConfig) {
	if conf.MaxEvents == 0 {
		conf.MaxEvents = 10
	}

	if conf.XPaths == nil {
		conf.XPaths = map[string]string{
			"Application": "System[(Level=2) and *[System[TimeCreated[timediff(@SystemTime) <= 3600000]]]",
		}
	}
}

func (w *WindowsEventsTailer) Start() error {
	var err error
	w.subscription, err = winlog.Subscribe(w.cfg)
	if err != nil {
		w.cfg.Close()
		return fmt.Errorf("fail to initialize subscription: %w", err)
	}

	ctx, cancel := context.WithCancel(context.Background())
	w.ctx = ctx
	w.cancel = cancel

	go w.run()
	log.Println("Windows events monitoring started...")
	return nil
}

func (w *WindowsEventsTailer) run() {
	ctx := w.ctx
	defer func() {
		close(w.lines)
		close(w.errors)
		winlog.Close(w.subscription)
		w.cfg.Close()
		for _, h := range w.publisherCache {
			winlog.Close(h)
		}
		log.Println("Windows events monitoring stopped, all resources cleaned up.")
	}()

	for {
		select {
		case <-ctx.Done():
			return
		default:
			status, err := windows.WaitForSingleObject(w.cfg.SignalEvent, 1000)
			if err != nil {
				w.errors <- fmt.Errorf("fail to wait for event signal: %w", err)
				continue
			}

			if status == windows.WAIT_OBJECT_0 {
				events, err := winlog.GetRenderedEvents(w.cfg, w.publisherCache, w.subscription, w.maxEvents, 0)

				if err == syscall.Errno(259) {
					windows.ResetEvent(w.cfg.SignalEvent)
				} else if err != nil {
					w.errors <- fmt.Errorf("fail to get events: %w", err)
					continue
				}

				for _, eventXML := range events {
					select {
					case w.lines <- eventXML:
					case <-ctx.Done():
						return
					}
				}
				err = winlog.SetBookmarkRegistry(w.cfg.Bookmark, registry.CURRENT_USER, BOOKMARK_REGISTRY_PATH, w.bookmark)
				if err != nil {
					w.errors <- fmt.Errorf("fail to save bookmark to registry: %w", err)
				}
			}
		}
	}
}

func (w *WindowsEventsTailer) Lines() <-chan string {
	return w.lines
}

func (w *WindowsEventsTailer) Errors() <-chan error {
	return w.errors
}

func (w *WindowsEventsTailer) Stop() {
	if w.cancel != nil {
		w.cancel()
	}
}
