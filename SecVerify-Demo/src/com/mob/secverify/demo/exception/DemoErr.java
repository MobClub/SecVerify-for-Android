package com.mob.secverify.demo.exception;

public enum DemoErr {
	/** 服务端响应错误 */
	SERVER_RESPONSE_ERROR(1098, "Server response error"),
	/** 未知错误 */
	UNKNOWN_ERROR(1099, "Server response error");

	private int code;
	private String message;

	DemoErr(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return this.code;
	}

	public String getMessage() {
		return this.message;
	}

	public static DemoErr valueOf(int value) {
		switch (value) {
			case 1098: return SERVER_RESPONSE_ERROR;
			case 1099: return UNKNOWN_ERROR;
		}
		return null;
	}
}
