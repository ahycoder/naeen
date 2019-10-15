package answerable;

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

public class AdapterCommentsAnswerable extends RecyclerView.Adapter<AdapterCommentsAnswerable.ViewHolder> {
    private ArrayList<StructCommentAnswerable> list;
    public AdapterCommentsAnswerable(ArrayList<StructCommentAnswerable> list) {this.list = list;}

    @Override
    public int getItemCount() {return list.size();}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answerable_item_recycler_comments,parent, false);
      ViewHolder viewHolder = new ViewHolder(view);
      return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      final StructCommentAnswerable item = list.get(position);
      Log.i("TAG","comment :"+item.text);
      holder.txtItemCommentAnswerableNumbersLike.setText(""+ item.like); ;
      holder.txtItemCommentAnswerableDate.setText(""+ item.date);
      int widthImage = G.getWidthDispaly()/5;
      AndroidNetworking.get(item.imagUserUrl)
        .setPriority(Priority.MEDIUM)
        .setBitmapMaxHeight(widthImage)
        .setBitmapMaxWidth(widthImage)
        .setBitmapConfig(Bitmap.Config.ARGB_8888)
        .build()
        .getAsBitmap(new BitmapRequestListener() {
          @Override
          public void onResponse(Bitmap bitmap) {
            holder.imgItemCommentAnswerableImageUrl.setImageBitmap(bitmap);
          }
          @Override
          public void onError(ANError error) {
            holder.imgItemCommentAnswerableImageUrl.setVisibility(View.GONE);
          }
        });
      // update ui in layout item in here  example :  holder.txt_name.setText(item.name);

      holder.layItemCommentAnswerableViolation.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
      });
      holder.layItemCommentAnswerableLike.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int currentLike=Integer.parseInt(holder.txtItemCommentAnswerableNumbersLike.getText().toString());
          if(isLikedThisComment(item.commentId)){
            holder.txtItemCommentAnswerableNumbersLike.setText(""+ (currentLike-1));
            removeLikedThisCommentSqlite(item.commentId);
            if(item.like!=0)removeLikedThisCommentServer(item.commentId);
          }else {
            addLikedThisCommentSqlite(item.commentId);
            addLikedThisCommentServer(item.commentId);
            holder.txtItemCommentAnswerableNumbersLike.setText(""+ (currentLike+1));
          }

        }
      });

      holder.txtItemCommentAnswerableTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
      holder.txtItemCommentAnswerableTitle.setTextColor(R.color.colorAccent);
      holder.txtItemCommentAnswerableTitle.setAlignment(Paint.Align.RIGHT);
      holder.txtItemCommentAnswerableTitle.setTypeFace(Typeface.createFromAsset(G.context.getAssets(), "fonts/IRANSansMobile.ttf"));
      holder.txtItemCommentAnswerableTitle.setText(""+ item.text); ;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      private TextView txtItemCommentAnswerableDate;
      private JustifiedTextView txtItemCommentAnswerableTitle;
      private TextView txtItemCommentAnswerableNumbersLike;
      private CircularImageView imgItemCommentAnswerableImageUrl;
      private LinearLayout layItemCommentAnswerableViolation,layItemCommentAnswerableLike;

      public ViewHolder(View view) {
        super(view);
        txtItemCommentAnswerableDate =view.findViewById(R.id.txtItemCommentAnswerableDate);
        txtItemCommentAnswerableTitle =view.findViewById(R.id.txtItemCommentAnswerableTitle);
        txtItemCommentAnswerableNumbersLike =view.findViewById(R.id.txtItemCommentAnswerableNumbersLike);
        imgItemCommentAnswerableImageUrl =view.findViewById(R.id.imgItemCommentAnswerableImageUrl);
        layItemCommentAnswerableViolation =view.findViewById(R.id.layItemCommentAnswerableViolation);
        layItemCommentAnswerableLike =view.findViewById(R.id.layItemCommentAnswerableLike);
      }

    }

    private boolean isLikedThisComment(int commentId){
      SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
      Cursor cursor = database.rawQuery("SELECT * FROM commentanswerablelike", null);
      boolean result = false;
      while(cursor.moveToNext()) {
        if (cursor.getInt(cursor.getColumnIndex("commentanswerable_id"))==commentId){
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
    insertValues.put("commentanswerable_id", commentId);
    database.insert("commentanswerablelike",null,insertValues);
    database.close();
  }
  private void removeLikedThisCommentSqlite(int commentId){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    database.delete("commentanswerablelike","commentanswerable_id = '"+commentId+"'",null);
    database.close();

  }

  private void addLikedThisCommentServer(int commentId){
    requestLikeCommentToServer("AddLikeCommentAnswerable",commentId);
    }
  private void removeLikedThisCommentServer(int commentId){
    requestLikeCommentToServer("RemoveLikeCommentAnswerable",commentId);

  }
private void requestLikeCommentToServer(String key,int commentId){
  AndroidNetworking.post(Constant.URL_ANSWERABLS)
    .addBodyParameter("Key",key)
    .addBodyParameter("answerableComment_id",commentId+"")
    .setPriority(Priority.MEDIUM)
    .build();

}
}
