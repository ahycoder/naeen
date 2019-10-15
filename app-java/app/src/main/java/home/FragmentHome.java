package home;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import custom.CircularImageView;
import custom.dialog.CustomDialog;
import custom.dialog.SweetAlertDialog;
import helper.database.HelperDatabase;
import helper.directory.HelperDirectory;
import helper.login.HelperLogin;
import helper.login.onLoginListener;
import helper.utility.Constant;
import ir.naeen.yousefi.R;
import main.G;
import needs.AdapterNeeds;
import needs.StructNeeds;
import news.AdapterNews;
import news.StructNews;


public class FragmentHome extends Fragment {

  public FragmentHome() {
  }

  private CircularImageView circleImgHomeUser;
  private TextView txtUserName;
  private ImageButton imgHomeAnswerabe, imgHomeNeedsMarked, imgHomeNewsMarked, imgHomeMyNeeds;
  private RecyclerView recyclerHome;
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view=inflater.inflate(R.layout.fragment_home, container, false);
    circleImgHomeUser=view.findViewById(R.id.circleImgHomeUser);
    txtUserName=view.findViewById(R.id.txtUserName);
    imgHomeAnswerabe =view.findViewById(R.id.imgHomeAnswerabe);
    imgHomeNeedsMarked =view.findViewById(R.id.imgHomeNeedsMarked);
    imgHomeNewsMarked =view.findViewById(R.id.imgHomeNewsMarked);
    imgHomeMyNeeds =view.findViewById(R.id.imgHomeMyNeeds);
    recyclerHome=view.findViewById(R.id.recyclerHome);
    recyclerHome .setLayoutManager(new LinearLayoutManager(G.currentActivity));
    txtUserName.setText(G.preferences.getString(Constant.USER_NAME,"نام کاربری"));


