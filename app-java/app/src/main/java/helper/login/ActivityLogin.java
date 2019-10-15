package helper.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;

import androidx.appcompat.app.AppCompatActivity;
import helper.utility.Constant;
import ir.naeen.yousefi.R;
import main.G;
import main.MainActivity;
import me.cheshmak.android.sdk.core.Cheshmak;

public class ActivityLogin extends AppCompatActivity{
    private CountDownTimer countDownTimer;
    private TextView txtLogin;
    private TextView txtTime;
    private TextView txtSendAgainCode;
    private EditText edtLogin;
    private Button btnLogin;
    private SharedPreferences preferences;

    @Override
    protected void onResume() {
        int state_login=preferences.getInt(HelperLogin.STATE_LOGIN,HelperLogin.STATE_GET_MOBILE);
        Bundle bundle = getIntent().getExtras();
        if(state_login== HelperLogin.STATE_GET_MOBILE){
            stateUiGetMobile();
        }else if(state_login== HelperLogin.STATE_SEND_CODE){
            stateUiSendCode();
            if(bundle !=null) {
              countDownTimer(bundle.getInt("RemindTime"));
            }
        }else if(state_login== HelperLogin.STATE_FAILD_CODE){
            stateUiFailedCode();
            if(bundle !=null) {
                countDownTimer(bundle.getInt("RemindTime"));
            }
        }
        super.onResume();
    }

