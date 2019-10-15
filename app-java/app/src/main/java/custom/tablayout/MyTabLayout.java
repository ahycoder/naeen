package custom.tablayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import ir.naeen.yousefi.R;


@SuppressWarnings("unused")
public class MyTabLayout extends RelativeLayout {

    private TabLayout tabLayout;

    private TabLayout.Tab tabOne;
    private TabLayout.Tab tabTwo;
    private TabLayout.Tab tabThree;
    private TabLayout.Tab tabFour;
    private TabLayout.Tab tabFive;

    private RelativeLayout parentLayout;
    private RelativeLayout selectedTabLayout;
    private FloatingActionButton actionButton;
    private ImageView backgroundImage;
    private ImageView backgroundImage2;

    private ImageView tabOneImageView;
    private ImageView tabTwoImageView;
    private ImageView tabThreeImageView;
    private ImageView tabFourImageView;
    private ImageView tabFiveImageView;

    private List<TabLayout.Tab> tabs = new ArrayList<>();
    private List<Integer> tabSize = new ArrayList<>();

    private int numberOfTabs = 5;
    private int currentPosition;
    private int startingPosition;

    private OnClickListener tabOneOnClickListener;
    private OnClickListener tabTwoOnClickListener;
    private OnClickListener tabThreeOnClickListener;
    private OnClickListener tabFourOnClickListener;
    private OnClickListener tabFiveOnClickListener;


    private Drawable defaultTabOneButtonIcon;
    private Drawable defaultTabTwoButtonIcon;
    private Drawable defaultTabThreeButtonIcon;
    private Drawable defaultTabFourButtonIcon;
    private Drawable defaultTabFiveButtonIcon;


    private int defaultButtonColor;
    private int defaultTabColor;


    private boolean SCROLL_STATE_DRAGGING = false;

    private static final String CURRENT_POSITION_SAVE_STATE = "buttonPosition";

    public MyTabLayout(Context context) {
        super(context);
        init();
    }

