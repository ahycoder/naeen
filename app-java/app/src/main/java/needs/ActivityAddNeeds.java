package needs;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import main.ActivityTop;
import main.G;
import custom.dialog.SweetAlertDialog;
import helper.utility.Constant;
import helper.image.HelperImage;
import helper.database.HelperDatabase;
import ir.naeen.yousefi.R;
import main.MainActivity;

public class ActivityAddNeeds extends ActivityTop {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.needs_add_activity);
    ui();
    openBottemSheetCategory();
    startActivityForImageAdd();


    layAddNeedsCategory.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
             openBottemSheetCategory();
           }
     });

      layAddNeedsYear.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          txtCustomBottomSheetTitle.setText("سال ساخت");
          ArrayList<String> years = new ArrayList<>();
          for (int i = 1399; i > 1366; i--) {
            years.add(i + "");
          }
          years.add("1366 به قبل");
          AdapterBottomSheet adapterBottomSheet = new AdapterBottomSheet(years, new AdapterBottomSheet.onItemSelectedListener() {
            @Override
            public void onItem(String item) {
              txtAddNeedsYear.setText("" + item);
              bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
          });
          recyclerBottomSheet.setAdapter(adapterBottomSheet);
          bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
      });
    layAddNeddsCity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        txtCustomBottomSheetTitle.setText("انتخاب شهر");
        AdapterCityBottomSheet adapterBottomSheet = new AdapterCityBottomSheet(getListCity(), new AdapterCityBottomSheet.onItemSelectedListener() {
          @Override
          public void onItem(StructCity item) {
            txtAddNeedsCity.setText(""+item.name);
            bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
          }
        });
        recyclerBottomSheet.setAdapter(adapterBottomSheet);
        bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
      }
    });
     layAddNeedsRoom.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
         txtCustomBottomSheetTitle.setText("تعداد اتاق");
         ArrayList<String> rooms= new ArrayList<>();
         rooms.add("بدون اتاق");
         rooms.add("یک");
         rooms.add("دو");
         rooms.add("سه");
         rooms.add("چهار");
         rooms.add("پنج یا بیشتر");
         AdapterBottomSheet adapterBottomSheet = new AdapterBottomSheet(rooms, new AdapterBottomSheet.onItemSelectedListener() {
           @Override
           public void onItem(String item) {
             txtAddNeedsRoom.setText(""+item);
             bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
           }
         });
         recyclerBottomSheet.setAdapter(adapterBottomSheet);
         bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);

       }
     });

    layAddNeedsKind.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        txtCustomBottomSheetTitle.setText("نوع آگهی");
        ArrayList<String> kinds= new ArrayList<>();
        kinds.add("ارئه");
        kinds.add("درخواستی");
        AdapterBottomSheet adapterBottomSheet = new AdapterBottomSheet(kinds, new AdapterBottomSheet.onItemSelectedListener() {
          @Override
          public void onItem(String item) {
            txtAddNeedsKind.setText(""+item);
            bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
          }
        });
        recyclerBottomSheet.setAdapter(adapterBottomSheet);
        bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);

      }
    });
    View.OnClickListener clickListenerAddImage= new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if((fileImages.size()+1)==Integer.parseInt(view.getTag().toString())){
          startImagePicker();
        }
      }
    };
    imgAddNeeds1.setOnClickListener(clickListenerAddImage);
    imgAddNeeds2.setOnClickListener(clickListenerAddImage);
    imgAddNeeds3.setOnClickListener(clickListenerAddImage);


    View.OnClickListener clickListeneRemove= new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int currentRemove =Integer.parseInt(view.getTag().toString());
        fileImages.remove(currentRemove-1);
        manageAddImage();
      }
    };
    imgAddNeesRemove1.setOnClickListener(clickListeneRemove);
    imgAddNeesRemove2.setOnClickListener(clickListeneRemove);
    imgAddNeesRemove3.setOnClickListener(clickListeneRemove);
    fabNeedsAdd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(handleItemsBeforeSendNeed()){
          uploadImages(new endUploadedImagesListener() {
            @Override
            public void onEndUploaded() {
              Log.i("TAG","savvvvvvvvvvvee");
              AddNeed();
            }
          });
        }
      }
    });


  }

  private ArrayList<StructCity> getListCity(){
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM city", null);
    ArrayList<StructCity>  cities= new ArrayList<>();
    while(cursor.moveToNext()) {
      StructCity  structCity = new StructCity();
      structCity.name= cursor.getString(cursor.getColumnIndex("city_name"));
      structCity.id= cursor.getInt(cursor.getColumnIndex("city_id"));
      cities.add(structCity);
    }
    cursor.close();
    database.close();
    return cities;
  }

  private StructNeedsCategory getItemCategory(int id) {
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM needscat", null);
    StructNeedsCategory category = new StructNeedsCategory();
    while(cursor.moveToNext()) {
      int needscat_id = cursor.getInt(cursor.getColumnIndex("needscat_id"));
      if (needscat_id==id){
        category.name= cursor.getString(cursor.getColumnIndex("needscat_name"));
        category.up = cursor.getInt(cursor.getColumnIndex("needscat_up"));
        category.isYear = cursor.getInt(cursor.getColumnIndex("needscat_isYear"));
        category.isPrice = cursor.getInt(cursor.getColumnIndex("needscat_isPrice"));
        category.isArea = cursor.getInt(cursor.getColumnIndex("needscat_isArea"));
        category.isRent = cursor.getInt(cursor.getColumnIndex("needscat_isRent"));
        category.isMileage = cursor.getInt(cursor.getColumnIndex("needscat_isMileage"));
        category.isRoom = cursor.getInt(cursor.getColumnIndex("needscat_isRoom"));
        category.isKind = cursor.getInt(cursor.getColumnIndex("needscat_isKind"));
        break;
      }
    }
    cursor.close();
    database.close();
    return category;
  }
  private int getIdCity(String name) {
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM city", null);
    int id=1;
    while(cursor.moveToNext()) {
      String cityNmae = cursor.getString(cursor.getColumnIndex("city_name"));
      if (name.equals(cityNmae)){
        id = cursor.getInt(cursor.getColumnIndex("city_id"));
      }
    }
    cursor.close();
    database.close();
    return id;
  }

  private void  AddNeed(){
    SweetAlertDialog pDialog = new SweetAlertDialog(G.currentActivity, SweetAlertDialog.PROGRESS_TYPE);
    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
    pDialog.setTitleText("درحال ارسال آگهی");
    pDialog.setCancelable(false);
    pDialog.show();
    Log.i("TAG", "AddNeed itemSelectedCategory:" + itemSelectedCategory.id);
    AndroidNetworking.post(Constant.URL_NEEDS)
      .setPriority(Priority.MEDIUM)
      .addBodyParameter("Key","AddNeeds")
      .addBodyParameter("login_userId",G.getUserId())
      .addBodyParameter("needscat_id",itemSelectedCategory.id+"")
      .addBodyParameter("needs_title",edtAddNeddsTitle.getText().toString())
      .addBodyParameter("needs_desc",edtAddNeddsDesc.getText().toString())
      .addBodyParameter("needs_city",getIdCity(txtAddNeedsCity.getText().toString())+"")
      .addBodyParameter("needs_year", txtAddNeedsYear.getText().toString())
      .addBodyParameter("needs_price",edtAddNeddsPrice.getText().toString())
      .addBodyParameter("needs_area",edtAddNeddsArea.getText().toString())
      .addBodyParameter("needs_rent",edtAddNeddsRent.getText().toString())
      .addBodyParameter("needs_mileage",edtAddNeddsMileage.getText().toString())
    //TODO needs_fast
      .addBodyParameter("needs_kind",txtAddNeedsKind.getText().toString())
    //TOTO needs_chat
      .addBodyParameter("needs_room",txtAddNeedsRoom.getText().toString())
      .addBodyParameter("needs_imageUrl",""+new JSONArray(pathUploadedImages))
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray jsonArray) {
          pDialog.dismissWithAnimation();
          try {
            if (jsonArray.get(0).toString().equals(200+"")){
              Toast.makeText(G.context,jsonArray.get(1)+"",Toast.LENGTH_SHORT).show();
              new SweetAlertDialog(G.currentActivity, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("ارسال آگهی")
                .setContentText("آگهی شما با موفقیت ثبت گردید")
                .setConfirmText("برمیگردم به نیازمندیها")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                  @Override
                  public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();
                    Intent intent=new Intent(G.currentActivity,MainActivity.class);
                    intent.putExtra(Constant.POSITIO_TAB_LAYOT,0);
                    G.currentActivity.startActivity(intent);
                    G.currentActivity.finish();
                  }
                })
                .show();

            }else if(jsonArray.get(0).toString().equals(400+"")){
              Toast.makeText(G.context,jsonArray.get(1)+"",Toast.LENGTH_SHORT).show();
            }

          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
        @Override
        public void onError(ANError error) {
          pDialog.dismissWithAnimation();
          new SweetAlertDialog(G.currentActivity, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("ارسال آگهی")
            .setContentText("متاسفانه آگهی شما ارسال نشد")
            .setConfirmText("بعدا ارسال میکنم")
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
              @Override
              public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
              }
            })
            .show();
          Log.i("TAG","anError AddNeeds ***"+error);
        }
      });
  }
      private void uploadImages(endUploadedImagesListener endUploadedImagesListener){
        pathUploadedImages.clear();
        if (fileImages.size()>0){
          for (int i=0;i<fileImages.size();i++){
            Bitmap bitmap =HelperImage.getBitmapFromSDCard(fileImages.get(i));
            String stringImage=HelperImage.BitMapToString(bitmap);
            AndroidNetworking.post(Constant.URL_IMAGE_REQUEST)
              .addBodyParameter("Key", "NeedImage")
              .addBodyParameter("image", stringImage)
              .addBodyParameter("name", G.getUserId()+System.currentTimeMillis())
              .setPriority(Priority.MEDIUM)
              .build()
              .getAsString(new StringRequestListener() {
               @Override
               public void onResponse(String response) {
                 Log.i("TAG","response uploadImages ***"+response);
                 pathUploadedImages.add(response);
                 if(pathUploadedImages.size()==fileImages.size()){
                   endUploadedImagesListener.onEndUploaded();
                 }
               }
               @Override
               public void onError(ANError anError) {
                 Log.i("TAG","anError uploadImages ***"+anError);
               }
             });
          }
        }else{
          endUploadedImagesListener.onEndUploaded();
        }


      }

      private interface endUploadedImagesListener{
          void onEndUploaded();
  }


  private void startImagePicker(){
    ImagePicker.create(ActivityAddNeeds.this)
      .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
      .folderMode(false) // folder mode (false by default)
      .toolbarFolderTitle("عکس آگهی") // folder selection title
      .toolbarImageTitle("انتخاب کنید") // image selection title
      .toolbarArrowColor(G.context.getResources().getColor(R.color.colorAccent)) // Toolbar 'up' arrow color
      .includeVideo(false) // Show video on image picker
      .single() // single mode
      .showCamera(true) // show camera or not (true by default)
      .theme(R.style.AppTheme)
      .start();
  }
  private ArrayList<File> fileImages = new ArrayList();

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
      fileImages.add(new File(ImagePicker.getFirstImageOrNull(data).getPath()));
      manageAddImage();
    }
  }

  private void startActivityForImageAdd(){
    imgAddNeeds1.setVisibility(View.VISIBLE);
    imgAddNeeds2.setVisibility(View.INVISIBLE);
    imgAddNeeds3.setVisibility(View.INVISIBLE);
    imgAddNeeds1.setImageResource(R.drawable.add_image_needs);
    imgAddNeeds2.setImageResource(R.drawable.add_image_needs);
    imgAddNeeds3.setImageResource(R.drawable.add_image_needs);
    imgAddNeesRemove1.setVisibility(View.INVISIBLE);
    imgAddNeesRemove2.setVisibility(View.INVISIBLE);
    imgAddNeesRemove3.setVisibility(View.INVISIBLE);
  }
  private void manageAddImage(){
    startActivityForImageAdd();
    Map<Integer, ImageView> mapImageAdd = new HashMap();
    mapImageAdd.put(0, imgAddNeeds1);
    mapImageAdd.put(1, imgAddNeeds2);
    mapImageAdd.put(2, imgAddNeeds3);
    Map<Integer, ImageView> mapImageRemove = new HashMap();
    mapImageRemove.put(0, imgAddNeesRemove1);
    mapImageRemove.put(1, imgAddNeesRemove2);
    mapImageRemove.put(2, imgAddNeesRemove3);
    for (int i=0;i<fileImages.size();i++){
      //TODO test this
      mapImageAdd.get(i).setImageBitmap(BitmapFactory.decodeFile(String.valueOf(fileImages.get(i))));
      mapImageRemove.get(i).setVisibility(View.VISIBLE);
      if(i!=2) mapImageAdd.get(i+1).setVisibility(View.VISIBLE);
    }
    mapImageAdd.clear();
    mapImageRemove.clear();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    Intent intent =new Intent(G.currentActivity, MainActivity.class);
    intent.putExtra(Constant.POSITIO_TAB_LAYOT,0);
    G.currentActivity.startActivity(intent);
    G.currentActivity.finish();
  }

  private boolean handleItemsBeforeSendNeed(){
    boolean isTitle = handleEdtTitle();
    boolean isDesc = handleEdtDesc();
    boolean isCity = handleCity();

    boolean isPrice;
    if (itemSelectedCategory.isPrice==1){
       isPrice = handleEdtPrice();
    }else{
      isPrice=true;
      edtAddNeddsPrice.setText("0");
    }

    boolean isRent;
    if (itemSelectedCategory.isRent==1){
      isRent = handleEdtRent();
    }else{
      isRent=true;
      edtAddNeddsRent.setText("0");
    }

    boolean isMileage;
    if (itemSelectedCategory.isMileage==1){
      isMileage = handleEdtMileage();
    }else{
      isMileage=true;
      edtAddNeddsMileage.setText("0");
    }

    boolean isArea;
    if (itemSelectedCategory.isArea==1){
      isArea = handleEdtArea();
    }else{
      isArea=true;
      edtAddNeddsArea.setText("0");
    }

    boolean isRoom;
    if (itemSelectedCategory.isRoom==1){
      isRoom = handleRoom();
    }else{
      isRoom=true;
      txtAddNeedsRoom.setText("0");
    }

    boolean isYear;
    if (itemSelectedCategory.isYear==1){
      isYear = handleYear();
    }else{
      isYear=true;
      txtAddNeedsYear.setText("0");
    }

    boolean isKind;
    if (itemSelectedCategory.isKind==1){
      isKind = handleKind();
    }else{
      isKind=true;
      txtAddNeedsKind.setText("0");
    }
    Log.i("TAG", "handle :::: "+isTitle + isDesc + isCity + isPrice +isRent+ isMileage + isArea + isRoom + isYear + isKind );
    if(isTitle && isDesc && isCity && isPrice && isRent && isMileage && isArea && isRoom && isYear && isKind){
      return true;
    }
    return false;

  }


  private boolean handleEdtTitle(){
    boolean result=false;
    if(edtAddNeddsTitle.getText().toString().length()>50){
      result=false;
    }else{
      if(edtAddNeddsTitle.getText().toString().length()<3){
        result=false;
      }else{
        result=true;
      }
    }
    Log.i("TAG", "handleTitle : "+result );
    return result;
  }

  private boolean handleEdtDesc(){
    boolean result=false;
    if(edtAddNeddsDesc.getText().toString().length()>1000){
      result=false;
    }else{
      if(edtAddNeddsDesc.getText().toString().length()<10){
        result=false;
      }else{
        result=true;
      }
    }
    Log.i("TAG", "handleDesc : "+result );
    return result;
  }
  private boolean handleEdtPrice(){
    boolean result=false;
    if(edtAddNeddsPrice.getText().toString().length()<1){
      result=false;
    }else{
      result=true;
    }
    Log.i("TAG", "handlePrice : "+result );
    return result;
  }
  private boolean handleEdtRent(){
    boolean result=false;
    if(edtAddNeddsRent.getText().toString().length()<1){
      result=false;
    }else{
      result=true;
    }
    Log.i("TAG", "handleRent : "+result);
    return result;
  }
  private boolean handleEdtMileage(){
    boolean result=false;
    if(edtAddNeddsMileage.getText().toString().length()<1){
      result=false;
    }else{
      result=true;
    }
    Log.i("TAG", "handleMileage : "+result );
    return result;
  }
  private boolean handleEdtArea(){
    boolean result=false;
    if(edtAddNeddsArea.getText().toString().length()<1){
      result=false;
    }else{
      result=true;
    }
    Log.i("TAG", "handleArea : "+result );
    return result;
  }
  private boolean handleCity(){
    boolean result=false;
    if(txtAddNeedsCity.getText().toString().length()<1 || txtAddNeedsCity.getText().toString().equals("انتخاب")){
      result=false;
    }else{
      result=true;
    }
    Log.i("TAG", "handleCity : "+result );
    return result;
  }
  private boolean handleRoom(){
    boolean result=false;
    if(txtAddNeedsRoom.getText().toString().length()<1 || txtAddNeedsRoom.getText().toString().equals("انتخاب")){
      result=false;
    }else{
      result=true;
    }
    Log.i("TAG", "handleRoom : "+result );
    return result;
  }
  private boolean handleYear(){
    boolean result=false;
    if(txtAddNeedsYear.getText().toString().length()<1 || txtAddNeedsYear.getText().toString().equals("انتخاب")){
      result=false;
    }else{
      result=true;
    }
    Log.i("TAG", "handleYear : "+result );
    return result;
  }
  private boolean handleKind(){
    boolean result=false;
    if(txtAddNeedsKind.getText().toString().length()<1 || txtAddNeedsKind.getText().toString().equals("انتخاب")){
      result=false;
    }else{
      result=true;
    }
     Log.i("TAG", "handleKind : "+result );
    return result;
  }
  private ArrayList<StructNeedsCategory> getCategories(int up){
    ArrayList<StructNeedsCategory> needsCategories= new ArrayList<>();
    SQLiteDatabase database = HelperDatabase.getInstance().getWritableDatabase();
    Cursor cursor = database.rawQuery("SELECT * FROM needscat", null);
    while(cursor.moveToNext()) {
      StructNeedsCategory structNeedsCategory = new StructNeedsCategory();
      if(cursor.getInt(cursor.getColumnIndex("needscat_up"))==up){
        structNeedsCategory.id = cursor.getInt(cursor.getColumnIndex("needscat_id"));
        structNeedsCategory.name = cursor.getString(cursor.getColumnIndex("needscat_name"));
        needsCategories.add(structNeedsCategory);
      }
    }
    cursor.close();
    database.close();

    return needsCategories;
  }

  private void openBottemSheetCategory(){
    bottomSheetCategory.setState(BottomSheetBehavior.STATE_EXPANDED);
    AdapterNeedCatBottomSheet adapterNeedCatBottomSheet = new AdapterNeedCatBottomSheet(getCategories(0), new AdapterNeedCatBottomSheet.onItemNeedCatSelectedListener() {
      @Override
      public void onItem(StructNeedsCategory item,int position) {
        for (int i=0;i<getCategories(0).size();i++){
          recyclerNeedCat1BottomSheet.getChildAt(i).findViewById(R.id.imgArrowtItemCustomBottomSheet).setVisibility(View.GONE);
        }
        recyclerNeedCat1BottomSheet.getChildAt(position).findViewById(R.id.imgArrowtItemCustomBottomSheet).setVisibility(View.VISIBLE);
        AdapterNeedCatBottomSheet adapter2NeedCatBottomSheet= new AdapterNeedCatBottomSheet(getCategories(item.id), new AdapterNeedCatBottomSheet.onItemNeedCatSelectedListener() {
          @Override
          public void onItem(StructNeedsCategory item,int position) {
            Log.i("TAG", "openBottemSheetCategory itemSelectedCategory:" + item.id);
            txtAddNeedsCategory.setText(item.name);
            itemSelectedCategory =item;
           // itemSelectedCategory =getItemCategory(item.id);
             layAddNeedsYear.setVisibility(View.GONE);
             layAddNeddsPrice.setVisibility(View.GONE);
             layAddNeddsArea.setVisibility(View.GONE);
             layAddNeddsRent.setVisibility(View.GONE);
             layAddNeddsMileage.setVisibility(View.GONE);
             layAddNeedsRoom.setVisibility(View.GONE);
             layAddNeedsKind.setVisibility(View.GONE);
             if(itemSelectedCategory.isYear==1) layAddNeedsYear.setVisibility(View.VISIBLE);
            if(itemSelectedCategory.isPrice==1) layAddNeddsPrice.setVisibility(View.VISIBLE);
            if(itemSelectedCategory.isArea==1) layAddNeddsArea.setVisibility(View.VISIBLE);
            if(itemSelectedCategory.isRent==1) layAddNeddsRent.setVisibility(View.VISIBLE);
            if(itemSelectedCategory.isMileage==1) layAddNeddsMileage.setVisibility(View.VISIBLE);
            if(itemSelectedCategory.isRoom==1) layAddNeedsRoom.setVisibility(View.VISIBLE);
            if(itemSelectedCategory.isKind==1) layAddNeedsKind.setVisibility(View.VISIBLE);
            bottomSheetCategory.setState(BottomSheetBehavior.STATE_COLLAPSED);
          }
        });
        recyclerNeedCat2BottomSheet.setAdapter(adapter2NeedCatBottomSheet);
      }
    });
    recyclerNeedCat1BottomSheet.setAdapter(adapterNeedCatBottomSheet);
  }

  private FloatingActionButton fabNeedsAdd;
  private LinearLayout layAddNeedsKind,layAddNeedsCategory,layAddNeddsImage,layAddNeddsTitle,layAddNeddsDesc,layAddNeddsCity,layAddNeddsPrice,layAddNeedsRoom,layAddNeddsMileage,layAddNeddsRent,layAddNeddsArea,layAddNeedsYear;
  private TextView txtAddNeedsCategory,txtAllNeedCatBottom_sheet;;
  private EditText edtAddNeddsTitle,edtAddNeddsDesc,edtAddNeddsPrice,edtAddNeddsRent,edtAddNeddsMileage,edtAddNeddsArea;
  private StructNeedsCategory itemSelectedCategory ;
  private ImageView imgAddNeeds1,imgAddNeeds2,imgAddNeeds3,imgAddNeesRemove1,imgAddNeesRemove2,imgAddNeesRemove3;
  private ArrayList<String> pathUploadedImages= new ArrayList<>();
  private TextView txtAddNeedsRoom, txtAddNeedsYear, txtAddNeedsCity,txtCustomBottomSheetTitle, txtAddNeedsKind;
  private RecyclerView recyclerBottomSheet;
  private RecyclerView recyclerNeedCat1BottomSheet,recyclerNeedCat2BottomSheet;
  private LinearLayout sheetLayout,needCatBottom_sheet;
  private BottomSheetBehavior bottomSheet;
  private BottomSheetBehavior bottomSheetCategory;
  private void ui(){
    fabNeedsAdd =findViewById(R.id.fabNeedsAdd);
    txtAddNeedsCategory =findViewById(R.id.txtAddNeedsCategory);
    imgAddNeeds1 =findViewById(R.id.imgAddNeeds1);
    imgAddNeeds2 =findViewById(R.id.imgAddNeeds2);
    imgAddNeeds3 =findViewById(R.id.imgAddNeeds3);
    imgAddNeesRemove1 =findViewById(R.id.imgAddNeesRemove1);
    imgAddNeesRemove2 =findViewById(R.id.imgAddNeesRemove2);
    imgAddNeesRemove3 =findViewById(R.id.imgAddNeesRemove3);
    layAddNeedsCategory=findViewById(R.id.layAddNeedsCategory);
    // layAddNeddsImage=findViewById(R.id.layAddNeddsImage);


    layAddNeedsKind=findViewById(R.id.layAddNeedsKind);
    layAddNeddsCity=findViewById(R.id.layAddNeddsCity);
    layAddNeedsYear=findViewById(R.id.layAddNeedsYear);
    layAddNeddsArea=findViewById(R.id.layAddNeddsArea);
    layAddNeddsRent=findViewById(R.id.layAddNeddsRent);
    layAddNeddsMileage=findViewById(R.id.layAddNeddsMileage);
    layAddNeedsRoom=findViewById(R.id.layAddNeedsRoom);
    layAddNeddsTitle=findViewById(R.id.layAddNeddsTitle);
    layAddNeddsDesc=findViewById(R.id.layAddNeddsDesc);
    layAddNeddsPrice=findViewById(R.id.layAddNeddsPrice);
    edtAddNeddsTitle=findViewById(R.id.edtAddNeddsTitle);
    edtAddNeddsDesc=findViewById(R.id.edtAddNeddsDesc);
    edtAddNeddsPrice=findViewById(R.id.edtAddNeddsPrice);
    edtAddNeddsRent=findViewById(R.id.edtAddNeddsRent);
    edtAddNeddsMileage=findViewById(R.id.edtAddNeddsMileage);
    edtAddNeddsArea=findViewById(R.id.edtAddNeddsArea);
    txtAddNeedsRoom =findViewById(R.id.txtAddNeedsityRoom);
    txtAddNeedsYear =findViewById(R.id.txtAddNeedsityYear);
    txtAddNeedsCity =findViewById(R.id.txtAddNeedsityCity);
    txtAddNeedsKind =findViewById(R.id.txtAddNeedsityKind);
    txtCustomBottomSheetTitle=findViewById(R.id.txtCustomBottomSheetTitle);

    sheetLayout = findViewById(R.id.bottom_sheet);
    needCatBottom_sheet = findViewById(R.id.needCatBottom_sheet);
    recyclerNeedCat1BottomSheet = findViewById(R.id.recyclerNeedCat1BottomSheet);
    recyclerNeedCat1BottomSheet.setLayoutManager(new LinearLayoutManager(G.currentActivity));
    recyclerNeedCat2BottomSheet = findViewById(R.id.recyclerNeedCat2BottomSheet);
    recyclerNeedCat2BottomSheet.setLayoutManager(new LinearLayoutManager(G.currentActivity));
    txtAllNeedCatBottom_sheet = findViewById(R.id.txtAllNeedCatBottom_sheet);
    txtAllNeedCatBottom_sheet.setText("باید نوع آگهی خود را انتخاب کنید");
    recyclerBottomSheet = findViewById(R.id.recyclerBottomSheet);
    recyclerBottomSheet.setLayoutManager(new LinearLayoutManager(this));
    bottomSheet = BottomSheetBehavior.from(sheetLayout);
    bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
    bottomSheetCategory = BottomSheetBehavior.from(needCatBottom_sheet);
    bottomSheetCategory.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
      @Override
      public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
          bottomSheetCategory.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
      }
      @Override
      public void onSlide(@NonNull View bottomSheet, float slideOffset) {
      }
    });
  }

}
