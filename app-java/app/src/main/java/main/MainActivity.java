package main;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import answerable.AnswerbleFragment;
import custom.tablayout.MyTabLayout;
import helper.utility.Constant;
import home.FragmentHome;
import ir.naeen.yousefi.R;
import needs.NeedsFragment;
import news.NewsFragment;
import utility.FragmentUtility;


public class MainActivity extends ActivityTop{
  MyTabLayout tabLayout;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    //TODO Pushe
    tabLayout =findViewById(R.id.spaceTabLayout);
    Bundle bundle = getIntent().getExtras();
    int position=2;
    if(bundle !=null) {
      position = bundle.getInt(Constant.POSITIO_TAB_LAYOT);
    }
    tabLayout.startPosiion(position);
    List<Fragment> fragmentList = new ArrayList<>();
    fragmentList.add(new FragmentUtility());
    fragmentList.add(new AnswerbleFragment());
    fragmentList.add(new NeedsFragment());
    fragmentList.add(new NewsFragment());
    fragmentList.add(new FragmentHome());

    tabLayout.initialize(findViewById(R.id.viewPagerMain), getSupportFragmentManager(), fragmentList);

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    for (Fragment fragment : getSupportFragmentManager().getFragments()) {
      fragment.onActivityResult(requestCode, resultCode, data);
    }
  }


}