    public MyTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initArrts(attrs);
    }

    public MyTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initArrts(attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.tab_layout, this);
        parentLayout = findViewById(R.id.tabLayout);
        selectedTabLayout = findViewById(R.id.selectedTabLayout);
        backgroundImage = findViewById(R.id.backgroundImage);
        backgroundImage2 = findViewById(R.id.backgroundImage2);
        actionButton = findViewById(R.id.fab);
        tabLayout = findViewById(R.id.spaceTab);
        defaultTabOneButtonIcon = getContext().getResources().getDrawable(R.drawable.tab_utility);
        defaultTabTwoButtonIcon = getContext().getResources().getDrawable(R.drawable.tab_answerable);
        defaultTabThreeButtonIcon = getContext().getResources().getDrawable(R.drawable.tab_needs);
        defaultTabFourButtonIcon = getContext().getResources().getDrawable(R.drawable.tab_news);
        defaultTabFiveButtonIcon = getContext().getResources().getDrawable(R.drawable.tab_home);
        defaultButtonColor = ContextCompat.getColor(getContext(), R.color.colorTabBackGroundIcon);
        defaultTabColor = ContextCompat.getColor(getContext(), R.color.colorTabBackGround);
    }

    public void startPosiion(int startingPosition) {
        this.startingPosition = startingPosition;
    }

    private void initArrts(AttributeSet attrs) {
        switch (startingPosition) {
            case 0:
                actionButton.setImageDrawable(defaultTabOneButtonIcon);
                actionButton.setOnClickListener(tabOneOnClickListener);
                break;
            case 1:
                actionButton.setImageDrawable(defaultTabTwoButtonIcon);
                actionButton.setOnClickListener(tabTwoOnClickListener);
                break;
            case 2:
                actionButton.setImageDrawable(defaultTabThreeButtonIcon);
                actionButton.setOnClickListener(tabThreeOnClickListener);
                break;
            case 3:
                actionButton.setImageDrawable(defaultTabFourButtonIcon);
                actionButton.setOnClickListener(tabFourOnClickListener);
                break;
            case 4:
                actionButton.setImageDrawable(defaultTabFiveButtonIcon);
                actionButton.setOnClickListener(tabFiveOnClickListener);
                break;
        }
    }

    public void initialize(ViewPager viewPager, FragmentManager fragmentManager, List<Fragment> fragments) {
        viewPager.setAdapter(new PagerAdapter(fragmentManager, fragments));
        tabLayout.setupWithViewPager(viewPager);
        getViewTreeObserver().addOnGlobalLayoutListener(
          new ViewTreeObserver.OnGlobalLayoutListener() {
              @Override
              public void onGlobalLayout() {
                  tabSize.add(tabOne.getCustomView().getWidth());
                  tabSize.add(getWidth());
                  tabSize.add(backgroundImage.getWidth());
                  moveTab(tabSize, startingPosition);
                  if (currentPosition == 0) {
                      currentPosition = startingPosition;
                      tabs.get(startingPosition).getCustomView().setAlpha(0);
                  } else moveTab(tabSize, currentPosition);

                  getViewTreeObserver().removeOnGlobalLayoutListener(this);
              }
          });


        viewPager.setCurrentItem(startingPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (SCROLL_STATE_DRAGGING) {
                    tabs.get(position).getCustomView().setAlpha(positionOffset);
                    if (position < numberOfTabs - 1) {
                        tabs.get(position + 1).getCustomView().setAlpha(1 - positionOffset);
                    }

                    if (!tabSize.isEmpty()) {
                        if (position < currentPosition) {
                            final float endX = -tabSize.get(2) / 2 + getX(position, tabSize) + 42;
                            final float startX = -tabSize.get(2) / 2 + getX(currentPosition, tabSize) + 42;

                            if (!tabSize.isEmpty()) {
                                float a = endX - (positionOffset * (endX - startX));
                                selectedTabLayout.setX(a);
                            }

                        } else {
                            position++;
                            final float endX = -tabSize.get(2) / 2 + getX(position, tabSize) + 42;
                            final float startX = -tabSize.get(2) / 2 + getX(currentPosition, tabSize) + 42;

                            float a = startX + (positionOffset * (endX - startX));
                            selectedTabLayout.setX(a);
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                for (TabLayout.Tab t : tabs) t.getCustomView().setAlpha(1);
                tabs.get(position).getCustomView().setAlpha(0);
                moveTab(tabSize, position);
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                SCROLL_STATE_DRAGGING = state == ViewPager.SCROLL_STATE_DRAGGING;
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    for (TabLayout.Tab t : tabs) t.getCustomView().setAlpha(1);
                    tabs.get(currentPosition).getCustomView().setAlpha(0);
                    moveTab(tabSize, currentPosition);
                }
            }
        });

        tabOne = tabLayout.getTabAt(0);
        tabTwo = tabLayout.getTabAt(1);
        tabThree = tabLayout.getTabAt(2);
        tabFour = tabLayout.getTabAt(3);
        tabFive = tabLayout.getTabAt(4);

        tabOne.setCustomView(R.layout.tab_icon_layout);
        tabTwo.setCustomView(R.layout.tab_icon_layout);
        tabThree.setCustomView(R.layout.tab_icon_layout);
        tabFour.setCustomView(R.layout.tab_icon_layout);
        tabFive.setCustomView(R.layout.tab_icon_layout);

        tabs.add(tabOne);
        tabs.add(tabTwo);
        tabs.add(tabThree);
        tabs.add(tabFour);
        tabs.add(tabFive);

        tabOneImageView =tabOne.getCustomView().findViewById(R.id.tabImageView);
        tabTwoImageView =tabTwo.getCustomView().findViewById(R.id.tabImageView);
        tabThreeImageView =tabThree.getCustomView().findViewById(R.id.tabImageView);
        tabFourImageView =tabFour.getCustomView().findViewById(R.id.tabImageView);
        tabFiveImageView =tabFive.getCustomView().findViewById(R.id.tabImageView);

        selectedTabLayout.bringToFront();
        tabLayout.setSelectedTabIndicatorHeight(0);

    setAttrs();
}


    private void setAttrs() {
        setTabColor(defaultTabColor);
        setButtonColor(defaultButtonColor);
        setTabOneIcon(defaultTabOneButtonIcon);
        setTabTwoIcon(defaultTabTwoButtonIcon);
        setTabThreeIcon(defaultTabThreeButtonIcon);
        setTabFourIcon(defaultTabFourButtonIcon);
        setTabFiveIcon(defaultTabFiveButtonIcon);
    }

    private void moveTab(List<Integer> tabSize, int position) {
        if (!tabSize.isEmpty()) {
            float backgroundX = -tabSize.get(2) / 2 + getX(position, tabSize) + 42;
            switch (position) {
                case 0:
                    actionButton.setImageDrawable(defaultTabOneButtonIcon);
                    actionButton.setOnClickListener(tabOneOnClickListener);
                    break;
                case 1:
                    actionButton.setImageDrawable(defaultTabTwoButtonIcon);
                    actionButton.setOnClickListener(tabTwoOnClickListener);
                    break;
                case 2:
                    actionButton.setImageDrawable(defaultTabThreeButtonIcon);
                    actionButton.setOnClickListener(tabThreeOnClickListener);
                    break;
                case 3:
                    actionButton.setImageDrawable(defaultTabFourButtonIcon);
                    actionButton.setOnClickListener(tabFourOnClickListener);
                    break;
                case 4:
                    actionButton.setImageDrawable(defaultTabFiveButtonIcon);
                    actionButton.setOnClickListener(tabFiveOnClickListener);
                    break;

            }

            selectedTabLayout.animate().x(backgroundX).setDuration(100);
        }
    }


    private float getX(int position, List<Integer> sizesList) {
        if (!sizesList.isEmpty()) {
            float tabWidth = sizesList.get(0);

            float numberOfMargins = 2 * numberOfTabs;
            float itemsWidth = (int) (numberOfTabs * tabWidth);
            float marginsWidth = sizesList.get(1) - itemsWidth;

            float margin = marginsWidth / numberOfMargins;

            float half = 42;


            float x = 0;
            switch (position) {
                case 0:
                    x = tabWidth / 2 + margin - half;
                    break;
                case 1:
                    x = tabWidth * 3 / 2 + 3 * margin - half;
                    break;
                case 2:
                    x = tabWidth * 5 / 2 + 5 * margin - half;
                    break;
                case 3:
                    x = tabWidth * 7 / 2 + 7 * margin - half;
                    break;
                case 4:
                    x = tabWidth * 9 / 2 + 9 * margin - half;
                    break;
            }
            return x;
        }
        return 0;
    }

    private void setCurrentPosition(int currentPosition) {

    }

    public int getCurrentPosition() {
        return currentPosition;
    }


    @Override
    public void setOnClickListener(OnClickListener l) {
        setTabOneOnClickListener(l);
        setTabTwoOnClickListener(l);
        setTabThreeOnClickListener(l);
        setTabFourOnClickListener(l);
        setTabFiveOnClickListener(l);
    }


    /***********************************************************************************************
     * getters and setters for the OnClickListeners of each tab position
     **********************************************************************************************/
    public OnClickListener getTabOneOnClickListener() {
        return tabOneOnClickListener;
    }

    public OnClickListener getTabTwoOnClickListener() {
        return tabTwoOnClickListener;
    }

    public OnClickListener getTabThreeOnClickListener() {
        return tabThreeOnClickListener;
    }

    public OnClickListener getTabFourOnClickListener() {
        return tabFourOnClickListener;
    }

    public OnClickListener getTabFiveOnClickListener() {
        return tabFiveOnClickListener;
    }

    public void setTabOneOnClickListener(OnClickListener l) {
        tabOneOnClickListener = l;
    }

    public void setTabTwoOnClickListener(OnClickListener l) {
        tabTwoOnClickListener = l;
    }

    public void setTabThreeOnClickListener(OnClickListener l) {
        tabThreeOnClickListener = l;
    }

    public void setTabFourOnClickListener(OnClickListener l) {
        tabFourOnClickListener = l;
    }

    public void setTabFiveOnClickListener(OnClickListener l)
    {
        tabFiveOnClickListener = l;
    }

    /***********************************************************************************************
     * Tab and Views getters, setters and customization for them
     **********************************************************************************************/
    public View getTabLayout() {
        return parentLayout;
    }

    public void setTabColor(@ColorInt int backgroundColor) {
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP);
        Drawable image = ContextCompat.getDrawable(getContext(), R.drawable.tab_background);
        Drawable image2 = ContextCompat.getDrawable(getContext(), R.drawable.tab_background2);
        image.setColorFilter(porterDuffColorFilter);
        image2.setColorFilter(porterDuffColorFilter);

        backgroundImage.setImageDrawable(image);
        backgroundImage2.setImageDrawable(image2);
    }

    public FloatingActionButton getButton() {
        return actionButton;
    }

    public void setButtonColor(@ColorInt int backgroundColor) {
        actionButton.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
    }


    public View getTabOneView() {
        return tabOne.getCustomView();
    }

    public View getTabTwoView() {
        return tabTwo.getCustomView();
    }

    public View getTabThreeView() {
        return tabThree.getCustomView();
    }

    public View getTabFourView() {
        return tabFour.getCustomView();
    }

    public View getTabFiveView() {
        return tabFive.getCustomView();
    }


    public void setTabOneView(View tabOneView) {
        tabOne.setCustomView(tabOneView);
    }

    public void setTabOneView(@LayoutRes int tabOneLayout) {
        tabOne.setCustomView(tabOneLayout);
    }

    public void setTabTwoView(View tabTwoView) {
        tabTwo.setCustomView(tabTwoView);
    }

    public void setTabTwoView(@LayoutRes int tabTwoLayout) {
        tabOne.setCustomView(tabTwoLayout);
    }

    public void setTabThreeView(View tabThreeView) {
        tabThree.setCustomView(tabThreeView);
    }

    public void setTabThreeView(@LayoutRes int tabThreeLayout) {
        tabThree.setCustomView(tabThreeLayout);
    }

    public void setTabFourView(View tabFourView) {
       tabFour.setCustomView(tabFourView);

    }

    public void setTabFourView(@LayoutRes int tabFourLayout) {
        tabFour.setCustomView(tabFourLayout);
    }

    public void setTabFiveView(View tabFiveView) {
        tabFour.setCustomView(tabFiveView);
    }

    public void setTabFiveView(@LayoutRes int tabFiveLayout) {
       tabFour.setCustomView(tabFiveLayout);
    }


    public void setTabOneIcon(@DrawableRes int tabOneIcon) {
        defaultTabOneButtonIcon = getContext().getResources().getDrawable(tabOneIcon);
        tabOneImageView.setImageResource(tabOneIcon);
    }

    public void setTabOneIcon(Drawable tabOneIcon) {
        defaultTabOneButtonIcon = tabOneIcon;
        tabOneImageView.setImageDrawable(tabOneIcon);
    }

    public void setTabTwoIcon(@DrawableRes int tabTwoIcon) {
        defaultTabTwoButtonIcon = getContext().getResources().getDrawable(tabTwoIcon);
        tabTwoImageView.setImageResource(tabTwoIcon);
    }

    public void setTabTwoIcon(Drawable tabTwoIcon) {
        defaultTabTwoButtonIcon = tabTwoIcon;
        tabTwoImageView.setImageDrawable(tabTwoIcon);
    }

    public void setTabThreeIcon(@DrawableRes int tabThreeIcon) {
        defaultTabThreeButtonIcon = getContext().getResources().getDrawable(tabThreeIcon);
        tabThreeImageView.setImageResource(tabThreeIcon);
    }

    public void setTabThreeIcon(Drawable tabThreeIcon) {
        defaultTabThreeButtonIcon = tabThreeIcon;
        tabThreeImageView.setImageDrawable(tabThreeIcon);
    }

    public void setTabFourIcon(@DrawableRes int tabFourIcon) {
            defaultTabFourButtonIcon = getContext().getResources().getDrawable(tabFourIcon);
            tabFourImageView.setImageResource(tabFourIcon);
    }

    public void setTabFourIcon(Drawable tabFourIcon) {
        defaultTabFourButtonIcon = tabFourIcon;
            tabFourImageView.setImageDrawable(tabFourIcon);
    }

    public void setTabFiveIcon(@DrawableRes int tabFiveIcon) {
        defaultTabFiveButtonIcon = getContext().getResources().getDrawable(tabFiveIcon);
            tabFiveImageView.setImageResource(tabFiveIcon);
    }

    public void setTabFiveIcon(Drawable tabFiveIcon) {
        defaultTabFiveButtonIcon = tabFiveIcon;
        tabFiveImageView.setImageDrawable(tabFiveIcon);
    }

}
