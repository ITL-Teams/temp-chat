package com.example.tempchat.service;

import android.content.Context;

import com.example.tempchat.model.ChatMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ChatDatabase {
  public static void delete(String fileName, Context context) {
    context.deleteFile(fileName);
  }

  public static void save(ArrayList<ChatMessage> messages, String fileName, Context context) {
    FileOutputStream fos;
    try {
      fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(messages);
      oos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static ArrayList<ChatMessage> read(String fileName, Context context) {
    FileInputStream fis;
    try {
      fis = context.openFileInput(fileName);
      ObjectInputStream ois = new ObjectInputStream(fis);
      ArrayList<ChatMessage> messages = (ArrayList<ChatMessage>) ois.readObject();
      ois.close();
      return messages;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new ArrayList<ChatMessage>();
  }
}
