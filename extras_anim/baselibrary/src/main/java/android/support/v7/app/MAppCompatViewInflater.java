package android.support.v7.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;

import com.mingle.baselibrary.R;
import com.mingle.widget.AnimetionButton;
import com.mingle.widget.AnimetionCheckBox;
import com.mingle.widget.AnimetionCheckedTextView;
import com.mingle.widget.AnimetionCompleteTextView;
import com.mingle.widget.AnimetionEditText;
import com.mingle.widget.AnimetionFrameLayout;
import com.mingle.widget.AnimetionImageButton;
import com.mingle.widget.AnimetionImageView;
import com.mingle.widget.MultiAutoCompleteTextView;
import com.mingle.widget.AnimetionRadioButton;
import com.mingle.widget.AnimetionRatingBar;
import com.mingle.widget.AnimetionRelativeLayout;
import com.mingle.widget.AnimetionWidgetFactor;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Created by zzz40500 on 15/8/26.
 */
public class MAppCompatViewInflater {

    static final Class<?>[] sConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};

    private static final String LOG_TAG = "AppCompatViewInflater";

    private static final Map<String, Constructor<? extends View>> sConstructorMap
            = new ArrayMap<>();

    private final Object[] mConstructorArgs = new Object[2];

    public View createView(View parent, final String name, @NonNull Context context,
                           @NonNull AttributeSet attrs, boolean inheritContext,
                           boolean readAndroidTheme, boolean readAppTheme) {
        final Context originalContext = context;

        // We can emulate Lollipop's android:theme attribute propagating down the view hierarchy
        // by using the parent's context
        if (inheritContext && parent != null) {
            context = parent.getContext();
        }
        if (readAndroidTheme || readAppTheme) {
            // We then apply the theme on the context, if specified
            context = themifyContext(context, attrs, readAndroidTheme, readAppTheme);
        }


        android.view.View  view = AnimetionWidgetFactor.getInstant().parseWidget(name,context,attrs);

        if (view != null) {
            return view;
        }
        // We need to 'inject' our tint aware Views in place of the standard framework versions
        switch (name) {
            case "EditText":
                return new AnimetionEditText(context, attrs);
            case "RadioButton":
                return new AnimetionRadioButton(context, attrs);
            case "ImageView":
                return new AnimetionImageView(context, attrs);
            case "ImageButton":
                return new AnimetionImageButton(context, attrs);
            case "CheckedTextView":
                return new AnimetionCheckedTextView(context, attrs);
            case "AutoCompleteTextView":
                return new AnimetionCompleteTextView(context, attrs);
            case "MultiAutoCompleteTextView":
                return new MultiAutoCompleteTextView(context, attrs);
            case "RatingBar":
                return new AnimetionRatingBar(context, attrs);
            case "Button":
                return new AnimetionButton(context, attrs);
            case "RelativeLayout":
                return new AnimetionRelativeLayout(context, attrs);
            case "FrameLayout":
                return new AnimetionFrameLayout(context, attrs);
        }

        if (originalContext != context) {
            // If the original context does not equal our themed context, then we need to manually
            // inflate it using the name so that android:theme takes effect.
            return createViewFromTag(context, name, attrs);
        }

        return null;
    }

    private View createViewFromTag(Context context, String name, AttributeSet attrs) {
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }

        try {
            mConstructorArgs[0] = context;
            mConstructorArgs[1] = attrs;

            if (-1 == name.indexOf('.')) {
                // try the android.widget prefix first...
                return createView(context, name, "android.widget.");
            } else {
                return createView(context, name, null);
            }
        } catch (Exception e) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null;
        } finally {
            // Don't retain references on context.
            mConstructorArgs[0] = null;
            mConstructorArgs[1] = null;
        }
    }

    private View createView(Context context, String name, String prefix)
            throws ClassNotFoundException, InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);

        try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                Class<? extends View> clazz = context.getClassLoader().loadClass(
                        prefix != null ? (prefix + name) : name).asSubclass(View.class);

                constructor = clazz.getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            }
            constructor.setAccessible(true);
            return constructor.newInstance(mConstructorArgs);
        } catch (Exception e) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null;
        }
    }

    /**
     * Allows us to emulate the {@code android:theme} attribute for devices before L.
     */
    private static Context themifyContext(Context context, AttributeSet attrs,
                                          boolean useAndroidTheme, boolean useAppTheme) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.View, 0, 0);
        int themeId = 0;
        if (useAndroidTheme) {
            // First try reading android:theme if enabled
            themeId = a.getResourceId(R.styleable.View_android_theme, 0);
        }
        if (useAppTheme && themeId == 0) {
            // ...if that didn't work, try reading app:theme (for legacy reasons) if enabled
            themeId = a.getResourceId(R.styleable.View_theme, 0);

            if (themeId != 0) {
                Log.i(LOG_TAG, "app:theme is now deprecated. "
                        + "Please move to using android:theme instead.");
            }
        }
        a.recycle();

        if (themeId != 0 && (!(context instanceof ContextThemeWrapper)
                || ((ContextThemeWrapper) context).getThemeResId() != themeId)) {
            // If the context isn't a ContextThemeWrapper, or it is but does not have
            // the same theme as we need, wrap it in a new wrapper
            context = new ContextThemeWrapper(context, themeId);
        }
        return context;
    }


}
