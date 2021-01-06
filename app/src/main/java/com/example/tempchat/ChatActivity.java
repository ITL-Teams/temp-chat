package com.example.tempchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.tempchat.service.ChatWebSocketListener;
import com.example.tempchat.service.SocketUtils;
import com.example.tempchat.utils.MessageFormatter;

public class ChatActivity extends AppCompatActivity {
  private ListView chat;
  private SocketUtils socketUtils;
  private ChatWebSocketListener socketListener;
  private ArrayAdapter<String> messages;
  private Handler socketOnMessage,
                  socketOnClose;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    socketUtils = MainActivity.socket;
    socketListener = MainActivity.socketListener;
    socketOnMessage = new MessageHandler();

    messages = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

    chat = (ListView) findViewById(R.id.chat);
    chat.setAdapter(messages);

    socketListener.setOnMessageHandler(socketOnMessage);
  }

  class MessageHandler extends Handler {
    @Override
    public void handleMessage(@NonNull Message msg) {
      System.out.println("ON message");
      MessageFormatter messageFormatter = new MessageFormatter(msg);
      String text = "Username: " + messageFormatter.getChatMessage().getUserName() + "\n";
      text += "Message: " + messageFormatter.getChatMessage().getContent();
      messages.add(text);
    }
  }
}
