package shipper

import (
	"gopkg.in/yaml.v3"

	"github.com/chrisis58/log-lens/client/pkg/registry"

	"github.com/chrisis58/log-lens/client/internal/state"
	"github.com/chrisis58/log-lens/client/internal/tailer"
)

type Shipper interface {
	Start() error
	Stop() error
}

type ShipperDeps struct {
	ShipperState state.ShipperState
	Tailers      []tailer.Tailer
}

var shipperRegistry = registry.NewRegistry[Shipper, ShipperDeps]()

func RegisterShipper[C any](name string, constructor func(conf C, deps ShipperDeps) (Shipper, error)) {
	registry.Register(shipperRegistry, name, constructor)
}

func GetShipper(shipperType string, shipperConf yaml.Node, deps ShipperDeps) (Shipper, error) {
	shipper, err := shipperRegistry.Get(shipperType, shipperConf, deps)
	if err != nil {
		return nil, err
	}
	return shipper, nil
}
