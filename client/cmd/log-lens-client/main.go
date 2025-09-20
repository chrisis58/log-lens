package main

import (
	"log"
	"time"

	"github.com/chrisis58/log-lens/client/internal/config"
	"github.com/chrisis58/log-lens/client/internal/tailer"
)

func main() {

	// TODO: read from arguments
	config, err := config.LoadConfig()
	if err != nil {
		log.Fatalf("fail to load config: %v", err)
	}

	for tailerType, tailerConf := range config.Tailers {

		for _, conf := range tailerConf {
			tailer, err := tailer.GetTailer(tailerType, &conf)
			if err != nil {
				log.Fatalf("fail to create tailer: %v", err)
			}
			if err = tailer.Start(); err != nil {
				log.Fatalf("fail to start tailer: %v", err)
			}
			defer tailer.Stop()
			go func() {
				for line := range tailer.Lines() {
					log.Println("new log: ", line)
				}
			}()
		}
	}

	log.Println("reading logs for 60 seconds...")
	time.Sleep(60 * time.Second)

	log.Println("shutting down...")

	time.Sleep(2 * time.Second)
	log.Println("program exited.")
}
