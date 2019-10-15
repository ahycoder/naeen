package custom.imageSlider;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;

import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;
import main.G;
import custom.dialog.SweetAlertDialog;
import ir.naeen.yousefi.R;


public class ImagePagerAdapter extends PagerAdapter {

    private ArrayList<String>  imageUrls;
    private SweetAlertDialog pDialog;


    public ImagePagerAdapter(ArrayList<String> imageUrls, SweetAlertDialog pDialog) {
        this.imageUrls = imageUrls;
        this.pDialog = pDialog;
    }


    @Override
    public int getCount() {
        return imageUrls.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = G.layoutInflater.inflate(R.layout.lay_image_slider, null);
        ImageView image = (ImageView) view.findViewById(R.id.image);

        Log.i("TAG","position: "+position);
        AndroidNetworking.get(imageUrls.get(position).toString())
          .setPriority(Priority.IMMEDIATE)
          .setBitmapConfig(Bitmap.Config.ARGB_8888)
          .getResponseOnlyFromNetwork()
          .build()
          .getAsBitmap(new BitmapRequestListener() {
              @Override
              public void onResponse(Bitmap bitmap)
              {
                  image.setImageBitmap(bitmap);
                  if (position+1==getCount()){
                    pDialog.dismissWithAnimation();
                  }
              }
              @Override
              public void onError(ANError error) {
                //TODO set defualt image
                pDialog.dismissWithAnimation();
              }
          });
        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
