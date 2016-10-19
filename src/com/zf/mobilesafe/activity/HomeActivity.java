package com.zf.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zf.mobilesafe.R;

public class HomeActivity extends Activity {
	private GridView gv;
	private String[] mItems = new String[] { "手机防盗", "通讯卫士", "软件管理", "进程管理",
			"流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };

	private int[] mPics = new int[] { R.drawable.home_safe,
			R.drawable.home_callmsgsafe, R.drawable.home_apps,
			R.drawable.home_taskmanager, R.drawable.home_netmanager,
			R.drawable.home_trojan, R.drawable.home_sysoptimize,
			R.drawable.home_tools, R.drawable.home_settings };
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gv = (GridView) findViewById(R.id.gv);
		gv.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = View.inflate(HomeActivity.this, R.layout.home_item,
						null);
				ImageView ivItem = (ImageView) view.findViewById(R.id.iv_item);
				TextView tvItem = (TextView) view.findViewById(R.id.tv_item);
				ivItem.setImageResource(mPics[position]);
				tvItem.setText(mItems[position]);
				return view;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return mItems[position];
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mItems.length;
			}
		});
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					showDialog();
					break;
				case 7:
					startActivity(new Intent(HomeActivity.this,
							AdToolsActivity.class));
				case 8:
					startActivity(new Intent(HomeActivity.this,
							SettingActivity.class));
					break;

				}
			}
		});
	}

	private void showDialog() {
		String password = sp.getString("password", "");
		if (!TextUtils.isEmpty(password)) {
			showPasswordInput(password);
		}else{
			showPasswordSet();		}
	}

	private void showPasswordInput(final String password) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_input_pass, null);
//		dialog.setView(view);
		dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题
		final EditText etPassword = (EditText) view
				.findViewById(R.id.et_password);
		Button btnOK = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		
		btnOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String pass = etPassword.getText().toString();
				if(password.equals(pass)){
					dialog.dismiss();
					startActivity(new Intent(HomeActivity.this,
							LostFindActivity.class));
				}else {
					Toast.makeText(HomeActivity.this, "输入的密码错误", 0).show();
				}
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void showPasswordSet() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_set_pass, null);
		// dialog.setView(view);
		dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题
		final EditText etPassword = (EditText) view
				.findViewById(R.id.et_password);
		final EditText etPasswordConfirm = (EditText) view
				.findViewById(R.id.et_password_confirm);

		Button btnOK = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pass = etPassword.getText().toString();
				String confirmPass = etPasswordConfirm.getText().toString();
				if (!TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirmPass)) {
					if (pass.equals(confirmPass)) {
						sp.edit().putString("password", pass).commit();
						dialog.dismiss();
						startActivity(new Intent(HomeActivity.this,
								LostFindActivity.class));
					} else {
						Toast.makeText(HomeActivity.this, "两次密码不一致", 0).show();
					}
				} else {
					Toast.makeText(HomeActivity.this, "输入的内容不能为空", 0).show();
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();

	}
}
