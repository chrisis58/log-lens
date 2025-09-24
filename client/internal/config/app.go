package config

import (
	"fmt"
	"os"
	"path/filepath"

	"gopkg.in/yaml.v3"
)

// config file path:
//
//	%USERPROFILE%\.log-lens-client.yaml on Windows
//	$HOME/.log-lens-client.yaml on Linux and MacOS
const CONFIG_FILENAME = ".log-lens-client.yaml"

type AppConfig struct {
	Tailers map[string][]yaml.Node `yaml:"tailers"`

	State map[string]yaml.Node `yaml:"state"`

	Shipper yaml.Node `yaml:"shipper"`
}

// TODO: add http-shipper
type ShipperConfig struct {
	RemoteUrl string `yaml:"remoteUrl"`
	Token     string `yaml:"token"`
}

func LoadConfig() (*AppConfig, error) {

	homeDir, err := os.UserHomeDir()
	if err != nil {
		return nil, fmt.Errorf("fail to get user home dir: %w", err)
	}

	configPath := filepath.Join(homeDir, CONFIG_FILENAME)

	var data []byte

	if _, err := os.Stat(os.ExpandEnv(configPath)); os.IsNotExist(err) {
		defaultConfig := AppConfig{}
		data, err = yaml.Marshal(&defaultConfig)
		if err != nil {
			return nil, err
		}
		if err := os.WriteFile(os.ExpandEnv(configPath), data, 0644); err != nil {
			return nil, err
		}
	} else {
		data, err = os.ReadFile(os.ExpandEnv(configPath))
		if err != nil {
			return nil, err
		}
	}

	var config AppConfig
	if err := yaml.Unmarshal(data, &config); err != nil {
		return nil, err
	}
	return &config, nil
}
