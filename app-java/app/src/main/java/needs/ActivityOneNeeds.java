package needs;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.viewpager.widget.ViewPager;
import main.ActivityTop;
import main.G;
import custom.imageSlider.ImagePagerAdapter;
import custom.imageSlider.PageIndicator;
import custom.justify.JustifiedTextView;
import custom.dialog.SweetAlertDialog;
import helper.database.HelperDatabase;
import helper.font.FontContextWrapper;
import helper.utility.Constant;
import ir.naeen.yousefi.R;
import main.MainActivity;

public class ActivityOneNeeds extends ActivityTop {
  private String needs_id;
  private TextView txtOneNeedsTitle,txtOneNeedsCity,txtOneNeedsYear,txtOneNeedsPrice,txtOneNeedsArea,txtOneNeedsRent,
    txtOneNeedsMileage,txtOneNeedsKind,txtOneNeedsRoom,txtOneNeedsDate;;
  private LinearLayout layOneNeedsYear,layOneNeedsPrice,layOneNeedsArea,layOneNeedsRent,
    layOneNeedsMileage,layOneNeedsRoom;
  private JustifiedTextView jTxtOneNeedsDesc;
  private CollapsingToolbarLayout collapsOneNeeds;
   private PageIndicator indicator;
   private ViewPager pagerHeaderOneNeeds;
   private boolean isImageUrl;
   private SweetAlertDialog pDialog;
  private ImageView imgMarkOneNeeds,imgShareOneNeeds;
  private boolean isMarkedNeed;
  private String needs_price,needs_title,needs_date,needs_imageUrl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getIntent().getExtras();
    if(bundle !=null) {
      needs_id = bundle.getString("needs_id");
      needs_price = bundle.getString("needs_price");
      needs_title = bundle.getString("needs_title");
      needs_date = bundle.getString("needs_date");
      isImageUrl = bundle.getBoolean("imageUrl");
      if (isImageUrl) needs_imageUrl = bundle.getString("needs_imageUrl");

      pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
      pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
      pDialog.setTitleText("درحال دریافت آگهی");
      pDialog.setCancelable(false);
      pDialog.setCancelText("بیخیال");
      pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
         @Override
         public void onClick(SweetAlertDialog sweetAlertDialog) {
           goToMainActity();
         }
       });
      pDialog.show();
      getDataOneNeedsOfServer(needs_id);
    }
    ui();


    isMarkedNeed =isMarkedNeed(Integer.parseInt(needs_id));
    if (isMarkedNeed){
      imgMarkOneNeeds.setImageResource(R.drawable.ic_mark);
    }else {
      imgMarkOneNeeds.setImageResource(R.drawable.ic_un_mark);
    }


    imgMarkOneNeeds.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isMarkedNeed){
          unMarkedNeed(Integer.parseInt(needs_id));
          isMarkedNeed =false;
          imgMarkOneNeeds.setImageResource(R.drawable.ic_un_mark);
        }else {
          markedNeed(Integer.parseInt(needs_id));
          isMarkedNeed=true;
          imgMarkOneNeeds.setImageResource(R.drawable.ic_mark);
        }
      }
    });


  }
  private boolean isMarkedNeed(int needs_id){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM needsmarked", null);
    boolean result = false;
    while(cursor.moveToNext()) {
      if (cursor.getInt(cursor.getColumnIndex("needs_id"))==needs_id){
        result = true;
        break;
      }
    }
    cursor.close();
    database.close();
    return result;
  }

  private void markedNeed(int needs_id){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    ContentValues insertValues = new ContentValues();
    insertValues.put("needs_id", needs_id);
    insertValues.put("needs_price", needs_price);
    insertValues.put("needs_title", needs_title);
    insertValues.put("needs_date", needs_date);
    insertValues.put("needs_imageUrl",needs_imageUrl);
    database.insert("needsmarked",null,insertValues);
    database.close();
  }
  private void unMarkedNeed(int needs_id){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    database.delete("needsmarked","needs_id = '"+needs_id+"'",null);
    database.close();

  }

  private void getDataOneNeedsOfServer(String needs_id){
    Log.i("TAG","getDataOneNeedsOfServer : ");
    AndroidNetworking.post(Constant.URL_NEEDS)
      .addBodyParameter("Key","NeedsOne")
      .addBodyParameter("needs_id",needs_id)
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray jsonArray) {
          Log.i("TAG","response : "+jsonArray);
          try {
            JSONObject jsonobject= jsonArray.getJSONObject(0);
            String login_userId = jsonobject.getString("login_userId");
            String needscat_id = jsonobject.getString("needscat_id");
            String needs_title = jsonobject.getString("needs_title");
            if (isImageUrl){
              txtOneNeedsTitle.setVisibility(View.GONE);
              collapsOneNeeds.setTitle(needs_title);
              collapsOneNeeds.setCollapsedTitleTextColor(G.context.getResources().getColor(R.color.colorToobarText));
              collapsOneNeeds.setExpandedTitleColor(G.context.getResources().getColor(R.color.colorToobarText));
            }else{
              txtOneNeedsTitle.setVisibility(View.VISIBLE);
            txtOneNeedsTitle.setText(needs_title);
            }
            String needs_desc = jsonobject.getString("needs_desc");jTxtOneNeedsDesc.setText(needs_desc);


            String needs_city = jsonobject.getString("needs_city");
            txtOneNeedsCity.setText(getNameCity(needs_city));

            String needs_price = jsonobject.getString("needs_price");
            if (needs_price.equals("0")){
              layOneNeedsPrice.setVisibility(View.GONE);
            }else {
              txtOneNeedsPrice.setText(needs_price);
            }


            String needs_area = jsonobject.getString("needs_area");
            if (needs_area.equals("0")){
              layOneNeedsArea.setVisibility(View.GONE);
            }else {
              txtOneNeedsArea.setText(needs_area);
            }


            String needs_year = jsonobject.getString("needs_year");
            if (needs_year.equals("0")){
              layOneNeedsYear.setVisibility(View.GONE);
            }else {
              txtOneNeedsYear.setText(needs_year);
            }


            String needs_rent = jsonobject.getString("needs_rent");
            if (needs_rent.equals("0")){
              layOneNeedsRent.setVisibility(View.GONE);
            }else {
              txtOneNeedsRent.setText(needs_rent);
            }

            String needs_mileage = jsonobject.getString("needs_mileage");
            if (needs_mileage.equals("0")){
             layOneNeedsMileage.setVisibility(View.GONE);
            }else {
              txtOneNeedsMileage.setText(needs_mileage);
            }

            String needs_room = jsonobject.getString("needs_room");
            if (needs_room.equals("0")){
              layOneNeedsRoom.setVisibility(View.GONE);
            }else {
              txtOneNeedsRoom.setText(needs_room);
            }


            String needs_imageUrl = jsonobject.getString("needs_imageUrl");
            String needs_fast = jsonobject.getString("needs_fast");
            String needs_kind = jsonobject.getString("needs_kind");txtOneNeedsKind.setText(needs_kind);
            String needs_date = jsonobject.getString("needs_date");txtOneNeedsDate.setText(needs_date);

            if (isImageUrl){
              JSONArray jsonArrayNeedsImageUrl= new JSONArray(needs_imageUrl);
              ArrayList<String> imageUrls = new ArrayList<String>();
              for (int i = 0; i <jsonArrayNeedsImageUrl.length(); i++) {
                imageUrls.add(jsonArrayNeedsImageUrl.get(i).toString());
                Log.i("LOG", "PercjsonArray1ent: " + jsonArrayNeedsImageUrl.get(i).toString());
              }
              indicator.setIndicatorsCount(imageUrls.size());
              pagerHeaderOneNeeds.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int index) {}
                @Override
                public void onPageScrolled(int startIndex, float percent, int pixel) {
                  indicator.setPercent(percent);
                  Log.i("LOG", "Percent: " + startIndex + ", " + percent);
                  indicator.setCurrentPage(startIndex);
                }
                @Override
                public void onPageScrollStateChanged(int arg0) {

                }
              });
              ImagePagerAdapter adapter = new ImagePagerAdapter(imageUrls,pDialog);
              pagerHeaderOneNeeds.setAdapter(adapter);
            }else {
              pDialog.dismissWithAnimation();
            }

          } catch (JSONException e) {
            e.printStackTrace();
            pDialog.dismissWithAnimation();
            dialogeWarnNoGeNeeds();
          }
        }
        @Override
        public void onError(ANError error) {
          Log.i("TAG","error : "+error);

          pDialog.dismissWithAnimation();
          dialogeWarnNoGeNeeds();
        }
      });

  }
  private void dialogeWarnNoGeNeeds(){
    new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE)
      .setTitleText("دریافت آگهی")
      .setContentText("متاسفانه آگهی قابل مشاهده نیست")
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
  private void ui(){
    if (isImageUrl){
      setContentView(R.layout.needs_one_whit_image_activity);
      indicator =findViewById(R.id.indicator);
      pagerHeaderOneNeeds =findViewById(R.id.pagerHeaderOneNeeds);
      collapsOneNeeds=findViewById(R.id.collapsOneNeeds);
    }else {
      setContentView(R.layout.needs_one_whit_no_image_activity);
    }
    imgMarkOneNeeds=findViewById(R.id.imgMarkOneNeeds);
    imgShareOneNeeds=findViewById(R.id.imgShareOneNeeds);
    txtOneNeedsTitle=findViewById(R.id.txtOneNeedsTitle);
    txtOneNeedsTitle.setTextColor(G.context.getResources().getColor(R.color.colorToobarText));
    jTxtOneNeedsDesc=findViewById(R.id.jTxtOneNeedsDesc);
    jTxtOneNeedsDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
    jTxtOneNeedsDesc.setTextColor(R.color.sd_error_stroke_color);
    jTxtOneNeedsDesc.setAlignment(Paint.Align.RIGHT);
    txtOneNeedsCity=findViewById(R.id.txtOneNeedsCity);
    txtOneNeedsYear=findViewById(R.id.txtOneNeedsYear);
    txtOneNeedsPrice=findViewById(R.id.txtOneNeedsPrice);
    txtOneNeedsArea=findViewById(R.id.txtOneNeedsArea);
    txtOneNeedsRent=findViewById(R.id.txtOneNeedsRent);
    txtOneNeedsMileage=findViewById(R.id.txtOneNeedsMileage);
    txtOneNeedsKind=findViewById(R.id.txtOneNeedsKind);
    txtOneNeedsRoom=findViewById(R.id.txtOneNeedsRoom);
    txtOneNeedsDate=findViewById(R.id.txtOneNeedsDate);


    layOneNeedsYear=findViewById(R.id.layOneNeedsYear);
    layOneNeedsPrice=findViewById(R.id.layOneNeedsPrice);
    layOneNeedsArea=findViewById(R.id.layOneNeedsArea);
    layOneNeedsRent=findViewById(R.id.layOneNeedsRent);
    layOneNeedsMileage=findViewById(R.id.layOneNeedsMileage);
    layOneNeedsRoom=findViewById(R.id.layOneNeedsRoom);



  }
  private String getNameCity(String id) {
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM city", null);
    String name="";
    while(cursor.moveToNext()) {
        int idCurrent = cursor.getInt(cursor.getColumnIndex("city_id"));
      if ((idCurrent+"").equals(id)){
        name = cursor.getString(cursor.getColumnIndex("city_name"));
      }
    }
    cursor.close();
    database.close();
    return name;
  }
  private void goToMainActity(){
    Intent intent =new Intent(G.currentActivity, MainActivity.class);
    intent.putExtra(Constant.POSITIO_TAB_LAYOT,2);
    G.currentActivity.startActivity(intent);
    G.currentActivity.finish();
  }
  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(FontContextWrapper.wrap(newBase));
  }
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    goToMainActity();
  }
}
