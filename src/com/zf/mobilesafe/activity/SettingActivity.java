package com.zf.mobilesafe.activity;

import com.zf.mobilesafe.R;
import com.zf.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	private SettingItemView siv;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		siv = (SettingItemView) findViewById(R.id.siv);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean b = sp.getBoolean("ischecked", true);
		if (b) {
			siv.setCB(true);
			siv.setDesc("自动更新已开启");
		} else {
			siv.setCB(false);
			siv.setDesc("自动更新已关闭");
		}
		// siv.setDesc("自动更新已开启");
		// siv.setCB(true);
		siv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv.isChecked()) {
					siv.setCB(false);
					siv.setDesc("自动更新已关闭");
					sp.edit().putBoolean("ischecked", false).commit();
				} else {
					siv.setCB(true);
					siv.setDesc("自动更新已开启");
					sp.edit().putBoolean("ischecked", true).commit();
				}
			}
		});
	}
}
