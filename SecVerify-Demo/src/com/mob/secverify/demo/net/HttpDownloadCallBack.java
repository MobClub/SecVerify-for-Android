package com.mob.secverify.demo.net;

import java.io.File;

/**
 * Created by weishj on 2017/12/20.
 */

public abstract class HttpDownloadCallBack implements HttpCallBack<File> {
	@Override
	public abstract void onSuccess(File file);
}
