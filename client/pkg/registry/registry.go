package registry

import (
	"fmt"
	"log"
	"sync"

	"gopkg.in/yaml.v3"
)

// FactoryFunc 是一个通用的、泛型的工厂函数签名。
// I: Interface - 代表组件需要实现的接口，例如 tailer.Tailer
// C: Config - 代表组件的配置结构体
// D: Dependencies - 代表需要注入的依赖容器
type FactoryFunc[I any, D any] func(specificConfig *yaml.Node, deps D) (I, error)
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
		log.Fatalf("类型 '%s' 已经被注册", name)
	}

	factory := func(specificConfig *yaml.Node, deps D) (I, error) {
		conf, err := parseSpecificConfig[C](specificConfig)
		if err != nil {
			return *new(I), fmt.Errorf("解析 '%s' 的配置失败: %w", name, err)
		}
		return constructor(*conf, deps)
	}

	log.Printf("注册类型: %s", name)
	r.factories[name] = factory
}

func (r *Registry[I, D]) Get(typeName string, specificConfig *yaml.Node, deps D) (I, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	factory, exists := r.factories[typeName]
	if !exists {
		return *new(I), fmt.Errorf("未知的类型: %s", typeName)
	}
	return factory(specificConfig, deps)
}

func parseSpecificConfig[C any](specificConfig *yaml.Node) (*C, error) {
	var config C
	if err := specificConfig.Decode(&config); err != nil {
		return nil, err
	}
	return &config, nil
}
