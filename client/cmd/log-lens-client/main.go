package main

import (
	"log"
	"time"

	"github.com/chrisis58/log-lens/client/internal/config"
	"github.com/chrisis58/log-lens/client/internal/shipper"
	"github.com/chrisis58/log-lens/client/internal/state"
	"github.com/chrisis58/log-lens/client/internal/tailer"
)

func main() {

	// TODO: read from arguments
	config, err := config.LoadConfig()
	if err != nil {
		log.Fatalf("fail to load config: %v", err)
	}

	stateManager, err := state.Get(config.State, state.StateDeps{})
	if err != nil {
		log.Fatalf("fail to get state manager: %v", err)
	}
	defer stateManager.Close()

	tailers := make([]tailer.Tailer, 0)
	for tailerType, tailerConf := range config.Tailers {

		for _, conf := range tailerConf {
			tailer, err := tailer.GetTailer(tailerType, conf, tailer.TailerDeps{State: stateManager})
			if err != nil {
				log.Fatalf("fail to create tailer: %v", err)
			}
			if err = tailer.Start(); err != nil {
				log.Fatalf("fail to start tailer: %v", err)
			}
			tailers = append(tailers, tailer)
			defer tailer.Stop()
		}
	}

	// currently only console shipper is supported
	shipper, err := shipper.GetShipper(shipper.SHIPPER_CONSOLE, config.Shipper, shipper.ShipperDeps{Tailers: tailers})
	if err != nil {
		log.Fatalf("fail to get shipper: %v", err)
	}
	if err = shipper.Start(); err != nil {
		log.Fatalf("fail to start shipper: %v", err)
	}
	defer shipper.Stop()

	log.Println("reading logs for 60 seconds...")
	time.Sleep(60 * time.Second)
	log.Println("program exited.")
}
