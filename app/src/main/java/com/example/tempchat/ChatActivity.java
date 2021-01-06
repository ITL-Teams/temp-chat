package com.example.tempchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tempchat.model.ChatMessage;
import com.example.tempchat.service.ChatMessageAdapter;
import com.example.tempchat.service.ChatWebSocketListener;
import com.example.tempchat.service.SocketUtils;
import com.example.tempchat.utils.GlobalConfig;
import com.example.tempchat.utils.MessageFormatter;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
  private ListView chat;
  private EditText message;
  private SocketUtils socketUtils;
  private ChatWebSocketListener socketListener;
  private ChatMessageAdapter messages;
  private Handler socketOnMessage,
                  socketOnClose;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    socketUtils = GlobalConfig.socket;
    socketListener = GlobalConfig.socketListener;
    socketOnMessage = new MessageHandler();

    message = (EditText) findViewById(R.id.message);
    messages = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());

    chat = (ListView) findViewById(R.id.chat);
    chat.invalidateViews();
    chat.setAdapter(messages);
    chat.setDivider(null);
    chat.setDividerHeight(0);
    chat.setOnItemClickListener((parent, view, position, id) -> {
      ChatMessage message = messages.getChatMessage(position);

      if(message.getUserId().equals(GlobalConfig.userId))
        displayDeleteMessageAlert(
                this.getString(R.string.delete_message_title),
                this.getString(R.string.delete_message_body),
                android.R.drawable.ic_dialog_alert,
                (dialog, which) -> {
                  messages.remove(message);
                  Toast.makeText(this, R.string.message_deleted, Toast.LENGTH_SHORT).show();
                },
                message.isDeleted() ? null : (dialog, which) -> socketUtils.deleteMessage(message)
        );
    });

    socketListener.setOnMessageHandler(socketOnMessage);
  }

  public void disconnect(View view) {
    Intent intentMainActivity = new Intent(this, MainActivity.class);
    socketUtils.disconnect();
    messages.clear();
    startActivity(intentMainActivity);
  }

  public void sendMessage(View view) {
    ChatMessage message = new ChatMessage(
      GlobalConfig.username,
      this.message.getText().toString()
    );
    socketUtils.sendMessage(message);
    this.message.setText("");
  }

  private void displayDeleteMessageAlert(
    String alertTitle,
    String alertMessage,
    int icon,
    DialogInterface.OnClickListener deleteForMeListener,
    DialogInterface.OnClickListener deleteForEveryoneListener
  ) {
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);

    dialog.setTitle(alertTitle)
          .setMessage(alertMessage)
          .setIcon(icon)
          .setPositiveButton(this.getString(R.string.delete_for_me), deleteForMeListener)
          .setNegativeButton(this.getString(R.string.delete_message_negative_answer), null);

    if(deleteForEveryoneListener != null)
      dialog.setNeutralButton(this.getString(R.string.delete_for_everyone), deleteForEveryoneListener);

    dialog.show();
  }

  class MessageHandler extends Handler {
    @Override
    public void handleMessage(@NonNull Message msg) {
      MessageFormatter messageFormatter = new MessageFormatter(msg);

      if(messageFormatter.getMessageType() == MessageFormatter.MessageType.DELETE_MESSAGE) {
        messages.remove(
          messageFormatter.getDeleteMessage(),
          getApplicationContext().getString(R.string.message_deleted)
        );
      }
      else
        messages.add(messageFormatter.getChatMessage());
    }
  }
}
