package com.mob.secverify.demo.exception;

public class DemoException extends Exception {
	protected int code;

	public DemoException(Throwable e){
		super(e);
	}

	public DemoException(DemoErr demoErr) {
		super(demoErr.getMessage());
		this.code = demoErr.getCode();
	}

	public DemoException(DemoErr demoErr, Throwable throwable){
		super(demoErr.getMessage(),throwable);
		this.code = demoErr.getCode();
	}

	public DemoException(int code, String message) {
		super(message);
		this.code = code;
	}

	public DemoException(int code, String message, Throwable t) {
		super(message, t);
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	@Override
	public String toString() {
		return "{\"code\": " + code + ", \"message\": \"" + getMessage() + "\"}";
	}
}
