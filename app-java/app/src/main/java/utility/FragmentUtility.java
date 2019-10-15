package utility;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import main.G;
import custom.WeatherWidget;
import custom.blurbehind.BlurBehind;
import helper.database.HelperDatabase;
import helper.jalalicalendar.DateConverter;
import helper.jalalicalendar.JalaliDate;
import helper.jalalicalendar.JalaliDateFormatter;
import helper.utility.Constant;
import ir.naeen.yousefi.R;
import needs.AdapterCityBottomSheet;
import needs.StructCity;

public class FragmentUtility extends Fragment {
  private ImageView imgBannerMatchPhotography, imgBannerMatchReadBook, imgBannerMatchPublic;
  private String partMatchPhotography,partMatchReadBook,partMatchPublic;
  private TextView txtWeatherTemp,txtWeatherDay,txtWeatherCity,txtWeatherDate,txtWeatherHumidity,txtWeatherWind,txtCustomBottomSheetTitle;
  private ImageView imgWeather;
  private WeatherWidget WeatherWidget1,WeatherWidget2,WeatherWidget3,WeatherWidget4,WeatherWidget5;
  private LinearLayout layCurrentWeather;
  private BottomSheetBehavior bottomSheetCity;
  private LinearLayout SheetLayoutCity;
  private RecyclerView recyclerBottomSheet;
  private int citySelected=1;
  public FragmentUtility() {
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view=inflater.inflate(R.layout.fragment_utility, container, false);
    BlurBehind.getInstance()
      .withAlpha(80)
      .withFilterColor(Color.parseColor("#0075c0")) //or Color.RED
      .setBackground(G.currentActivity);
    ui(view);
    setBanner();
    getCurrentPartMatch();
    getWeather(citySelected);
    layCurrentWeather.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
           AdapterCityBottomSheet adapterBottomSheet = new AdapterCityBottomSheet(getListCity(), new AdapterCityBottomSheet.onItemSelectedListener() {
             @Override
             public void onItem(StructCity item) {
               bottomSheetCity.setState(BottomSheetBehavior.STATE_COLLAPSED);
               citySelected=item.id;
               getWeather(citySelected);
               txtWeatherCity.setText(""+item.name);
             }
           });
           recyclerBottomSheet.setAdapter(adapterBottomSheet);
           bottomSheetCity.setState(BottomSheetBehavior.STATE_EXPANDED);
         }
       });

    View.OnClickListener listener = new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(G.currentActivity, ActivityMatch.class);
            intent.putExtra("Match_kind", view.getTag().toString());
            String part ="0";
            switch (view.getTag().toString()) {
              case Constant.IMAGE_KEY_MATCH_PHOTOGRAPHY:
                part = partMatchPhotography;
                break;
              case Constant.IMAGE_KEY_MATCH_READBOOK:
                part = partMatchReadBook;
                break;
              case Constant.IMAGE_KEY_MATCH_PUBLIC:
                part = partMatchPublic;
                break;
            }
            intent.putExtra("Match_part", part);
            G.currentActivity.startActivity(intent);
            G.currentActivity.finish();
          }
        };
    imgBannerMatchPhotography.setOnClickListener(listener);
    imgBannerMatchReadBook.setOnClickListener(listener);
    imgBannerMatchPublic.setOnClickListener(listener);



    return view;
  }

 private void setBanner(){
   SQLiteDatabase database =HelperDatabase.getInstance().getWritableDatabase();
   Cursor cursor = database.rawQuery("SELECT * FROM banners", null);
   int index = 0;
   while(cursor.moveToNext()) {
     index++;
     int banner_id = cursor.getInt(cursor.getColumnIndex("banner_id"));
     String banner_key = cursor.getString(cursor.getColumnIndex("banner_key"));
     String banner_url = cursor.getString(cursor.getColumnIndex("banner_url"));
     int banner_idRelated = cursor.getInt(cursor.getColumnIndex("banner_idRelated"));
     switch (banner_key){
       case Constant.IMAGE_KEY_MATCH_PHOTOGRAPHY:
         setImageBanner(imgBannerMatchPhotography,banner_url);
         break;
       case Constant.IMAGE_KEY_MATCH_READBOOK:
         setImageBanner(imgBannerMatchReadBook,banner_url);
         break;
       case Constant.IMAGE_KEY_MATCH_PUBLIC:
         setImageBanner(imgBannerMatchPublic,banner_url);
         break;
     }

     Log.i("TAG", "#" + index + ": " + banner_id + " | " + banner_key + " | "+ banner_url + " | "+ banner_idRelated);
   }
   cursor.close();
   database.close();
 }

 private void setImageBanner(ImageView imageView,String banner_url){
   AndroidNetworking.get(banner_url)
     .setPriority(Priority.MEDIUM)
     .setBitmapConfig(Bitmap.Config.ARGB_8888)
     .build()
     .getAsBitmap(new BitmapRequestListener() {
       @Override
       public void onResponse(Bitmap bitmap) {
         imageView.setImageBitmap(bitmap);
       }
       @Override
       public void onError(ANError error) {
         imageView.setImageResource(R.mipmap.ic_launcher);
       }
     });
 }

  private void getCurrentPartMatch(){
    SQLiteDatabase database =HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM partMatch", null);
    cursor.moveToNext();
    if(cursor.getCount()>0){
      partMatchPhotography = cursor.getInt(cursor.getColumnIndex("partMatch_Photography"))+"";
      partMatchReadBook = cursor.getInt(cursor.getColumnIndex("partMatch_ReadBook"))+"";
      partMatchPublic= cursor.getInt(cursor.getColumnIndex("partMatch_Public"))+"";
    }
      cursor.close();
    database.close();
  }

  private void ui(View view){
    imgBannerMatchPhotography =view.findViewById(R.id.imgBannerMatchPhotography);
    imgBannerMatchReadBook =view.findViewById(R.id.imgBannerMatchReadBook);
    imgBannerMatchPublic =view.findViewById(R.id.imgBannerMatchPublic);
    imgBannerMatchPhotography.setTag(Constant.IMAGE_KEY_MATCH_PHOTOGRAPHY);
    imgBannerMatchReadBook.setTag(Constant.IMAGE_KEY_MATCH_READBOOK);
    imgBannerMatchPublic.setTag(Constant.IMAGE_KEY_MATCH_PUBLIC);
    txtWeatherTemp=view.findViewById(R.id.txtWeatherTemp);
    txtWeatherDay=view.findViewById(R.id.txtWeatherDay);
    txtWeatherCity=view.findViewById(R.id.txtWeatherCity);
    txtWeatherDate=view.findViewById(R.id.txtWeatherDate);
    txtWeatherHumidity=view.findViewById(R.id.txtWeatherHumidity);
    txtWeatherWind=view.findViewById(R.id.txtWeatherWind);
    imgWeather=view.findViewById(R.id.imgWeather);
    WeatherWidget1=view.findViewById(R.id.WeatherWidget1);
    WeatherWidget2=view.findViewById(R.id.WeatherWidget2);
    WeatherWidget3=view.findViewById(R.id.WeatherWidget3);
    WeatherWidget4=view.findViewById(R.id.WeatherWidget4);
    WeatherWidget5=view.findViewById(R.id.WeatherWidget5);
    layCurrentWeather=view.findViewById(R.id.layCurrentWeather);

    SheetLayoutCity = view.findViewById(R.id.bottom_sheet);
    bottomSheetCity = BottomSheetBehavior.from(SheetLayoutCity);
    bottomSheetCity.setState(BottomSheetBehavior.STATE_COLLAPSED);
    recyclerBottomSheet = view.findViewById(R.id.recyclerBottomSheet);
    txtCustomBottomSheetTitle = view.findViewById(R.id.txtCustomBottomSheetTitle);
    txtCustomBottomSheetTitle.setText("شهر خود را انتخاب کنید");
    recyclerBottomSheet.setLayoutManager(new LinearLayoutManager(G.currentActivity));
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

  private void getWeather(int cityId){
    AndroidNetworking.post(Constant.URL_WEATHER)
      .addBodyParameter("Key","weather")
      .addBodyParameter("City",cityId+"")
      .setPriority(Priority.MEDIUM)
      .setMaxStaleCacheControl(365, TimeUnit.SECONDS)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray response) {
          DateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.ENGLISH);
          DateConverter dateConverter = new DateConverter();

          Log.i("TAG","response weather :"+response);

          try {
            JSONObject object6=response.getJSONObject(5);
            txtWeatherHumidity.setText(object6.getInt("weather_humidity")+"");
            txtWeatherWind.setText(object6.getDouble("weather_wind")+"");
            txtWeatherTemp.setText(object6.getString("weather_temp")+"");
              String main = object6.getString("weather_main")+"";
              long sunset = Long.parseLong(object6.get("weather_sunset") + "");
              Calendar calendar = new GregorianCalendar();
              long current = Long.parseLong(object6.get("weather_date").toString());
              JalaliDate jalaliDate2 = new DateConverter().gregorianToJalali(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            txtWeatherDate.setText(jalaliDate2.format(new JalaliDateFormatter("yyyy/mm/dd", JalaliDateFormatter.FORMAT_IN_PERSIAN)));
              txtWeatherDay.setText(jalaliDate2.getDayOfWeek().getStringInPersian() + "");
              calendar.setTime(new Date(current));
              Log.i("TAG","main : "+main);
              if (main.equals("Snow")) {
                imgWeather.setImageResource(R.drawable.weather_snow);
              } else {
                if (main.equals("Rain")) {
                  imgWeather.setImageResource(R.drawable.weather_rain);
                } else {
                  if (main.equals("Clouds")) {
                    if (main.equals("few clouds")) {
                      if (current > sunset) {
                        imgWeather.setImageResource(R.drawable.weather_semi_clear_night);
                      } else {
                        imgWeather.setImageResource(R.drawable.weather_semi_clear_day);
                      }
                    } else {
                      imgWeather.setImageResource(R.drawable.weather_cloudy);
                    }
                  } else {
                    if (main.equals("Clear")) {
                      if (current > sunset) {
                        imgWeather.setImageResource(R.drawable.weather_clear_night);
                      } else {
                        imgWeather.setImageResource(R.drawable.weather_clear_day);
                      }
                    }
                  }
                }
              }
            ArrayList<String> days =forecastDays(jalaliDate2.getDayOfWeek().getStringInPersian() + "");

            JSONObject object1 =response.getJSONObject(0);
            WeatherWidget1.setTempMin(object1.getInt("weather_tempMin"));
            WeatherWidget1.setTempMax(object1.getInt("weather_tempMax"));
            WeatherWidget1.setImage(object1.getString("weather_main"));
            WeatherWidget1.setDay(days.get(0));
            JSONObject object2=response.getJSONObject(1);
            WeatherWidget2.setTempMin(object2.getInt("weather_tempMin"));
            WeatherWidget2.setTempMax(object2.getInt("weather_tempMax"));
            WeatherWidget2.setImage(object2.getString("weather_main"));
            WeatherWidget2.setDay(days.get(1));
            JSONObject object3=response.getJSONObject(2);
            WeatherWidget3.setTempMin(object3.getInt("weather_tempMin"));
            WeatherWidget3.setTempMax(object3.getInt("weather_tempMax"));
            WeatherWidget3.setImage(object3.getString("weather_main"));
            WeatherWidget3.setDay(days.get(2));
            JSONObject object4=response.getJSONObject(3);
            WeatherWidget4.setTempMin(object4.getInt("weather_tempMin"));
            WeatherWidget4.setTempMax(object4.getInt("weather_tempMax"));
            WeatherWidget4.setImage(object4.getString("weather_main"));
            WeatherWidget4.setDay(days.get(3));
            JSONObject object5=response.getJSONObject(4);
//            if (object5.getInt("weather_tempMin")==5&& object5.getInt("weather_tempMax")==5){
//              WeatherWidget5.setTempMin(object5.getInt("weather_tempMin"));
//              WeatherWidget5.setTempMax(object5.getInt("weather_tempMax"));
//              WeatherWidget5.setImage(object5.getString("weather_main"));
//              WeatherWidget5.setDay(days.get(4));
//            }else {
//              WeatherWidget5.setVisibility(View.GONE);
//            }


          } catch (JSONException e) {
            e.printStackTrace();
            Log.i("TAG","JSONException :"+e);
          }
        }

        @Override
        public void onError(ANError anError) {

        }
      });
  }
  private ArrayList<String> forecastDays(String currentDay){
    ArrayList<String> days= new ArrayList<>();
    switch (currentDay){
      case "یکشنبه":
        days.clear(); days.add(0,"دوشنبه");days.add(1,"سه شنبه");days.add(2,"چهارشنبه");days.add(3,"پنج شنبه");days.add(4,"جمعه");;
        break;
      case "دوشنبه":
        days.clear();days.add(0,"سه شنبه");days.add(1,"چهارشنبه");days.add(2,"پنج شنبه");days.add(3,"جمعه");days.add(4,"شنبه");
        break;
      case "سه شنبه":
        days.clear();days.add(0,"چهارشنبه");days.add(1,"پنج شنبه");days.add(2,"جمعه");days.add(3,"شنبه");days.add(4,"یکشنبه");
        break;
      case "چهارشنبه":
        days.clear();days.add(0,"پنج شنبه");days.add(1,"جمعه");days.add(2,"شنبه");days.add(3,"یکشنبه");days.add(4,"دوشنبه");
        break;
      case "پنج شنبه":
        days.clear(); ;days.add(0,"جمعه");days.add(1,"شنبه");days.add(2,"یکشنبه");days.add(3,"دوشنبه");days.add(4,"سه شنبه");
        break;
      case "جمعه":
        days.clear();days.add(0,"شنبه");days.add(1,"یکشنبه");days.add(2,"دوشنبه");days.add(3,"سه شنبه");days.add(4,"چهارشنبه");
        break;
      case "شنبه":
        days.clear(); days.add(0,"یکشنبه");days.add(1,"دوشنبه");days.add(2,"سه شنبه");days.add(3,"چهارشنبه");days.add(4,"پنج شنبه");
        break;
    }
    return days;
  }

}
