package org.example.posts.Services;

public class NotificationRequest {
    private Long userId;
    private String message;

    public NotificationRequest() {
    }

    public NotificationRequest(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

