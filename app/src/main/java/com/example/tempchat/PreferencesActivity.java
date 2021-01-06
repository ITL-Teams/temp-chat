package com.example.tempchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class PreferencesActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Disable App ScreenShot
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                         WindowManager.LayoutParams.FLAG_SECURE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_preferences);
  }

  public void back(View view) {
    Intent backToMenu = new Intent(getApplicationContext(), MainActivity.class);
    startActivity(backToMenu);
  }

}