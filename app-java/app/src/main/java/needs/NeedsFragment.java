package needs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import main.G;
import custom.dialog.SweetAlertDialog;
import helper.database.HelperDatabase;
import helper.login.HelperLogin;
import helper.login.onLoginListener;
import helper.utility.Constant;
import ir.naeen.yousefi.R;

public class NeedsFragment extends Fragment {
  private RecyclerView recyclerNeeds,recyclerNeedCat1BottomSheet,recyclerNeedCat2BottomSheet,recyclerBottomSheet;
  private FloatingActionButton fabAddNeeds;
  private ImageView imgBannerNeeds;
  private LinearLayout sheetLayoutCategor, SheetLayoutCity;
  private BottomSheetBehavior bottomSheetCategory,bottomSheetCity;
  private CheckBox chkNeedFast,chkNeedImage;
  private TextView txtNeedCity,txtNeedCat,txtAllNeedCatBottom_sheet,txtCustomBottomSheetTitle;
  private  AdapterNeedCatBottomSheet adapter2NeedCatBottomSheet;
  private int IdItemSelectedCategory =0;
  private int IdItemSelectedCity =0;
  public NeedsFragment() {

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    View view=inflater.inflate(R.layout.needs_fragment, container, false);;
    chkNeedFast =view.findViewById(R.id.chkNeedFast);
    chkNeedImage =view.findViewById(R.id.chkNeedImage);
    txtNeedCity =view.findViewById(R.id.txtNeedCity);
    txtNeedCat =view.findViewById(R.id.txtNeedCat);
    recyclerNeeds =view.findViewById(R.id.recyclerNeeds);
    fabAddNeeds =view.findViewById(R.id.fabAddNeeds);
    imgBannerNeeds =view.findViewById(R.id.imgBannerNeeds);

    txtCustomBottomSheetTitle = view.findViewById(R.id.txtCustomBottomSheetTitle);
    txtCustomBottomSheetTitle.setText("همه شهرها");
    SheetLayoutCity = view.findViewById(R.id.bottom_sheet);
    bottomSheetCity = BottomSheetBehavior.from(SheetLayoutCity);
    bottomSheetCity.setState(BottomSheetBehavior.STATE_COLLAPSED);
    recyclerBottomSheet = view.findViewById(R.id.recyclerBottomSheet);
    recyclerBottomSheet.setLayoutManager(new LinearLayoutManager(G.currentActivity));

    sheetLayoutCategor = view.findViewById(R.id.needCatBottom_sheet);
    bottomSheetCategory = BottomSheetBehavior.from(sheetLayoutCategor);
    bottomSheetCategory.setState(BottomSheetBehavior.STATE_COLLAPSED);
    recyclerNeedCat1BottomSheet = view.findViewById(R.id.recyclerNeedCat1BottomSheet);
    recyclerNeedCat1BottomSheet.setLayoutManager(new LinearLayoutManager(G.currentActivity));
    recyclerNeedCat2BottomSheet = view.findViewById(R.id.recyclerNeedCat2BottomSheet);
    recyclerNeedCat2BottomSheet.setLayoutManager(new LinearLayoutManager(G.currentActivity));
    txtAllNeedCatBottom_sheet = view.findViewById(R.id.txtAllNeedCatBottom_sheet);
    recyclerNeeds .setLayoutManager(new LinearLayoutManager(G.currentActivity));



      changeBehaviorFabWhitBottmSheet();

      txtNeedCat.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          bottomSheetCity.setState(BottomSheetBehavior.STATE_COLLAPSED);

          AdapterNeedCatBottomSheet adapterNeedCatBottomSheet = new AdapterNeedCatBottomSheet(getCategories(0), new AdapterNeedCatBottomSheet.onItemNeedCatSelectedListener() {
            @Override
            public void onItem(StructNeedsCategory item,int position) {

              for (int i=0;i<getCategories(0).size();i++){
                recyclerNeedCat1BottomSheet.getChildAt(i).findViewById(R.id.imgArrowtItemCustomBottomSheet).setVisibility(View.GONE);
              }
                recyclerNeedCat1BottomSheet.getChildAt(position).findViewById(R.id.imgArrowtItemCustomBottomSheet).setVisibility(View.VISIBLE);

              adapter2NeedCatBottomSheet = new AdapterNeedCatBottomSheet(getCategories(item.id), new AdapterNeedCatBottomSheet.onItemNeedCatSelectedListener() {
                @Override
                public void onItem(StructNeedsCategory item,int position) {
                  txtNeedCat.setText(item.name);
                  IdItemSelectedCategory = item.id;
                  bottomSheetCategory.setState(BottomSheetBehavior.STATE_COLLAPSED);
                  startFilter();
                }
              });
              recyclerNeedCat2BottomSheet.setAdapter(adapter2NeedCatBottomSheet);
            }
          });
          recyclerNeedCat1BottomSheet.setAdapter(adapterNeedCatBottomSheet);
          bottomSheetCategory.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
      });
      txtAllNeedCatBottom_sheet.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
             IdItemSelectedCategory =0;
             txtNeedCat.setText("همه آگهی ها");
             bottomSheetCategory.setState(BottomSheetBehavior.STATE_COLLAPSED);
             startFilter();

           }
         });
      txtNeedCity.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
         bottomSheetCategory.setState(BottomSheetBehavior.STATE_COLLAPSED);

         AdapterCityBottomSheet adapterBottomSheet = new AdapterCityBottomSheet(getListCity(), new AdapterCityBottomSheet.onItemSelectedListener() {
            @Override
            public void onItem(StructCity item) {
              txtNeedCity.setText("" + item.name);
              bottomSheetCity.setState(BottomSheetBehavior.STATE_COLLAPSED);
              IdItemSelectedCity = item.id;
              startFilter();

            }
          });
          recyclerBottomSheet.setAdapter(adapterBottomSheet);
          bottomSheetCity.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
      });
    txtCustomBottomSheetTitle.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
         IdItemSelectedCity =0;
         txtNeedCity.setText("همه شهرها");
         bottomSheetCity.setState(BottomSheetBehavior.STATE_COLLAPSED);
         startFilter();
       }
     });
      CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          startFilter();
        }
      };
    chkNeedFast.setOnCheckedChangeListener(changeListener);
    chkNeedImage.setOnCheckedChangeListener(changeListener);

      fabAddNeeds.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          HelperLogin.getInstance().checkLogin(new onLoginListener() {
             @Override
             public void onSucsess() {
               Intent intent = new Intent(G.currentActivity, ActivityAddNeeds.class);
               G.currentActivity.startActivity(intent);
               G.currentActivity.finish();
             }
           });

        }
      });

        getNeedsOfServerNoFilter(new setOnResultListener() {
          @Override
          public void onResult(ArrayList<StructNeeds> needsArrayList) {
            AdapterNeeds recyclerAdapterNeeds = new AdapterNeeds(needsArrayList);
            recyclerNeeds.setAdapter(recyclerAdapterNeeds);
          }
        });
        setBanner();


    return view;
  }

  private void changeBehaviorFabWhitBottmSheet() {
    bottomSheetCategory.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
      @SuppressLint("RestrictedApi")
      @Override
      public void onStateChanged(@NonNull View view, int i) {
        if (bottomSheetCategory.getState()==BottomSheetBehavior.STATE_COLLAPSED){
          fabAddNeeds.setVisibility(View.VISIBLE);
        }else if (bottomSheetCategory.getState()==BottomSheetBehavior.STATE_EXPANDED){
          fabAddNeeds.setVisibility(View.GONE);
        }
      }

      @Override
      public void onSlide(@NonNull View view, float v) {

      }
    });

    bottomSheetCity.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
      @SuppressLint("RestrictedApi")
      @Override
      public void onStateChanged(@NonNull View view, int i) {
        if (bottomSheetCity.getState()==BottomSheetBehavior.STATE_COLLAPSED){
          fabAddNeeds.setVisibility(View.VISIBLE);
        }else if (bottomSheetCity.getState()==BottomSheetBehavior.STATE_EXPANDED){
          fabAddNeeds.setVisibility(View.GONE);
        }
      }

      @Override
      public void onSlide(@NonNull View view, float v) {

      }
    });
  }

  private void  startFilter(){
    getNeedsOfServerWhitFilter(IdItemSelectedCategory, IdItemSelectedCity, chkNeedFast.isChecked(), chkNeedImage.isChecked(), new setOnResultListener() {
      @Override
      public void onResult(ArrayList<StructNeeds> needsArrayList) {
        if (needsArrayList.size()<1){
          Toast.makeText(G.context,"ایتمی وجود ندارد",Toast.LENGTH_SHORT).show();
          needsArrayList.clear();
        }
          AdapterNeeds recyclerAdapterNeeds = new AdapterNeeds(needsArrayList);
          recyclerNeeds.setAdapter(recyclerAdapterNeeds);
      }
    });
  }
  private void setBanner(){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM banners", null);
    int index = 0;
    while(cursor.moveToNext()) {
      index++;
      int banner_id = cursor.getInt(cursor.getColumnIndex("banner_id"));
      String banner_key = cursor.getString(cursor.getColumnIndex("banner_key"));
      String banner_url = cursor.getString(cursor.getColumnIndex("banner_url"));
      int banner_idRelated = cursor.getInt(cursor.getColumnIndex("banner_idRelated"));
      if (banner_key.equals("FragmentNeeds")){
        AndroidNetworking.get(banner_url)
          .setPriority(Priority.HIGH)
          .setBitmapConfig(Bitmap.Config.ARGB_8888)
          .build()
          .getAsBitmap(new BitmapRequestListener() {
            @Override
            public void onResponse(Bitmap bitmap)
            {
              imgBannerNeeds.setImageBitmap(bitmap);
            }
            @Override
            public void onError(ANError error) {
              imgBannerNeeds.setVisibility(View.GONE);
            }
          });
        break;
      }
    }
    cursor.close();
    database.close();
  }

  private ArrayList<StructNeedsCategory> getCategories(int up){
    ArrayList<StructNeedsCategory> needsCategories= new ArrayList<>();
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM needscat", null);
    while(cursor.moveToNext()) {
      StructNeedsCategory structNeedsCategory = new StructNeedsCategory();
      if(cursor.getInt(cursor.getColumnIndex("needscat_up"))==up){
        structNeedsCategory.id = cursor.getInt(cursor.getColumnIndex("needscat_id"));
        structNeedsCategory.name = cursor.getString(cursor.getColumnIndex("needscat_name"));
        needsCategories.add(structNeedsCategory);
      }
    }
    cursor.close();
    database.close();

    return needsCategories;
  }
  private ArrayList<StructCity> getListCity(){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM city", null);
    ArrayList<StructCity>  cities= new ArrayList<>();
    while(cursor.moveToNext()) {
      StructCity  structCity = new StructCity();
      structCity.name= cursor.getString(cursor.getColumnIndex("city_name"));
      structCity.id= cursor.getInt(cursor.getColumnIndex("city_id"));
      cities.add(structCity);
    }
    cursor.close();
    database.close();
    return cities;
  }
  public interface setOnResultListener {
    void onResult(ArrayList<StructNeeds> needsArrayList);
  }
  public  static void getNeedsOfServerWhitFilter(int IdItemSelectedForFilter, int city, boolean isFast, boolean haveImage, setOnResultListener listener ){
    SweetAlertDialog pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialog.setTitleText("درحال بارگزاری اطلاعات");
    pDialog.setCancelable(false);
    pDialog.show();
    String Filter_ID="";
    String Filter_CITY="";
    String Filter_FAST="";
    String Filter_IMAGE="";
    if(IdItemSelectedForFilter ==0){Filter_ID="NO";}else{Filter_ID="YES";}
    if(city==0){Filter_CITY="NO";}else{Filter_CITY=city+"";}
    if(!isFast){Filter_FAST="NO";}else{Filter_FAST="YES";}
    if(!haveImage){Filter_IMAGE="NO";}else{Filter_IMAGE="YES"; }
    AndroidNetworking.get( Constant.URL_NEEDS)
      .addQueryParameter("Key","Needs")
      .addQueryParameter("Filter_ID",Filter_ID)
      .addQueryParameter("needscat_id",IdItemSelectedForFilter+"")
      .addQueryParameter("Filter_CITY",Filter_CITY)
      .addQueryParameter("Filter_FAST",Filter_FAST)
      .addQueryParameter("Filter_IMAGE",Filter_IMAGE)
      .setPriority(Priority.IMMEDIATE)
      .setMaxAgeCacheControl(G.MAX_CASH_NET_MINUTE, TimeUnit.MINUTES)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray jsonArray) {
          pDialog.cancel();
          ArrayList<StructNeeds> needsArrayList = new ArrayList<>();
          for (int i = 0; i < jsonArray.length(); i++) {
            try {
              JSONObject jsonobject = jsonArray.getJSONObject(i);
              StructNeeds structNeeds = new StructNeeds();
              structNeeds.id = jsonobject.getInt("needs_id");
              structNeeds.imageUrl = jsonobject.getString("needs_imageUrl");
              structNeeds.title = jsonobject.getString("needs_title");
              structNeeds.price = jsonobject.getString("needs_price");
              structNeeds.date = jsonobject.getString("needs_date");
              structNeeds.fast = jsonobject.getInt("needs_fast");
              needsArrayList.add(structNeeds);
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
          listener.onResult(needsArrayList);
        }

        @Override
        public void onError(ANError anError) {
          pDialog.cancel();
        }
      });
  }
  public  static void getNeedsOfServerNoFilter(setOnResultListener listener ){
    SweetAlertDialog pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialog.setTitleText("درحال بارگزاری اطلاعات");
    pDialog.setCancelable(false);
    pDialog.show();
    AndroidNetworking.get( Constant.URL_NEEDS)
      .addQueryParameter("Key","NeedsNoFilter")
      .setPriority(Priority.IMMEDIATE)
      .setMaxAgeCacheControl(G.MAX_CASH_NET_MINUTE, TimeUnit.MINUTES)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray jsonArray) {
          pDialog.cancel();
          try {
          ArrayList<StructNeeds> needsArrayList = new ArrayList<>();
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
          listener.onResult(needsArrayList);
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }

        @Override
        public void onError(ANError anError) {
          pDialog.cancel();
        }
      });
  }
}