    private void countDownTimer(int millisInFuture) {
        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            public void onTick(long millisUntilFinished) {
                String v = String.format("%02d", millisUntilFinished / 60000);
                int va = (int) ((millisUntilFinished % 60000) / 1000);
                txtTime.setText("" + v + ":" + String.format("%02d", va));
            }

            public void onFinish() {
                SharedPreferences.Editor sEdit = G.preferences.edit();
                sEdit.putInt(HelperLogin.STATE_LOGIN, HelperLogin.STATE_GET_MOBILE);
                sEdit.apply();
                stateUiGetMobile();
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        txtLogin=findViewById(R.id.txtLogin);
        txtTime=findViewById(R.id.txtTime);
        txtSendAgainCode=findViewById(R.id.txtSendAgainCode);
        edtLogin=findViewById(R.id.edtLogin);
        btnLogin=findViewById(R.id.btnLogin);
        preferences =getSharedPreferences("preferences", Context.MODE_PRIVATE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int stateLogin=preferences.getInt(Constant.STATE_LOGIN,Constant.STATE_GET_MOBILE);
                if(stateLogin== HelperLogin.STATE_GET_MOBILE){
                    getMobileAndSendToServer();
                }else if(stateLogin== HelperLogin.STATE_SEND_CODE){
                    sendVrifyCode();
                } else if(stateLogin== HelperLogin.STATE_FAILD_CODE){
                    sendVrifyCode();
                }
            }
        });
        txtSendAgainCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putInt(HelperLogin.STATE_LOGIN,HelperLogin.STATE_GET_MOBILE).apply();
                countDownTimer.cancel();
                stateUiGetMobile();
            }
        });
    }
    private void stateUiGetMobile(){
        txtSendAgainCode.setVisibility(View.GONE);
        txtTime.setVisibility(View.GONE);
        txtLogin.setText("شماره همراه خود را واردکنید");
        btnLogin.setText("ارسال شماره همراه ");
    }
    private void stateUiSendCode(){
        txtSendAgainCode.setVisibility(View.GONE);
        txtTime.setVisibility(View.VISIBLE);
        edtLogin.setText("");
        txtLogin.setText("کد ارسال شده را وارد کنید");
        btnLogin.setText("ارسال کد ");
    }
    private void stateUiFailedCode(){
        txtSendAgainCode.setVisibility(View.VISIBLE);
        txtTime.setVisibility(View.VISIBLE);
        txtLogin.setText("کد وارد شده اشتباه می باشد \n لطفا مجددا وارد کنید");
        btnLogin.setText("ارسال کد");
    }
    private void sendVrifyCode() {
        AndroidNetworking.post(HelperLogin.URL_LOGIN)
          .addBodyParameter("Key", "SendCode")
          .addBodyParameter("verifyCode", edtLogin.getText().toString())
          .addBodyParameter("userId", G.getUserId())
          .setPriority(Priority.MEDIUM)
          .build()
          .getAsJSONArray(new JSONArrayRequestListener() {
              @Override
              public void onResponse(JSONArray response) {
                  SharedPreferences.Editor sEdit = G.preferences.edit();
                  try {
                      if (response.get(0).toString().equals(300+"")){
                          countDownTimer.cancel();
                          stateUiGetMobile();
                          sEdit.putInt(HelperLogin.STATE_LOGIN,HelperLogin.STATE_GET_MOBILE);
                      }else if (response.get(0).toString().equals(400+"")){
                          stateUiFailedCode();
                          sEdit.putInt(HelperLogin.STATE_LOGIN,HelperLogin.STATE_FAILD_CODE);
                      }else if (response.get(0).toString().equals(200+"")){
                          countDownTimer.cancel();
                          sEdit.putInt(HelperLogin.STATE_LOGIN,HelperLogin.STATE_LOGIN_OK);
                          Cheshmak.sendTag("ActiveLogin");
//                          if(G.preferences.getInt(Constant.STATE_ARRIVE_ACTIVITY_COMPLETE_LOGIN,Constant.STATE_ARRIVE_ACTIVITY_COMPLETE_LOGIN_NO)==Constant.STATE_ARRIVE_ACTIVITY_COMPLETE_LOGIN_NO){
//                              ActivityLogin.this.finish();
//                              ActivityLogin.this.startActivity(new Intent(ActivityLogin.this, ActivityLoginComplete.class));
//                          }else{
                              ActivityLogin.this.finish();
                              ActivityLogin.this.startActivity(new Intent(ActivityLogin.this, MainActivity.class));
//                          }
                      }
                      sEdit.apply();
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
              }

              @Override
              public void onError(ANError anError) {

              }
          });

    }
    private void getMobileAndSendToServer() {
        Log.i("TAG","getMobileAndSendToServer");
        if(isMobile(edtLogin.getText().toString())){
            AndroidNetworking.post(HelperLogin.URL_LOGIN)
              .addBodyParameter("Key", "GetMobile")
              .addBodyParameter("mobile", correctMobile(edtLogin.getText().toString()))
              .addBodyParameter("userId", G.getUserId())
              .setPriority(Priority.MEDIUM)
              .build()
              .getAsJSONArray(new JSONArrayRequestListener() {
                  @Override
                  public void onResponse(JSONArray response) {
                      Log.i("TAG","response LOGIN : "+response);
                      try {
                          if (response.get(0).toString().equals(400+"")){
                              Toast.makeText(ActivityLogin.this,response.get(1).toString(),Toast.LENGTH_SHORT).show();
                          }else if (response.get(0).toString().equals(200+"")) {
                              Log.i("TAG","response: "+response);
                              SharedPreferences.Editor sEdit = G.preferences.edit();
                              sEdit.putInt(HelperLogin.STATE_LOGIN,HelperLogin.STATE_SEND_CODE);
                              sEdit.putString("VerifyCode",response.get(1).toString());
                              sEdit.apply();
                              stateUiSendCode();
                              int millisInFuture =Integer.parseInt(response.get(1).toString());
                              countDownTimer= new CountDownTimer(millisInFuture, 1000) {
                                  public void onTick(long millisUntilFinished) {
                                      String v = String.format("%02d", millisUntilFinished/60000);
                                      int va = (int)( (millisUntilFinished%60000)/1000);
                                      txtTime.setText("" +v+":"+String.format("%02d",va));
                                  }
                                  public void onFinish() {
                                      SharedPreferences.Editor sEdit = G.preferences.edit();
                                      sEdit.putInt(HelperLogin.STATE_LOGIN,HelperLogin.STATE_GET_MOBILE);
                                      sEdit.apply();
                                      stateUiGetMobile();
                                  }
                              }.start();
                          }
                      } catch (JSONException e) {
                          e.printStackTrace();
                      }
                  }

                  @Override
                  public void onError(ANError anError) {
                      Log.i("TAG","anError LOGIN : "+anError);
                  }
              });
        }else{
            Toast.makeText(ActivityLogin.this,"شماره همراه را درست وارد کنید",Toast.LENGTH_SHORT).show();
        }


    }
    private boolean isMobile(String mobile){
        if (!mobile.matches("(\\+98|0)?9\\d{9}")) {
            return  false;
        }
        return true;

    }
    private String correctMobile(String mobile){
        String result=mobile;
        char charAtZero = mobile.charAt(0);
        char charAtOne = mobile.charAt(1);
        char charAtTwo = mobile.charAt(2);
        if(Character.toString(charAtZero).equals("0")){
            return mobile;
        }else if(Character.toString(charAtZero).equals("9")){
            return "0"+mobile;
        }else if(Character.toString(charAtZero).equals("+") && Character.toString(charAtOne).equals("9") && Character.toString(charAtTwo).equals("8")){
            return mobile.replace("+98","0");
        }
        return result;
    }

    @Override
    protected void onPause() {
   if(countDownTimer !=null){
       countDownTimer.cancel();
   }
        super.onPause();
    }

}
