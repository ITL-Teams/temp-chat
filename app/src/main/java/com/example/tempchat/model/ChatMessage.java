package com.example.tempchat.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatMessage {
  private String userId;
  private String username;
  private String content;
  private String date;
  private String time;

  public ChatMessage(
          String username,
          String content,
          String date,
          String time,
          String userId) {
    this.username = username;
    this.content = content;
    this.date = date;
    this.time = time;
    this.userId = userId;
  }

  public ChatMessage(String username, String content) {
    this.username = username;
    this.content = content;
  }

  public static ChatMessage fromJson(JSONObject json) throws JSONException {
    String username = json.getString("username");
    String content = json.getString("content");
    String date = json.getString("date");
    String time = json.getString("time");
    String userId = json.getString("user_id");

    return new ChatMessage(username, content, date, time, userId);
  }

  public JSONObject toJson() throws JSONException {
    JSONObject json = new JSONObject();
    json.put("username", username);
    json.put("content", content);

    return json;
  }

  public String getUserId() {
    return userId;
  }

  public String getUserName() {
    return username;
  }

  public String getContent() {
    return content;
  }

  public String getDate() {
    return date;
  }

  public String getTime() {
    return time;
  }

}
