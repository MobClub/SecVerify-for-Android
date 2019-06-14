package com.mob.secverify.demo.ui.component;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.mob.tools.utils.ResHelper;

public class CommonProgressDialog extends Dialog {

    private Context mContext;
	private static CommonProgressDialog dialog;
	private WindowManager windowManager;

    public CommonProgressDialog(Context context) {
        super(context, ResHelper.getStyleRes(context, "Dialog_Common"));
        mContext = context;
    }

    public CommonProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        int width = (int) (getDeviceWidth(mContext) * 0.2);
		View view = getLayoutInflater().inflate(ResHelper.getLayoutRes(mContext, "sec_verify_demo_common_progress_dialog"), null);
        setContentView(view, new RelativeLayout.LayoutParams(width, width));
    }

    public static void showProgressDialog(Context context) {
		dismissProgressDialog();
		dialog = new CommonProgressDialog(context);
		dialog.show();
	}

	public static void dismissProgressDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
	}

	private int getDeviceWidth(Context context) {
		if (windowManager == null) {
			windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
}
