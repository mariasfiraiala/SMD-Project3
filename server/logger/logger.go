package logger

import (
	"fmt"
	"os"
	"path/filepath"
	"sync"

	"github.com/sirupsen/logrus"
)

type ClientLogManager struct {
	mu       sync.RWMutex
	loggers  map[string]*logrus.Logger
	logFiles []*os.File
	basePath string
}

func New(directory string) (*ClientLogManager, error) {
	if err := os.MkdirAll(directory, 0755); err != nil {
		return nil, err
	}

	return &ClientLogManager{
		loggers:  make(map[string]*logrus.Logger),
		basePath: directory,
	}, nil
}

func (m *ClientLogManager) getLogger(clientID string) (*logrus.Logger, error) {
	m.mu.RLock()
	l, exists := m.loggers[clientID]
	m.mu.RUnlock()
	if exists {
		return l, nil
	}

	m.mu.Lock()
	defer m.mu.Unlock()

	if l, exists := m.loggers[clientID]; exists {
		return l, nil
	}

	filename := filepath.Join(m.basePath, fmt.Sprintf("%s.log", clientID))

	file, err := os.OpenFile(filename, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0666)
	if err != nil {
		return nil, err
	}

	m.logFiles = append(m.logFiles, file)

	newLogger := logrus.New()
	newLogger.SetOutput(file)
	newLogger.SetFormatter(&logrus.TextFormatter{
		FullTimestamp: true,
	})

	m.loggers[clientID] = newLogger

	return newLogger, nil
}

func (m *ClientLogManager) LogTelemetry(clientID, message string) {
	clientLogger, err := m.getLogger(clientID)
	if err != nil {
		logrus.Errorf("Failed to open log file for client %s: %v", clientID, err)
		return
	}

	clientLogger.Info(message)
}

func (m *ClientLogManager) Close() {
	m.mu.Lock()
	defer m.mu.Unlock()
	for _, file := range m.logFiles {
		file.Close()
	}
}
