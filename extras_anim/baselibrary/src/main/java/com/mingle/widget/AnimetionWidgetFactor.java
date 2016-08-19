package com.mingle.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by zzz40500 on 15/9/5.
 */
public class AnimetionWidgetFactor {


    private  static AnimetionWidgetFactor instant;

    public static AnimetionWidgetFactor getInstant() {

        if(instant == null){
            instant=new AnimetionWidgetFactor();
        }

        return instant;
    }

    private WidgetParser mWidgetParser;

    public android.view.View parseWidget(String name, Context context, AttributeSet attrs){

        if(mWidgetParser != null){
            return mWidgetParser.parseWidget(name,context,attrs);
        }
        return  null;
    }


    public void setWidgetParser(WidgetParser widgetParser) {
        mWidgetParser = widgetParser;
    }

    public interface WidgetParser {
        android.view.View  parseWidget(String name, Context context, AttributeSet attrs);
    }
}
