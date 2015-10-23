package com.shopivr.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import z.lib.base.CommonAndroid;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.shopivrtablet.R;

public class TADownloadManager {
	private Context context;
	private static final int NOTIFICATION_ID = 1030 * 10 + 4;
	private NotificationCompat.Builder mBuilder;

	public TADownloadManager(Context context) {
		this.context = context;
		mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setContentTitle(context.getString(R.string.apk_download)).setContentText(context.getString(R.string.download_in_progress)).setSmallIcon(R.drawable.icon);
		mBuilder.setAutoCancel(false);
		mBuilder.setOngoing(true);
		// mBuilder.getNotification().flags |= Notification.FLAG_NO_CLEAR;
	}

	public boolean isDownloadApk() {
		return isDownloadApk;
	}

	public void setDownloadApk(boolean isDownloadApk) {
		this.isDownloadApk = isDownloadApk;
	}

	public void downloadApk(final String xurl) {
		final String PATH = "/mnt/sdcard/Download/";
		final String PATHFILE = "/mnt/sdcard/Download/update.apk";
		showNotification(0, 100);
		CommonAndroid.toast(context, context.getString(R.string.update_start_download_apk));
		new AsyncTask<String, String, String>() {
			@Override
			protected String doInBackground(String... params) {
				isDownloadApk = true;
				String error = "";
				try {
					URL url = new URL(xurl);
					HttpURLConnection c = (HttpURLConnection) url.openConnection();
					c.setRequestMethod("GET");
					c.setDoOutput(true);
					c.connect();

					File file = new File(PATH);
					file.mkdirs();

					File outputFile = new File(file, "update.apk");
					if (outputFile.exists()) {
						outputFile.delete();
					}

					FileOutputStream fos = new FileOutputStream(outputFile);
					InputStream is = c.getInputStream();
					byte[] buffer = new byte[1024];
					int len1 = 0;
					int current = 0;
					int max = is.available();
					showNotification(current, max);
					while ((len1 = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len1);
						current = current + len1;

						showNotification(current / 1024, max);
					}

					fos.close();
					is.close();
				} catch (Exception e) {
					error = e.getMessage();
				}
				isDownloadApk = false;
				return error;
			}

			protected void onPostExecute(String result) {
				if (CommonAndroid.isBlank(result)) {
					File xfile = new File(PATHFILE);
					if (xfile.exists()) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.fromFile(new File(PATHFILE)), "application/vnd.android.package-archive");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
					}
				} else {
					CommonAndroid.toast(context, context.getString(R.string.downloadapkerror));
				}

				showNotification(0, 0);
			};
		}.execute("");
	}

	private boolean isDownloadApk = false;

	private NotificationManager getNotifyManager() {
		NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		return mNotifyManager;
	}

	private void showNotification(int start, int max) {
		// LogUtils.e("showNotification", String.format("%s %s", start, max));
		if (start == max) {
			getNotifyManager().cancel(NOTIFICATION_ID);
			return;
		} else {
			mBuilder.setProgress(max, start, false);
			getNotifyManager().notify(NOTIFICATION_ID, mBuilder.build());
		}
	}
}