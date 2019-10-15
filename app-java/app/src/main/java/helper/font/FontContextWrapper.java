package helper.font;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;


/**
 * Created by chris on 19/12/2013
 * Project: Calligraphy
 */
public class FontContextWrapper extends ContextWrapper {

    private FontLayoutInflater mInflater;

    private final int mAttributeId;

    public static ContextWrapper wrap(Context base) {
        return new FontContextWrapper(base);
    }


    public static View onActivityCreateView(Activity activity, View parent, View view, String name, Context context, AttributeSet attr) {
        return get(activity).onActivityCreateView(parent, view, name, context, attr);
    }


    static FontActivityFactory get(Activity activity) {
        if (!(activity.getLayoutInflater() instanceof FontLayoutInflater)) {
            throw new RuntimeException("This activity does not wrap the Base Context! See FontContextWrapper.wrap(Context)");
        }
        return (FontActivityFactory) activity.getLayoutInflater();
    }


    FontContextWrapper(Context base) {
        super(base);
        mAttributeId = FontConfig.get().getAttrId();
    }


    @Deprecated
    public FontContextWrapper(Context base, int attributeId) {
        super(base);
        mAttributeId = attributeId;
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mInflater == null) {
                mInflater = new FontLayoutInflater(LayoutInflater.from(getBaseContext()), this, mAttributeId, false);
            }
            return mInflater;
        }
        return super.getSystemService(name);
    }

}
