package com.example.tempchat.service;

import android.os.Handler;

import com.example.tempchat.model.ChatMessage;
import com.example.tempchat.utils.Logger;
import com.example.tempchat.utils.MessageFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatWebSocketListener extends WebSocketListener {
  private Logger logger;
  private MessageFormatter messageFormatter;
  private Handler onOpenHandler,
                  onMessageHandler,
                  onCloseHandler,
                  onFailureHandler;

  public ChatWebSocketListener() {
    messageFormatter = new MessageFormatter();
    logger = new Logger(ChatWebSocketListener.class);
  }

  public void setOnOpenHandler(Handler onOpenHandler) {
    this.onOpenHandler = onOpenHandler;
  }

  public void setOnMessageHandler(Handler onMessageHandler) {
    this.onMessageHandler = onMessageHandler;
  }

  public void setOnCloseHandler(Handler onCloseHandler) {
    this.onCloseHandler = onCloseHandler;
  }

  public void setOnFailureHandler(Handler onFailureHandler) {
    this.onFailureHandler = onFailureHandler;
  }

  @Override
  public void onOpen(WebSocket webSocket, Response response) {
    String info = "Connected to server";
    logger.info(info);
    messageFormatter.putText(info);

    if(onOpenHandler != null)
      onOpenHandler.sendMessage(messageFormatter.getMessage());
  }

  @Override
  public void onMessage(WebSocket webSocket, String data) {
    logger.debug("data: " + data);

    try {
      messageFormatter.putChatMessage(
        ChatMessage.fromJson(
          new JSONObject(data)
        )
      );
    } catch (JSONException jsonException) {
      logger.error(jsonException.getMessage());
      messageFormatter.putText(data); // OnConnect get clientId
    }

    if(onMessageHandler != null)
      onMessageHandler.sendMessage(messageFormatter.getMessage());
  }

  @Override
  public void onClosed(WebSocket webSocket, int code, String reason) {
    String info = "Connection closed";
    logger.info("Connection closed");

    messageFormatter.putText(info);

    if(onCloseHandler != null)
      onCloseHandler.sendMessage(messageFormatter.getMessage());
  }

  @Override
  public void onFailure(WebSocket webSocket, Throwable t, Response response) {
    String info = t.getMessage();
    logger.error(info);

    messageFormatter.putText(info);

    if(onFailureHandler != null)
      onFailureHandler.sendMessage(messageFormatter.getMessage());
  }

}
