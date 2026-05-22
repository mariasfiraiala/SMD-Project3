package com.calculatoristul.keyboard.model;

import com.google.gson.annotations.SerializedName;
import androidx.annotation.Keep; 

@Keep 
public class MessagePayload {
    
    @SerializedName("client_id")
    public String client_id;
    
    @SerializedName("message")
    public String message;

    public MessagePayload(String clientId, String message) {
        this.client_id = clientId;
        this.message = message;
    }
}
