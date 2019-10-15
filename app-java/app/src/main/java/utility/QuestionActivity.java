package utility;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import main.ActivityTop;
import main.G;
import custom.dialog.SweetAlertDialog;
import helper.utility.Constant;
import ir.naeen.yousefi.R;

public class QuestionActivity extends ActivityTop  {
  private String Match_kind,Match_part;
  private Button btnQuestionOption1,btnQuestionOption2,btnQuestionOption3,btnQuestionOption4;
  private TextView txtQuestion,txtQuestionTime;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.question_activity);
    btnQuestionOption1=findViewById(R.id.btnQuestionOption1);
    btnQuestionOption2=findViewById(R.id.btnQuestionOption2);
    btnQuestionOption3=findViewById(R.id.btnQuestionOption3);
    btnQuestionOption4=findViewById(R.id.btnQuestionOption4);
    txtQuestion=findViewById(R.id.txtQuestion);
    txtQuestionTime=findViewById(R.id.txtQuestionTime);
    Bundle bundle = getIntent().getExtras();
    if(bundle !=null) {
      Match_kind = bundle.getString("Match_kind");
      Match_part = bundle.getString("Match_part");
    }
    getQuestions();
  }
  private void getQuestions(){
    Log.i("TAG","Match_kind:"+Match_kind);
    Log.i("TAG","Match_part:"+Match_part);
    AndroidNetworking.post(Constant.URL_MATCH)
      .addBodyParameter("Key","Question")
      .addBodyParameter("match_kind", Match_kind)
      .addBodyParameter("match_part",Match_part)
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray jsonArray) {
          ArrayList<StructQuestion> list= new ArrayList<>();
          Log.i("TAG","response :: "+jsonArray);
          for (int i = 0; i < jsonArray.length(); i++) {
            try {
              JSONObject jsonobject=  jsonArray.getJSONObject(i);
              StructQuestion question = new StructQuestion();
              question.id = jsonobject.getInt("question_id");
              question.text = jsonobject.getString("question_text");
              question.time = jsonobject.getInt("question_time");
              question.option1=jsonobject.getString("question_option1");
              question.option2=jsonobject.getString("question_option2");
              question.option3=jsonobject.getString("question_option3");
              question.option4=jsonobject.getString("question_option4");
              question.result = jsonobject.getString("question_result");
              list.add(question);
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
          new HandelQuestion(list,btnQuestionOption1,btnQuestionOption2,btnQuestionOption3,btnQuestionOption4,txtQuestion,txtQuestionTime).setOnEndQuestionListener(new HandelQuestion.setOnEndQuestionListener() {
            @Override
            public void onEndListener(int correctQuestion, int inCorrectQuestion) {
              takePartMatchQuestion(correctQuestion,inCorrectQuestion);
              Log.i("TAG","END MATHHHHH :: correctQuestion : "+correctQuestion +"   inCorrectQuestion :"+inCorrectQuestion);
            }
          }).run();
        }
        @Override
        public void onError(ANError error) {
          Log.i("TAG","ANError:"+error.getErrorBody());
          Log.i("TAG","ANError:"+error.getErrorDetail());
          Log.i("TAG","ANError:"+error.getErrorCode());
          new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("مسابقه")
            .setConfirmText("دریافت سوالات با خطا مواجه شد")
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
              @Override
              public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                goTOMatchActivity();
              }
            })
            .show();
        }
      });
  }

  private void takePartMatchQuestion(int correctQuestion, int inCorrectQuestion){
    AndroidNetworking.post(Constant.URL_MATCH)
      .addBodyParameter("Key","takePartMatchQuestion")
      .addBodyParameter("match_kind",Match_kind)
      .addBodyParameter("match_part",Match_part)
      .addBodyParameter("correctQuestion",correctQuestion+"")
      .addBodyParameter("inCorrectQuestion",inCorrectQuestion+"")
      .addBodyParameter("login_userId",G.getUserId())
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray jsonArray) {
          new SweetAlertDialog(G.currentActivity, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("مسابقه")
            .setContentText("منتظر نتایج باشید شاید شما برنده باشید")
            .setConfirmText("باشه")
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
              @Override
              public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                goTOMatchActivity();
              }
            })
            .show();
        }
        @Override
        public void onError(ANError error) {

          new SweetAlertDialog(G.currentActivity, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("مسابقه")
            .setContentText("متاسفانه نتایج شما ثبت نشد")
            .setConfirmText("شروع مجدد")
            .setCancelText("لغو")
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
              @Override
              public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                //TODO match again
              }
            })
            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
              @Override
              public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                goTOMatchActivity();
              }
            })
            .show();
        }
      });
  }

  private void goTOMatchActivity(){
    Intent intent= new Intent(G.currentActivity,ActivityMatch.class);
    intent.putExtra("Match_kind",Match_kind);
    Log.i("TAG","goTOMatchActivity :Match_part:"+Match_part);
    intent.putExtra("Match_part",Match_part);
    G.currentActivity.startActivity(intent);
    G.currentActivity.finish();
  }
}
