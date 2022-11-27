package com.example.seatreservation.Chat;

import java.io.Serializable;

public class Message implements Serializable {
    String message;
    String userId;
    String userName;
    String userProfile;
    String createdAt;
    String chatRoomIdx;
    String type;

    public Message(String message, String userId, String userName, String userProfile, String createdAt, String chatRoomIdx, String type) {
        this.message = message;
        this.userId = userId;
        this.userName = userName;
        this.userProfile = userProfile;
        this.createdAt = createdAt;
        this.chatRoomIdx = chatRoomIdx;
        this.type = type;
    }

    public Message() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getChatRoomIdx() {
        return chatRoomIdx;
    }

    public void setChatRoomIdx(String chatRoomIdx) {
        this.chatRoomIdx = chatRoomIdx;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userProfile='" + userProfile + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", chatRoomIdx='" + chatRoomIdx + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
