package com.example.tempchat.utils;

import android.util.Log;

public class Logger {
  private Class source;

  public Logger(Class<?> source) {
    this.source = source;
  }

  public void info(String message) {
    Log.i(source.getName(), message);
  }

  public void error(String message) {
    Log.e(source.getName(), message);
  }

  public void debug(String message) {
    Log.d(source.getName(), message);
  }
}
