package com.mob.secverify.demo;

import com.mob.secverify.demo.exception.DemoException;

public abstract class ResultListener<T> {
	public abstract void onComplete(T data);
	public abstract void onFailure(DemoException e);
}