    txtUserName.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
       HelperLogin.getInstance().checkLogin(new onLoginListener() {
         @Override
         public void onSucsess() {
           new CustomDialog(G.currentActivity)
             .setTitleText("تغییر نام کاربری")
             .setIsGonePrivateCheckBox(true)
             .setIsChangeName(true)
             .setConfirmClickListener(new CustomDialog.OnConfirmCommentClickListener() {
               @Override
               public void onClick(CustomDialog customDialog, String message, boolean isPrivate) {
                 HelperLogin.getInstance().changeUserName(message);
                 txtUserName.setText(message);
               }
             })
             .show();
         }
       });

     }
   });

    File imgUserFile = new File(G.APP_DIR + "/image_user.png");
    if (imgUserFile.exists()) {
      circleImgHomeUser.setImageBitmap(BitmapFactory.decodeFile(imgUserFile.getAbsolutePath()));
    }else{
      circleImgHomeUser.setImageResource(R.drawable.ic_user_login);
    }
    circleImgHomeUser.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        HelperLogin.getInstance().checkLogin(new onLoginListener() {
           @Override
           public void onSucsess() {
             ImagePicker.create(G.currentActivity)
               .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
               .folderMode(false) // folder mode (false by default)
               .toolbarFolderTitle("عکس کاربری") // folder selection title
               .toolbarImageTitle("یک مورد انتخاب کنید") // image selection title
               .toolbarArrowColor(G.context.getResources().getColor(R.color.colorAccent)) // Toolbar 'up' arrow color
               .includeVideo(false) // Show video on image picker
               .single() // single mode
               .showCamera(true) // show camera or not (true by default)
               .theme(R.style.AppTheme) // must inherit ef_BaseTheme. please refer to lay_image_slider
               .start();
           }
         });

      }
    });


    imgHomeMyNeeds.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        myNeeds();
        imgHomeMyNeeds.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_selected_background));
        imgHomeNeedsMarked.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_background));
        imgHomeNewsMarked.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_background));
        imgHomeAnswerabe.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_background));
      }
    });

    imgHomeNeedsMarked.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        needsMarked();
        imgHomeMyNeeds.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_background));
        imgHomeNeedsMarked.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_selected_background));
        imgHomeNewsMarked.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_background));
        imgHomeAnswerabe.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_background));

      }
    });
    imgHomeNewsMarked.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        newsMarked();
        imgHomeMyNeeds.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_background));
        imgHomeNeedsMarked.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_background));
        imgHomeNewsMarked.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_selected_background));
        imgHomeAnswerabe.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_background));
      }
    });

    imgHomeAnswerabe.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        resultAnswerableToMe();
        imgHomeMyNeeds.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_background));
        imgHomeNeedsMarked.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_background));
        imgHomeNewsMarked.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_background));
        imgHomeAnswerabe.setBackground(G.context.getResources().getDrawable(R.drawable.home_menu_selected_background));
      }
    });


    return view ;
  }

  private void myNeeds() {
    SweetAlertDialog pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialog.setTitleText("درحال بارگزاری اطلاعات");
    pDialog.setCancelable(false);
    pDialog.show();
    AndroidNetworking.get( Constant.URL_NEEDS)
      .addQueryParameter("Key","MyNeeds")
      .addQueryParameter("login_userId",G.getUserId())
      .setPriority(Priority.IMMEDIATE)
      .setMaxAgeCacheControl(G.MAX_CASH_NET_MINUTE, TimeUnit.MINUTES)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray jsonArray) {
          pDialog.cancel();
          ArrayList<StructNeeds> needsArrayList = new ArrayList<>();
          Log.i("TAG","jsonArray***"+jsonArray);
         try {
          if (jsonArray.get(0).equals("No")){
            Log.i("TAG","NoNoNoNoNoNoNoNoNo***");
          }else{
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                StructNeeds structNeeds = new StructNeeds();
                structNeeds.id = jsonobject.getInt("needs_id");
                structNeeds.imageUrl = jsonobject.getString("needs_imageUrl");
                structNeeds.title = jsonobject.getString("needs_title");
                structNeeds.price = jsonobject.getString("needs_price");
                structNeeds.date = jsonobject.getString("needs_date");
                structNeeds.fast = jsonobject.getInt("needs_fast");
                needsArrayList.add(structNeeds);

            }
          }
          } catch (JSONException e) {
            e.printStackTrace();
          }


          AdapterNeeds recyclerAdapterNeeds = new AdapterNeeds(needsArrayList);
          recyclerHome.setAdapter(recyclerAdapterNeeds);
        }

        @Override
        public void onError(ANError anError) {
          pDialog.cancel();
          Log.i("TAG","anError getNeedsOfServerWhitFilter ***"+anError);
        }
      });
  }

  private void needsMarked() {
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM needsmarked", null);
    ArrayList<StructNeeds> needsArrayList = new ArrayList<>();
    while(cursor.moveToNext()) {
      StructNeeds structNeeds = new StructNeeds();
      structNeeds.id = cursor.getInt(cursor.getColumnIndex("needs_id"));
      structNeeds.imageUrl = cursor.getString(cursor.getColumnIndex("needs_imageUrl"));
      structNeeds.title =cursor.getString(cursor.getColumnIndex("needs_title"));
      structNeeds.price = cursor.getString(cursor.getColumnIndex("needs_price"));
      structNeeds.date = cursor.getString(cursor.getColumnIndex("needs_date"));
      structNeeds.fast = 0;
      needsArrayList.add(structNeeds);
    }
    cursor.close();
    database.close();
    AdapterNeeds adapterNeeds = new AdapterNeeds(needsArrayList);
    recyclerHome .setAdapter(adapterNeeds);

  }

  private void newsMarked() {
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM newsmarked", null);
    ArrayList<StructNews> newsArrayList= new ArrayList<>();
    while(cursor.moveToNext()) {
      StructNews structNews= new StructNews();
      structNews.id= cursor.getInt(cursor.getColumnIndex("news_id"));
      structNews.seen = cursor.getInt(cursor.getColumnIndex("news_seen"));
      structNews.title = cursor.getString(cursor.getColumnIndex("news_title"));
      structNews.date = cursor.getString(cursor.getColumnIndex("news_date"));
      structNews.imagUrl = cursor.getString(cursor.getColumnIndex("news_imgUrl"));
      newsArrayList.add(structNews);
    }
    cursor.close();
    database.close();
    AdapterNews recyclerAdapterNews = new AdapterNews(newsArrayList);
    recyclerHome .setAdapter(recyclerAdapterNews);
  }

  private void resultAnswerableToMe() {

  }


  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
      Image image = ImagePicker.getFirstImageOrNull(data);
      new CustomDialog(G.currentActivity)
        //.setImage(image)
        .setTitleText("عوض کردن عکس کاربری")
        .setIsGoneEditText(true)
        .setIsGonePrivateCheckBox(true)
        .setConfirmClickListener(new CustomDialog.OnConfirmCommentClickListener() {
          @Override
          public void onClick(CustomDialog customDialog, String message, boolean isPrivate) {
            HelperDirectory.getInstance().copyFile(image.getPath(),G.APP_DIR + "/image_user.png");
            HelperLogin.getInstance().uploadImageRequest(image);
            circleImgHomeUser.setImageBitmap(BitmapFactory.decodeFile(new File(image.getPath()).getAbsolutePath()));
          }
        })
        .show();
    }
  }
}
