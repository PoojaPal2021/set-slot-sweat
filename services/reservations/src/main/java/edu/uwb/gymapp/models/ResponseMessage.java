package edu.uwb.gymapp.models;

public class ResponseMessage {

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
