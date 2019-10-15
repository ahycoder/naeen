package answerable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import custom.dialog.CustomDialog;
import custom.dialog.SweetAlertDialog;
import custom.justify.JustifiedTextView;
import helper.jalalicalendar.JalaliCalendar;
import helper.utility.Constant;
import ir.naeen.yousefi.R;
import main.ActivityTop;
import main.G;
import main.MainActivity;

public class ActivityOneAnswerable extends ActivityTop {
  private ImageView imgHeaderOneAnswerable;
  private JustifiedTextView justifiedTxtDescOneAnswerable;
  private TextView txtTitleOneAnswerable;
  private ArrayList<StructCommentAnswerable> structCommentAnswerableArrayList= new ArrayList<>();
  private RecyclerView recyclerCommentAnswerable;
  private FloatingActionButton fabCommentAnswerable;
  private String answerable_id,answerable_name;
  private CollapsingToolbarLayout collapsOneAnswerable;
  private SweetAlertDialog pDialog;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.one_answerable_activity);
    imgHeaderOneAnswerable=findViewById(R.id.imgHeaderOneAnswerable);
    recyclerCommentAnswerable =findViewById(R.id.recyclerCommentAnswerable );
    fabCommentAnswerable =findViewById(R.id.fabCommentAnswerable );
    collapsOneAnswerable =findViewById(R.id.collapsOneAnswerable );
    recyclerCommentAnswerable .setLayoutManager(new LinearLayoutManager(this));
    txtTitleOneAnswerable=findViewById(R.id.txtTitleOneAnswerable);
    handelJustifyTextView();

    Bundle bundle = getIntent().getExtras();
    if(bundle !=null) {
      answerable_id = bundle.getString("answerable_id");
      answerable_name = bundle.getString("answerable_name");

      pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
      pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
      pDialog.setTitleText("درحال دریافت صفحه مسئول");
      pDialog.setCancelable(false);
      pDialog.setCancelText("بیخیال");
      pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
          goToMainActity();
        }
      });
        pDialog.show();
      getDataOneAnswerableOfServer(answerable_id);

    }

    fabCommentAnswerable.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new CustomDialog(G.currentActivity)
          .setTitleText("پیام خود را برای " +answerable_name+" بنویسید")
          .setConfirmClickListener(new CustomDialog.OnConfirmCommentClickListener() {
            @Override
            public void onClick(CustomDialog customDialog, String message, boolean isPrivate) {
              sendCommentToAnswerabls(answerable_id,message,isPrivate);
            }
          })
          .show();

      }
    });

  }

  private SweetAlertDialog pDialogProgressSendComment;
  private void sendCommentToAnswerabls(String answerabls_id,String comment,boolean isPrivate) {
    Log.i("TAG","sendCommentToAnswerabls:");
    pDialogProgressSendComment = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    pDialogProgressSendComment.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialogProgressSendComment.setTitleText("درحال ارسال پیام شما");
    pDialogProgressSendComment.setCancelable(false);
    pDialogProgressSendComment.show();
    int answerable_private=0;  if (isPrivate)answerable_private=1;
    AndroidNetworking.post( Constant.URL_ANSWERABLS)
      .addBodyParameter("Key","SendCommentToAnswerable")
      .addBodyParameter("answerableComment_text",comment)
      .addBodyParameter("login_userId",G.getUserId())
      .addBodyParameter("answerable_id",answerabls_id)
      .addBodyParameter("answerableComment_private",answerable_private+"")
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray response) {
          pDialogProgressSendComment.dismissWithAnimation();
          try {
            if(response.getBoolean(0)){
              new SweetAlertDialog(G.currentActivity, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("ارسال پیام")
                .setContentText("پیام شما با موفقیت ارسال شد")
                .setConfirmText("باشه")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                  @Override
                  public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();
                  }
                })
                .show();
            };
          } catch (JSONException e) {
            e.printStackTrace();
            dialogeWarnNoSendComment();
          }
        }
        @Override
        public void onError(ANError error) {
          pDialogProgressSendComment.dismissWithAnimation();
          dialogeWarnNoSendComment();
        }
      });
  }

  private void dialogeWarnNoSendComment(){
    new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE)
      .setTitleText("ارسال پیام")
      .setContentText("متاسفانه پیام شما ارسال نشد")
      .setConfirmText("بعدا تلاش خواهم کرد")
      .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
          sDialog.dismissWithAnimation();
        }

      })
      .show();
  }
  private void getCommentsOneAnswerableOfServer(final String answerable_id) {
    AndroidNetworking.post(Constant.URL_ANSWERABLS)
      .addBodyParameter("Key","ReadCommentsOneAnswerable")
      .addBodyParameter("answerable_id",answerable_id)
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsString(new StringRequestListener() {
           @Override
           public void onResponse(String response) {
             pDialog.dismissWithAnimation();
              Log.i("TAG","response comment: "+response);
              try {
                JSONArray jsonArray= new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                  JSONObject jsonobject = jsonArray.getJSONObject(i);
                  StructCommentAnswerable structCommentAnswerable = new StructCommentAnswerable();
                  structCommentAnswerable.like = jsonobject.getInt("answerableComment_like");
                  structCommentAnswerable.userId = jsonobject.getString("login_userId");
                  structCommentAnswerable.imagUserUrl=jsonobject.getString("login_imgUserUrl");
                  structCommentAnswerable.date = JalaliCalendar.convertToJalaliMyFormat(jsonobject.getString("answerableComment_date"));
                  structCommentAnswerable.commentId = jsonobject.getInt("answerableComment_id");
                  structCommentAnswerable.text = jsonobject.getString("answerableComment_text");
                  structCommentAnswerableArrayList.add(structCommentAnswerable);
                }
              } catch (JSONException e) {
                e.printStackTrace();
              }
          AdapterCommentsAnswerable recyclerAdapterAnswerable = new AdapterCommentsAnswerable(structCommentAnswerableArrayList);
          recyclerCommentAnswerable .setAdapter(recyclerAdapterAnswerable);
           }

           @Override
           public void onError(ANError anError) {
             pDialog.dismissWithAnimation();
           }
         });
  }

  private void handelJustifyTextView() {
    justifiedTxtDescOneAnswerable=findViewById(R.id.justifiedTxtDescOneAnswerable);
    justifiedTxtDescOneAnswerable.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
    justifiedTxtDescOneAnswerable.setLineSpacing(12);
    justifiedTxtDescOneAnswerable.setTextColor(R.color.colorAccent);
    justifiedTxtDescOneAnswerable.setAlignment(Paint.Align.RIGHT);
    justifiedTxtDescOneAnswerable.setTypeFace(Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile.ttf"));
  }

  private void getDataOneAnswerableOfServer(String answerable_id){
    Log.i("TAG","answerable_id : "+answerable_id);
    AndroidNetworking.post( Constant.URL_ANSWERABLS)
      .addBodyParameter("Key","AnswerableOne")
      .addBodyParameter("answerable_id",answerable_id)
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsString(new StringRequestListener() {
           @Override
           public void onResponse(String response) {
             Log.i("TAG", "getDataOneAnswerableOfServer : " + response);
              try {
                JSONArray jsonArray= new JSONArray(response);
                JSONObject jsonobject= jsonArray.getJSONObject(0);
                AndroidNetworking.get(jsonobject.getString("answerable_imageUrl"))
                  .setPriority(Priority.MEDIUM)
                  .setBitmapMaxHeight(G.getheightDispaly()/3)
                  .setBitmapMaxWidth(G.getWidthDispaly())
                  .setBitmapConfig(Bitmap.Config.ARGB_8888)
                  .build()
                  .getAsBitmap(new BitmapRequestListener() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                      imgHeaderOneAnswerable.setImageBitmap(bitmap);
                      getCommentsOneAnswerableOfServer(answerable_id);
                    }
                    @Override
                    public void onError(ANError error) {
                      imgHeaderOneAnswerable.setVisibility(View.GONE);
                      getCommentsOneAnswerableOfServer(answerable_id);
                    }
                  });

                txtTitleOneAnswerable.setText(jsonobject.getString("answerable_title")+"");
                justifiedTxtDescOneAnswerable.setText(jsonobject.getString("answerable_desc")+"");
                collapsOneAnswerable.setTitle(jsonobject.getString("answerable_name")+"");
              } catch (JSONException e) {
                e.printStackTrace();
                pDialog.dismissWithAnimation();
                dialogeWarnNoGeAnswerable();
              }
           }

           @Override
           public void onError(ANError anError) {
             pDialog.dismissWithAnimation();
             dialogeWarnNoGeAnswerable();
           }
         });

  }
  private void dialogeWarnNoGeAnswerable(){
    new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE)
      .setTitleText("صفحه مسئول")
      .setContentText("متاسفانه صفحه مسئول قابل مشاهده نیست")
      .setConfirmText("بعدا می بینم")
      .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
          sDialog.dismissWithAnimation();

          goToMainActity();
        }

      })
      .show();
  }
  private void goToMainActity(){
    Intent intent =new Intent(G.currentActivity, MainActivity.class);
    intent.putExtra(Constant.POSITIO_TAB_LAYOT,1);
    G.currentActivity.startActivity(intent);
    G.currentActivity.finish();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    pDialog.dismissWithAnimation();
    goToMainActity();
  }

}
