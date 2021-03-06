package com.example.tempchat;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tempchat.service.PreferenceService;
import com.example.tempchat.service.SoundService;
import com.example.tempchat.utils.GlobalConfig;

public class PreferencesActivity extends AppCompatActivity {
  private PreferenceService userPreferences;
  private EditText server_address;
  private CheckBox show_status,
                   delete_on_disconnect,
                   save_connection_string,
                   save_chats,
                   mute_notifications;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Disable App ScreenShot
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                         WindowManager.LayoutParams.FLAG_SECURE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_preferences);

    userPreferences = new PreferenceService(this);

    server_address = (EditText) findViewById(R.id.server_address);
    show_status = (CheckBox) findViewById(R.id.show_status);
    delete_on_disconnect = (CheckBox) findViewById(R.id.delete_on_disconnect);
    save_connection_string = (CheckBox) findViewById(R.id.save_connection_string);
    save_chats = (CheckBox) findViewById(R.id.save_chats);
    mute_notifications = (CheckBox) findViewById(R.id.mute);

    // Load Existing Preferences
    if(!GlobalConfig.SERVER_ADDRESS.equals(GlobalConfig.DEFAULT_SERVER_ADDRESS))
      server_address.setText(GlobalConfig.SERVER_ADDRESS);
    show_status.setChecked(GlobalConfig.SHOW_STATUS);
    delete_on_disconnect.setChecked(GlobalConfig.DELETE_MESSAGES_ON_DISCONNECT);
    save_connection_string.setChecked(GlobalConfig.SAVE_CONNECTION_STRING);
    save_chats.setChecked(GlobalConfig.SAVE_CHATS_ON_DISCONNECT);
    mute_notifications.setChecked(GlobalConfig.MUTE_NOTIFICATIONS);
  }

  public void back(View view) {
    finish();
  }
  
  public void save(View view) {
    if(server_address.getText().toString().equals(""))
      userPreferences.setServerAddress(GlobalConfig.DEFAULT_SERVER_ADDRESS);
    else
      userPreferences.setServerAddress(server_address.getText().toString());

    if(!save_connection_string.isChecked())
      userPreferences.new ConnectionString().delete();

    userPreferences.setSaveConnectionString(save_connection_string.isChecked());
    userPreferences.setShowStatus(show_status.isChecked());
    userPreferences.setDeleteMessagesOnDisconnect(delete_on_disconnect.isChecked());
    userPreferences.setSaveChatsOnDisconnect(save_chats.isChecked());
    userPreferences.setMuteNotifications(mute_notifications.isChecked());

    Toast.makeText(this, R.string.saved_preferences, Toast.LENGTH_SHORT).show();
  }

}