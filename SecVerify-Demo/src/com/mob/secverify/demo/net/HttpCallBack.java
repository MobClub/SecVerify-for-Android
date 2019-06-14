package com.mob.secverify.demo.net;

/**
 * Created by weishj on 2017/12/20.
 */

public interface HttpCallBack<T> {

	/**
	 * 开始
	 * @param url
	 */
	void onStart(String url);

	/**
	 * 加载…
	 * @param progress
	 * @param count
	 */
	void onLoading(long progress, long count);

	/**
	 * 成功
	 * @param t 返回的对象
	 */
	void onSuccess(T t);

	/**
	 * 失败
	 * @param responseCode
	 * @param e
	 */
	void onFailure(int responseCode, Throwable e);

	/**
	 * 取消
	 */
	void onCancel();
}
