package helper.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.esafirm.imagepicker.model.Image;

import org.json.JSONArray;
import org.json.JSONException;

import custom.dialog.SweetAlertDialog;
import helper.image.HelperImage;
import helper.utility.Constant;
import main.G;

public class HelperLogin {
  public static final int STATE_GET_MOBILE=0;
  public static final int STATE_SEND_CODE=1;
  public static final int STATE_FAILD_CODE=3;
  public static final int STATE_LOGIN_OK=4;
  public static final int STATE_LOGIN_UNKNOW=5;
  public static final String STATE_LOGIN="STATE_LOGIN";
  public static final String URL_LOGIN="http://ccoder.ir/login.php";
  
  private static HelperLogin instance;
  public HelperLogin() {}
  public static HelperLogin getInstance(){
    if(instance == null){
      synchronized (HelperLogin.class) {
        if(instance == null){
          instance = new HelperLogin();
        }
      }
    }
    return instance;
  }
  public void checkLogin(onLoginListener onLoginListener){
    int stateLogin=G.preferences.getInt(Constant.STATE_LOGIN,Constant.STATE_GET_MOBILE);
    if(stateLogin==Constant.STATE_LOGIN_OK){
      onLoginListener.onSucsess();
    }else if(stateLogin==Constant.STATE_LOGIN_UNKNOW){
      checkLoignIsOkWhenLoginIsUnknow(onLoginListener);
    }else {
      shouldLogin(onLoginListener);
    }

 }
 private void shouldLogin(onLoginListener onLoginListener){
   SweetAlertDialog pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE);
   pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
   pDialog.setTitleText("ثبت نام");
   pDialog.setContentText("شما باید ابتدا در برنامه ثبت نام کنید");
   pDialog.setConfirmText("ثبت نام");
   pDialog.setCancelable(false);
   pDialog.setCancelText("لغو");
   pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
     @Override
     public void onClick(SweetAlertDialog sweetAlertDialog) {
       pDialog.dismissWithAnimation();
       G.currentActivity.finish();
       G.currentActivity.startActivity(new Intent( G.currentActivity, ActivityLogin.class));
     }
   });
   pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
     @Override
     public void onClick(SweetAlertDialog sweetAlertDialog) {
       pDialog.dismissWithAnimation();
     }
   });
   pDialog.show();
 }
 private void checkLoignIsOkWhenLoginIsUnknow(onLoginListener onLoginListener){
   SweetAlertDialog pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
     pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
     pDialog.setTitleText("وضعیت ثبت نام ");
     pDialog.setContentText("درحال بررسی وضعیت ثبت نام ");
     pDialog.setCancelable(false);
     pDialog.show();

   AndroidNetworking.post(Constant.URL_LOGIN)
     .addBodyParameter("Key", "CheckLoginIsOk")
     .addBodyParameter("userId", G.getUserId())
     .setPriority(Priority.MEDIUM)
     .build()
     .getAsString(new StringRequestListener() {
       @Override
       public void onResponse(String response) {
           pDialog.dismissWithAnimation();
         if (response.equals(400+"")){
           G.preferences.edit().putInt(Constant.STATE_LOGIN, Constant.STATE_GET_MOBILE).apply();
           shouldLogin(onLoginListener);
         }else if (response.equals(200+"")) {
           G.preferences.edit().putInt(Constant.STATE_LOGIN, Constant.STATE_LOGIN_OK).apply();
           onLoginListener.onSucsess();
         }
       }
       @Override
       public void onError(ANError anError) {
         pDialog.dismissWithAnimation();
         G.preferences.edit().putInt(Constant.STATE_LOGIN, Constant.STATE_LOGIN_UNKNOW).apply();
         SweetAlertDialog pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE);
         pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
         pDialog.setTitleText("وضعیت ثبت نام");
         pDialog.setContentText("بررسی وضعیت ثبت نام شما با خطا مواجه شد");
         pDialog.setConfirmText("بعدا تلاش می کنم");
         pDialog.show();
       }
     });
 }

  public void checkLoignIsOk(){
    AndroidNetworking.post(Constant.URL_LOGIN)
      .addBodyParameter("Key", "CheckLoginIsOk")
      .addBodyParameter("userId", G.getUserId())
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsString(new StringRequestListener() {
        @Override
        public void onResponse(String response) {
          Log.i("TAG","checkLoignIsOk onResponse"+response);
          if (response.equals(400+"")){
            G.preferences.edit().putInt(Constant.STATE_LOGIN, Constant.STATE_GET_MOBILE).apply();
          }else if (response.equals(200+"")) {
            G.preferences.edit().putInt(Constant.STATE_LOGIN, Constant.STATE_LOGIN_OK).apply();
          }
        }
        @Override
        public void onError(ANError anError) {
          Log.i("TAG","checkLoignIsOk Error"+anError.getErrorBody());
          G.preferences.edit().putInt(Constant.STATE_LOGIN, Constant.STATE_LOGIN_UNKNOW).apply();
        }
      });
  }


  public void getRemindTimeForLoginOfServer(onLoginRemindTimeListener timeListener) {
    SweetAlertDialog pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialog.setTitleText("وضعیت ثبت نام ");
    pDialog.setContentText("درحال بررسی وضعیت ثبت نام ");
    pDialog.setCancelable(false);
    pDialog.show();
    AndroidNetworking.post(Constant.URL_LOGIN)
      .addBodyParameter("Key", "RemindTime")
      .addBodyParameter("userId", G.getUserId())
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray response) {
          try{
            if (response.get(0).toString().equals(400+"")){
              SharedPreferences.Editor sEdit = G.preferences.edit();
              sEdit.putInt(Constant.STATE_LOGIN, Constant.STATE_GET_MOBILE);
              sEdit.apply();
              timeListener.onRemind(0);
            }else if (response.get(0).toString().equals(200+"")){
              int millisInFuture = Integer.parseInt(response.get(1).toString());
              timeListener.onRemind(millisInFuture);
            }
            pDialog.dismissWithAnimation();

          }catch (JSONException e){
            e.printStackTrace();
            timeListener.onRemind(0);
            pDialog.dismissWithAnimation();

          }

        }

        @Override
        public void onError(ANError anError) {
          timeListener.onRemind(0);
          pDialog.dismissWithAnimation();
        }
      });
  }





  public void  changeUserName(String userName){
    SweetAlertDialog pDialogChangeProfileUser = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    pDialogChangeProfileUser.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialogChangeProfileUser.setTitleText("درحال بررسی اطلاعات شما");
    pDialogChangeProfileUser.setCancelable(false);
    pDialogChangeProfileUser.show();
    AndroidNetworking.post(Constant.URL_LOGIN)
      .addBodyParameter("Key", "ChangeUserName")
      .addBodyParameter("login_userName", userName)
      .addBodyParameter("userId", G.getUserId())
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsString(new StringRequestListener() {
        @Override
        public void onResponse(String response) {
           pDialogChangeProfileUser.dismissWithAnimation();
          new SweetAlertDialog(G.currentActivity, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("نام کاربری")
            .setContentText("نام کاربری شما با موفقیت تغییر یافت")
            .setConfirmText("باشه")
            .show();
          SharedPreferences.Editor sEdit = G.preferences.edit();
          sEdit.putString(Constant.USER_NAME,userName);
          sEdit.apply();
        }

        @Override
        public void onError(ANError anError)
        { pDialogChangeProfileUser.dismissWithAnimation();
          dialogeWarnNoChangeName();
        }
      });
  }
  public void uploadImageRequest(Image image) {
    SweetAlertDialog pDialogChangeProfileUser = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    pDialogChangeProfileUser.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialogChangeProfileUser.setTitleText("درحال بررسی اطلاعات شما");
    pDialogChangeProfileUser.setCancelable(false);
    pDialogChangeProfileUser.show();
    String stringImage =HelperImage.convertImageToString(image);
    AndroidNetworking.post(Constant.URL_IMAGE_REQUEST)
      .addBodyParameter("Key", "ChangeUserImage")
      .addBodyParameter("imageBeforeUrl", G.preferences.getString(Constant.URL_USER_IMAGE,""))
      .addBodyParameter("imageNew", stringImage)
      .addBodyParameter("name", G.getUserId())
      .addBodyParameter("UserId", G.getUserId())
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsString(new StringRequestListener() {
        @Override
        public void onResponse(String response) {
          pDialogChangeProfileUser.dismissWithAnimation();
          if (response.length()>0){
            new SweetAlertDialog(G.currentActivity, SweetAlertDialog.SUCCESS_TYPE)
              .setTitleText("ارسال عکس")
              .setContentText("عکس شما با موفقیت تغییر یافت")
              .setConfirmText("باشه")
              .show();
            SharedPreferences.Editor sEdit = G.preferences.edit();
            sEdit.putString(Constant.URL_USER_IMAGE,response);
            sEdit.apply();
          }else{
            dialogeWarnNoSendImage();
          }

        }

        @Override
        public void onError(ANError anError)
        {
          pDialogChangeProfileUser.dismissWithAnimation();
          dialogeWarnNoSendImage();
        }
      });


  }
  private void dialogeWarnNoSendImage(){
    new SweetAlertDialog(G.currentActivity, SweetAlertDialog.ERROR_TYPE)
      .setTitleText("ارسال عکس")
      .setContentText("متاسفانه عکس شما ارسال نشد")
      .setConfirmText("بعدا ارسال میکنم")
      .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
          sDialog.dismissWithAnimation();
        }
      })
      .show();
  }
  private void dialogeWarnNoChangeName(){
    new SweetAlertDialog(G.currentActivity, SweetAlertDialog.ERROR_TYPE)
      .setTitleText("نام کاربری")
      .setContentText("متاسفانه نام کاربری شما تغییر نیافت")
      .setConfirmText("بعدا عوض می کن")
      .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
          sDialog.dismissWithAnimation();
        }
      })
      .show();
  }
}
