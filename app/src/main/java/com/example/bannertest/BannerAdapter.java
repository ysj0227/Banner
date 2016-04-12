package com.example.bannertest;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class BannerAdapter extends PagerAdapter {
	private List<ImageView> mImageViews;
	private OnClickListener mOnClickListener;

	public BannerAdapter(List<ImageView> mImageViews,
			OnClickListener onClickListener) {
		this.mImageViews = mImageViews;
		mOnClickListener = onClickListener;
	}

	@Override
	public int getCount() {
		return mImageViews.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		ImageView iv = mImageViews.get(position);

		ViewGroup parent = (ViewGroup) iv.getParent();
		if (parent != null && parent.equals(container)) {
			parent.removeAllViews();
		}
		((ViewPager) container).addView(iv);
        iv.setTag(position);
		iv.setOnClickListener(mOnClickListener);
		return iv;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);

	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

	@Override
	public void finishUpdate(View arg0) {

	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public void setmImageViews(List<ImageView> mImageViews) {
		this.mImageViews = mImageViews;
	}

}
