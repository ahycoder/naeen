package utility;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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
import com.androidnetworking.interfaces.StringRequestListener;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import main.G;
import custom.CircularImageView;
import helper.database.HelperDatabase;
import helper.utility.Constant;
import ir.naeen.yousefi.R;

public class AdapterMatch extends RecyclerView.Adapter<AdapterMatch.ViewHolder> {
    private ArrayList<StructMatch> list;
    private String matchKind;
    private long doubleClickLastTime = 0L;
    public AdapterMatch(ArrayList<StructMatch> list, String matchKind) {
      this.list = list;
      this.matchKind = matchKind;
    }

    @Override
    public int getItemCount() {
      return list.size();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_photography_item,parent, false);
      ViewHolder viewHolder = new ViewHolder(view);
      return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      final StructMatch item = list.get(position);
//      holder.imgMatchUser.setCornerRadiiDP(10,10,10,10);580*380
      holder.txtItemMatchPoint.setText(""+ item.point);
      if (item.userName.equals("null")){
      holder.txtMatchUserName.setVisibility(View.INVISIBLE);
      }
      holder.txtMatchUserName.setText(" "+item.userName);
      AndroidNetworking.get(item.imgUserUrl)
        .setPriority(Priority.MEDIUM)
        .setBitmapConfig(Bitmap.Config.ARGB_8888)
        .build()
        .getAsBitmap(new BitmapRequestListener() {
          @Override
          public void onResponse(Bitmap bitmap) {
            holder.imgMatchUser.setImageBitmap(bitmap);
          }
          @Override
          public void onError(ANError error) {
            //TODO defult user image
            holder.imgMatchUser.setImageResource(R.mipmap.ic_launcher);
          }
        });

      if (matchKind.equals(Constant.IMAGE_KEY_MATCH_PHOTOGRAPHY)) {
        holder.imgMatchPhotograph.setVisibility(View.VISIBLE);
        AndroidNetworking.get(item.imageUrl)
          .setPriority(Priority.MEDIUM)
          .setBitmapConfig(Bitmap.Config.ARGB_8888)
          .build()
          .getAsBitmap(new BitmapRequestListener() {
            @Override
            public void onResponse(Bitmap bitmap) {
              holder.imgMatchPhotograph.setImageBitmap(bitmap);
            }

            @Override
            public void onError(ANError error) {
              holder.layMatchPhotographyItem.setVisibility(View.GONE);
            }
          });
        if (isLikedThisMatchPhotographyImage(item.id)) {
          holder.imgMatchPhotographLike.setImageDrawable(G.context.getResources().getDrawable(R.drawable.ic_like));
        }else{
          holder.imgMatchPhotographLike.setImageDrawable(G.context.getResources().getDrawable(R.drawable.ic_dislike));
        }
        holder.imgMatchPhotographLike.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            int currentLike = Integer.parseInt(holder.txtItemMatchPoint.getText().toString());
            if (isLikedThisMatchPhotographyImage(item.id)) {
              removeLikedThisMatchPhotographyImageSqlite(item.id);
              holder.imgMatchPhotographLike.setImageDrawable(G.context.getResources().getDrawable(R.drawable.ic_dislike));
              if (item.point != 0) {
                removeLikedThisMatchPhotographyImageServer(item.id);
                holder.txtItemMatchPoint.setText("" + (currentLike - 1));
              } else {
                holder.txtItemMatchPoint.setText("" + 0);
              }
            } else {
              addLikedThisMatchPhotographyImageSqlite(item.id);
              holder.imgMatchPhotographLike.setImageDrawable(G.context.getResources().getDrawable(R.drawable.ic_like));
              addLikedThisMatchPhotographyImageServer(item.id);
              holder.txtItemMatchPoint.setText("" + (currentLike + 1));
            }
          }
        });

      }

     holder.imgMatchPhotograph.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(System.currentTimeMillis() - doubleClickLastTime < 300){
          doubleClickLastTime = 0;
          if (!isLikedThisMatchPhotographyImage(item.id)) {
            addLikedThisMatchPhotographyImageSqlite(item.id);
            addLikedThisMatchPhotographyImageServer(item.id);
            int currentLike = Integer.parseInt(holder.txtItemMatchPoint.getText().toString());
            holder.txtItemMatchPoint.setText("" + (currentLike + 1));
            holder.imgMatchPhotographLike.setImageDrawable(G.context.getResources().getDrawable(R.drawable.ic_like));
          }
        }else{
          doubleClickLastTime = System.currentTimeMillis();
        }
      }
    });

    }
    class ViewHolder extends RecyclerView.ViewHolder {
      private TextView txtMatchUserName;
      private TextView txtItemMatchPoint;
      private CircularImageView imgMatchUser;
      private ImageView imgMatchPhotograph;
      private ImageView imgMatchPhotographLike;
      private LinearLayout layMatchPhotographyItem;
      public ViewHolder(View view) {
        super(view);
        txtMatchUserName =view.findViewById(R.id.txtMatchUserName);
        txtItemMatchPoint =view.findViewById(R.id.txtItemMatchPoint);
        imgMatchUser =view.findViewById(R.id.imgMatchUser);
        imgMatchPhotograph =view.findViewById(R.id.imgMatchPhotograph);
        imgMatchPhotographLike =view.findViewById(R.id.imgMatchPhotographLike);
        layMatchPhotographyItem =view.findViewById(R.id.layMatchPhotographyItem);
      }
    }
  private boolean isLikedThisMatchPhotographyImage(int match_id){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM matchphotographylike", null);
    boolean result = false;
    while(cursor.moveToNext()) {
      if (cursor.getInt(cursor.getColumnIndex("match_id"))==match_id){
        result = true;
        break;
      }
    }
    cursor.close();
    database.close();
    Log.i("TAG","result::"+result);
    return result;
  }

  private void addLikedThisMatchPhotographyImageSqlite(int match_id){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    ContentValues insertValues = new ContentValues();
    insertValues.put("match_id", match_id);
    database.insert("matchphotographylike",null,insertValues);
    database.close();
  }
  private void removeLikedThisMatchPhotographyImageSqlite(int match_id){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    database.delete("matchphotographylike","match_id = '"+match_id+"'",null);
    database.close();

  }

  private void addLikedThisMatchPhotographyImageServer(int commentId){
    requestLikeMatchPhotographyImageToServer("AddLikeMatchPhotographyImage",commentId);
  }
  private void removeLikedThisMatchPhotographyImageServer(int commentId){
    requestLikeMatchPhotographyImageToServer("RemoveLikeMatchPhotographyImage",commentId);

  }
  private void requestLikeMatchPhotographyImageToServer(String key,int match_id){
      AndroidNetworking.post(Constant.URL_MATCH)
      .addBodyParameter("Key",key)
      .addBodyParameter("match_id",match_id+"")
      .setPriority(Priority.MEDIUM)
      .build()
        .getAsString(new StringRequestListener() {
          @Override
          public void onResponse(String response) {
            Log.i("TAG","response :"+response);
          }

          @Override
          public void onError(ANError anError) {
            Log.i("TAG","onError :"+anError.getErrorDetail());
          }
        });
  }
}
