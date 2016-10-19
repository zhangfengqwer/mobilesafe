package com.zf.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.zf.mobilesafe.R;
import com.zf.mobilesafe.utils.StreamUtil;

public class SplashActivity extends Activity {

	private static final int CODE_UPDATE = 0;
	private static final int CODE_ENTER_HOME = 1;
	private static final int CODE_URL = 2;
	protected static final int CODE_IOE = 3;

	private SharedPreferences sp;
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
				Toast.makeText(SplashActivity.this, "URL异常", Toast.LENGTH_SHORT)
						.show();
				enterHome();
				break;
			case CODE_IOE:
				Toast.makeText(SplashActivity.this, "网络异常", Toast.LENGTH_SHORT)
						.show();
				enterHome();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		copyDB("address.db");
		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText("版本号：" + getVersionName());
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean boolean1 = sp.getBoolean("ischecked", false);
		if (boolean1) {
			checkUpdate();
		} else {
			handler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);
		}
	}

	// 拷贝数据库至data文件夹中
	private void copyDB(String DBName) {
		File file = new File(getFilesDir(), DBName);
		InputStream in = null;
		FileOutputStream out = null;
		try {
			in = getAssets().open(DBName);
			out = new FileOutputStream(file);
			int len = 0;
			byte[] b = new byte[1024];
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

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
				enterHome();
			}
		});
		builder.show();

	}

	private void enterHome() {
		startActivity(new Intent(SplashActivity.this, HomeActivity.class));
		finish();
	}

	private void checkUpdate() {
		final Message msg = handler.obtainMessage();
		final long startTime = System.currentTimeMillis();
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
						// System.out.println("versionName" + mVersionName
						// + ",description:" + mDesc);
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
					long endTime = System.currentTimeMillis();
					long usedTime = endTime - startTime;
					if (usedTime < 2000) {
						try {
							Thread.sleep(2000 - usedTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
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

	protected void download() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			String target = Environment.getExternalStorageDirectory()
					+ "/update.apk";
			// XUtils
			HttpUtils utils = new HttpUtils();
			utils.download(mDownURL, target, new RequestCallBack<File>() {

				// 下载文件的进度
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					super.onLoading(total, current, isUploading);
					System.out.println("下载进度:" + current + "/" + total);
				}

				// 下载成功
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					System.out.println("下载成功");
					// 跳转到系统下载页面
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setDataAndType(Uri.fromFile(arg0.result),
							"application/vnd.android.package-archive");
					// startActivity(intent);
					startActivityForResult(intent, 0);// 如果用户取消安装的话,
														// 会返回结果,回调方法onActivityResult
				}

				// 下载失败
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Toast.makeText(SplashActivity.this, "下载失败!",
							Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			Toast.makeText(SplashActivity.this, "没有找到sdcard!",
					Toast.LENGTH_SHORT).show();
		}
	}

	// 如果用户取消安装的话,回调此方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
