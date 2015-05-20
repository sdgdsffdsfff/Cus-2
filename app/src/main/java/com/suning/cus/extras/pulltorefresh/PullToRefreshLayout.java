package com.suning.cus.extras.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class PullToRefreshLayout extends PullToRefreshBase<LinearLayout> {

	private LinearLayout mLinearLayout;
	
	public PullToRefreshLayout(Context context) {
		super(context);
		
	}

	@Override
	protected LinearLayout createRefreshableView(Context context,
			AttributeSet attrs) {
		LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mLinearLayout = new LinearLayout(context);
		return mLinearLayout;
	}

	@Override
	protected boolean isReadyForPullDown() {
		
		return true;
	}

	@Override
	protected boolean isReadyForPullUp() {
		// TODO Auto-generated method stub
		return true;
	}

    @Override
    protected LoadingLayout createHeaderLoadingLayout(Context context, AttributeSet attrs) {
        return new RotateLoadingLayout(context);
    }
}
