package com.example.bannertest;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends Activity {

	private BannerViewPager<BannerBaseData> viewpager;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewpager=(BannerViewPager<BannerBaseData>) findViewById(R.id.viewpager);
		viewpager.setBannerDotLayout(findViewById(R.id.banner_dot));
		viewpager.setBannerData(getBannerAd(),null);

		Toast.makeText(MainActivity.this, "aaa", Toast.LENGTH_SHORT).show();

	}

	private  List<BannerBaseData> getBannerAd() {
		List<BannerBaseData> data=new ArrayList<BannerBaseData>();
		BannerBaseData object=new BannerBaseData();
		object.setImage("http://a.hiphotos.baidu.com/image/h%3D200/sign=dd48609bd6c8a786a12a4d0e5708c9c7/a50f4bfbfbedab64fb188da1f036afc379311e6d.jpg");
		data.add(object);
		object=new BannerBaseData();
		object.setImage("http://www.pocc.cc/news/2013/08/137783100215.jpg");
		data.add(object);
		object=new BannerBaseData();
		object.setImage("http://a0.att.hudong.com/15/08/300218769736132194086202411_950.jpg");
		data.add(object);
		object=new BannerBaseData();
		object.setImage("http://img.61gequ.com/allimg/2011-4/201142614314278502.jpg");
		data.add(object);
		object=new BannerBaseData();
		object.setImage("http://pic.nipic.com/2008-06-02/200862134922819_2.jpg");
        data.add(object);
//    	object=new BannerBaseData();
//		object.setImage("http://pic30.nipic.com/20130618/11860366_201437262000_2.jpg");
//        data.add(object);
		return data;
	}

}
