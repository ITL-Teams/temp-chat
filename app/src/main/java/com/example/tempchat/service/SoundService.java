package com.example.tempchat.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.tempchat.R;

public class SoundService {
  public static boolean mute = false;

  public static void newMessage(Context context) {
    play(R.raw.new_message, context);
  }

  public static void disconnect(Context context) {
    play(R.raw.disconnect, context);
  }

  public static void connect(Context context) {
    play(R.raw.connect, context);
  }

  private static void play(int reference, Context context) {
    if(mute) return;

    MediaPlayer mediaPlayer = MediaPlayer.create(context, reference);
    mediaPlayer.start();
  }
}
