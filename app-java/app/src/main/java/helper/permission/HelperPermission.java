package helper.permission;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import main.G;
import custom.dialog.SweetAlertDialog;
import helper.utility.Constant;

public class HelperPermission {

private onGrantedPermissionListener onGrantedPermissionListener;
  private static HelperPermission instance;
  public HelperPermission() {}
  public static HelperPermission getInstance(){
    if(instance == null){
      synchronized (HelperPermission.class) {
        if(instance == null){
          instance = new HelperPermission();
        }
      }
    }
    return instance;
  }

  public  void checkGrantedPermission(onGrantedPermissionListener onGrantedPermissionListener){
    this.onGrantedPermissionListener=onGrantedPermissionListener;
    int granted = ActivityCompat.checkSelfPermission(G.currentActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
    if(granted==0){
      onGrantedPermissionListener.onGranted();
    }else{
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        boolean showRationale = G.currentActivity.shouldShowRequestPermissionRationale( Manifest.permission.WRITE_EXTERNAL_STORAGE );
        boolean isDenyUntilNow=G.preferences.getBoolean(Constant.IS_DENY_PERMISSION_STORAGE_UNTI_NOW,Constant.DONT_DENY_PERMISSION_STORAGE_UNTI_NOW);
        if(!showRationale && isDenyUntilNow){
          NotShowRequestPermission();
        }else{
          showGuidDialogPermission();
        }
      }

    }
  }
  public  void showGuidDialogPermission (){
    SweetAlertDialog pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.NORMAL_TYPE);
    pDialog.setTitleText("دسترسی به کارت حافظه");
    pDialog.setContentText("برای اجرای درست برنامه نیازمند این دسترسی هستیم");
    pDialog.setConfirmText("اجازه می دهم");
    pDialog.setCancelable(false);
    pDialog.setCancelText("خروج از برنامه");
    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
          pDialog.dismissWithAnimation();
          requestPermission();
          boolean isDenyUntilNow=G.preferences.getBoolean(Constant.IS_DENY_PERMISSION_STORAGE_UNTI_NOW,Constant.DONT_DENY_PERMISSION_STORAGE_UNTI_NOW);
          if(!isDenyUntilNow){
            SharedPreferences.Editor sEdit = G.preferences.edit();
            sEdit.putBoolean(Constant.IS_DENY_PERMISSION_STORAGE_UNTI_NOW,Constant.DENY_PERMISSION_STORAGE_UNTI_NOW);
            sEdit.apply();
          }
        }
      });
      pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
          pDialog.dismissWithAnimation();
          G.ExitApp();
        }
      });
    pDialog.show();
  }

    public static void requestPermission(){
      ActivityCompat.requestPermissions(G.currentActivity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
   }


  @RequiresApi(api = Build.VERSION_CODES.M)
  public  void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode ==100) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          onGrantedPermissionListener.onGranted();
        } else {
          boolean showRationale = G.currentActivity.shouldShowRequestPermissionRationale( Manifest.permission.WRITE_EXTERNAL_STORAGE );
          boolean isDenyUntilNow=G.preferences.getBoolean(Constant.IS_DENY_PERMISSION_STORAGE_UNTI_NOW,Constant.DONT_DENY_PERMISSION_STORAGE_UNTI_NOW);
          if(!showRationale && isDenyUntilNow){
            NotShowRequestPermission();
          }else{
            showGuidDialogPermission();
          }
        }
      }

  }
private void NotShowRequestPermission(){
  SweetAlertDialog pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE);
  pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
  pDialog.setTitleText("خطای مجوز دسترسی");
  pDialog.setContentText("در قسمت permission  مجوز فعال شود");
  pDialog.setConfirmText("انجام می دهم");
  pDialog.setCancelable(false);
  pDialog.setCancelText("خروج از برنامه");
  pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
      pDialog.dismissWithAnimation();
      requestPermission();
      Intent intent = new Intent();
      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
      Uri uri = Uri.fromParts("package", G.context.getPackageName(), null);
      intent.setData(uri);
      G.currentActivity.startActivityForResult(intent,101);
    }
  });
  pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
      pDialog.dismissWithAnimation();
      G.ExitApp();
    }
  });
  pDialog.show();
}

}
