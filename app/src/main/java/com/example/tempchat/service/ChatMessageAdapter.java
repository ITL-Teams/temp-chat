package com.example.tempchat.service;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tempchat.R;
import com.example.tempchat.model.ChatMessage;
import com.example.tempchat.utils.GlobalConfig;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends ArrayAdapter {
  private final Activity activity;
  private final List<ChatMessage> list;
  private String userId;

  public ChatMessageAdapter(Activity activity, ArrayList<ChatMessage> list) {
    super(activity, 0, list);
    this.activity = activity;
    this.list = list;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ChatMessage chatMessage = list.get(position);

    View rowView = convertView;
    ViewHolder view;

    // Get a new instance of the row layout view
    LayoutInflater inflater = activity.getLayoutInflater();
    System.out.println(chatMessage.getUserId().equals(GlobalConfig.userId));
    System.out.println("OnMessage   : " + chatMessage.getUserId());
    System.out.println("GlobalConfig: " + GlobalConfig.userId);
    if (chatMessage.getUserId().equals(GlobalConfig.userId))
      rowView = inflater.inflate(R.layout.chat_message_2, null);
    else
      rowView = inflater.inflate(R.layout.chat_message_1, null);

    // Hold the view objects in an object, that way the don't need to be "re-  finded"
    view = new ViewHolder();
    view.username = (TextView) rowView.findViewById(R.id.user_name);
    view.content = (TextView) rowView.findViewById(R.id.message_content);
    view.date = (TextView) rowView.findViewById(R.id.date);
    view.time = (TextView) rowView.findViewById(R.id.time);

    rowView.setTag(view);

    /** Set data to your Views. */
    view.username.setText(chatMessage.getUserName());
    view.content.setText(chatMessage.getContent());
    view.date.setText(chatMessage.getDate());
    view.time.setText(chatMessage.getTime());

    return rowView;
  }

  protected static class ViewHolder {
    protected TextView username,
            content,
            date,
            time;
  }

}
