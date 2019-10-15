package main;

import android.content.Context;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;

import androidx.appcompat.app.AppCompatActivity;
import helper.font.FontContextWrapper;

public class ActivityTop extends AppCompatActivity{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    G.currentActivity = this;
  }

  @Override
  protected void onResume() {
    super.onResume();
  }
  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(FontContextWrapper.wrap(newBase));
  }
  @Override
  protected void onPause() {
    super.onPause();
    AndroidNetworking.cancelAll();
  }
}