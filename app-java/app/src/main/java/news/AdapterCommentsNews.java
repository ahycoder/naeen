package news;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
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

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import main.G;
import custom.CircularImageView;
import custom.justify.JustifiedTextView;
import helper.utility.Constant;
import helper.database.HelperDatabase;
import ir.naeen.yousefi.R;

public class AdapterCommentsNews extends RecyclerView.Adapter<AdapterCommentsNews.ViewHolder> {
    private ArrayList<StructCommentNews> list;
    public AdapterCommentsNews(ArrayList<StructCommentNews> list) {this.list = list;}

    @Override
    public int getItemCount() {return list.size();}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_recycler_comments,parent, false);
      ViewHolder viewHolder = new ViewHolder(view);
      return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      final StructCommentNews item = list.get(position);
      Log.i("TAG","comment :"+item.text);
      holder.txtItemCommentNewsNumbersLike.setText(""+ item.like); ;
      holder.txtItemCommentNewsDate.setText(""+ item.date);
      int widthImage = G.getWidthDispaly()/5;
      AndroidNetworking.get(item.imagUserUrl)
        .setPriority(Priority.MEDIUM)
        .setBitmapConfig(Bitmap.Config.ARGB_8888)
        .setBitmapMaxHeight(widthImage)
        .setBitmapMaxWidth(widthImage)
        .build()
        .getAsBitmap(new BitmapRequestListener() {
          @Override
          public void onResponse(Bitmap bitmap) {
            holder.imgItemCommentNewsImageUrl.setImageBitmap(bitmap);
          }
          @Override
          public void onError(ANError error) {
            //TODO defult user image
            holder.imgItemCommentNewsImageUrl.setImageResource(R.mipmap.ic_launcher);
          }
        });
      // update ui in layout item in here  example :  holder.txt_name.setText(item.name);

      holder.layItemCommentNewsViolation.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
      });
      holder.layItemCommentNewsLike.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int currentLike=Integer.parseInt(holder.txtItemCommentNewsNumbersLike.getText().toString());
          if(isLikedThisComment(item.commentId)){
            holder.txtItemCommentNewsNumbersLike.setText(""+ (currentLike-1));
            removeLikedThisCommentSqlite(item.commentId);
            if(item.like!=0)removeLikedThisCommentServer(item.commentId);
          }else {
            addLikedThisCommentSqlite(item.commentId);
            addLikedThisCommentServer(item.commentId);
            holder.txtItemCommentNewsNumbersLike.setText(""+ (currentLike+1));
          }

        }
      });

      holder.txtItemCommentNewsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
      holder.txtItemCommentNewsTitle.setTextColor(R.color.colorAccent);
      holder.txtItemCommentNewsTitle.setAlignment(Paint.Align.RIGHT);
      holder.txtItemCommentNewsTitle.setTypeFace(Typeface.createFromAsset(G.context.getAssets(), "fonts/IRANSansMobile.ttf"));
      holder.txtItemCommentNewsTitle.setText(""+ item.text); ;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      private TextView txtItemCommentNewsDate;
      private JustifiedTextView txtItemCommentNewsTitle;
      private TextView txtItemCommentNewsNumbersLike;
      private ImageView imgItemNewsImageUrl;
      private CircularImageView imgItemCommentNewsImageUrl;
      private LinearLayout layItemCommentNewsViolation,layItemCommentNewsLike;

      public ViewHolder(View view) {
        super(view);
        txtItemCommentNewsDate =view.findViewById(R.id.txtItemCommentNewsDate);
        txtItemCommentNewsTitle =view.findViewById(R.id.txtItemCommentNewsTitle);
        txtItemCommentNewsNumbersLike =view.findViewById(R.id.txtItemCommentNewsNumbersLike);
        imgItemNewsImageUrl =view.findViewById(R.id.imgItemNewsImageUrl);
        imgItemCommentNewsImageUrl =view.findViewById(R.id.imgItemCommentNewsImageUrl);
        layItemCommentNewsViolation =view.findViewById(R.id.layItemCommentNewsViolation);
        layItemCommentNewsLike =view.findViewById(R.id.layItemCommentNewsLike);
      }

    }

    private boolean isLikedThisComment(int commentId){
      SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
      Cursor cursor = database.rawQuery("SELECT * FROM commentnewslike", null);
      boolean result = false;
      while(cursor.moveToNext()) {
        if (cursor.getInt(cursor.getColumnIndex("commentnews_id"))==commentId){
          result = true;
          break;
        }
      }
      cursor.close();
      database.close();
      return result;
    }
  private void addLikedThisCommentSqlite(int commentId){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    ContentValues insertValues = new ContentValues();
    insertValues.put("commentnews_id", commentId);
    database.insert("commentnewslike",null,insertValues);
    database.close();
  }
  private void removeLikedThisCommentSqlite(int commentId){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    database.delete("commentnewslike","commentnews_id = '"+commentId+"'",null);
    database.close();

  }

  private void addLikedThisCommentServer(int commentId){
    requestLikeCommentToServer("AddLikeCommentNews",commentId);
    }
  private void removeLikedThisCommentServer(int commentId){
    requestLikeCommentToServer("RemoveLikeCommentNews",commentId);

  }
private void requestLikeCommentToServer(String key,int commentId){
    AndroidNetworking.post(Constant.URL_NEWS)
    .addBodyParameter("Key",key)
    .addBodyParameter("newsComment_id",commentId+"")
    .setPriority(Priority.MEDIUM)
    .build();
}
}
