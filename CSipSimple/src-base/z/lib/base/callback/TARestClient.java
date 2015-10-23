package z.lib.base.callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import z.lib.base.callback.RestClient.RequestMethod;
import android.os.AsyncTask;
import android.util.Log;

public class TARestClient {

	public static final int TIME_OUT = 10 * 1000;
	public static final int BUFFER = 1024 * 2;
	private ArrayList<NameValuePair> params;
	private ArrayList<NameValuePair> headers;

	private String url;

	private int responseCode;
	private String message;

	private String response;

	public String getResponse() {
		return response;
	}

	public String getErrorMessage() {
		return message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public TARestClient(String url) {
		this.url = url;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
		addHeader("Content-Type", "application/json; charset=utf-8");
		addHeader("Accept", "application/json; charset=utf-8");

	}

	public void addParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public void addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	public void execute(final RequestMethod method, final TACallBack callBack) {
		callBack.onStart();
		new AsyncTask<String, String, String>() {
			public String doInBackground(String[] params) {
				xexecute(method);
				return null;
			};

			protected void onPostExecute(String result) {
				callBack.onSuccess(getResponseCode() + "", getErrorMessage(), getResponse());
			};
		}.execute("");
	}

	private void xexecute(RequestMethod method) {
		try {
			switch (method) {
			case GET: {
				// add parameters
				String combinedParams = "";
				if (!params.isEmpty()) {
					combinedParams += "?";
					for (NameValuePair p : params) {
						String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
						if (combinedParams.length() > 1) {
							combinedParams += "&" + paramString;
						} else {
							combinedParams += paramString;
						}
					}
				}

				HttpGet request = new HttpGet(url + combinedParams);

				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}

				this.executeRequest(request, url);
				break;
			}
			case POST: {
				HttpPost request = new HttpPost(url);

				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());

				}

				if (!params.isEmpty()) {
					// UrlEncodedFormEntity urlEncodedFormEntity = new
					// UrlEncodedFormEntity(params, HTTP.UTF_8);
					// request.setEntity(urlEncodedFormEntity);
				}
				try {
					JSONObject params_post = new JSONObject();
					JSONObject temp = new JSONObject();
					for (NameValuePair p : params) {
						temp.put(p.getName(), p.getValue());
					}
					params_post.put("req", temp);
					request.setHeader("json", params_post.toString());
					StringEntity se = new StringEntity(params_post.toString());
					se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
					request.setEntity(se);
				} catch (Exception exception) {

				}

				this.executeRequest(request, url);
				break;
			}
			case PUT: {
				HttpPut request = new HttpPut(url);
				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}
				if (!params.isEmpty()) {
					request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				}
				this.executeRequest(request, url);
				break;

			}
			case DELETE: {
				HttpDelete request = new HttpDelete(url);
				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}
				this.executeRequest(request, url);
				break;

			}
			}
		} catch (Exception exception) {
		}
	}

	private void executeRequest(HttpUriRequest request, String url) {

		int timeout = TIME_OUT;
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
		HttpConnectionParams.setSoTimeout(httpParameters, timeout);

		HttpClient client = new DefaultHttpClient(httpParameters);

		HttpResponse httpResponse;

		try {
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				response = EntityUtils.toString(entity);
			}

		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
		}
	}

	public void executeUploadFile() throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpPut request = new HttpPut(url);
		MultipartEntity partEntity = new MultipartEntity();

		HttpResponse httpResponse;
		for (NameValuePair h : headers) {
			request.addHeader(h.getName(), h.getValue());
		}

		for (NameValuePair p : params) {
			if (p.getName().equals("user[avatar]")) {
				partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()), "image/jpeg"));
			} else if (p.getName().equals("file")) {
				partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()), "image/jpeg"));
			} else if (p.getName().equals("user[cover]")) {
				partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()), "image/jpeg"));
			} else if (p.getName().equals("post[url]")) {
				partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()), "image/jpeg"));
			} else {
				partEntity.addPart(p.getName(), new StringBody(p.getValue()));
			}
		}

		request.setEntity(partEntity);
		try {

			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				response = EntityUtils.toString(entity);
			}

		} catch (ClientProtocolException e) {
			Log.e("ERRRRO", "x", e);
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
			Log.e("ERRRRO", "x", e);
		}

	}

	public void executeUploadFile(boolean isPost) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpEntityEnclosingRequestBase request = new HttpPut(url);

		if (isPost) {
			request = new HttpPost(url);
		}
		MultipartEntity partEntity = new MultipartEntity();

		HttpResponse httpResponse;
		for (NameValuePair h : headers) {
			request.addHeader(h.getName(), h.getValue());
		}

		for (NameValuePair p : params) {
			if (p.getName().equals("user[avatar]")) {
				partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()), "image/jpeg"));
			} else if (p.getName().equals("file")) {
				partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()), "image/jpeg"));
			} else if (p.getName().equals("image")) {
				partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()), "image/jpeg"));
			} else if (p.getName().equals("user[cover]")) {
				partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()), "image/jpeg"));
			} else if (p.getName().equals("post[url]")) {
				partEntity.addPart(p.getName(), new FileBody(new File(p.getValue()), "image/jpeg"));
			} else {
				partEntity.addPart(p.getName(), new StringBody(p.getValue()));
			}
		}

		request.setEntity(partEntity);
		try {

			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				response = EntityUtils.toString(entity);
			}

		} catch (ClientProtocolException e) {
			Log.e("ERRRRO", "x", e);
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
			Log.e("ERRRRO", "x", e);
		}

	}

	public void exeDownloadFile(RequestInfo requestInfo, final IDownloadUploadFileCallBack downloadUploadFileCallBack) {

		if (downloadUploadFileCallBack != null) {
			downloadUploadFileCallBack.start();
		}

		File mDirectory = new File(requestInfo.getFileFolderSaveFile());

		if (!mDirectory.exists()) {
			mDirectory.mkdirs();
		}

		if (!mDirectory.exists()) {
			if (downloadUploadFileCallBack != null) {
				downloadUploadFileCallBack.error(IDownloadUploadFileCallBack.STATUS_CREATE_FILE_FOLDER_FAIL);
			}

			return;
		}

		// create file
		File mFile = new File(mDirectory, requestInfo.getFileNameSave());

		HttpURLConnection urlConnection = null;
		FileOutputStream fileOutput = null;
		long total = 0;
		long fileSize = 0;
		try {
			URL tmp = new URL(requestInfo.getUrl());
			urlConnection = (HttpURLConnection) tmp.openConnection();
			urlConnection.setDoInput(true);
			urlConnection.setRequestMethod("GET");
			urlConnection.setReadTimeout(TIME_OUT);
			urlConnection.setConnectTimeout(TIME_OUT);
			urlConnection.connect();

			String typeData = urlConnection.getHeaderField("content-type");
			if (typeData.contains("text/plain")) {

			} else {
				fileSize = Long.parseLong(urlConnection.getHeaderField("content-length"));
				String getDateModifier = urlConnection.getHeaderField("last-modified");

				if (downloadUploadFileCallBack != null) {

					downloadUploadFileCallBack.onProcess(fileSize, total);
				}

				fileOutput = new FileOutputStream(mFile);

				InputStream inputStream = urlConnection.getInputStream();

				byte[] buffer = new byte[BUFFER];
				int bufferLength = 0;

				while ((bufferLength = inputStream.read(buffer)) > 0) {
					total += bufferLength;
					fileOutput.write(buffer, 0, bufferLength);

					if (downloadUploadFileCallBack != null) {
						downloadUploadFileCallBack.onProcess(fileSize, total);
					}
				}

				fileOutput.close();
			}
		} catch (Exception e) {
		} finally {
			try {
				urlConnection.disconnect();
			} catch (Exception e2) {
			}
		}

		if (fileSize == 0 || mFile.length() < fileSize && fileSize != 0) {
			mFile.delete();
			if (downloadUploadFileCallBack != null) {
				downloadUploadFileCallBack.error(IDownloadUploadFileCallBack.STATUS_DOWNLOAD_UPLOAD_FAIL);
			}
		} else {
			if (downloadUploadFileCallBack != null) {
				downloadUploadFileCallBack.sucess();
			}
		}
	}

	public interface IDownloadUploadFileCallBack {
		public static final int STATUS_CREATE_FILE_FOLDER_FAIL = 1;
		public static final int STATUS_SUCESS = 2;
		public static final int STATUS_DOWNLOAD_UPLOAD_FAIL = 3;

		/**
		 * 
		 */
		public void start();

		/**
		 * @param code
		 */
		public void error(int code);

		public void sucess();

		/**
		 * 
		 * @param total
		 * @param curent
		 */
		public void onProcess(long total, long curent);

	}
}

// ============================================================
// RequestInfo
