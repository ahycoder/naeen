package helper.database;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.G;
import custom.dialog.SweetAlertDialog;
import helper.utility.Constant;

public class UpdateDatabase {
  public static void updateDb(onEndUpdateDatabaseListener onEndUpdateDatabaseListener) {
    SweetAlertDialog pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialog.setTitleText("درحال بروزرسانی اطلاعات");
    pDialog.setCancelable(false);
    pDialog.show();
    AndroidNetworking.post(Constant.URL_UPDATE_TABLES)
      .addBodyParameter("Key","UpdateDB")
      .setPriority(Priority.IMMEDIATE)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray jsonArray) {
          Log.i("TAG","onResponse : onSucsess"+jsonArray);
          SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
          try {
            JSONObject jsonobjectFinal= jsonArray.getJSONObject(0);
            JSONArray imageArray =jsonobjectFinal.getJSONArray("banner");
            for (int i = 0; i < imageArray.length(); i++) {

              JSONObject jsonobject= imageArray.getJSONObject(i);
              int banner_id =jsonobject.getInt("banner_id");
              String banner_key =jsonobject.getString("banner_key");
              String banner_url= jsonobject.getString("banner_url");
              int banner_idRelated =jsonobject.getInt("banner_idRelated");
              String queryBanner = "INSERT or replace INTO 'banners' ('banner_id','banner_key','banner_url','banner_idRelated') VALUES (" + banner_id + ", '" + banner_key + "', '" + banner_url + "' , " + banner_idRelated + ")";
              Log.i("TAG","query :::"+queryBanner);
              database.execSQL(queryBanner);
            }
            JSONArray partMatchArray =jsonobjectFinal.getJSONArray("partMatch");
            JSONObject jsonobjectPartMatch= partMatchArray.getJSONObject(0);
            int partMatch_id =jsonobjectPartMatch.getInt("partMatch_id");
            int partMatch_Photography =jsonobjectPartMatch.getInt("partMatch_Photography");
            int partMatch_ReadBook= jsonobjectPartMatch.getInt("partMatch_ReadBook");
            int partMatch_Public= jsonobjectPartMatch.getInt("partMatch_Public");
            String queryPartMatch = "INSERT or replace INTO 'partMatch' ('partMatch_id','partMatch_Photography','partMatch_ReadBook','partMatch_Public') VALUES (" + partMatch_id + ", " + partMatch_Photography + ", " + partMatch_ReadBook + " , " + partMatch_Public + ")";
            database.execSQL(queryPartMatch);
            JSONArray answerablsArray =jsonobjectFinal.getJSONArray("answerable");
            for (int i = 0; i < answerablsArray.length(); i++) {
              JSONObject jsonobject= answerablsArray.getJSONObject(i);
              int answerabl_id =jsonobject.getInt("answerable_id");
              String answerabl_name =jsonobject.getString("answerable_name");
              String answerabl_title= jsonobject.getString("answerable_title");
              String answerabl_imageUrl= jsonobject.getString("answerable_imageUrl");
              int answerable_showComments =jsonobject.getInt("answerable_showComments");
              int answerabl_sort =jsonobject.getInt("answerable_sort");
              String queryAnweAble = "INSERT or replace INTO 'answerabls' ('answerable_id','answerable_name','answerable_title','answerable_imageUrl','answerable_showComments','answerable_sort') VALUES (" + answerabl_id + ", '" + answerabl_name + "', '" + answerabl_title + "' , '" + answerabl_imageUrl + "',"+answerable_showComments +","+answerabl_sort +")";
              database.execSQL(queryAnweAble);
            }
            JSONArray needscat_idArray =jsonobjectFinal.getJSONArray("needscat");
            for (int i = 0; i < needscat_idArray.length(); i++) {
              JSONObject jsonobject = needscat_idArray.getJSONObject(i);
              int needscat_id = jsonobject.getInt("needscat_id");
              String needscat_name = jsonobject.getString("needscat_name");
              int needscat_up = jsonobject.getInt("needscat_up");
              int needscat_isYear = jsonobject.getInt("needscat_isYear");
              int needscat_isPrice = jsonobject.getInt("needscat_isPrice");
              int needscat_isArea = jsonobject.getInt("needscat_isArea");
              int needscat_isRent = jsonobject.getInt("needscat_isRent");
              int needscat_isMileage = jsonobject.getInt("needscat_isMileage");
              int needscat_isRoom = jsonobject.getInt("needscat_isRoom");
              int needscat_isKind = jsonobject.getInt("needscat_isKind");
              String queryNeedscat = "INSERT or replace INTO 'needscat' ('needscat_id','needscat_name','needscat_up','needscat_isYear','needscat_isPrice','needscat_isArea','needscat_isRent','needscat_isMileage','needscat_isRoom','needscat_isKind') VALUES ("
                + needscat_id + ", '" + needscat_name + "', " + needscat_up + "," + needscat_isYear + "," + needscat_isPrice + "," + needscat_isArea + "," + needscat_isRent + "," + needscat_isMileage + "," + needscat_isRoom + "," + needscat_isKind + ")";
              database.execSQL(queryNeedscat);
            }
            JSONArray cityArray = jsonobjectFinal.getJSONArray("city");
            for (int j = 0; j < cityArray.length(); j++) {
              JSONObject jsonobjectCity = cityArray.getJSONObject(j);
              int city_id = jsonobjectCity.getInt("city_id");
              String city_name = jsonobjectCity.getString("city_name");
              int city_sort = jsonobjectCity.getInt("city_sort");
              String queryCity = "INSERT or replace INTO 'city' ('city_id','city_name','city_sort') VALUES (" + city_id + ", '" + city_name + "', " + city_sort + ")";
              database.execSQL(queryCity);
            }

            database.close();
            onEndUpdateDatabaseListener.onSucsess();
            pDialog.dismissWithAnimation();

          } catch (JSONException e) {
            if (database.isOpen())database.close();
            onEndUpdateDatabaseListener.onError();
            pDialog.dismissWithAnimation();
            e.printStackTrace();
          }

        }
        @Override
        public void onError(ANError error) {
          Log.i("TAG","error : error"+error);
          onEndUpdateDatabaseListener.onError();
          pDialog.dismissWithAnimation();
        }
      });
  }
}
