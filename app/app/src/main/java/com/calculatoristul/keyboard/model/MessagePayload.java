package com.calculatoristul.keyboard.model;

public class MessagePayload {
    public String client_id;
    public String message;

    public MessagePayload(String clientId, String message) {
        this.client_id = clientId;
        this.message = message;
    }
}