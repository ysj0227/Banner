package com.example.bannertest;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import android.app.Application;
import android.os.Environment;

public class MyApplication extends Application {
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		long maxMemory = Runtime.getRuntime().maxMemory();
		File cacheDir = new File(
				Environment
						.getExternalStorageDirectory()
						+ "/banner/cache/");
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc().build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.imageDownloader(
						new BaseImageDownloader(getApplicationContext(),
								10 * 1000, 15 * 1000))
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCacheSize((int) (maxMemory / 8))
				.memoryCache(new LruMemoryCache((int) (maxMemory / 8)))
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.discCacheSize(5 * 1024 * 1024).discCacheFileCount(100)
				.discCache(new LimitedAgeDiscCache(cacheDir, 1024 * 1024 * 20))
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}
}
