package com.mob.secverify.demo.net;

/**
 * Created by weishj on 2017/12/20.
 */

public abstract class HttpConnectCallBack implements HttpCallBack<String> {
	@Override
	public abstract void onSuccess(String content);
}
