package com.example.tempchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

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

  class MessageHandler extends Handler {
    @Override
    public void handleMessage(@NonNull Message msg) {
      System.out.println("ON message");
      MessageFormatter messageFormatter = new MessageFormatter(msg);
      messages.add(messageFormatter.getChatMessage());
    }
  }
}
