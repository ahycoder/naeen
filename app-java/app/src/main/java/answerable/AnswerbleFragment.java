package answerable;

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

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import main.G;
import helper.database.HelperDatabase;
import ir.naeen.yousefi.R;


public class AnswerbleFragment extends Fragment {
  private RecyclerView recyclerAnswerabls;
  private ImageView imgBannerAnswerable;


  public AnswerbleFragment() {

  }



  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view=inflater.inflate(R.layout.answerable_fragment, container, false);
    recyclerAnswerabls=view.findViewById(R.id.recyclerAnswerabls);
    imgBannerAnswerable=view.findViewById(R.id.imgBannerAnswerable);
    recyclerAnswerabls .setLayoutManager(new LinearLayoutManager(G.currentActivity));

    getListAnswerableAndSetAdapter();
    setBanner();
    return  view;
  }
  private void setBanner(){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM banners", null);
    while(cursor.moveToNext()) {
      int banner_id = cursor.getInt(cursor.getColumnIndex("banner_id"));
      String banner_key = cursor.getString(cursor.getColumnIndex("banner_key"));
      String banner_url = cursor.getString(cursor.getColumnIndex("banner_url"));
      int banner_idRelated = cursor.getInt(cursor.getColumnIndex("banner_idRelated"));
      if (banner_key.equals("FragmentAnswerable")){
        AndroidNetworking.get(banner_url)
          .setPriority(Priority.MEDIUM)
          .setBitmapConfig(Bitmap.Config.ARGB_8888)
          .build()
          .getAsBitmap(new BitmapRequestListener() {
            @Override
            public void onResponse(Bitmap bitmap) {
              imgBannerAnswerable.setImageBitmap(bitmap);
            }
            @Override
            public void onError(ANError error) {
              imgBannerAnswerable.setVisibility(View.GONE);
            }
          });
        break;
      }
      Log.i("TAG", "#" +": " + banner_id + " | " + banner_key + " | "+ banner_url + " | "+ banner_idRelated);
    }
    cursor.close();
    database.close();
  }
  private void getListAnswerableAndSetAdapter(){
    ArrayList<StructAnswerabls> answerablsArrayList= new ArrayList<>();
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM answerabls", null);
    while(cursor.moveToNext()) {
      StructAnswerabls structAnswerabls = new StructAnswerabls();
      structAnswerabls.id = cursor.getInt(cursor.getColumnIndex("answerable_id"));
      structAnswerabls.name = cursor.getString(cursor.getColumnIndex("answerable_name"));
      structAnswerabls.title = cursor.getString(cursor.getColumnIndex("answerable_title"));
      structAnswerabls.imageUrl = cursor.getString(cursor.getColumnIndex("answerable_imageUrl"));
      structAnswerabls.showComments = cursor.getInt(cursor.getColumnIndex("answerable_showComments"));
      structAnswerabls.sort = cursor.getInt(cursor.getColumnIndex("answerable_sort"));
      answerablsArrayList.add(structAnswerabls);
    }

    AdapterAnswerabls recyclerAdapterNews = new AdapterAnswerabls(answerablsArrayList);
    recyclerAnswerabls .setAdapter(recyclerAdapterNews);
    cursor.close();
    database.close();
  }
}
