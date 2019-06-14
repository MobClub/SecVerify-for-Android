package com.mob.secverify.demo.net;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mob.tools.utils.Data;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by weishj on 2017/12/20.
 */

public class NetworkHelper extends AbstractHttp{
	private static final String TAG = "SecVerifyDemo" + NetworkHelper.class.getSimpleName();
	private Gson gson;

	public NetworkHelper() {
		super();
		gson = new Gson();
	}

	public NetworkHelper(int connectionTimeOut, int soTimeOut) {
		super(connectionTimeOut, soTimeOut);
		gson = new Gson();
	}

	private HttpURLConnection getHttpURLConnection(String url, HttpMethod httpMethod, Map<String, Object> params) throws Throwable {
		if(HttpMethod.GET ==httpMethod){
			if(params!=null){
				ArrayList<KVPair<String>> list = new ArrayList<KVPair<String>>();
				for(String key : params.keySet()){
					list.add(new KVPair<String>(key, String.valueOf(params.get(key))));
				}
				String paras = kvPairsToUrl(list);
				if (paras.length() > 0) {
					url += URL_AND_PARA_SEPARATOR + paras;
				}
			}
		}
		HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(url).openConnection();
		// Header
		httpURLConnection.setRequestProperty("Content-Type", "application/json");

		httpURLConnection.setConnectTimeout(connectionTimeOut);
		httpURLConnection.setReadTimeout(soTimeOut);
		httpURLConnection.setUseCaches(false);

		String json = null;
		if(HttpMethod.POST ==httpMethod){
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");
			Gson gson = new Gson();
			json = gson.toJson(params);
			if(!TextUtils.isEmpty(json))
				httpURLConnection.getOutputStream().write(json.getBytes());
		}else{
			httpURLConnection.setRequestMethod("GET");
		}
		Log.i(TAG, "=========== Http ==========");
		Log.i(TAG, "HEADER: "+ convertMaptoString(httpURLConnection.getHeaderFields()));
		Log.i(TAG, "PARAMS: " + json);
		Log.i(TAG, "HTTP METHOD: "+ httpURLConnection.getRequestMethod());
		Log.i(TAG, "URL:"+ url);
		Log.i(TAG, "=========== Http ==========");
		return httpURLConnection;
	}

	@Override
	public void asyncConnect(String url, HttpCallBack<String> httpCallBack) {
		asyncConnect(url, null, httpCallBack);
	}

	@Override
	public void asyncConnect(String url, Map<String, Object> params,
							 HttpCallBack<String> httpCallBack) {
		asyncConnect(url, params, HttpMethod.GET, httpCallBack);

	}

	@Override
	public void asyncConnect(final String url, final Map<String, Object> params,
							 final HttpMethod httpMethod, final HttpCallBack<String> httpCallBack) {
		asyncThread(new Runnable() {
			@Override
			public void run() {
				syncConnect(url, params, httpMethod, httpCallBack);
			}
		});

	}

	@Override
	public String syncConnect(String url) {
		return syncConnect(url, null);
	}

	@Override
	public String syncConnect(String url, Map<String, Object> params) {
		return syncConnect(url, params, HttpMethod.GET);
	}

	@Override
	public String syncConnect(String url, Map<String, Object> params,
							  HttpMethod httpMethod) {
		return syncConnect(url, params, httpMethod, null);
	}

	@Override
	public String syncConnect(String url, Map<String, Object> params,
							  HttpCallBack<String> httpCallBack) {
		return syncConnect(url, params, HttpMethod.GET, httpCallBack);
	}

	@Override
	public String syncConnect(String url, Map<String, Object> params,
							  HttpMethod httpMethod, HttpCallBack<String> httpCallBack) {

		if(TextUtils.isEmpty(url)){
			return null;
		}

		BufferedReader reader = null;

		HttpURLConnection httpURLConnection = null;

		int statusCode = -1;
		try {
			Log.v(TAG, url);

			if(httpCallBack!=null){
				httpCallBack.onStart(url);
			}
			httpURLConnection = getHttpURLConnection(url,httpMethod,params);
			httpURLConnection.connect();
			statusCode = httpURLConnection.getResponseCode();
			if(statusCode== HttpURLConnection.HTTP_OK){

				reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

				StringBuffer buffer = new StringBuffer();
				String line = null;

				long progress = 0;

				long count = httpURLConnection.getContentLength();
				isCancel = false;
				if(httpCallBack != null && count!=-1)
					httpCallBack.onLoading(progress, count);
				while ((!isCancel) && (line = reader.readLine())!=null) {
					buffer.append(line);

					if(httpCallBack != null && count!=-1){
						progress+= line.getBytes().length;
						httpCallBack.onLoading(progress, count);
					}
				}


				if(httpCallBack != null){
					if(!isCancel){
						progress = count;
						httpCallBack.onLoading(progress, count);
					}else{
						reader.close();
						httpCallBack.onCancel();
						return null;
					}
				}
				reader.close();

				// 正常响应示例：{"status":200,"res":{"phone":"13205558485"},"error":null}
				String str = buffer.toString();
				Log.i(TAG, "Response: "+ str);
				String res = "";
				try {
					JsonObject json = new JsonParser().parse(str).getAsJsonObject();
					JsonObject resJson = json.getAsJsonObject("res");
					res = resJson.toString();
				} catch (Throwable t) {
					Log.e(TAG, t.getMessage(), t);
				}

				if(httpCallBack != null && !isCancel) {
					httpCallBack.onSuccess(res);
				}

				if(httpURLConnection!=null) {
					httpURLConnection.disconnect();
				}
				return res;
			}else{
				reader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
				StringBuffer buffer = new StringBuffer();
				String line;
				while ((line = reader.readLine())!=null) {
					buffer.append(line);
				}
				String errResponse = buffer.toString();
				if(httpCallBack != null) {
					httpCallBack.onFailure(statusCode, new Throwable(errResponse));
				}

				Log.i(TAG, "Response: "+ errResponse);
				return errResponse;
			}
		} catch (Throwable t) {
			Log.e(TAG, t.getMessage(), t);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", statusCode);
			map.put("error", t.getMessage());
			String msg = new Gson().toJson(map);
			Throwable resp = new Throwable(msg, t);
			if(httpCallBack != null)
				httpCallBack.onFailure(statusCode, resp);
		} finally {

			if(httpURLConnection!=null){
				httpURLConnection.disconnect();
			}
		}

		return null;
	}

