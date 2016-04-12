package com.example.bannertest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressLint("ClickableViewAccessibility")
public class BannerViewPager<T extends BannerBaseData> extends ViewPager {
	private LinearLayout mBannerDotLayout;// 图片点的父布局
	private List<T> mBannerData;// 轮播banner的数据
	public static final int BANNER_TIME = 5;// banner切换时间，单位秒
	public static final int SLIDE_DISTANCE = 30;// viewpager切换滑动距离
	private List<View> mDots; // 图片的点
	private int currentItem = 1; // 当前图片的索引号
	private int mOldPosition = 0;
	private DisplayImageOptions options;
	private boolean isFirst = true;// 防止定时切换，一开始就切换
	private long releaseTime;// banner切换的时刻
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ScheduledExecutorService scheduledExecutorService;
	private List<ImageView> mImageViews;// 滑动的图片集合
	private BannerAdapter mBannerAdapter;
	private int mLastX = 0;// 获得当前X坐标

	public BannerViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		initImageLoader(context);
	}

	@SuppressWarnings("deprecation")
	private void initImageLoader(Context context) {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 设置点的布局
	 * 
	 * @param view
	 */
	public void setBannerDotLayout(View view) {
		mBannerDotLayout = (LinearLayout) view;
	}

	/**
	 * 设置banner数据&&添加viewpager页面点击事件
	 * 
	 * @param data
	 * @param onClickListener
	 */
	@SuppressWarnings("deprecation")
	public void setBannerData(List<T> data, OnClickListener onClickListener) {
		mBannerData = data;
		mImageViews = new ArrayList<ImageView>();
		mBannerAdapter = new BannerAdapter(mImageViews, onClickListener);
		this.setOnPageChangeListener(new MyPageChangeListener());
		this.setOnTouchListener(mOnTouchListener);
		this.setAdapter(mBannerAdapter);
		initBanner();
		startBanner();
	}
	@SuppressLint("InflateParams")
	private void initBanner() {

		// 当banner个数大于1个时
		if (mBannerData.size() > 1) {
			mBannerData.add(mBannerData.get(0));
			mBannerData.add(0, mBannerData.get(mBannerData.size() - 2));
		}
		addDynamicView();

		if (mBannerData.size() > 1) {
			mDots = new ArrayList<View>();

			if (mBannerDotLayout != null) {
				// 添加点
				for (int i = 0; i < mBannerData.size() - 2; i++) {
					View dot = LayoutInflater.from(getContext()).inflate(
							R.layout.banner_dot, null);
					mDots.add(dot);
					mBannerDotLayout.addView(dot);

				}
				mOldPosition = currentItem - 1;
				mDots.get(mOldPosition).findViewById(R.id.dot)
						.setBackgroundResource(R.drawable.dot_focused);
			}
			this.setCurrentItem(currentItem);
		}

	}

	private void addDynamicView() {

		// 动态添加图片和下面指示的圆点
		// 初始化图片资源
		for (int i = 0; i < mBannerData.size(); i++) {
			ImageView mImageView = new ImageView(getContext());
			imageLoader.displayImage(mBannerData.get(i).getImage(), mImageView,
					options);
			mImageViews.add(mImageView);
		}
		mBannerAdapter.notifyDataSetChanged();
	}

	private void startBanner() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当Activity显示出来后，每5秒切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1,
				BANNER_TIME, TimeUnit.SECONDS);
	}

	private class ScrollTask implements Runnable {

		@Override
		public void run() {

			// 防止一开始就切换
			if (isFirst) {
				isFirst = false;
				return;
			} else {
				if (mImageViews.size() > 1) {
					Long now = System.currentTimeMillis();
					if (now - releaseTime < BANNER_TIME * 1000 - 500) {
						return;
					}
					currentItem = (++currentItem) % (mImageViews.size());

					mHandler.sendEmptyMessage(0);
				}
			}

		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (mImageViews.size() > 0)
				BannerViewPager.this.setCurrentItem(currentItem, true);

		}
	};

	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:

				mLastX = (int) event.getX();

				break;

			case MotionEvent.ACTION_UP:
				// 向右滑动
				if (mLastX - event.getX() > SLIDE_DISTANCE) {

					currentItem = ++currentItem % mBannerData.size();
					BannerViewPager.this.setCurrentItem(currentItem);
					return true;
				} else {
					// 向左滑
					if (mLastX - event.getX() < -SLIDE_DISTANCE) {
						currentItem--;
						if (currentItem < 0) {
							currentItem = mBannerData.size() - 1;
							BannerViewPager.this.setCurrentItem(currentItem);
							return true;
						}
					}
				}

			}
			return false;
		}

	};

	private class MyPageChangeListener implements OnPageChangeListener {

		private boolean mMark = false;// 标识是否是第一张或最后一张

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			releaseTime = System.currentTimeMillis();
			// 当图片是最后一页切到第一页时或者第一页切到最后一页
			if ((currentItem == 1 || currentItem == mBannerData.size() - 2)
					&& mMark && arg1 == 0) {
				BannerViewPager.this.setCurrentItem(currentItem, false);
				mMark = false;
			}

		}

		@Override
		public void onPageSelected(int position) {
			if (mDots == null) {
				return;
			}
			// 最后一页
			if (position == mBannerData.size() - 1) {
				position = 1;
				mMark = true;
			} else {
				// 第一页
				if (position == 0) {
					position = mBannerData.size() - 2;
					mMark = true;
				}
			}

			currentItem = position;
			releaseTime = System.currentTimeMillis();
			if (mBannerDotLayout != null) {
				mDots.get(mOldPosition).findViewById(R.id.dot)
						.setBackgroundResource(R.drawable.dot_normal);
				mDots.get(position - 1).findViewById(R.id.dot)
						.setBackgroundResource(R.drawable.dot_focused);
			}
			mOldPosition = position - 1;

		}
	}

	/**
	 * 开启定时切换
	 */
	public void startScheduled() {
		if (scheduledExecutorService.isShutdown()) {
			isFirst = true;
			startBanner();
		}
	}

	/**
	 * 关闭定时切换
	 */
	public void stopScheduled() {
		if (scheduledExecutorService != null) {
			scheduledExecutorService.shutdown();
		}
	}
}
