package main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import helper.font.FontConfig;
import helper.login.HelperLogin;
import helper.utility.Constant;
import ir.naeen.yousefi.R;
import me.cheshmak.android.sdk.core.Cheshmak;

public class G extends Application {

  public  static Context context;
  public  static SharedPreferences preferences;
  public  static  Handler handler;
  public static LayoutInflater layoutInflater;
  public static DisplayMetrics displayMetrics;
  public static AppCompatActivity currentActivity;
  public static final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
  public static final String APP_DIR =SDCARD+"/naeen";
  public static final String DB_DIR = APP_DIR + "/db";
  public static final int      DOWNLOAD_BUFFER_SIZE = 8 * 1024;
  public static final String DB_NAME = "naeen.sqlite";
  public static final int MAX_CASH_NET_MINUTE =10;
  private static Long currentTimeApp;
  private static Long currentTimeServer;

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(base);
  }



  @Override
  public void onCreate() {
    super.onCreate();

    context=getApplicationContext();
    preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
    handler= new Handler();
    layoutInflater =LayoutInflater.from(context);
    displayMetrics = new DisplayMetrics();
    Cheshmak.with(context);
    Cheshmak.initTracker(Constant.CHESHMAK_ID);
    AndroidNetworking.initialize(context);
    HelperLogin.getInstance().checkLoignIsOk();
    FontConfig.initDefault(new FontConfig.Builder()
      .setDefaultFontPath("fonts/IRANSansMobile.ttf")
      .setFontAttrId(R.attr.fontPath)
      .build());
    setCurrentTimeFromServer();
  }
  public static String getUserId() {
    if (G.preferences.getString("UserId", null) == null) {
      SharedPreferences.Editor sEdit = G.preferences.edit();
      String userId = "user" + System.currentTimeMillis()+(int)(Math.random() * 500 + 1);;
      sEdit.putString("UserId", userId);
      sEdit.apply();
      return userId;
    }
    return G.preferences.getString("UserId", null);
  }
  public static int getWidthDispaly(){
    currentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    return displayMetrics.widthPixels;
  }
  public static int getheightDispaly(){
    currentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    return displayMetrics.heightPixels;
  }
  public static void ExitApp(){
    G.currentActivity.finish();
    System.exit(0);
  }

  private void setCurrentTimeFromServer(){
    currentTimeApp =System.currentTimeMillis();
    AndroidNetworking.post(Constant.URL_Time)
      .addBodyParameter("Key","CurrentTime")
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsString(new StringRequestListener() {
        @Override
        public void onResponse(String response) {
//           currentTimeServer =Long.parseLong(response);
//          Log.i("DATE","currentTimeApp : "+currentTimeApp);
//          Log.i("DATE","currentTimeServer : "+response);
        }

        @Override
        public void onError(ANError anError) {

        }
      });
  }

  public static Long getCurrentTimeFromServer(){
    if (currentTimeServer!=null){
      Long difreentTimeInApp =System.currentTimeMillis()-currentTimeApp;
      return difreentTimeInApp+currentTimeServer;
    }
    return System.currentTimeMillis();
  }
}

