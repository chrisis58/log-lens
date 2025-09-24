package shipper

import (
	"context"

	"github.com/chrisis58/log-lens/client/internal/tailer"
)

const SHIPPER_CONSOLE = "console"

func init() {
	RegisterShipper(SHIPPER_CONSOLE, NewConsoleShipper)
}

type ConsoleShipper struct {
	ctx     context.Context
	cancel  context.CancelFunc
	tailers []tailer.Tailer
}

type ConsoleShipperConfig struct {
}

func NewConsoleShipper(conf ConsoleShipperConfig, deps ShipperDeps) (Shipper, error) {
	ctx, cancel := context.WithCancel(context.Background())

	return &ConsoleShipper{
		ctx:     ctx,
		cancel:  cancel,
		tailers: deps.Tailers,
	}, nil
}

func (s *ConsoleShipper) Start() error {
	go func() {
		for {
			for _, t := range s.tailers {
				select {
				case line := <-t.Lines():
					// just print to console
					println(line)
				case <-s.ctx.Done():
					return
				}
			}
		}
	}()
	return nil
}

func (s *ConsoleShipper) Stop() error {
	s.cancel()
	return nil
}
