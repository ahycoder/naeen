package custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.model.Image;

import java.io.File;

import custom.CircularImageView;
import ir.naeen.yousefi.R;
import main.G;

public class CustomDialog extends Dialog{

  private TextView txtTitleDialogSendComment;
  private EditText edtDialogSendComment;
  private CheckBox chkPrivateDialpgSendComment;
  private Button cancelDialogSendComment;
  private Button confirmDialogSendComment;
  private OnConfirmCommentClickListener mConfirmClickListener;
  private String mTitleText="لطفا پیام خود را بنویسید";
  private boolean isGonePrivateCheckBox=false;
  private boolean isGoneEditText=false;
  private boolean isChangeName=false;
  private CircularImageView circleImgCustomDialog ;
  private Image image ;

  public CustomDialog(Context context) {
    super(context, R.style.comment_dialog);
    setCancelable(false);
    setCanceledOnTouchOutside(false);
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_send_comment);
    circleImgCustomDialog=findViewById(R.id.circleImgCustomDialog);
    txtTitleDialogSendComment=findViewById(R.id.txtTitleDialogSendComment);
    edtDialogSendComment=findViewById(R.id.edtDialogSendComment);
    chkPrivateDialpgSendComment=findViewById(R.id.chkPrivateDialpgSendComment);
    cancelDialogSendComment=findViewById(R.id.cancelDialogSendComment);
    confirmDialogSendComment=findViewById(R.id.confirmDialogSendComment);
    setTitleText(mTitleText);
    setIsGonePrivateCheckBox(isGonePrivateCheckBox);
    setIsGoneEditText(isGoneEditText);
    confirmDialogSendComment.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.i("TAG","confirmDialogSendComment");
        if (!isGoneEditText){
          if (!isChangeName){
            if (edtDialogSendComment.getText().toString().length()<6 || edtDialogSendComment.getText().toString().length()>201){
              Toast.makeText(G.currentActivity,"متن پیام حداقل 6 و حداکثر200 حرف باید باشد",Toast.LENGTH_SHORT).show();
            }else {
              if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(CustomDialog.this,edtDialogSendComment.getText().toString(),chkPrivateDialpgSendComment.isChecked());
              }
              dismiss();
            }
          }else {
            if (edtDialogSendComment.getText().toString().length()<3 || edtDialogSendComment.getText().toString().length()>20){
              Toast.makeText(G.currentActivity,"متن پیام حداقل 3 و حداکثر20 حرف باید باشد",Toast.LENGTH_SHORT).show();
            }else {
              if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(CustomDialog.this,edtDialogSendComment.getText().toString(),chkPrivateDialpgSendComment.isChecked());
              }
              dismiss();
            }
          }

        }else {
          if (mConfirmClickListener != null) {
            mConfirmClickListener.onClick(CustomDialog.this,edtDialogSendComment.getText().toString(),chkPrivateDialpgSendComment.isChecked());
          }
          dismiss();
        }

      }
    });
    cancelDialogSendComment.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
  }

  public CustomDialog setImage(Image image){
    this.image=image;
    if (image == null){
      Log.i("TAG","image");
    }
    if (circleImgCustomDialog == null){
      Log.i("TAG","circleImgCustomDialog");
    }
    if (this.image != null ) {
      circleImgCustomDialog.setVisibility(View.VISIBLE);
      circleImgCustomDialog.setImageBitmap(BitmapFactory.decodeFile(new File(this.image.getPath()).getAbsolutePath()));
    }
    return this;
  }
  public CustomDialog setIsChangeName(boolean isGhange){
    isChangeName = isGhange;
    return this;
  }
  public CustomDialog setIsGonePrivateCheckBox(boolean isGone){
    isGonePrivateCheckBox = isGone;
    if (chkPrivateDialpgSendComment != null) {
      if (isGonePrivateCheckBox){
        chkPrivateDialpgSendComment.setVisibility(View.GONE);
      }else {
        chkPrivateDialpgSendComment.setVisibility(View.VISIBLE);
      }
    }
    return this;
  }
  public CustomDialog setIsGoneEditText(boolean isGone){
    isGoneEditText = isGone;
    if (edtDialogSendComment != null) {
      if (isGoneEditText){
        edtDialogSendComment.setVisibility(View.GONE);
      }else {
        edtDialogSendComment.setVisibility(View.VISIBLE);
      }
    }
    return this;
  }
  public CustomDialog setTitleText(String text) {

    mTitleText = text;
    if (txtTitleDialogSendComment != null && mTitleText != null) {
      txtTitleDialogSendComment.setText(mTitleText);
    }
    return this;
  }
  public CustomDialog setConfirmClickListener(OnConfirmCommentClickListener listener) {
    mConfirmClickListener = listener;
    return this;
  }
  public interface OnConfirmCommentClickListener {
    void onClick(CustomDialog customDialog, String message, boolean isPrivate);
  }



}
