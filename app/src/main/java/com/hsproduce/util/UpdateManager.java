package com.hsproduce.util;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.reflect.TypeToken;
import com.hsproduce.R;
import com.hsproduce.bean.AppVersion;
import com.hsproduce.bean.Result;

/**
 * @author sunmc
 * @date 2015-11-19
 */

public class UpdateManager {
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 保存版本信息 */
	public static AppVersion appVersion;
	/* 下载保存路径 */
	private String mSavePath = "/download/";

	DownloadManager downloadManager;
	long mTaskId;

	private Context mContext;

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	/**
	 * 检测软件更新
	 */
	public boolean checkUpdate() {
		if (isUpdate()) {
			// 显示提示对话框
			showNoticeDialog();
			return true;
		}
		return false;
	}

	/**
	 * 检查软件是否有更新版本
	 * 
	 * @return
	 */
	public boolean isUpdate() {
		// 获取当前软件版本
		int 当前版本 = getVersionCode(mContext);
		// 获取服务器最新版本
		try {
			String result = HttpUtil.sendGet(PathUtil.获取最新版本, "");
			Result<AppVersion> res = new Result<>();
			res	= TaskUtil.handle(res, result, new TypeToken<Result<AppVersion>>(){}.getType());
			if (res.isFlag()) {
				appVersion = res.getData();
			}
		} catch (Exception e) {
			return false;
		}

		if (null != appVersion) {
			int 最新版本 = appVersion.get版本号();
			// 版本判断
			if (最新版本 > 当前版本) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int 当前版本 = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			当前版本 = context.getPackageManager().getPackageInfo("com.sunmc.mesterminal", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 当前版本;
	}

	/**
	 * 显示软件更新对话框
	 */
	public void showNoticeDialog() {
		final AlertDialog noticeDialog = DialogUtil.getDialog(mContext);
		noticeDialog.setContentView(R.layout.dialog_upate);
		// 显示更新的内容
		LinearLayout ll = (LinearLayout) noticeDialog.findViewById(R.id.update_content);
		if (null != appVersion) {
			String updateInfo = appVersion.get版本描述();
			if (updateInfo != null) {
				String[] updateInfos = updateInfo.split(";");
				for (String info : updateInfos) {
					TextView tv = new TextView(noticeDialog.getContext());
					tv.setText(info);
					tv.setTextSize(20);
					tv.setTextColor(Color.WHITE);
					ll.addView(tv);
				}
			}
		}

		Button bt_now = (Button) noticeDialog.findViewById(R.id.bt_update);
		Button bt_update_latter = (Button) noticeDialog.findViewById(R.id.bt_update_later);
		bt_now.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				noticeDialog.dismiss();
				// 下载文件
				downloadAPK(PathUtil.文件下载 + appVersion.get下载地址(), appVersion.get下载地址());
			}
		});
		bt_update_latter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				noticeDialog.dismiss();
			}
		});

		noticeDialog.show();
	}

	//使用系统下载器下载
	private void downloadAPK(String versionUrl, String versionName) {
		//创建下载任务
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(versionUrl));
		request.setAllowedOverRoaming(false);//漫游网络是否可以下载

		//设置文件类型，可以在下载结束后自动打开该文件
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(versionUrl));
		request.setMimeType(mimeString);

		//在通知栏中显示，默认就是显示的
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
		request.setVisibleInDownloadsUi(true);

		//sdcard的目录下的download文件夹，必须设置
		request.setDestinationInExternalPublicDir("/download/", versionName);
		//request.setDestinationInExternalFilesDir(),也可以自己制定下载路径

		//将下载请求加入下载队列
		downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
		//加入下载队列后会给该任务返回一个long型的id，
		//通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
		mTaskId = downloadManager.enqueue(request);

	}

}
