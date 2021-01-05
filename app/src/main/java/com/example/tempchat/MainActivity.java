package com.example.tempchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.example.tempchat.service.ChatWebSocketListener;
import com.example.tempchat.service.SocketUtils;

public class MainActivity extends AppCompatActivity {
  public static SocketUtils socket;
  public static ChatWebSocketListener socketListener;
  private EditText chatCode,
                   username,
                   encryptionKey;
  private Handler socketOnConnect,
                  socketOnFailure;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    chatCode = (EditText) findViewById(R.id.chatCode);
    username = (EditText) findViewById(R.id.username);
    encryptionKey = (EditText) findViewById(R.id.encryptionKey);

    socketOnConnect = new SocketOnConnect();
    socketOnFailure = new SocketOnFailure();

    socketListener = new ChatWebSocketListener();
    socketListener.setOnOpenHandler(socketOnConnect);
    socketListener.setOnFailureHandler(socketOnFailure);

    socket = new SocketUtils(socketListener);
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
      Intent goToChat = new Intent(getApplicationContext(), ChatActivity.class);
      goToChat.putExtra("username", username.getText().toString());
      goToChat.putExtra("encryptionKey", encryptionKey.getText().toString());
      goToChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      socketListener.setOnOpenHandler(null);
      socketListener.setOnFailureHandler(null);

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