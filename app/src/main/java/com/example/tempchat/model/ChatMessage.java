package com.example.tempchat.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatMessage {
  private String username;
  private String content;

  public ChatMessage(String username, String content) {
    this.username = username;
    this.content = content;
  }

  public static ChatMessage fromJson(JSONObject json) throws JSONException {
    String username = json.getString("username");
    String content = json.getString("content");

    return new ChatMessage(username, content);
  }

  public JSONObject toJson() throws JSONException {
    JSONObject json = new JSONObject();
    json.put("username", username);
    json.put("content", content);

    return json;
  }

  public String getUserName() {
    return username;
  }

  public String getContent() {
    return content;
  }

}
