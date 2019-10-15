package intro;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import main.ActivityTop;
import main.G;
import main.SplashScreen;
import custom.imageSlider.PageIndicator;
import helper.font.FontContextWrapper;
import ir.naeen.yousefi.R;

public class IntroActivity extends ActivityTop implements FragmentIntro1.OnFragmentInteractionListener,FragmentIntro2.OnFragmentInteractionListener,FragmentIntro3.OnFragmentInteractionListener,FragmentIntro4.OnFragmentInteractionListener {
private ImageView imgNextIntro;
private TextView textEnterIntro;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
     setContentView(R.layout.intro_activity);
    imgNextIntro=findViewById(R.id.imgNextIntro);
    textEnterIntro=findViewById(R.id.textEnterIntro);
    ArrayList<Fragment> fragmentList = new ArrayList<>();
    fragmentList.add(new FragmentIntro1());
    fragmentList.add(new FragmentIntro2());
    fragmentList.add(new FragmentIntro3());
    fragmentList.add(new FragmentIntro4());
    AdapterIntro adapterIntro= new AdapterIntro(fragmentList,getSupportFragmentManager());
    ViewPager viewPager =findViewById(R.id.viewPagerIntro);
     PageIndicator indicator =findViewById(R.id.indicatorIntro);;
     indicator.setIndicatorsCount(fragmentList.size());
    viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageSelected(int index) {
        Log.i("LOG", "index: " + index);
        if (index==(fragmentList.size()-1)){
          textEnterIntro.setVisibility(View.VISIBLE);
          imgNextIntro.setVisibility(View.GONE);
        }else {
          textEnterIntro.setVisibility(View.GONE);
          imgNextIntro.setVisibility(View.VISIBLE);
        }

      }
      @Override
      public void onPageScrolled(int startIndex, float percent, int pixel) {
        indicator.setPercent(percent);
        indicator.setCurrentPage(startIndex);
        Log.i("LOG", "startIndex: " + startIndex);
      }
      @Override
      public void onPageScrollStateChanged(int arg0) {

      }
    });
    imgNextIntro.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(indicator.getCurrentPage()!=(fragmentList.size()-1)){
          viewPager.setCurrentItem(indicator.getCurrentPage()+1,true);
          indicator.setCurrentPage(indicator.getCurrentPage()+1);
        }
      }
    });
    textEnterIntro.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(indicator.getCurrentPage()==(fragmentList.size()-1)){
          G.currentActivity.startActivity(new Intent(G.currentActivity, SplashScreen.class));
          G.currentActivity.finish();
        }
      }
    });
      indicator.setRotationY(180);
    viewPager.setRotationY(180);
    viewPager.setAdapter(adapterIntro);

  }

  @Override
  public void onFragmentInteraction(Uri uri) {

  }
  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(FontContextWrapper.wrap(newBase));
  }
}
