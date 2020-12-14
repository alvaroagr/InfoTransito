package com.example.infotransito;

public class Comment {

    private String id;
    private String content;
    private String userId;
    private String userName;
    private String postId;
    private long timestamp;

    public Comment() {
    }

    public Comment(String id, String content, String userId, String userName, String postId,long timestamp) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.userName = userName;
        this.postId = postId;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
