package com.example.tempchat.utils;

import com.example.tempchat.service.ChatWebSocketListener;
import com.example.tempchat.service.SocketUtils;

public class GlobalConfig {
  // Socket Connection
  public static String SOCKET_SCHEME = "ws";
  public static String DEFAULT_SERVER_ADDRESS = "10.0.2.2";
  public static String SERVER_PORT = "3000";

  // Preferences
  public static String PREFERENCES_FILE = "user_preferences";
  public static String SERVER_ADDRESS = DEFAULT_SERVER_ADDRESS;
  public static boolean SHOW_STATUS = false;
  public static boolean DELETE_MESSAGES_ON_DISCONNECT = true;
  public static boolean SAVE_CONNECTION_STRING = false;
  public static boolean SAVE_CHATS_ON_DISCONNECT = false;
  public static boolean MUTE_NOTIFICATIONS = false;

  // Run time variables
  public static String userId;
  public static String username;
  public static String encryptionKey;
  public static String chatCode;
  public static SocketUtils socket;
  public static ChatWebSocketListener socketListener;
}
