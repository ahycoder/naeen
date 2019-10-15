package needs;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import helper.utility.Constant;


public class NeedsNet {
  public static void getNeedsOfServer(String search){
    JSONArray jsonArray= new JSONArray();
    try {
      JSONObject object= new JSONObject();
      object.put("Key","NeedsSearch");
      object.put("Search",search);
      jsonArray.put(object);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    AndroidNetworking.post( Constant.URL_NEEDS)
      .addJSONArrayBody(jsonArray) // posting json
      .setPriority(Priority.MEDIUM)
      .build()
      .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray jsonArray) {
          ArrayList<StructNeeds> needsArrayList = new ArrayList<>();
          for (int i = 0; i < jsonArray.length(); i++) {
            try {
              JSONObject jsonobject = jsonArray.getJSONObject(i);
              StructNeeds structNeeds = new StructNeeds();
              structNeeds.id = jsonobject.getInt("needs_id");
              structNeeds.imageUrl = jsonobject.getString("needs_imageUrl");
              structNeeds.title = jsonobject.getString("needs_title");
              structNeeds.price = jsonobject.getString("needs_price");
              structNeeds.date = jsonobject.getString("needs_date");
              needsArrayList.add(structNeeds);
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        }
        @Override
        public void onError(ANError error) {
          // handle error
        }
      });
  }

}
