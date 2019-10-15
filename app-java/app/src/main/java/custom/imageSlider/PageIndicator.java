package custom.imageSlider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.ImageView;

import main.G;
import ir.naeen.yousefi.R;


public class PageIndicator extends ImageView {

    private Paint            fillPaint;
    private Paint            strokePaint;
    private int              count;
    private int              indicatorWidth;
    private static final int CIRCLE_RADIUS       = 20;
    private static final int CIRCLE_SPACE        = 22;
    private static final int CIRCLE_STROKE_COLOR = R.color.colorMain2;
    private static final int CIRCLE_FILL_COLOR   = R.color.colorMain2;
    private int              screenWidth;
    private float            offsetX;
    private int              currentPageIndex;
    private float            percent;


    public PageIndicator(Context context) {
        super(context);
        initialize();
    }


    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }


    public PageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }


    private void initialize() {
        fillPaint = new Paint();
        fillPaint.setStyle(Style.FILL);
        fillPaint.setColor(G.context.getResources().getColor(CIRCLE_FILL_COLOR));
        fillPaint.setAntiAlias(true);

        strokePaint = new Paint();
        strokePaint.setStyle(Style.STROKE);
        strokePaint.setColor(G.context.getResources().getColor(CIRCLE_STROKE_COLOR));
        strokePaint.setAntiAlias(true);

        screenWidth = G.context.getResources().getDisplayMetrics().widthPixels;
    }


    public void setIndicatorsCount(int value) {
        count = value;
        computeIndicatorWidth();
    }


    public void setCurrentPage(int value) {
        currentPageIndex = value;
        invalidate();
    }
    public int getCurrentPage() {
        return  currentPageIndex;
    }


    public void setPercent(float percent) {
        this.percent = percent;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < count; i++) {
            Paint paint = strokePaint;
            float radius = CIRCLE_RADIUS;

            boolean canDrawFill = false;
            if (i == currentPageIndex) {
                fillPaint.setAlpha((int) ((1.0f - percent) * 255));
                //radius *= 2;
                canDrawFill = true;
            }

            if (percent > 0) {
                if (i == currentPageIndex + 1) {
                    fillPaint.setAlpha((int) (percent * 255));
                    canDrawFill = true;
                }
            }
            canvas.drawCircle(offsetX + i * (CIRCLE_RADIUS + CIRCLE_SPACE), 10, radius / 1.5f, strokePaint);

            if (canDrawFill) {
                canvas.drawCircle(offsetX + i * (CIRCLE_RADIUS + CIRCLE_SPACE), 10, radius / 1.5f, fillPaint);
            }
        }
    }


    private void computeIndicatorWidth() {
        indicatorWidth = count * (CIRCLE_RADIUS + CIRCLE_SPACE);
        offsetX = (screenWidth - indicatorWidth) / 2;
    }
}
