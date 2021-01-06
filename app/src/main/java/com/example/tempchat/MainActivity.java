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
import android.widget.EditText;

import com.example.tempchat.service.ChatWebSocketListener;
import com.example.tempchat.service.SocketUtils;
import com.example.tempchat.utils.GlobalConfig;
import com.example.tempchat.utils.MessageFormatter;

public class MainActivity extends AppCompatActivity {
  private SocketUtils socket;
  private ChatWebSocketListener socketListener;
  private EditText chatCode,
                   username,
                   encryptionKey;
  private Handler socketOnConnect,
                  socketOnFailure;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Disable App ScreenShot
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                         WindowManager.LayoutParams.FLAG_SECURE);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    chatCode = (EditText) findViewById(R.id.chatCode);
    username = (EditText) findViewById(R.id.username);
    encryptionKey = (EditText) findViewById(R.id.encryptionKey);

    socketOnConnect = new SocketOnConnect();
    socketOnFailure = new SocketOnFailure();

    socketListener = new ChatWebSocketListener();
    socketListener.setOnMessageHandler(socketOnConnect);
    socketListener.setOnFailureHandler(socketOnFailure);

    socket = new SocketUtils(socketListener);

    GlobalConfig.socket = socket;
    GlobalConfig.socketListener = socketListener;
  }

  public void connect(View view) {
    if (!validFields())
      return;

    socket.disconnect();
    socket.connect(chatCode.getText().toString());
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
      MessageFormatter messageFormatter = new MessageFormatter(msg);
      Intent goToChat = new Intent(getApplicationContext(), ChatActivity.class);

      GlobalConfig.username = username.getText().toString();
      GlobalConfig.encryptionKey = encryptionKey.getText().toString();
      GlobalConfig.chatCode = chatCode.getText().toString();
      GlobalConfig.userId = messageFormatter.getText();

      goToChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      socketListener.setOnFailureHandler(null);
      socketListener.setOnMessageHandler(null);

      getApplicationContext().startActivity(goToChat);
    }
  }

  private class SocketOnFailure extends  Handler {
    @Override
    public void handleMessage(@NonNull Message msg) {
      String alertTitle = getApplicationContext().getString(R.string.failedToConnectTitle);
      String alertMessage = getApplicationContext().getString(R.string.failedToConnectMessage);
      displayAlert(alertTitle, alertMessage, android.R.drawable.ic_dialog_alert);
    }
  }
}