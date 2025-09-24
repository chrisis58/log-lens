package tailer

import (
	"fmt"

	"gopkg.in/yaml.v3"

	"github.com/chrisis58/log-lens/client/internal/state"
	"github.com/chrisis58/log-lens/client/pkg/registry"
)

type Tailer interface {
	Lines() <-chan string
	Errors() <-chan error
	Start() error
	Stop()
}

type TailerDeps struct {
	State state.BookmarkState
}

var tailerRegistry = registry.NewRegistry[Tailer, TailerDeps]()

func RegisterTailer[C any](name string, constructor registry.FactoryFuncWithConfig[Tailer, C, TailerDeps]) {
	registry.Register(tailerRegistry, name, constructor)
}

func GetTailer(tailerType string, tailerConf *yaml.Node, deps TailerDeps) (Tailer, error) {
	tailer, err := tailerRegistry.Get(tailerType, tailerConf, deps)
	if err != nil {
		return nil, fmt.Errorf("unknown tailer type: %s", tailerType)
	}
	return tailer, nil
}
