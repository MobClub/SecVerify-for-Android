package com.mob.secverify.demo.net;

import java.io.File;
import java.util.Map;

/**
 * Created by weishj on 2017/12/20.
 */

public abstract class AbstractHttp {
	/** 默认编码  (UTF-8) */
	public static final String DEFAULT_ENCODING = "UTF-8";
	/** GET请求时 url和 参数的分隔符 */
	public static final String URL_AND_PARA_SEPARATOR = "?";
	/** 是否取消请求 */
	protected boolean isCancel = false;
	public static final int DEFAULT_BYTE_LENGTH = 8192;
	protected static final int LONG_TIME = 10000;
	protected int connectionTimeOut = LONG_TIME;
	protected int soTimeOut = LONG_TIME;
	public enum HttpMethod{
		/** GET请求 */
		GET,
		/** POST请求 */
		POST
	}

	/**
	 * 构造
	 */
	public AbstractHttp(){
		super();
	}

	public AbstractHttp(int connectionTimeOut,int soTimeOut){
		super();
		if(connectionTimeOut<0 || soTimeOut <0){
			throw new RuntimeException("connectionTimeOut<0 || soTimeOut<0");
		}

		if(connectionTimeOut>0)
			this.connectionTimeOut = connectionTimeOut;
		if(soTimeOut>0)
			this.soTimeOut = soTimeOut;
	}

	//-----------------------------------------

	/**
	 * 取消请求
	 */
	protected void cancel(){
		isCancel = true;
	}

	/**
	 * 异步线程
	 * @param runnable
	 */
	protected void asyncThread(Runnable runnable){
		new Thread(runnable).start();
	}


	/**
	 * 异步连接  默认GET请求
	 * @param url
	 * @param httpCallBack
	 */
	public abstract void asyncConnect(String url, HttpCallBack<String> httpCallBack);

	/**
	 * 异步连接  默认GET请求
	 * @param url
	 * @param params
	 * @param httpCallBack
	 */
	public abstract void asyncConnect(String url, Map<String, Object> params, HttpCallBack<String> httpCallBack);
	/**
	 * 异步连接
	 * @param url
	 * @param params
	 * @param httpMethod
	 * @param httpCallBack
	 */
	public abstract void asyncConnect(String url, Map<String, Object> params, HttpMethod httpMethod, HttpCallBack<String> httpCallBack);

	/**
	 * 同步连接  默认GET请求
	 * @param url
	 */
	public abstract String syncConnect(String url);

	/**
	 * 同步连接  默认GET请求
	 * @param url
	 * @param params
	 */
	public abstract String syncConnect(String url, Map<String, Object> params);

	/**
	 * 同步连接  默认GET请求
	 * @param url
	 * @param params
	 * @param httpMethod
	 */
	public abstract String syncConnect(String url, Map<String, Object> params, HttpMethod httpMethod);

	/**
	 * 同步连接  默认GET请求
	 * @param url
	 * @param params
	 * @param httpCallBack
	 */
	public abstract String syncConnect(String url, Map<String, Object> params, HttpCallBack<String> httpCallBack);

	/**
	 * 同步连接
	 * @param url
	 * @param params
	 * @param httpMethod
	 * @param httpCallBack
	 */
	public abstract String syncConnect(String url, Map<String, Object> params, HttpMethod httpMethod, HttpCallBack<String> httpCallBack);


	/**
	 * 异步下载文件
	 * @param url
	 * @param fileName
	 * @param httpDownloadCallBack
	 * @return
	 */
	public abstract void asyncDownloadFile(String url, String fileName, HttpCallBack<File> httpDownloadCallBack);

	/**
	 * 同步下载文件
	 * @param url
	 * @param fileName
	 * @return
	 */
	public abstract File syncDownloadFile(String url, String fileName);

	/**
	 * 同步下载文件
	 * @param url
	 * @param fileName
	 * @param httpDownloadCallBack
	 * @return
	 */
	public abstract File syncDownloadFile(String url, String fileName, HttpCallBack<File> httpDownloadCallBack);



}
