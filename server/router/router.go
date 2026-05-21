package router

import (
	logger "attacker/logger"

	"github.com/gin-gonic/gin"
)

type TelemetryPayload struct {
	ClientID string `json:"client_id"`
	Message  string `json:"message"`
}

func CreateRouter(logger *logger.ClientLogManager) *gin.Engine {
	router := gin.Default()
	router.SetTrustedProxies(nil)
	router.POST("/api/:version/telemetry", func(c *gin.Context) {
		var payload TelemetryPayload
		if err := c.ShouldBindJSON(&payload); err != nil {
			c.JSON(400, gin.H{"error": "Invalid payload"})
			return
		}
		logger.LogTelemetry(payload.ClientID, payload.Message)
		c.JSON(200, gin.H{"status": "success"})
	})
	return router
}
