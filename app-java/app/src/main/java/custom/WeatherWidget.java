package custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.naeen.yousefi.R;


public class WeatherWidget extends LinearLayout {
  private TextView txtWeatherWidgetTempMax,txtWeatherWidgetTempMin,txtWeatherWidgetDay;
  private ImageView imgWeatherWidget;
  public WeatherWidget(@NonNull Context context) {
    super(context);
    initialize();
  }

  public WeatherWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initialize();
  }

  public WeatherWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize();
  }
  public void initialize() {
    View view= LayoutInflater.from(getContext()).inflate(R.layout.weather_widget, this);
    txtWeatherWidgetTempMax=view.findViewById(R.id.txtWeatherWidgetTempMax);
    txtWeatherWidgetTempMin=view.findViewById(R.id.txtWeatherWidgetTempMin);
    txtWeatherWidgetDay=view.findViewById(R.id.txtWeatherWidgetDay);
    imgWeatherWidget=view.findViewById(R.id.imgWeatherWidget);
  }
  public void setTempMin(int minTemp){
    txtWeatherWidgetTempMin.setText(""+minTemp);
  }
  public void setTempMax(int maxTemp){
    txtWeatherWidgetTempMax.setText(""+maxTemp);
  }
  public void setDay(String dt){
    txtWeatherWidgetDay.setText(dt+"");
  }
  public void setImage(String main){
    if(main.equals("Snow")){
      imgWeatherWidget.setImageResource(R.drawable.weather_snow);
    }else{
      if(main.equals("Rain")){
          imgWeatherWidget.setImageResource(R.drawable.weather_rain);
      }else{
        if(main.equals("Clouds")){
          imgWeatherWidget.setImageResource(R.drawable.weather_cloudy);
        }else{
          if(main.equals("Clear")){
            imgWeatherWidget.setImageResource(R.drawable.weather_clear_day);
          }else{

          }
        }
      }
    }


  }
}
