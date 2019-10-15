package helper.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class login {
  private static login instance;
  private Activity activity;
  private EditText edtMobileAndCode;
  private Button btnDoAction;
  private TextView txtSendAginCode;
  private TextView txtTimer;
  private SharedPreferences preferences;
  public login(Activity activity) {
    this.activity=activity;
    preferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE);

  }
  public static login getInstance(Activity activity){
    if(instance == null){
      synchronized (login.class) {
        if(instance == null){
          instance = new login(activity);
        }
      }
    }
    return instance;
  }

  public void init(EditText edtMobileAndCode, Button btnDoAction, TextView txtSendAginCode,TextView txtTimer){
    this.edtMobileAndCode=edtMobileAndCode;
    this.btnDoAction=btnDoAction;
    this.txtSendAginCode=txtSendAginCode;
    this.txtTimer=txtTimer;
  }
}
