package com.example.tempchat.utils;

import android.os.Message;
import android.util.Pair;

import com.example.tempchat.model.ChatMessage;

public class MessageFormatter {
  private Object content;
  private MessageType messageType;

  public MessageFormatter() {
    messageType = MessageType.EMPTY;
  }

  public MessageFormatter(Message message) {
    Pair<MessageType, Object> messageContent = (Pair<MessageType, Object>) message.obj;
    messageType = messageContent.first;
    content = messageContent.second;
  }

  public void putDeleteMessage(ChatMessage deleteMessage) {
    content = deleteMessage;
    messageType = MessageType.DELETE_MESSAGE;
  }

  public void putText(String text) {
    content = text;
    messageType = MessageType.TEXT;
  }

  public void putChatMessage(ChatMessage chatMessage) {
    content = chatMessage;
    messageType = MessageType.CHAT_MESSAGE;
  }

  public ChatMessage getChatMessage() {
    if(messageType == MessageType.CHAT_MESSAGE)
      return (ChatMessage) content;

    return null;
  }

  public ChatMessage getDeleteMessage() {
    if(messageType == MessageType.DELETE_MESSAGE)
      return (ChatMessage) content;

    return null;
  }

  public String getText() {
    if(messageType == MessageType.TEXT)
      return (String) content;

    return null;
  }

  public Message getMessage() {
    Message newMessage = new Message();
    Pair<MessageType, Object> content = new Pair<>(messageType, this.content);
    newMessage.obj = content;
    return newMessage;
  }

  public MessageType getMessageType() {
    return messageType;
  }

  public enum MessageType {
    EMPTY,
    TEXT,
    CHAT_MESSAGE,
    DELETE_MESSAGE
  }

}
