package com.mob.secverify.demo.net;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.mob.secverify.demo.ResultListener;
import com.mob.secverify.demo.exception.DemoErr;
import com.mob.secverify.demo.exception.DemoException;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class HttpManager {
	private static final String TAG = HttpManager.class.getSimpleName();
	private static HttpManager instance;
	private NetworkHelper netHelper;
	private Gson gson;

	private HttpManager() {
		netHelper = new NetworkHelper(3000, 10000);
		gson = new Gson();
	}

	public static HttpManager getInstance() {
		if (instance == null) {
			synchronized (HttpManager.class) {
				if (instance == null) {
					instance = new HttpManager();
				}
			}
		}
		return instance;
	}

	public <T> void asyncPost(String url, Map<String, Object> params, final ResultListener<T> resultListener) {
		netHelper.asyncConnect(url, params, AbstractHttp.HttpMethod.POST, new HttpConnectCallBack() {
			@Override
			public void onStart(String url) {

			}

			@Override
			public void onLoading(long progress, long count) {

			}

			@Override
			public void onSuccess(String resp) {
				Class<T> clazz = null;
				try {
					clazz = (Class<T>) ((ParameterizedType) resultListener.getClass()
							.getGenericSuperclass()).getActualTypeArguments()[0];
				} catch (Throwable t) {
					Log.e(TAG, "post" + t.getMessage(), t);
				}

				final T data;
				if (clazz == HashMap.class) {
					data = gson.fromJson(resp, clazz);
				} else if (clazz == String.class) {
					data = (T) resp;
				} else {
					data = gson.fromJson(resp, clazz);
				}
				new Handler(Looper.getMainLooper(), new Handler.Callback() {
					@Override
					public boolean handleMessage(Message message) {
						resultListener.onComplete(data);
						return false;
					}
				}).sendEmptyMessage(0);
			}

			@Override
			public void onFailure(final int responseCode, final Throwable e) {
				new Handler(Looper.getMainLooper(), new Handler.Callback() {
					@Override
					public boolean handleMessage(Message message) {
						handleError(responseCode, e, resultListener);
						return false;
					}
				}).sendEmptyMessage(0);
			}

			@Override
			public void onCancel() {

			}
		});
	}

	private <T> void handleError(int responseCode, Throwable t, ResultListener<T> resultListener) {
		try {
			if (t != null) {
				HashMap<String, Object> map = gson.fromJson(t.getMessage(), HashMap.class);
				if (map != null) {
					// 错误响应示例：{"status":5119508,"res":null,"error":"免密登录失败"}
					double status = (Double) map.get("status");
					int code = (int) status;
					String msg = (String) map.get("error");
					if (resultListener != null) {
						resultListener.onFailure(new DemoException(code, msg, t));
					}
				} else {
					if (resultListener != null) {
						resultListener.onFailure(new DemoException(t));
					}
				}
			} else {
				if (resultListener != null) {
					resultListener.onFailure(new DemoException(DemoErr.UNKNOWN_ERROR));
				}
			}
		} catch (Throwable t1) {
			Log.e(TAG, "Server response error", t1);
			if (resultListener != null) {
				resultListener.onFailure(new DemoException(DemoErr.SERVER_RESPONSE_ERROR, t));
			}
		}
	}
}
