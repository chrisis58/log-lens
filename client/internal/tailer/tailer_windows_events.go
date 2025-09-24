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

	"github.com/chrisis58/log-lens/client/internal/state"
)

const (
	WINDOWS_EVTLOG_TAILER          = "windows"
	WINDOWS_EVTLOG_BOOKMARK_PREFIX = "llc:windows:" // llc: means Log Lens Client
)

func init() {
	RegisterTailer(WINDOWS_EVTLOG_TAILER, NewWindowsEventsTailer)
}

type WindowsEventsTailer struct {
	state    state.BookmarkState
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

func NewWindowsEventsTailer(conf WindowsConfig, deps TailerDeps) (Tailer, error) {
	if deps.State == nil {
		log.Println("deps.State is nil")
		return nil, fmt.Errorf("TailerDeps.State is nil, please initialize state before creating WindowsEventsTailer")
	}

	config, err := winlog.DefaultSubscribeConfig()
	if err != nil {
		return nil, fmt.Errorf("fail to get default subscribe config: %w", err)
	}

	resolveWindowsConfigDefaultValues(&conf)

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

	state := deps.State
	bookmarkXML, err := state.Load(WINDOWS_EVTLOG_BOOKMARK_PREFIX + conf.Name)
	if err != nil {
		config.Close()
		return nil, fmt.Errorf("fail to load bookmark from state: %w", err)
	}
	config.Bookmark, err = winlog.CreateBookmark(bookmarkXML)
	if err != nil {
		log.Printf("fail to load bookmark from registry: %v", err)
	}

	return &WindowsEventsTailer{
		state:          state,
		bookmark:       WINDOWS_EVTLOG_BOOKMARK_PREFIX + conf.Name,
		maxEvents:      conf.MaxEvents,
		cfg:            config,
		publisherCache: make(map[string]windows.Handle),
		lines:          make(chan string),
		errors:         make(chan error),
	}, nil
}

func resolveWindowsConfigDefaultValues(conf *WindowsConfig) {
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

				bookmarkXML, err := winlog.RenderFragment(w.cfg.Bookmark, wevtapi.EvtRenderBookmark)
				if err != nil {
					w.errors <- fmt.Errorf("RenderFragment failed: %v", err)
				}
				w.state.Save(w.bookmark, bookmarkXML)
				// save bookmark
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
