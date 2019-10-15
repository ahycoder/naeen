package news;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import helper.database.HelperDatabase;
import helper.utility.Constant;
import ir.naeen.yousefi.R;
import main.G;

public class NewsFragment extends Fragment {
  private RecyclerView recyclerNews;
  private ImageView imgBannerNews;
  public NewsFragment() {

  }

  @Override
  public void onResume() {
    super.onResume();
   Log.i("TAG","NewsFragment: "+getActivity().getSupportFragmentManager());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view =inflater.inflate(R.layout.news_fragment, container, false);
    recyclerNews =view.findViewById(R.id.recyclerNews );
    imgBannerNews =view.findViewById(R.id.imgBannerNews );
    recyclerNews .setLayoutManager(new LinearLayoutManager(G.currentActivity));
    getNews(new setOnResultListener() {
       @Override
       public void onResult(ArrayList<StructNews> newsArrayList) {
         AdapterNews recyclerAdapterNews = new AdapterNews(newsArrayList);
         recyclerNews .setAdapter(recyclerAdapterNews);
       }
     });
    setBanner();
    return view;
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
      if (banner_key.equals("FragmentNews")){
        AndroidNetworking.get(banner_url)
          .setPriority(Priority.MEDIUM)
          .setBitmapConfig(Bitmap.Config.ARGB_8888)
          .build()
          .getAsBitmap(new BitmapRequestListener() {
            @Override
            public void onResponse(Bitmap bitmap) {
              imgBannerNews.setImageBitmap(bitmap);
            }
            @Override
            public void onError(ANError error) {
              //TODO defult news image
              imgBannerNews.setVisibility(View.GONE);
            }
          });
        break;
      }
      Log.i("TAG", "#" + index + ": " + banner_id + " | " + banner_key + " | "+ banner_url + " | "+ banner_idRelated);
    }
    cursor.close();
    database.close();
  }
  public interface setOnResultListener {
    void onResult(ArrayList<StructNews> newsArrayList);
  }
  public static void getNews(setOnResultListener listener) {
    AndroidNetworking.post(Constant.URL_NEWS)
      .addBodyParameter("Key","News")
      .setPriority(Priority.IMMEDIATE)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray jsonArray) {
          ArrayList<StructNews> newsArrayList= new ArrayList<>();
          Log.i("TAG","response : "+jsonArray);
          for (int i = 0; i < jsonArray.length(); i++) {
            try {
              JSONObject jsonobject= jsonArray.getJSONObject(i);
              StructNews structNews= new StructNews();
              structNews.id= jsonobject.getInt("news_id");
              structNews.seen=jsonobject.getInt("news_seen");
              structNews.title=jsonobject.getString("news_title");
              structNews.date=jsonobject.getString("news_date");
              structNews.imagUrl=jsonobject.getString("news_imgUrl");
              newsArrayList.add(structNews);
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
          if(listener !=null){
            listener.onResult(newsArrayList);
          }
        }

        @Override
        public void onError(ANError anError) {

        }
      });
  }

}
