package com.mob.secverify.demo.util;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class NetWorkUtil {

	public static final int NET_ETHNET = 0;
	public static final int NET_CELLULAR = 1;
	public static int CURRENT_TAG = NET_ETHNET;


	public static void getNetState(Context context, int tag) {
		CURRENT_TAG = tag;
		Log.e("cdl", "========000=========");
		if (Build.VERSION.SDK_INT >= 21) {
			Log.e("cdl", "======111===========");
			final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkRequest.Builder builder = new NetworkRequest.Builder();
			Log.e("cdl", "=======222==========");
			builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
//            builder.addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET);
//            builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
			if (tag == NET_ETHNET) {
				builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
			} else if (tag == NET_CELLULAR) {
				builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
			}
			NetworkRequest request = builder.build();
			Log.e("cdl", "=======333==========");
			ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {

				@TargetApi(Build.VERSION_CODES.M)
				@Override
				public void onAvailable(Network network) {
					super.onAvailable(network);
					Log.e("cdl", "==========已根据功能和传输类型找到合适的网络=======");
					if (Build.VERSION.SDK_INT >= 23) {
						Log.e("cdl", "=====44444==23==========");
						connectivityManager.bindProcessToNetwork(network);
					} else {
						Log.e("cdl", "===444444====<<<<23==========");
						ConnectivityManager.setProcessDefaultNetwork(network);
					}
//                    get("https://www.baidu.com");
					Log.e("cdl", "=========4444========");
					connectivityManager.unregisterNetworkCallback(this);
				}
			};
			connectivityManager.requestNetwork(request, callback);
		}
	}

	public static String get(final String url) {
		Log.e("cdl", "=========5555555==========");
		final StringBuilder sb = new StringBuilder();
		FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				BufferedReader br = null;
				InputStreamReader isr = null;
				URLConnection conn;
				try {
					URL geturl = new URL(url);
					conn = geturl.openConnection();//创建连接
					conn.connect();//get连接
					isr = new InputStreamReader(conn.getInputStream());//输入流
					br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line);//获取输入流数据
					}
					Log.e("cdl", "==" + sb.toString());
				} catch (Exception e) {
					Log.e("cdl", "=====6666666==============");
					e.printStackTrace();
				} finally {//执行流的关闭
					if (br != null) {
						try {
							if (br != null) {
								br.close();
							}
							if (isr != null) {
								isr.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return sb.toString();
			}
		});
		new Thread(task).start();
		String s = null;
		try {
			s = task.get();//异步获取返回值
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}


	public static NetworkInfo getNetworkState(Context context) {

		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == connManager)
			return null;
		NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
		if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
			return null;
		}
		return activeNetInfo;
	}


	public static String GetNetworkType(Context context) {
		String strNetworkType = "";
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				strNetworkType = "WIFI";
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				String _strSubTypeName = networkInfo.getSubtypeName();

				Log.e("cocos2d-x", "Network getSubtypeName : " + _strSubTypeName);

				// TD-SCDMA   networkType is 17
				int networkType = networkInfo.getSubtype();
				switch (networkType) {
					case TelephonyManager.NETWORK_TYPE_GPRS:
					case TelephonyManager.NETWORK_TYPE_EDGE:
					case TelephonyManager.NETWORK_TYPE_CDMA:
					case TelephonyManager.NETWORK_TYPE_1xRTT:
					case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
						strNetworkType = "2G";
						break;
					case TelephonyManager.NETWORK_TYPE_UMTS:
					case TelephonyManager.NETWORK_TYPE_EVDO_0:
					case TelephonyManager.NETWORK_TYPE_EVDO_A:
					case TelephonyManager.NETWORK_TYPE_HSDPA:
					case TelephonyManager.NETWORK_TYPE_HSUPA:
					case TelephonyManager.NETWORK_TYPE_HSPA:
					case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
					case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
					case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
						strNetworkType = "3G";
						break;
					case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
						strNetworkType = "4G";
						break;
					default:
						// http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
						if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
							strNetworkType = "3G";
						} else {
							strNetworkType = _strSubTypeName;
						}

						break;
				}
				Log.e("cocos2d-x", "Network getSubtype : " + Integer.valueOf(networkType).toString());
			}
		}
		Log.e("cocos2d-x", "Network Type : " + strNetworkType);
		return strNetworkType;
	}


}