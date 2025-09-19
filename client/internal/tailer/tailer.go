package tailer

import (
	"fmt"
	"log"

	"gopkg.in/yaml.v3"
)

type Tailer interface {
	Lines() <-chan string
	Errors() <-chan error
	Start() error
	Stop()
}

type unparsedFactoryFunc func(specificConfig *yaml.Node) (Tailer, error)

var tailerRegistry = make(map[string]unparsedFactoryFunc)

func register(name string, factory unparsedFactoryFunc) error {
	if _, exists := tailerRegistry[name]; exists {
		return fmt.Errorf("tailer type has already been registered: %s", name)
	}
	log.Printf("register tailer type: %s", name)
	tailerRegistry[name] = factory
	return nil
}

func RegisterTailer[T any](name string, constructor func(conf T) (Tailer, error)) {
	factory := func(specificConfig *yaml.Node) (Tailer, error) {
		conf, err := parseSpecificConfig[T](specificConfig)
		if err != nil {
			return nil, fmt.Errorf("failed to parse '%s' tailer config: %w", name, err)
		}

		return constructor(*conf)
	}

	if err := register(name, factory); err != nil {
		log.Fatalf("%v", err)
	}
}

func parseSpecificConfig[T any](specificConfig *yaml.Node) (*T, error) {
	var config T
	if err := specificConfig.Decode(&config); err != nil {
		return nil, err
	}
	return &config, nil
}

func GetTailer(tailerType string, tailerConf *yaml.Node) (Tailer, error) {
	factory, exists := tailerRegistry[tailerType]
	if !exists {
		return nil, fmt.Errorf("unknown tailer type: %s", tailerType)
	}
	return factory(tailerConf)
}
