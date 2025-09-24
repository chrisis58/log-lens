package registry

import (
	"fmt"
	"log"
	"sync"

	"gopkg.in/yaml.v3"
)

// I: Interface - represents the type being registered .e.g Tailer, Shipper
// C: Config - represents the specific config struct for the type being registered .e.g FileTailerConfig, WindowsEventLogTailerConfig
// D: Dependencies - represents the dependencies that need to be injected into the type being registered .e.g TailerDeps, ShipperDeps
type FactoryFunc[I any, D any] func(specificConfig yaml.Node, deps D) (I, error)
type FactoryFuncWithConfig[I any, C any, D any] func(conf C, deps D) (I, error)

type Registry[I any, D any] struct {
	mu        sync.RWMutex
	factories map[string]FactoryFunc[I, D]
}

func NewRegistry[I any, D any]() *Registry[I, D] {
	return &Registry[I, D]{
		factories: make(map[string]FactoryFunc[I, D]),
	}
}

func Register[C any, I any, D any](r *Registry[I, D], name string, constructor FactoryFuncWithConfig[I, C, D]) {
	r.mu.Lock()
	defer r.mu.Unlock()

	if _, exists := r.factories[name]; exists {
		log.Fatalf("type '%s' already registered", name)
	}

	factory := func(specificConfig yaml.Node, deps D) (I, error) {
		conf, err := parseSpecificConfig[C](specificConfig)
		if err != nil {
			return *new(I), fmt.Errorf("failed to parse '%s' config: %w", name, err)
		}
		return constructor(*conf, deps)
	}

	r.factories[name] = factory
}

func (r *Registry[I, D]) Get(typeName string, specificConfig yaml.Node, deps D) (I, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	factory, exists := r.factories[typeName]
	if !exists {
		return *new(I), fmt.Errorf("unknown type: %s", typeName)
	}
	return factory(specificConfig, deps)
}

func parseSpecificConfig[C any](specificConfig yaml.Node) (*C, error) {
	var config C
	if err := specificConfig.Decode(&config); err != nil {
		return nil, err
	}
	return &config, nil
}
