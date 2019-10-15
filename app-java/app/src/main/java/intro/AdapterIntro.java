package intro;


import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class AdapterIntro extends FragmentPagerAdapter {
      private ArrayList<Fragment> fragmentList = new ArrayList<>();

      public AdapterIntro(ArrayList<Fragment> items,FragmentManager fragmentManager) {
        super(fragmentManager);
        fragmentList=items;
      }

      @Override
      public Fragment getItem(int position) {
        return fragmentList.get(position);
      }

      public Fragment getRawItem(int position) {
        return fragmentList.get(position);
      }

      @Override
      public int getCount() {
        return fragmentList.size();
      }

      public void add(Fragment item) {
        fragmentList.add(item);
      }


    }




