package com.mob.secverify.demo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import com.mob.MobSDK;
import com.mob.secverify.log.VerifyLog;

import java.lang.reflect.Method;

public class OperatorUtils {

	/**
	 * 获取设备蜂窝网络运营商
	 *
	 * @return ["中国电信CTCC":3]["中国联通CUCC:2]["中国移动CMCC":1]["other":0]["无sim卡":-1]["数据流量未打开":-2]
	 */
	public static int getCellularOperatorType() {
		int opeType = -1;
		// No sim
		if (!hasSim()) {
			return opeType;
		}
		// Mobile data disabled
		if (!isMobileDataEnabled(MobSDK.getContext())) {
			opeType = -2;
			return opeType;
		}
		// Check cellular operator
		TelephonyManager tm = (TelephonyManager) MobSDK.getContext().getSystemService(Context.TELEPHONY_SERVICE);
		String operator = tm.getSimOperator();
		// 中国联通
		if ("46001".equals(operator) || "46006".equals(operator) || "46009".equals(operator)) {
			opeType = 2;
			// 中国移动
		} else if ("46000".equals(operator) || "46002".equals(operator) || "46004".equals(operator) || "46007".equals(operator)) {
			opeType = 1;
			// 中国电信
		} else if ("46003".equals(operator) || "46005".equals(operator) || "46011".equals(operator)) {
			opeType = 3;
		} else {
			opeType = 0;
		}
		return opeType;
	}

	public static boolean hasSim() {
		TelephonyManager tm = (TelephonyManager) MobSDK.getContext().getSystemService(Context.TELEPHONY_SERVICE);
		if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断数据流量开关是否打开
	 *
	 * @param context
	 * @return
	 */
	public static boolean isMobileDataEnabled(Context context) {
		try {
			Method method = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
			method.setAccessible(true);
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			return (Boolean) method.invoke(connectivityManager);
		} catch (Throwable t) {
			VerifyLog.getInstance().d(t, VerifyLog.FORMAT, "isMobileDataEnabled", "Check mobile data encountered exception");
			return false;
		}
	}
}
