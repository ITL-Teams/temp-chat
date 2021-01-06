package com.example.tempchat.model;

import androidx.annotation.StringRes;

import com.example.tempchat.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatMessage {
  private String userId;
  private String messageId;
  private String username;
  private String content;
  private String date;
  private String time;
  private boolean delete;
  private boolean deleted;

  public ChatMessage(
          String username,
          String content,
          String date,
          String time,
          String userId,
          String messageId,
          boolean delete) {
    this.username = username;
    this.content = content;
    this.date = date;
    this.time = time;
    this.userId = userId;
    this.messageId = messageId;
    this.delete = delete;
    this.deleted = false;
  }

  public ChatMessage(String username, String content) {
    this.username = username;
    this.content = content;
  }

  public static ChatMessage fromJson(JSONObject json) throws JSONException {
    String username,
           content,
           messageId;

    boolean delete = json.getBoolean("delete");

    if(!delete) {
      username = json.getString("username");
      content = json.getString("content");
      messageId = json.getString("message_id");
    }
    else {
      username = "";
      content = "";
      messageId = json.getString("delete_message_id");
    }

    String date = json.getString("date");
    String time = json.getString("time");
    String userId = json.getString("user_id");

    return new ChatMessage(username, content, date, time, userId, messageId, delete);
  }

  public JSONObject toJson() throws JSONException {
    JSONObject json = new JSONObject();
    json.put("delete", false);
    json.put("username", username);
    json.put("content", content);

    return json;
  }

  public JSONObject deleteMessageToJson() throws JSONException {
    JSONObject json = new JSONObject();
    json.put("delete", true);
    json.put("delete_all", false);
    json.put("delete_message_id", messageId);

    return json;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;

    ChatMessage that = (ChatMessage) object;

    return messageId.equals(that.messageId);
  }

  @Override
  public int hashCode() {
    int result = userId != null ? userId.hashCode() : 0;
    result = 31 * result + (username != null ? username.hashCode() : 0);
    result = 31 * result + (content != null ? content.hashCode() : 0);
    result = 31 * result + (date != null ? date.hashCode() : 0);
    result = 31 * result + (time != null ? time.hashCode() : 0);
    return result;
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

  public void setContent(String content) {
    this.content = content;
  }

  public String getDate() {
    return date;
  }

  public String getTime() {
    return time;
  }

  public boolean isADeleteMessage() {
    return delete;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void delete(String deleteMessage) {
    deleted = true;
    content = "--" + deleteMessage + "--";
  }

}
