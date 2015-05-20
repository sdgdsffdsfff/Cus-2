package com.suning.cus.extras;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 用于ScrollView与ListView合用，解决计算ListView里面每个Item的高度
 * Created by 14110105 on 2015/4/28.
 */
public class ExtraListView extends ListView {

    public ExtraListView(Context context) {
        super(context);
    }

    public ExtraListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtraListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
