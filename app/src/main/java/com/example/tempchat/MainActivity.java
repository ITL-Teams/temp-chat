package com.example.tempchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.tempchat.model.ChatMessage;
import com.example.tempchat.service.AES;
import com.example.tempchat.service.ChatWebSocketListener;
import com.example.tempchat.service.PreferenceService;
import com.example.tempchat.service.SocketUtils;
import com.example.tempchat.service.SoundService;
import com.example.tempchat.utils.GlobalConfig;
import com.example.tempchat.utils.MessageFormatter;

public class MainActivity extends AppCompatActivity {
  private PreferenceService userPreferences;
  private SocketUtils socket;
  private ChatWebSocketListener socketListener;
  private EditText chatCode,
                   username,
                   encryptionKey;
  private Handler socketOnConnect,
                  socketOnFailure,
                  socketOnMessage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Disable App ScreenShot
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                         WindowManager.LayoutParams.FLAG_SECURE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override
  protected void onStart() {
    super.onStart();
    chatCode = (EditText) findViewById(R.id.chatCode);
    username = (EditText) findViewById(R.id.username);
    encryptionKey = (EditText) findViewById(R.id.encryptionKey);

    socketOnConnect = new SocketOnConnect();
    socketOnMessage = new SocketOnMessage();
    socketOnFailure = new SocketOnFailure();

    socketListener = new ChatWebSocketListener();
    socketListener.setOnOpenHandler(socketOnConnect);
    socketListener.setOnMessageHandler(socketOnMessage);
    socketListener.setOnFailureHandler(socketOnFailure);

    socket = new SocketUtils(socketListener);

    userPreferences = new PreferenceService(this);
    userPreferences.loadPreferences();

    GlobalConfig.socket = socket;
    GlobalConfig.socketListener = socketListener;

    PreferenceService.ConnectionString connectionString = userPreferences.new ConnectionString();
    String[] connectionStrings = connectionString.read();
    username.setText(connectionStrings[connectionString.USERNAME]);
    encryptionKey.setText(connectionStrings[connectionString.ENCRYPTION_KEY]);
    chatCode.setText(connectionStrings[connectionString.CHAT_CODE]);

    SoundService.mute = GlobalConfig.MUTE_NOTIFICATIONS;

    if(GlobalConfig.MUTE_NOTIFICATIONS) {
      Button connect = (Button) findViewById(R.id.connect);
      connect.setSoundEffectsEnabled(true);
    }
  }

  public void connect(View view) {
    if (!validFields())
      return;

    socket.disconnect();
    socket.connect(chatCode.getText().toString());
  }

  public void preferences(View view) {
    Intent openPreferences = new Intent(getApplicationContext(), PreferencesActivity.class);
    startActivity(openPreferences);
  }

  private boolean validFields() {
    String chatCode = this.chatCode.getText().toString(),
            username = this.username.getText().toString(),
            encryptionKey = this.encryptionKey.getText().toString();

    String alertMessage = null;
    String alertTitle = this.getString(R.string.emptyField);

    if (chatCode.isEmpty())
      alertMessage = this.getString(R.string.emptyChatCodeAlert);

    else if (username.isEmpty())
      alertMessage = this.getString(R.string.emptyUserNameAlert);

    else if (encryptionKey.isEmpty())
      alertMessage = this.getString(R.string.emptyEncryptionKeyAlert);

    if(alertMessage != null) {
      displayAlert(alertTitle, alertMessage, android.R.drawable.ic_delete);
      return false;
    }

    return true;
  }

  private void displayAlert(String alertTitle, String alertMessage, int icon) {
    new AlertDialog.Builder(this)
      .setTitle(alertTitle)
      .setMessage(alertMessage)
      .setNeutralButton(android.R.string.ok, null)
      .setIcon(icon).show();
  }

  private class SocketOnConnect extends Handler {
    @Override
    public void handleMessage(@NonNull Message msg) {
      SoundService.connect(getApplicationContext());
      if(!GlobalConfig.SAVE_CONNECTION_STRING) return;
      PreferenceService.ConnectionString connectionString = userPreferences.new ConnectionString();
      connectionString.save(
        username.getText().toString(),
        encryptionKey.getText().toString(),
        chatCode.getText().toString()
      );
    }
  }

  private class SocketOnMessage extends Handler {
    @Override
    public void handleMessage(@NonNull Message msg) {
      MessageFormatter messageFormatter = new MessageFormatter(msg);
      Intent goToChat = new Intent(getApplicationContext(), ChatActivity.class);

      GlobalConfig.username = username.getText().toString();
      GlobalConfig.encryptionKey = encryptionKey.getText().toString();
      GlobalConfig.chatCode = chatCode.getText().toString();
      GlobalConfig.userId = messageFormatter.getText();

      goToChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      socketListener.setOnFailureHandler(null);
      socketListener.setOnMessageHandler(null);

      if(GlobalConfig.SHOW_STATUS) {
        String connectionMessage = GlobalConfig.username + "OOOOOOconnect";
        ChatMessage userStatus = new ChatMessage(
          GlobalConfig.username,
          connectionMessage
        );

        connectionMessage = AES.encrypt(connectionMessage, GlobalConfig.encryptionKey);
        userStatus.setContent(connectionMessage);

        socket.sendMessage(userStatus);
      }

      getApplicationContext().startActivity(goToChat);
    }
  }

  private class SocketOnFailure extends  Handler {
    @Override
    public void handleMessage(@NonNull Message msg) {
      String alertTitle = getApplicationContext().getString(R.string.failedToConnectTitle);
      String alertMessage = getApplicationContext().getString(R.string.failedToConnectMessage);
      displayAlert(alertTitle, alertMessage, R.drawable.cloud);
    }
  }
}