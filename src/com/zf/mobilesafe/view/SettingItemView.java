package com.zf.mobilesafe.view;

import com.zf.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private TextView tvTitle;
	private TextView tvDesc;
	private CheckBox cbStatus;

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}
	
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		String mTitle = attrs.getAttributeValue(NAMESPACE, "title");// 根据属性名称,获取属性的值
//		String mDescOn = attrs.getAttributeValue(NAMESPACE, "desc_on");
//		String mDescOff = attrs.getAttributeValue(NAMESPACE, "desc_off");
		initView();
	}

	public SettingItemView(Context context) {
		super(context);
		initView();
	}

	public void initView(){
		View view = View.inflate(getContext(), R.layout.view_setting_item, this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		cbStatus = (CheckBox) findViewById(R.id.cb_status);
		tvTitle.setText("自动更新设置");
	}
	
	public void setDesc(String desc){
		tvDesc.setText(desc);
	}
	
	public void setCB(boolean cb){
		cbStatus.setChecked(cb);
	}
	
	public boolean isChecked(){
		return cbStatus.isChecked();
	}
}
