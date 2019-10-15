package utility;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class HandelQuestion {
  private Button btnOption1,btnOption2,btnOption3,btnOption4;
  private TextView txtQuestion,txtTime;
  private ArrayList<StructQuestion> list;
  private static int numberQuestion=0;
  private static int correctQuestion=0;
  private static int inCorrectQuestion=0;
  private CountDownTimer countDownTimer;
  private setOnEndQuestionListener listener;
  public interface setOnEndQuestionListener{
    void onEndListener(int correctQuestion,int inCorrectQuestion);
  }
  public HandelQuestion(ArrayList<StructQuestion> lis,Button btnQuestionOption1, Button btnQuestionOption2, Button btnQuestionOption3, Button btnQuestionOption4, TextView txtQuestio, TextView txtQuestionTime){
    btnOption1=btnQuestionOption1;
    btnOption2=btnQuestionOption2;
    btnOption3=btnQuestionOption3;
    btnOption4=btnQuestionOption4;
    txtQuestion=txtQuestio;
    txtTime=txtQuestionTime;
    list=lis;
  }
  public HandelQuestion setOnEndQuestionListener (setOnEndQuestionListener listener){
    this.listener=listener;
    return this;

  }
  public void run(){
    if(list.size()==(numberQuestion)){
      listener.onEndListener(correctQuestion,inCorrectQuestion);
       numberQuestion=0;
       correctQuestion=0;
       inCorrectQuestion=0;

    }else{
      StructQuestion question =list.get(numberQuestion);
      countDownTimer =new CountDownTimer(question.time*1000, 1000) {
        public void onTick(long millisUntilFinished) {
          txtTime.setText("" +millisUntilFinished/1000);
        }
        public void onFinish() {
          inCorrectQuestion++;
          numberQuestion++;
          run();
        }
      }.start();
      txtQuestion.setText(question.text);
      btnOption1.setText(question.option1);
      btnOption2.setText(question.option2);
      btnOption3.setText(question.option3);
      btnOption4.setText(question.option4);
      txtTime.setText(question.time+"");
      btnOption1.setTag(question.option1);
      btnOption2.setTag(question.option2);
      btnOption3.setTag(question.option3);
      btnOption4.setTag(question.option4);
      View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View btn) {
          countDownTimer.cancel();
          if(btn.getTag().toString().equals(question.result)){
            correctQuestion++;
          }else{
            inCorrectQuestion++;
          }
          numberQuestion++;
          run();
        }
      };
      btnOption1.setOnClickListener(clickListener);
      btnOption2.setOnClickListener(clickListener);
      btnOption3.setOnClickListener(clickListener);
      btnOption4.setOnClickListener(clickListener);
    }
  }

}
