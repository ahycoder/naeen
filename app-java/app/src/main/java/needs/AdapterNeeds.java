package needs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.recyclerview.widget.RecyclerView;
import main.G;
import custom.CardViewLabel;
import custom.RoundedImageView;
import helper.jalalicalendar.HelperPastDate;
import ir.naeen.yousefi.R;

public class AdapterNeeds extends RecyclerView.Adapter<AdapterNeeds.ViewHolder> {
    private ArrayList<StructNeeds> list;
    public AdapterNeeds(ArrayList<StructNeeds> list) {
      this.list = list;
    }

    @Override
    public int getItemCount() {return list.size();}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.neeeds_item_recycler,parent, false);
      ViewHolder viewHolder = new ViewHolder(view);
      return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      final StructNeeds item = list.get(position);
      //holder.layItemNeeds.setBackground(new DrawableBuilder().cornerRadius(12).solidColor(Color.parseColor("#ffffff")).build());
      holder.imgItemNeeds.setCornerRadiiDP(10,0,10,0);
      holder.txtItemNeedsTitle.setText(""+ item.title);
      if (item.price.equals("0")){
        holder.txtItemNeedsPrice.setVisibility(View.GONE);
        holder.txtItemNeedsPriceTitle.setVisibility(View.GONE);
      }else {
        holder.txtItemNeedsPrice.setText(""+ item.price);
      }



      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date resultdate = new Date(G.getCurrentTimeFromServer());
      HelperPastDate helperPastDate= new HelperPastDate(sdf.format(resultdate),item.date);
      holder.txtItemNeedsDate.setText(""+ helperPastDate.calculate());

      int widthImage =(int) (G.getWidthDispaly()/4.5);
      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthImage, widthImage);
      //width and height of your Image ,if it is inside Relative change the LinearLayout to RelativeLayout.
      holder.imgItemNeeds.setLayoutParams(layoutParams);
      if(item.imageUrl.equals("[]")){
        holder.imgItemNeeds.setImageResource(R.drawable.need_whitout_image);
        holder.imgItemNeeds.setPadding(10, 10, 10, 10);
      }else {
        try {
          JSONArray imageUrls= new JSONArray(item.imageUrl);
          AndroidNetworking.get(imageUrls.get(0).toString())
            .setPriority(Priority.IMMEDIATE)
            .setBitmapConfig(Bitmap.Config.ARGB_8888)
//            .setBitmapMaxHeight(widthImage)
//            .setBitmapMaxWidth(widthImage)
            .build()
            .getAsBitmap(new BitmapRequestListener() {
              @Override
              public void onResponse(Bitmap bitmap)
              {
                holder.imgItemNeeds.setVisibility(View.VISIBLE);
                holder.imgItemNeeds.setImageBitmap(bitmap);
              }
              @Override
              public void onError(ANError error) {
                holder.imgItemNeeds.setImageResource(R.drawable.add_image_needs);
              }
            });

        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
      if(item.fast==1){
        holder.layItemNeeds.setLabelVisual(true);
        holder.layItemNeeds.setLabelHeight(25);
        holder.layItemNeeds.setLabelOrientation(2);
        holder.layItemNeeds.setLabelDistance(15);
        holder.layItemNeeds.setLabelBackgroundColor(G.context.getResources().getColor(R.color.colorFastNeeds));
        holder.layItemNeeds.setLabelText("فوری");
        holder.layItemNeeds.setLabelTextSize(30);
      }else{
        holder.layItemNeeds.setLabelVisual(false);
      }
      // update ui in layout item in here  example :  holder.txt_name.setText(item.name);

      holder.layItemNeeds.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent= new Intent(G.currentActivity, ActivityOneNeeds.class);
          intent.putExtra("needs_id",""+item.id);
          if(item.imageUrl.equals("[]")){
            intent.putExtra("imageUrl",false);
          }else {
            intent.putExtra("imageUrl",true);
            intent.putExtra("needs_imageUrl",item.imageUrl);
          }
          intent.putExtra("needs_title",""+item.title);
          intent.putExtra("needs_price",""+item.price);
          intent.putExtra("needs_date",""+item.date);
          G.currentActivity.startActivity(intent);
        }
      });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      private TextView txtItemNeedsDate;
      private TextView txtItemNeedsTitle;
      private TextView txtItemNeedsPrice;
      private TextView txtItemNeedsPriceTitle;
      private RoundedImageView imgItemNeeds;
      private CardViewLabel layItemNeeds;

      public ViewHolder(View view) {
        super(view);
        txtItemNeedsDate =view.findViewById(R.id.txtItemNeedsDate);
        txtItemNeedsTitle =view.findViewById(R.id.txtItemNeedsTitle);
        txtItemNeedsPrice =view.findViewById(R.id.txtItemNeedsPrice);
        txtItemNeedsPriceTitle =view.findViewById(R.id.txtItemNeedsPriceTitle);
        imgItemNeeds =view.findViewById(R.id.imgItemNeeds);
        layItemNeeds =view.findViewById(R.id.layItemNeeds);
      }

    }
    }
