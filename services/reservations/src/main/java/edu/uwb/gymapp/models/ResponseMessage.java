package edu.uwb.gymapp.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseMessage {

    @JsonProperty
    private String message;

    public ResponseMessage() {
        this.message = null;
    }

    public ResponseMessage(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
