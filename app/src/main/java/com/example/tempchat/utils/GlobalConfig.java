package com.example.tempchat.utils;

import com.example.tempchat.service.ChatWebSocketListener;
import com.example.tempchat.service.SocketUtils;

public class GlobalConfig {
  // Socket Connection
  public static String SOCKET_SCHEME = "ws";
  public static String SERVER_ADDRESS = "10.0.2.2";
  public static String SERVER_PORT = "3000";

  // Run time variables
  public static String userId;
  public static String username;
  public static String encryptionKey;
  public static String chatCode;
  public static SocketUtils socket;
  public static ChatWebSocketListener socketListener;
}
