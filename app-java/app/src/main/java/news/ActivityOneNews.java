package news;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import main.ActivityTop;
import main.G;
import custom.justify.JustifiedTextView;
import custom.dialog.CustomDialog;
import custom.dialog.SweetAlertDialog;
import helper.database.HelperDatabase;
import helper.jalalicalendar.JalaliCalendar;
import helper.utility.Constant;
import ir.naeen.yousefi.R;
import main.MainActivity;

public class ActivityOneNews extends ActivityTop {
  private ImageView imgHeaderOneNews,imgMarkOneNews,imgShareOneNews;
  private JustifiedTextView justifiedTxtDescOneNews;
  private ArrayList<StructCommentNews> structCommentNewsArrayList= new ArrayList<>();
  private RecyclerView recyclerCommentNews;
  private FloatingActionButton fabCommentNews;
  private String news_id,news_title,news_date,news_imgUrl;int news_seen;
  private CollapsingToolbarLayout collapsOneNews;
  private SweetAlertDialog pDialog;
  private boolean isMarkedNews;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.one_news_activity);
    imgMarkOneNews=findViewById(R.id.imgMarkOneNews);
    imgShareOneNews=findViewById(R.id.imgShareOneNews);
    imgHeaderOneNews=findViewById(R.id.imgHeaderOneNews);
    recyclerCommentNews =findViewById(R.id.recyclerCommentNews );
    fabCommentNews =findViewById(R.id.fabCommentNews );
    collapsOneNews =findViewById(R.id.collapsOneNews );
    recyclerCommentNews .setLayoutManager(new LinearLayoutManager(this));
    handelJustifyTextView();
    Bundle bundle = getIntent().getExtras();
    if(bundle !=null) {
       news_id = bundle.getString("news_id");
      news_title = bundle.getString("news_title");
      news_date = bundle.getString("news_date");
      news_imgUrl = bundle.getString("news_imgUrl");
      news_seen = bundle.getInt("news_seen");
      pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
      pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
      pDialog.setTitleText("درحال دریافت خبر");
      pDialog.setCancelable(false);
      pDialog.setCancelText("بیخیال");
      pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
          goToMainActity();
        }
      });
      pDialog.show();
      getDataOneNewsOfServer(news_id);

    }
    isMarkedNews=isMarkedNews(Integer.parseInt(news_id));
      if (isMarkedNews){
        imgMarkOneNews.setImageResource(R.drawable.ic_mark);
      }else {
        imgMarkOneNews.setImageResource(R.drawable.ic_un_mark);
      }


    imgMarkOneNews.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isMarkedNews){
          unMarkedNews(Integer.parseInt(news_id));
          isMarkedNews=false;
          imgMarkOneNews.setImageResource(R.drawable.ic_un_mark);
        }else {
          markedNews(Integer.parseInt(news_id));
          isMarkedNews=true;
          imgMarkOneNews.setImageResource(R.drawable.ic_mark);
        }
      }
    });

      fabCommentNews.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          addCommentNews();
        }
      });
    imgShareOneNews.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String shareBody = justifiedTxtDescOneNews.getText();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, collapsOneNews.getTitle().toString());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_news_using)));
      }
    });

  }

  private boolean isMarkedNews(int news_id){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM newsmarked", null);
    boolean result = false;
    while(cursor.moveToNext()) {
      if (cursor.getInt(cursor.getColumnIndex("news_id"))==news_id){
        result = true;
        break;
      }
    }
    cursor.close();
    database.close();
    return result;
  }

  private void markedNews(int news_id){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    ContentValues insertValues = new ContentValues();
    insertValues.put("news_id", news_id);
    insertValues.put("news_seen", news_seen);
    insertValues.put("news_title", news_title);
    insertValues.put("news_date", news_date);
    insertValues.put("news_imgUrl", news_imgUrl);
    database.insert("newsmarked",null,insertValues);
    database.close();
  }
  private void unMarkedNews(int news_id){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    database.delete("newsmarked","news_id = '"+news_id+"'",null);
    database.close();

  }

  private void addCommentNews() {
    new CustomDialog(G.currentActivity)
      .setTitleText("لطفا نظر خود را بنویسید")
      .setIsGonePrivateCheckBox(true)
      .setConfirmClickListener(new CustomDialog.OnConfirmCommentClickListener() {
        @Override
        public void onClick(CustomDialog customDialog, String message, boolean isPrivate) {
          sendCommentOneNewsOfServer(news_id,message);
        }
      })
      .show();
  }

  private SweetAlertDialog pDialogProgressSendComment;
  private void sendCommentOneNewsOfServer(String news_id,String comment) {
    pDialogProgressSendComment = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    pDialogProgressSendComment.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialogProgressSendComment.setTitleText("درحال ارسال نظر شما");
    pDialogProgressSendComment.setCancelable(false);
    pDialogProgressSendComment.show();
   AndroidNetworking.post(Constant.URL_NEWS)
      .addBodyParameter("Key","SendCommentOneNews")
      .addBodyParameter("text",comment)
      .addBodyParameter("user_id",G.getUserId())
      .addBodyParameter("news_id",news_id)
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray response) {
          pDialogProgressSendComment.dismissWithAnimation();
          Log.i("TAG","response news: "+response);
          try {
            if(response.getBoolean(0)){
              new SweetAlertDialog(G.currentActivity, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("نظر شما")
                .setContentText("نظر شما با موفقیت ثبت گردید")
                .setConfirmText("باشه")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                  @Override
                  public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();
                  }
                })
                .show();
            }else{
              dialogeWarnNoSendComment();
            }
          } catch (JSONException e) {
            e.printStackTrace();
            dialogeWarnNoSendComment();
          }
        }
        @Override
        public void onError(ANError anError) {
          pDialogProgressSendComment.dismissWithAnimation();
          dialogeWarnNoSendComment();
        }
      });
  }
  private void dialogeWarnNoSendComment(){
    new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE)
      .setTitleText("نظر شما")
      .setContentText("متاسفانه نظر شما ارسال نشد")
      .setConfirmText("بعدا ارسال میکنم")
      .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
          sDialog.dismissWithAnimation();
        }
      })
      .show();
  }
  private void getCommentsOneNewsOfServer(final String news_id) {
    AndroidNetworking.post(Constant.URL_NEWS)
      .addBodyParameter("Key","ReadCommentsOneNews")
      .addBodyParameter("news_id",news_id)
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray response) {
          pDialog.dismissWithAnimation();
          try {
            for (int i = 0; i < response.length(); i++) {
              JSONObject jsonobject = response.getJSONObject(i);
              StructCommentNews structCommentNews = new StructCommentNews();
              structCommentNews.like = jsonobject.getInt("newsComment_like");
              structCommentNews.userId = jsonobject.getString("login_userId");
              structCommentNews.imagUserUrl=jsonobject.getString("login_imgUserUrl");
              structCommentNews.date = JalaliCalendar.convertToJalaliMyFormat(jsonobject.getString("newsComment_date"));
              structCommentNews.commentId = jsonobject.getInt("newsComment_id");
              structCommentNews.text = jsonobject.getString("newsComment_text");
              structCommentNewsArrayList.add(structCommentNews);
            }


          } catch (JSONException e) {
            e.printStackTrace();
          }
          AdapterCommentsNews recyclerAdapterNews = new AdapterCommentsNews(structCommentNewsArrayList);
          recyclerCommentNews .setAdapter(recyclerAdapterNews);
        }

        @Override
        public void onError(ANError anError) {
          pDialog.dismissWithAnimation();
        }
      });
  }

  private void handelJustifyTextView() {
    justifiedTxtDescOneNews=findViewById(R.id.justifiedTxtDescOneNews);
    justifiedTxtDescOneNews.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
    justifiedTxtDescOneNews.setLineSpacing(15);
    justifiedTxtDescOneNews.setTextColor(R.color.colorAccent);
    justifiedTxtDescOneNews.setAlignment(Paint.Align.RIGHT);
    justifiedTxtDescOneNews.setTypeFace(Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile.ttf"));
  }

  private void getDataOneNewsOfServer(String news_id){
    AndroidNetworking.post(Constant.URL_NEWS)
      .addBodyParameter("Key","NewsOne")
      .addBodyParameter("news_id",news_id)
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray jsonArray) {
          Log.i("TAG","response : "+jsonArray);
          try {
            JSONObject jsonobject= jsonArray.getJSONObject(0);
            int news_seen = jsonobject.getInt("news_seen");
            String news_date = jsonobject.getString("news_date");
            AndroidNetworking.get(jsonobject.getString("news_imgUrl"))
              .setPriority(Priority.MEDIUM)
              .setBitmapConfig(Bitmap.Config.ARGB_8888)
              .setBitmapMaxHeight(G.getheightDispaly()/3)
              .setBitmapMaxWidth(G.getWidthDispaly())
              .build()
              .getAsBitmap(new BitmapRequestListener() {
                @Override
                public void onResponse(Bitmap bitmap) {
                  imgHeaderOneNews.setImageBitmap(bitmap);
                  getCommentsOneNewsOfServer(news_id);
                }
                @Override
                public void onError(ANError error) {
                  imgHeaderOneNews.setVisibility(View.GONE);
                  getCommentsOneNewsOfServer(news_id);
                }
              });
            justifiedTxtDescOneNews.setText(jsonobject.getString("news_desc")+"");
            //justifiedTxtTitleOneNews.setText(jsonobject.getString("news_title")+"");
            collapsOneNews.setTitle(jsonobject.getString("news_title")+"");
          } catch (JSONException e) {
            e.printStackTrace();
            pDialog.dismissWithAnimation();
            dialogeWarnNoGeNews();
          }
        }
        @Override
        public void onError(ANError error) {
          pDialog.dismissWithAnimation();
          dialogeWarnNoGeNews();
        }
      });
  }
  private void dialogeWarnNoGeNews(){
    new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE)
      .setTitleText("دریافت خبر")
      .setContentText("متاسفانه خبر قابل مشاهده نیست")
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
    intent.putExtra(Constant.POSITIO_TAB_LAYOT,3);
    G.currentActivity.startActivity(intent);
    G.currentActivity.finish();
  }

  @Override
  protected void onPause() {
    super.onPause();
    AndroidNetworking.cancelAll();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
      pDialog.dismissWithAnimation();
    goToMainActity();
  }


}
