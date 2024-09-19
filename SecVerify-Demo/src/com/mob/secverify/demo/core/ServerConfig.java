package com.mob.secverify.demo.core;

import android.text.TextUtils;

public class ServerConfig {
	private static final String PROTOCOL = "http://";
	private static final String SERVER_URL_RELEASE = "demo.verify.mob.com";

	public static String getServerUrl() {
		return checkSuffix(PROTOCOL + SERVER_URL_RELEASE);
	}

	private static String checkSuffix(String url) {
		if (!TextUtils.isEmpty(url) && !url.endsWith("/")) {
			url += "/";
		}
		return url;
	}
}
