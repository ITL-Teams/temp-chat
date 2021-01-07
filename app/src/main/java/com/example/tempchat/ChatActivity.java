package com.example.tempchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tempchat.model.ChatMessage;
import com.example.tempchat.service.AES;
import com.example.tempchat.service.ChatDatabase;
import com.example.tempchat.service.ChatMessageAdapter;
import com.example.tempchat.service.ChatWebSocketListener;
import com.example.tempchat.service.SocketUtils;
import com.example.tempchat.service.SoundService;
import com.example.tempchat.utils.GlobalConfig;
import com.example.tempchat.utils.MessageFormatter;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
  private ListView chat;
  private EditText message;
  private SocketUtils socketUtils;
  private ChatWebSocketListener socketListener;
  private ChatMessageAdapter messages;
  private Handler socketOnMessage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Disable App ScreenShot
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                         WindowManager.LayoutParams.FLAG_SECURE);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    socketUtils = GlobalConfig.socket;
    socketListener = GlobalConfig.socketListener;
    socketOnMessage = new MessageHandler();

    message = (EditText) findViewById(R.id.message);

    if(GlobalConfig.SAVE_CHATS_ON_DISCONNECT)
      messages = new ChatMessageAdapter(this, ChatDatabase.read(
        GlobalConfig.chatCode,
        this
      ));
    else
      messages = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());

    chat = (ListView) findViewById(R.id.chat);
    chat.invalidateViews();
    chat.setAdapter(messages);
    chat.setDivider(null);
    chat.setDividerHeight(0);
    chat.setOnItemClickListener((parent, view, position, id) -> {
      ChatMessage message = messages.getChatMessage(position);
      displayDeleteMessageAlert(
        this.getString(R.string.delete_message_title),
        this.getString(R.string.delete_message_body),
        R.drawable.trash,
        (dialog, which) -> {
          messages.remove(message);
          Toast.makeText(this, R.string.message_deleted, Toast.LENGTH_SHORT).show();
        },
        message.isDeleted() || !message.getUserId().equals(GlobalConfig.userId) ?
                null : (dialog, which) -> socketUtils.deleteMessage(message)
      );
    });

    socketListener.setOnMessageHandler(socketOnMessage);

    if(GlobalConfig.MUTE_NOTIFICATIONS) {
      Button button = (Button) findViewById(R.id.send);
      button.setSoundEffectsEnabled(true);

      button = (Button) findViewById(R.id.disconnect);
      button.setSoundEffectsEnabled(true);
    }
  }

  public void disconnect(View view) {
    SoundService.disconnect(this);
    // save user messages on disconnect
    if(GlobalConfig.SAVE_CHATS_ON_DISCONNECT)
      ChatDatabase.save(
        messages.getList(),
        GlobalConfig.chatCode,
        this
      );
    else
      ChatDatabase.delete(GlobalConfig.chatCode, this);

    // delete user messages on disconnect
    if(GlobalConfig.DELETE_MESSAGES_ON_DISCONNECT)
      deleteAll();

    if(GlobalConfig.SHOW_STATUS) {
      String disconnectionMessage =
              GlobalConfig.username + " " + getApplicationContext().getString(R.string.offline_user);
      ChatMessage userStatus = new ChatMessage(
              GlobalConfig.username,
              disconnectionMessage
      );

      disconnectionMessage = AES.encrypt(disconnectionMessage, GlobalConfig.encryptionKey);
      userStatus.delete(disconnectionMessage);
      socketUtils.sendMessage(userStatus);
    }

    socketUtils.disconnect();
    messages.clear();

    finish();
  }

  public void deleteAll() {
    for(int index = 0; index < messages.getCount(); index++) {
      ChatMessage message = (ChatMessage) messages.getItem(index);

      if(message.getUserId().equals(GlobalConfig.userId))
        socketUtils.deleteMessage(message);
    }
  }

  public void deleteMessages(View view) {
    if(messages.getList().size() <= 0) {
      Toast.makeText(
              this,
              this.getString(R.string.no_messages_to_delete),
              Toast.LENGTH_LONG
      ).show();
      return;
    }

    displayDeleteAllMessagesAlert(
            this.getString(R.string.delete_message_title),
            this.getString(R.string.delete_message_body),
            R.drawable.trash,
            (dialog, which) -> messages.clear(),
            (dialog, which) -> {
              deleteAll();
              messages.clear();
            }
    );
  }

  public void sendMessage(View view) {
    String message_content = this.message.getText().toString();

    if(message_content.isEmpty()) {
      Toast.makeText(
              this,
              this.getString(R.string.empty_message_alert),
              Toast.LENGTH_LONG
      ).show();
      return;
    }

    message_content = AES.encrypt(message_content, GlobalConfig.encryptionKey);

    ChatMessage message = new ChatMessage(
      GlobalConfig.username,
      message_content
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

  private void displayDeleteAllMessagesAlert(
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
      else if(messageFormatter.getMessageType() == MessageFormatter.MessageType.CHAT_MESSAGE) {
        SoundService.newMessage(getApplicationContext());
        ChatMessage chatMessage = messageFormatter.getChatMessage();
        String messageContent = chatMessage.getContent();
        messageContent = AES.decrypt(messageContent, GlobalConfig.encryptionKey);
        chatMessage.setContent(messageContent);
        messages.add(chatMessage);
      }
    }
  }
}
