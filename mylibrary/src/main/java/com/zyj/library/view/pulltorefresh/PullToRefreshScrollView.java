package com.zyj.library.view.pulltorefresh;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.zyj.library.R;


/**
 * 封装了ScrollView的下拉刷新
 *
 * @author Li Hong
 * @since 2013-8-22
 */
public abstract class PullToRefreshScrollView extends PullToRefreshBase<ScrollView> {

    View content;
    ScrollView scrollView;
	/**
	 * 构造方法
	 *
	 * @param context
	 *            context
	 */
	public PullToRefreshScrollView(Context context) {
		// this(context, null);
		super(context);
	}

	/**
	 * 构造方法
	 *
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 */
	public PullToRefreshScrollView(Context context, AttributeSet attrs) {
		// this(context, attrs, 0);
		super(context, attrs);
	}

	/**
	 * 构造方法
	 *
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 * @param defStyle
	 *            defStyle
	 */
	public PullToRefreshScrollView(Context context, AttributeSet attrs,
                                   int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 *
	 */
	@Override
	protected ScrollView createRefreshableView(Context context,
                                               AttributeSet attrs) {

        content = createContentView();
        scrollView = content.findViewById(R.id.root_view);
		return scrollView;
	}

	protected abstract View createContentView();

    public View getContent() {
        return content;
    }

    /**
	 */
	@Override
	protected boolean isReadyForPullDown() {
		return mRefreshableView.getScrollY() == 0;
	}

	/**
	 */
	@Override
	protected boolean isReadyForPullUp() {
		View scrollViewChild = mRefreshableView.getChildAt(0);
		if (null != scrollViewChild) {
			return mRefreshableView.getScrollY() >= (scrollViewChild
					.getHeight() - getHeight());
		}

		return false;
	}

	@Override
	protected LoadingLayout createHeaderLoadingLayout(Context context,
                                                      AttributeSet attrs) {
		return new RotateLoadingLayout(context);
	}

}
