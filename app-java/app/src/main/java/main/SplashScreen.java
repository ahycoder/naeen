package main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import custom.dialog.SweetAlertDialog;
import helper.database.UpdateDatabase;
import helper.database.onEndUpdateDatabaseListener;
import helper.directory.HelperDirectory;
import helper.directory.onCreateDirListener;
import helper.login.ActivityLogin;
import helper.login.HelperLogin;
import helper.login.onLoginRemindTimeListener;
import helper.permission.HelperPermission;
import helper.permission.onGrantedPermissionListener;
import helper.utility.Constant;
import intro.IntroActivity;
import ir.naeen.yousefi.R;

public class SplashScreen extends ActivityTop {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
   setContentView(R.layout.splash_screen_activity);
   boolean state_first_arrive=G.preferences.getBoolean(Constant.STATE_IS_FIRST_ARRIVE,true);
    if (state_first_arrive){
      SharedPreferences.Editor sEdit = G.preferences.edit();
      sEdit.putBoolean(Constant.STATE_IS_FIRST_ARRIVE, false);
      sEdit.apply();
      G.currentActivity.startActivity(new Intent(G.currentActivity, IntroActivity.class));
      G.currentActivity.finish();
    }else{
      G.handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          int state_login=G.preferences.getInt(Constant.STATE_LOGIN,Constant.STATE_GET_MOBILE);
          if(state_login== Constant.STATE_SEND_CODE || state_login== Constant.STATE_FAILD_CODE){
            HelperLogin.getInstance().getRemindTimeForLoginOfServer(new onLoginRemindTimeListener() {
              @Override
              public void onRemind(int millisIn) {
                if (millisIn>5000){
                  Intent intent = new Intent(G.currentActivity, ActivityLogin.class);
                  intent.putExtra("RemindTime",millisIn);
                  G.currentActivity.finish();
                  G.currentActivity.startActivity(intent);
                }else{
                  SharedPreferences.Editor sEdit = G.preferences.edit();
                  sEdit.putInt(Constant.STATE_LOGIN, Constant.STATE_GET_MOBILE);
                  sEdit.apply();
                  new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("پایان اعتبار کد فعالسازی")
                    .setContentText("متاسفانه مجددا باید عملیات ثبت نام را شروع کنید")
                    .setConfirmText("ثبت نام")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                      @Override
                      public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        Intent intent = new Intent(G.currentActivity, ActivityLogin.class);
                        G.currentActivity.finish();
                        G.currentActivity.startActivity(intent);
                      }
                    })
                    .setCancelText("ادامه برنامه")
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                      @Override
                      public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        goToMainActivity();
                      }
                    })
                    .show();
                }

              }
            });

          }else{
            DoTasksSplashScreen();
          }
        }
      },3000);

    }







  }


  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    HelperPermission.getInstance().onRequestPermissionResult(requestCode,permissions,grantResults);
  }

  private void DoTasksSplashScreen(){
    HelperPermission.getInstance().checkGrantedPermission(new onGrantedPermissionListener() {
      @Override
      public void onGranted() {
        HelperDirectory.getInstance().createFullDirApp(new onCreateDirListener() {
          @Override
          public void onSucsess() {
            UpdateDatabase.updateDb(new onEndUpdateDatabaseListener() {
              @Override
              public void onSucsess() {
                goToMainActivity();
              }

              @Override
              public void onError() {
                new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE)
                  .setTitleText("خطای بروزرسانی")
                  .setContentText("متاسفانه اطلاعات بروزرسانی نشدند")
                  .setConfirmText("ادامه برنامه")
                  .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                      sDialog.dismissWithAnimation();
                      goToMainActivity();
                    }
                  })
                  .show();

              }
            });
          }

          @Override
          public void onError() {
            new SweetAlertDialog(G.currentActivity, SweetAlertDialog.ERROR_TYPE)
              .setTitleText("خطای اجرای برنامه")
              .setContentText("متاسفانه اجرای برنامه با خطا مواجه شده است")
              .setConfirmText("خروج از برنامه")
              .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                  sDialog.dismissWithAnimation();
                  G.ExitApp();
                }
              })
              .show();
          }
        });
      }
    });
  }

  private void goToMainActivity(){
    Intent intent = new Intent(G.currentActivity, MainActivity.class);
    G.currentActivity.finish();
    G.currentActivity.startActivity(intent);
  }
}