	@Override
	public void asyncDownloadFile(final String url, final String fileName,
								  final HttpCallBack<File> httpDownloadCallBack) {
		asyncThread(new Runnable() {
			@Override
			public void run() {
				syncDownloadFile(url,fileName,httpDownloadCallBack);
			}
		});
	}

	@Override
	public File syncDownloadFile(String url, String fileName) {
		return syncDownloadFile(url, fileName, null);
	}

	@Override
	public File syncDownloadFile(String url, String fileName,
								 HttpCallBack<File> httpDownloadCallBack) {

		if(TextUtils.isEmpty(url)){
			return null;
		}

		File file = null;

		BufferedInputStream bis = null;

		FileOutputStream fos = null;

		HttpURLConnection httpURLConnection = null;

		int statusCode = -1;
		try {
			Log.v(TAG, url);

			if(TextUtils.isEmpty(fileName)){
				return null;
			}

			if(httpDownloadCallBack!=null)
				httpDownloadCallBack.onStart(url);

			httpURLConnection = getHttpURLConnection(url,HttpMethod.GET,null);
			httpURLConnection.connect();
			statusCode = httpURLConnection.getResponseCode();
			if(statusCode == HttpURLConnection.HTTP_OK){

				file = new File(fileName);
				fos = new FileOutputStream(file);

				long progress = 0;

				long count = httpURLConnection.getContentLength();

				bis = new BufferedInputStream(httpURLConnection.getInputStream());

				isCancel = false;
				byte[] buffer = new byte[DEFAULT_BYTE_LENGTH];
				int len = 0;
				if(httpDownloadCallBack!=null && count!=-1)
					httpDownloadCallBack.onLoading(progress, count);
				long time = System.currentTimeMillis();
				while((!isCancel) && (len = bis.read(buffer))!=-1){
					fos.write(buffer, 0, len);
					long temp = System.currentTimeMillis();
					if(temp-time>=1000){
						time = temp;
						if(httpDownloadCallBack!=null && count!=-1){
							progress += len;
							httpDownloadCallBack.onLoading(progress, count);
						}
					}
				}

				if(httpDownloadCallBack!=null ){
					if(!isCancel){
						progress = count;
						httpDownloadCallBack.onLoading(progress, count);
					}else{
						bis.close();
						fos.close();
						httpDownloadCallBack.onCancel();

						if(httpURLConnection!=null)
							httpURLConnection.disconnect();

						return file;
					}
				}

				bis.close();
				fos.close();

				if(httpDownloadCallBack!=null && !isCancel)
					httpDownloadCallBack.onSuccess(file);

			}else{
				if(httpDownloadCallBack!=null)
					httpDownloadCallBack.onFailure(statusCode, null);
			}

		} catch (Throwable e) {
			Log.e(TAG, e.getMessage(), e);
			if(httpDownloadCallBack!=null)
				httpDownloadCallBack.onFailure(statusCode, e);
		}finally{
			if(httpURLConnection!=null)
				httpURLConnection.disconnect();
		}

		return file;
	}

	/**
	 * Convert map to string.
	 * <p>
	 * This method will call the toString() method of K and V.
	 *
	 * @param map the map to be output.
	 * @return the string result.
	 */
	private String convertMaptoString(Map map) {
		if (map == null) {
			return "";
		}
		String str = "[";
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			str += " " + pair.getKey() + ":" + (pair.getValue() == null ? "" : pair.getValue());
		}
		return str + " ]";
	}

	private String kvPairsToUrl(ArrayList<KVPair<String>> values) throws Throwable {
		StringBuilder sb = new StringBuilder();
		for (KVPair<String> value : values) {
			String encodedName = urlEncode(value.name, DEFAULT_ENCODING);
			String encodedValue = value.value != null
					? Data.urlEncode(value.value, DEFAULT_ENCODING) : "";
			if (sb.length() > 0) {
				sb.append('&');
			}
			sb.append(encodedName).append('=').append(encodedValue);
		}
		return sb.toString();
	}

	public static String urlEncode(String s, String enc) throws Throwable {
		String text = URLEncoder.encode(s, enc);
		return TextUtils.isEmpty(text) ? text : text.replace("+", "%20");
	}
}
