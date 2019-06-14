package com.mob.secverify.demo.login;

import com.mob.MobSDK;
import com.mob.secverify.datatype.LoginToken;
import com.mob.secverify.datatype.VerifyResult;
import com.mob.secverify.demo.ResultListener;
import com.mob.secverify.demo.core.ServerConfig;
import com.mob.secverify.demo.entity.LoginResult;
import com.mob.secverify.demo.net.HttpManager;
import com.mob.tools.utils.DeviceHelper;

import java.util.HashMap;
import java.util.Map;

public class LoginTask {
	private static final String TAG = LoginToken.class.getSimpleName();
	private static final String URL_LOGIN = "demo/sdkLogin";
	private static LoginTask instance;

	private LoginTask() {}

	public static LoginTask getInstance() {
		if (instance == null) {
			synchronized (LoginTask.class) {
				if (instance == null) {
					instance = new LoginTask();
				}
			}
		}
		return instance;
	}

	public void login(VerifyResult verifyResult, ResultListener<LoginResult> resultListener) {
		Map<String, Object> values = new HashMap<String, Object>();
		if (verifyResult != null) {
			values.put("opToken", verifyResult.getOpToken());
			values.put("operator", verifyResult.getOperator());
			values.put("phoneOperator", verifyResult.getOperator());
			values.put("token", verifyResult.getToken());
			values.put("md5", DeviceHelper.getInstance(MobSDK.getContext()).getSignMD5());
		}
		HttpManager.getInstance().asyncPost(ServerConfig.getServerUrl() + URL_LOGIN, values, resultListener);
	}
}
