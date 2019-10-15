package answerable;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import custom.dialog.CustomDialog;
import custom.dialog.SweetAlertDialog;
import helper.utility.Constant;
import ir.naeen.yousefi.R;
import main.G;

public class AdapterAnswerabls extends RecyclerView.Adapter<AdapterAnswerabls.ViewHolder> {
    private ArrayList<StructAnswerabls> list;
    public AdapterAnswerabls(ArrayList<StructAnswerabls> list) {this.list = list;}

    @Override
    public int getItemCount() {
      Log.i("TAG", "#" +"size****: " + list.size() );
      return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answerabls_item_recycler,parent, false);
      ViewHolder viewHolder = new ViewHolder(view);
      return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      final StructAnswerabls item = list.get(position);
      holder.txtItemAnswerablsTitle.setText(""+ item.title); ;
      holder.txtItemAnswerablsName.setText(""+ item.name); ;

//      AndroidNetworking.get(item.imageUrl)
//        .setPriority(Priority.MEDIUM)
//        .setBitmapConfig(Bitmap.Config.ARGB_8888)
//        .build()
//        .getAsBitmap(new BitmapRequestListener() {
//          @Override
//          public void onResponse(Bitmap bitmap) {
//            holder.imgItemAnswerabls.setImageBitmap(bitmap);
//          }
//          @Override
//          public void onError(ANError error) {
//            holder.imgItemAnswerabls.setVisibility(View.GONE);
//          }
//        });
      // update ui in layout item in here  example :  holder.txt_name.setText(item.name);
         Log.i("TAG","Answerabl : "+item.id+" :: "+item.showComments);

      holder.layItemAnswerabls.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (item.showComments==1){
            Intent intent= new Intent(G.currentActivity,ActivityOneAnswerable.class);
            intent.putExtra("answerable_id",item.id+"");
            intent.putExtra("answerable_name",item.name+"");
            G.currentActivity.startActivity(intent);
            G.currentActivity.finish();

          }else{
            new CustomDialog(G.currentActivity)
              .setTitleText("پیام خود را برای " +item.name+" بنویسید")
              .setConfirmClickListener(new CustomDialog.OnConfirmCommentClickListener() {
                @Override
                public void onClick(CustomDialog customDialog, String message, boolean isPrivate) {
                  sendCommentToAnswerabls(item.id+"",message,isPrivate);
                }
              })
              .show();
          }
        }
      });


    }

    class ViewHolder extends RecyclerView.ViewHolder {
      private TextView txtItemAnswerablsName;
      private TextView txtItemAnswerablsTitle;
      private ImageView imgItemAnswerabls;
      private ConstraintLayout layItemAnswerabls;

      public ViewHolder(View view) {
        super(view);
        txtItemAnswerablsName =view.findViewById(R.id.txtItemAnswerablsName);
        txtItemAnswerablsTitle =view.findViewById(R.id.txtItemAnswerablsTitle);
        imgItemAnswerabls =view.findViewById(R.id.imgItemAnswerabls);
        imgItemAnswerabls.setVisibility(View.GONE);
        layItemAnswerabls =view.findViewById(R.id.layItemAnswerabls);

      }

    }

  private SweetAlertDialog pDialogProgressSendComment;
  private void sendCommentToAnswerabls(String answerabls_id,String comment,boolean isPrivate) {
      int answerable_private=0;  if (isPrivate)answerable_private=1;
    pDialogProgressSendComment = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    pDialogProgressSendComment.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialogProgressSendComment.setTitleText("درحال ارسال پیام شما");
    pDialogProgressSendComment.setCancelable(false);
    pDialogProgressSendComment.show();
    AndroidNetworking.post(Constant.URL_ANSWERABLS)
      .addBodyParameter("Key","SendCommentToAnswerable")
      .addBodyParameter("answerableComment_text",comment)
      .addBodyParameter("login_userId",G.getUserId())
      .addBodyParameter("answerable_id",answerabls_id)
      .addBodyParameter("answerableComment_private",answerable_private+"")
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray response) {
          pDialogProgressSendComment.dismissWithAnimation();
          try {
            if(response.getBoolean(0)){
              new SweetAlertDialog(G.currentActivity, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("ارسال پیام")
                .setContentText("پیام شما با موفقیت ارسال شد")
                .setConfirmText("باشه")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                  @Override
                  public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();
                  }
                })
                .show();
            };
          } catch (JSONException e) {
            e.printStackTrace();
            dialogeWarnNoSendComment();
          }
        }
        @Override
        public void onError(ANError error) {
          pDialogProgressSendComment.dismissWithAnimation();
          dialogeWarnNoSendComment();
        }
      });

  }
  private void dialogeWarnNoSendComment(){
    new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE)
      .setTitleText("ارسال پیام")
      .setContentText("متاسفانه پیام شما ارسال نشد")
      .setConfirmText("بعدا تلاش خواهم کرد")
      .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
          sDialog.dismissWithAnimation();
        }

      })
      .show();
  }

}


