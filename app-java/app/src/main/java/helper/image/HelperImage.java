package helper.image;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.esafirm.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HelperImage {
  public static String convertImageToString( Image image) {
    String result="";
    try {
      InputStream inputStream= new FileInputStream(image.getPath());
      byte[] bytes;
      byte[] buffer = new byte[8192];
      int bytesRead;
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      while (true) {
        if (!((bytesRead = inputStream.read(buffer)) != -1)) break;
        output.write(buffer, 0, bytesRead);
      }
      bytes = output.toByteArray();
      result= Base64.encodeToString(bytes, Base64.DEFAULT);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static Bitmap getBitmapFromSDCard(File file){
    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    return BitmapFactory.decodeFile(file.getAbsolutePath(),bmOptions);

  }
  public static String BitMapToString(Bitmap bitmap){
    ByteArrayOutputStream baos=new  ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
    byte [] b=baos.toByteArray();
    String temp=Base64.encodeToString(b, Base64.DEFAULT);
    return temp;
  }
}
