package com.zf.mobilesafe.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.zf.mobilesafe.R;
import com.zf.mobilesafe.utils.StreamUtil;

public class SplashActivity extends Activity {

	private static final int CODE_UPDATE = 0;
	private static final int CODE_ENTER_HOME = 1;
	private static final int CODE_URL = 2;
	protected static final int CODE_IOE = 3;
	private TextView tvVersion;
	private String mVersionName;
	private int mVersionCode;
	private String mDesc;
	private String mDownURL;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int i = msg.what;
			switch (i) {
			case CODE_UPDATE:
				showDialog();
				break;
			case CODE_ENTER_HOME:
				enterHome();
				break;
			case CODE_URL:
				Toast.makeText(SplashActivity.this, "URL异常", Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_IOE:
				Toast.makeText(SplashActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			}
		}
	};

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("最新版本：" + mVersionName);
		builder.setMessage(mDesc);
		builder.setPositiveButton("立即更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				System.out.println("立即更新");
				enterHome();
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				enterHome();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				enterHome();
			}
		});
		builder.show();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText("版本号：" + getVersionName());
		checkUpdate();
	}

	private void enterHome() {
		startActivity(new Intent(SplashActivity.this, HomeActivity.class));
		finish();
	}

	private void checkUpdate() {
		final Message msg = handler.obtainMessage();

		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection conn = null;
				try {
					URL url = new URL("http://192.168.0.102:8080/update.json");
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.connect();
					if (conn.getResponseCode() == 200) {
						InputStream inputStream = conn.getInputStream();
						String response = StreamUtil
								.readFromInputStream(inputStream);
						 System.out.println(response);
						JSONObject json = new JSONObject(response);
						mVersionName = json.getString("versionName");
						mVersionCode = json.getInt("verionCode");
						mDesc = json.getString("description");
						mDownURL = json.getString("downURL");
//						System.out.println("versionName" + mVersionName
//								+ ",description:" + mDesc);
						if (mVersionCode > getVersionCode()) {
							msg.what = CODE_UPDATE;
						} else {
							msg.what = CODE_ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					msg.what = CODE_URL;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					msg.what = CODE_IOE;
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					handler.sendMessage(msg);
					if (conn != null) {
						conn.disconnect();
					}
				}
			}
		}).start();

	}

	private String getVersionName() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			String versionName = packageInfo.versionName;
			// System.out.println(versionCode + versionName);
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private int getVersionCode() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			int versionCode = packageInfo.versionCode;
			// System.out.println(versionCode + versionName);
			return versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}

}
