package com.example.tempchat.service;

import com.example.tempchat.model.ChatMessage;
import com.example.tempchat.utils.GlobalConfig;
import com.example.tempchat.utils.Logger;

import org.json.JSONException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SocketUtils {
  private Logger logger;
  private WebSocketListener listener;
  private WebSocket ws;

  public SocketUtils(WebSocketListener listener) {
    logger = new Logger(SocketUtils.class);
    this.listener = listener;
  }

  public void connect(String chatRoom) {
    String serverUrl =
                    GlobalConfig.SOCKET_SCHEME + "://" +
                    GlobalConfig.SERVER_ADDRESS + ":" +
                    GlobalConfig.SERVER_PORT + "/chat/" +
                    chatRoom;

    Request request = new Request
            .Builder()
            .url(serverUrl)
            .build();

    OkHttpClient client = new OkHttpClient();
    ws = client.newWebSocket(request, listener);
    logger.info("Connecting with server...");
  }

  public void disconnect() {
    if(ws == null) {
      logger.info("Cannot disconnect: NULL Connection");
      return;
    }

    ws.close(SocketStatus.NORMAL_CLOSURE_STATUS, SocketStatus.NORMAL_CLOSURE);
    logger.info("Disconnected from server");
  }

  public void sendMessage(ChatMessage chatMessage) {
    try {
      ws.send(chatMessage.toJson().toString());
    } catch (JSONException jsonException) {
      logger.error("Disconnected from server: " + jsonException.getMessage());
      ws.close(SocketStatus.CLOSE_GOING_AWAY, jsonException.getMessage());
    }
  }

  public void deleteMessage(ChatMessage chatMessage) {
    try {
      ws.send(chatMessage.deleteMessageToJson().toString());
    } catch (JSONException jsonException) {
      logger.error("Disconnected from server: " + jsonException.getMessage());
      ws.close(SocketStatus.CLOSE_GOING_AWAY, jsonException.getMessage());
    }
  }

}

class SocketStatus {
  public static int NORMAL_CLOSURE_STATUS = 1000;
  public static String NORMAL_CLOSURE = "NORMAL_CLOSURE";
  public static int CLOSE_GOING_AWAY = 1001;
}