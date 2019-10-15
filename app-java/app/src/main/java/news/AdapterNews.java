package news;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import helper.database.HelperDatabase;
import ir.naeen.yousefi.R;
import main.G;

public class AdapterNews extends RecyclerView.Adapter<AdapterNews.ViewHolder> {
    private ArrayList<StructNews> list;
  private boolean isMarkedNews;
    public AdapterNews(ArrayList<StructNews> list) {this.list = list;}

    @Override
    public int getItemCount() {return list.size();}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_recycler,parent, false);
      ViewHolder viewHolder = new ViewHolder(view);
      return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      final StructNews item = list.get(position);
      Log.i("TAG","txtItemNewsTitle :"+item.title );
      holder.txtItemNewsTitle.setText(""+ item.title); ;
      holder.txtItemNewsNumbersSeen.setText(""+ item.seen); ;
      holder.txtItemNewsDate.setText(""+ item.date);
//      int widthImage = G.getWidthDispaly()/5;
//      AndroidNetworking.get(item.imagUrl)
//        .setPriority(Priority.MEDIUM)
//        .setBitmapConfig(Bitmap.Config.ARGB_8888)
//        .setBitmapMaxHeight(widthImage)
//        .setBitmapMaxWidth(widthImage)
//        .build()
//        .getAsBitmap(new BitmapRequestListener() {
//          @Override
//          public void onResponse(Bitmap bitmap) {
//            holder.imgItemNewsImageUrl.setImageBitmap(bitmap);
//          }
//          @Override
//          public void onError(ANError error) {
//            //TODO defult news image
//            holder.imgItemNewsImageUrl.setVisibility(View.GONE);
//          }
//        });

      isMarkedNews=isMarkedNews(item.id);
      if (isMarkedNews){
        holder.imgItemNewsBookMark.setImageResource(R.drawable.ic_mark);
      }else {
        holder.imgItemNewsBookMark.setImageResource(R.drawable.ic_un_mark);
      }
      holder.imgItemNewsBookMark.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (isMarkedNews){
              unMarkedNews(item.id);
              isMarkedNews=false;
              holder.imgItemNewsBookMark.setImageResource(R.drawable.ic_un_mark);
            }else {
              markedNews(item.id,item.seen,item.title,item.date,item.imagUrl);
              isMarkedNews=true;
              holder.imgItemNewsBookMark.setImageResource(R.drawable.ic_mark);
            }
          }
        });

      holder.cardItemNews.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(G.currentActivity, ActivityOneNews.class);
            intent.putExtra("news_id", "" + item.id);
            intent.putExtra("news_title", "" + item.title);
            intent.putExtra("news_seen", "" + item.seen);
            intent.putExtra("news_date", "" + item.date);
            intent.putExtra("news_imgUrl", "" + item.imagUrl);
            G.currentActivity.startActivity(intent);
            G.currentActivity.finish();
          }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      private TextView txtItemNewsDate;
      private TextView txtItemNewsTitle;
      private TextView txtItemNewsNumbersSeen;
      private ImageView imgItemNewsImageUrl;
      private ImageView imgItemNewsBookMark;
      private CardView cardItemNews;

      public ViewHolder(View view) {
        super(view);
        txtItemNewsDate =view.findViewById(R.id.txtItemNewsDate);
        txtItemNewsTitle =view.findViewById(R.id.txtItemNewsTitle);
        txtItemNewsNumbersSeen =view.findViewById(R.id.txtItemNewsNumbersSeen);
        imgItemNewsImageUrl =view.findViewById(R.id.imgItemNewsImageUrl);
        imgItemNewsImageUrl.setVisibility(View.GONE);
        imgItemNewsBookMark =view.findViewById(R.id.imgItemNewsBookMark);
        cardItemNews =view.findViewById(R.id.cardItemNews);
      }

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

  private void markedNews(int news_id,int news_seen,String news_title,String news_date,String news_imgUrl){
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

    }
