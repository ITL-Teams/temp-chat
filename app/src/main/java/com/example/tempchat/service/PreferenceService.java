package com.example.tempchat.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tempchat.utils.GlobalConfig;

public class PreferenceService {
  private SharedPreferences sharedPreferences;
  private String SERVER_ADDRESS_KEY = "server_address";
  private String SHOW_STATUS_KEY = "show_status";
  private String DELETE_MESSAGES_ON_DISCONNECT_KEY = "delete_messages";
  private String SAVE_CONNECTION_STRING_KEY = "save_connection";
  private String SAVE_CHATS_ON_DISCONNECT_KEY = "save_chats";

  public PreferenceService(Context context) {
    sharedPreferences = context.getSharedPreferences(
            GlobalConfig.PREFERENCES_FILE,
            Context.MODE_PRIVATE
    );
  }

  public void setServerAddress(String serverAddress) {
    saveString(serverAddress, SERVER_ADDRESS_KEY);
    GlobalConfig.SERVER_ADDRESS = serverAddress;
  }

  public void setShowStatus(boolean status) {
    saveBoolean(status, SHOW_STATUS_KEY);
    GlobalConfig.SHOW_STATUS = status;
  }

  public void setDeleteMessagesOnDisconnect(boolean deleteMessagesOnDisconnect) {
    saveBoolean(deleteMessagesOnDisconnect, DELETE_MESSAGES_ON_DISCONNECT_KEY);
    GlobalConfig.DELETE_MESSAGES_ON_DISCONNECT = deleteMessagesOnDisconnect;
  }

  public void setSaveConnectionString(boolean saveConnectionString) {
    saveBoolean(saveConnectionString, SAVE_CONNECTION_STRING_KEY);
    GlobalConfig.SAVE_CONNECTION_STRING = saveConnectionString;
  }

  public void setSaveChatsOnDisconnect(boolean chatsOnDisconnect) {
    saveBoolean(chatsOnDisconnect, SAVE_CHATS_ON_DISCONNECT_KEY);
    GlobalConfig.SAVE_CHATS_ON_DISCONNECT = chatsOnDisconnect;
  }

  public void loadPreferences() {
    GlobalConfig.SERVER_ADDRESS =
            sharedPreferences.getString(SERVER_ADDRESS_KEY, GlobalConfig.DEFAULT_SERVER_ADDRESS);
    GlobalConfig.SHOW_STATUS =
            sharedPreferences.getBoolean(SHOW_STATUS_KEY, false);
    GlobalConfig.DELETE_MESSAGES_ON_DISCONNECT =
            sharedPreferences.getBoolean(DELETE_MESSAGES_ON_DISCONNECT_KEY, true);
    GlobalConfig.SAVE_CONNECTION_STRING =
            sharedPreferences.getBoolean(SAVE_CONNECTION_STRING_KEY, false);
    GlobalConfig.SAVE_CHATS_ON_DISCONNECT =
            sharedPreferences.getBoolean(SAVE_CHATS_ON_DISCONNECT_KEY, false);
  }

  private void saveString(String string, String key) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(key, string);
    editor.commit();
  }

  private void saveBoolean(boolean bool, String key) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(key, bool);
    editor.commit();
  }

  public class ConnectionString {
    public int USERNAME = 0;
    public int ENCRYPTION_KEY = 1;
    public int CHAT_CODE = 2;

    public void save(
      String username,
      String encryptionKey,
      String chatCode
    ) {
      saveString(username, "username");
      saveString(encryptionKey, "encryptionKey");
      saveString(chatCode, "chatCode");
    }

    public String[] read() {
      return new String[] {
       sharedPreferences.getString("username", ""),
       sharedPreferences.getString("encryptionKey", ""),
       sharedPreferences.getString("chatCode", "")
      };
    }

    public void delete() {
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.remove("username");
      editor.remove("encryptionKey");
      editor.remove("chatCode");
      editor.commit();
    }

  }

}
