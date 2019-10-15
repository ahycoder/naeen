package utility;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import main.ActivityTop;
import main.G;
import custom.dialog.SweetAlertDialog;
import helper.database.HelperDatabase;
import helper.image.HelperImage;
import helper.login.HelperLogin;
import helper.login.onLoginListener;
import helper.utility.Constant;
import ir.naeen.yousefi.R;
import main.MainActivity;

public class ActivityMatch extends ActivityTop{
  private ImageView imageActivityMatch;
  private Button btnMatchDownloadBook;
  private FloatingActionButton fabStartMatch;
  private RecyclerView recyclerMatch;
  private ArrayList<StructMatch> matchList = new ArrayList<>();
  private String Match_kind,Match_part;
  private SweetAlertDialog pDialog;
  private SweetAlertDialog pDialogTakePartMatch;
  private TextView txtToolbarMatchActivit;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.match_activity);
    imageActivityMatch=findViewById(R.id.imageActivityMatch);
    fabStartMatch=findViewById(R.id.fabStartMatch);
    recyclerMatch=findViewById(R.id.recyclerMatch);
    btnMatchDownloadBook=findViewById(R.id.btnMatchDownloadBook);
    txtToolbarMatchActivit=findViewById(R.id.txtToolbarMatchActivit);
    recyclerMatch.setLayoutManager(new LinearLayoutManager(this));
    Bundle bundle = getIntent().getExtras();
    if(bundle !=null) {
      Match_kind = bundle.getString("Match_kind");
      Match_part = bundle.getString("Match_part");
      Log.i("TAG","onCreate :Match_part:"+Match_part);
      switch (Match_kind){
        case Constant.IMAGE_KEY_MATCH_PHOTOGRAPHY:
          txtToolbarMatchActivit.setText("مسابقه عکاسی");
          break;
        case Constant.IMAGE_KEY_MATCH_READBOOK:
          btnMatchDownloadBook.setVisibility(View.VISIBLE);
          if(new File(G.APP_DIR+"/book"+Match_part+".pdf").exists()){
            btnMatchDownloadBook.setText("خواندن کتاب");
          }else{
            btnMatchDownloadBook.setText("دانلود کتاب");
          }
          txtToolbarMatchActivit.setText("مسابقه کتابخوانی");
          break;
        case Constant.IMAGE_KEY_MATCH_PUBLIC:
          txtToolbarMatchActivit.setText("مسابقه عمومی");
          break;
      }
    }
    pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialog.setTitleText("درحال دریافت اطلاعات");
    pDialog.setCancelable(false);
    pDialog.setCancelText("لغو");
    pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
      @Override
      public void onClick(SweetAlertDialog sweetAlertDialog) {
        goToMainActity();
      }
    });
    pDialog.show();
    getListAndSetRecycler();
    Log.i("TAG","Match_kind: "+Match_kind);

    fabStartMatch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        HelperLogin.getInstance().checkLogin(new onLoginListener() {
          @Override
          public void onSucsess() {
            takePartMatch();
          }
        });
      }
    });
    btnMatchDownloadBook.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(new File(G.APP_DIR+"/book"+Match_part+".pdf").exists()){
          openBook();
        }else{
          downloadBook();
        }
      }
    });

  }


  private void setImageHeader(){
    HelperDatabase databaseHelper=  HelperDatabase.getInstance();
    SQLiteDatabase database =databaseHelper.getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM banners", null);
    while(cursor.moveToNext()) {
      String image_key = cursor.getString(cursor.getColumnIndex("banner_key"));
      String image_url = cursor.getString(cursor.getColumnIndex("banner_url"));
      if (image_key.equals("ImageActivityMatch")){
        AndroidNetworking.get(image_url)
          .setPriority(Priority.MEDIUM)
          .setBitmapConfig(Bitmap.Config.ARGB_8888)
          .build()
          .getAsBitmap(new BitmapRequestListener() {
            @Override
            public void onResponse(Bitmap bitmap)
            {
              pDialog.dismissWithAnimation();
              imageActivityMatch.setImageBitmap(bitmap);
            }
            @Override
            public void onError(ANError error) {
              pDialog.dismissWithAnimation();
              imageActivityMatch.setVisibility(View.GONE);
            }
          });

      }
    }
    cursor.close();
  }
  private void takePartMatch() {
    pDialogTakePartMatch = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    pDialogTakePartMatch.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialogTakePartMatch.setTitleText("درحال بررسی");
    pDialogTakePartMatch.setCancelable(false);
    pDialogTakePartMatch.setCancelText("لغو");
    pDialogTakePartMatch.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
      @Override
      public void onClick(SweetAlertDialog sweetAlertDialog) {
        goToMainActity();
      }
    });
    pDialogTakePartMatch.show();
    Log.i("TAG","Match_kind:"+Match_kind+"              Match_part:"+Match_part+"             G.getUserId():"+G.getUserId());
    AndroidNetworking.post(Constant.URL_MATCH)
      .addBodyParameter("Key","IsTakePartAlready")
      .addBodyParameter("match_kind", Match_kind)
      .addBodyParameter("match_part", Match_part)
      .addBodyParameter("login_userId",G.getUserId())
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray response) {
          pDialogTakePartMatch.dismissWithAnimation();
//          try {
//            if(response.getInt(0)==400){
//              new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE)
//                .setTitleText("شرکت در مسابقه")
//                .setContentText("متاسفانه شما قبلا در مسابقه شرکت کرده اید")
//                .setConfirmText("منتظر میمونم تا مسابقه بعدی")
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                  @Override
//                  public void onClick(SweetAlertDialog sDialog) {
//                    sDialog.dismissWithAnimation();
//                  }
//
//                })
//                .show();
          //  }else if(response.getInt(0)==200){
              switch (Match_kind){
                case "MatchPhotography":startMatchPhotography();break;
                case "MatchReadBook": startMatchReadBook();break;
                case "MatchPublic": startMatchPublic();break;
           //   }
            }
//          } catch (JSONException e) {
//            e.printStackTrace();
//            dialogeWarnNoGeTakePartMatch();
//          }

        }

        @Override
        public void onError(ANError anError) {

          dialogeWarnNoGeTakePartMatch();
        }
      });
  }
  private void dialogeWarnNoGeTakePartMatch(){
    new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE)
      .setTitleText("دریافت وضعیت مسابقه")
      .setContentText("متاسفانه دریافت وضعیت مسابقه با خطا مواجه شد")
      .setConfirmText("بعدا شرکت خواهم کرد")
      .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
          sDialog.dismissWithAnimation();
          goToMainActity();
        }

      })
      .show();
  }
  private void startMatchPhotography(){
    ImagePicker.create(ActivityMatch.this)
      .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
      .folderMode(false) // folder mode (false by default)
      .toolbarFolderTitle("مسابقه عکاسی") // folder selection title
      .toolbarImageTitle("یک مورد انتخاب کنید") // image selection title
      .toolbarArrowColor(G.context.getResources().getColor(R.color.colorAccent)) // Toolbar 'up' arrow color
      .includeVideo(false) // Show video on image picker
      .single() // single mode
      .showCamera(true) // show camera or not (true by default)
      .theme(R.style.AppTheme) // must inherit ef_BaseTheme. please refer to lay_image_slider
      .start();
  }
  private void startMatchReadBook(){
    Intent intent= new Intent(G.currentActivity, QuestionActivity.class);
    intent.putExtra("Match_kind",Constant.IMAGE_KEY_MATCH_READBOOK);
    intent.putExtra("Match_part",Match_part);
    G.currentActivity.startActivity(intent);
    G.currentActivity.finish();
  }
  private void startMatchPublic(){
    Intent intent= new Intent(G.currentActivity, QuestionActivity.class);
    intent.putExtra("Match_kind",Constant.IMAGE_KEY_MATCH_PUBLIC);
    intent.putExtra("Match_part",Match_part);
    G.currentActivity.startActivity(intent);
    G.currentActivity.finish();
  }
  private  void getListAndSetRecycler(){
    matchList.clear();
    AndroidNetworking.post(Constant.URL_MATCH)
      .addBodyParameter("Key","GetList")
      .addBodyParameter("Match_kind", Match_kind)
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray jsonArray) {
          Log.i("TAG","response final : "+jsonArray);

            try {
              if (jsonArray.get(0).equals("No")){

              }else{
                for (int i = 0; i < jsonArray.length(); i++) {
                  JSONObject jsonobject= jsonArray.getJSONObject(i);
                  StructMatch structMatch = new StructMatch();
                  structMatch.id = jsonobject.getInt("match_id");
                  structMatch.imageUrl = jsonobject.getString("match_imageUrl");
                  structMatch.point = jsonobject.getInt("match_point");
                  structMatch.imgUserUrl = jsonobject.getString("login_imgUserUrl");
                  structMatch.userName = jsonobject.getString("login_userName");
                  matchList.add(structMatch);
                }
              }

              //TODO uncomment setImageHeader and remove pDialog.dismiss
              //setImageHeader();
              pDialog.dismissWithAnimation();

          } catch (JSONException e) {
            e.printStackTrace();
            pDialog.dismissWithAnimation();
              dialogeWarnNoGeInformationMatch();
          }
          AdapterMatch adapterMatch = new AdapterMatch(matchList, Match_kind);
          recyclerMatch.setAdapter(adapterMatch);
        }

        @Override
        public void onError(ANError anError) {
          pDialog.dismissWithAnimation();
          dialogeWarnNoGeInformationMatch();
          Log.i("TAG","response getList anError: "+anError);
        }
      });

  }
  private void dialogeWarnNoGeInformationMatch(){
    new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE)
      .setTitleText("دریافت مسابقه")
      .setContentText("متاسفانه دریافت مسابقه و نتایج با خطا مواجه شد")
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
      Image image = ImagePicker.getFirstImageOrNull(data);
      uploadImageRequest(HelperImage.convertImageToString(image));
    }
  }
  private void uploadImageRequest(String stringImage) {
    AndroidNetworking.post(Constant.URL_IMAGE_REQUEST)
      .addBodyParameter("Key", "MathPhotographyImage")
      .addBodyParameter("image", stringImage)
      .addBodyParameter("name", G.getUserId())
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsString(new StringRequestListener() {
         @Override
         public void onResponse(String pathImage) {
           takePartMatchPhotography(pathImage);
         }
         @Override
         public void onError(ANError anError) {

         }
       });
  }
  private void takePartMatchPhotography(String pathImage){
    AndroidNetworking.post(Constant.URL_MATCH)
      .addBodyParameter("Key","takePartMatchPhotograhy")
      .addBodyParameter("match_kind",Constant.IMAGE_KEY_MATCH_PHOTOGRAPHY)
      .addBodyParameter("match_part",Match_part)
      .addBodyParameter("login_userId",G.getUserId())
      .addBodyParameter("matchPhoto_imageUrl",pathImage)
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray jsonArray) {
          new SweetAlertDialog(G.currentActivity, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("ارسال عکس")
            .setContentText("عکس شما با موفقیت ارسال شد")
            .setConfirmText("باشه")
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
              @Override
              public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();

              }
            })
            .show();
        }

        @Override
        public void onError(ANError anError) {

        }
      });
  }


  private SweetAlertDialog dialogDonloadProgresss;;
  private void downloadBook(){
    dialogDonloadProgresss=  new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    dialogDonloadProgresss.setTitleText("دانلود کتاب");
    dialogDonloadProgresss.setContentText("در حال دریافت کتاب");
    dialogDonloadProgresss.setCancelText("لغو");
    dialogDonloadProgresss.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
      @Override
      public void onClick(SweetAlertDialog sDialog) {
        sDialog.dismissWithAnimation();
        AndroidNetworking.cancel("downloadBook");
      }
    });
    dialogDonloadProgresss.show();
    File file=new File(G.APP_DIR);
    if (!file.exists()) {
      file.mkdirs();
    }
    String url=Constant.URL_DOWNLOAD_BOOK+Match_part+".pdf";
    String fileName="book"+Match_part+".pdf";

    AndroidNetworking.download(url,G.APP_DIR,fileName)
      .setTag("downloadBook")
      .setPriority(Priority.MEDIUM)
      .build()
      .startDownload(new DownloadListener() {
        @Override
        public void onDownloadComplete() {
          btnMatchDownloadBook.setText("خواندن کتاب");
          dialogDonloadProgresss.dismissWithAnimation();
        }
        @Override
        public void onError(ANError error) {
          if (new File(G.APP_DIR+"/"+fileName).exists()) {
            new File(G.APP_DIR+"/"+fileName).delete();
          }
          dialogDonloadProgresss.dismissWithAnimation();
          new SweetAlertDialog(G.currentActivity, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("دانلود کتاب")
            .setContentText("متاسفانه دریافت کتاب با خطا مواجه شد")
            .setConfirmText("دانلود مجدد")
            .setCancelText("لغو")
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
              @Override
              public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
                downloadBook();              }

            })
            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
              @Override
              public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();

              }

            })
            .show();
        }
      });

  }
  private void openBook(){
    File file = new File(G.APP_DIR+"/"+"book"+Match_part+".pdf");
    Intent intent = new Intent(Intent.ACTION_VIEW);
    if (Build.VERSION.SDK_INT < 24) {
    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
    } else {
    intent.setDataAndType(Uri.parse(file.getPath()), "application/pdf");
    }
    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    try {
      ActivityMatch.this.startActivity(intent);
    } catch (ActivityNotFoundException e) {
      new SweetAlertDialog(G.currentActivity, SweetAlertDialog.NORMAL_TYPE)
        .setTitleText("باز کردن کتاب")
        .setContentText("یک اپلیکیشن برای باز کردن فایل pdf نصب کنید")
        .setConfirmText("باشه")
        .show();
    }


  }

  private void goToMainActity(){
    Intent intent =new Intent(G.currentActivity, MainActivity.class);
    intent.putExtra(Constant.POSITIO_TAB_LAYOT,0);
    G.currentActivity.startActivity(intent);
    G.currentActivity.finish();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    goToMainActity();
  }

}
