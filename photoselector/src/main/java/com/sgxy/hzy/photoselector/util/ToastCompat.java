package com.sgxy.hzy.photoselector.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.reflect.Field;

public class ToastCompat {
    private static Field sField_TN;
    private static Field sField_TN_Handler;
    private Toast mToast;
    static {
        try {
            sField_TN = Toast.class.getDeclaredField("mTN");
            sField_TN.setAccessible(true);
            sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
            sField_TN_Handler.setAccessible(true);
        } catch (Exception e) {
        }
    }

    private static void hook(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafelyHandlerWarpper(preHandler));
        } catch (Exception e) {
        }
    }

    public void showToast(Context context, CharSequence cs) {
        if (mToast == null) {
            mToast = Toast.makeText(context, cs, Toast.LENGTH_LONG);
        } else {
            mToast.setText(cs);
        }
        hook(mToast);
        mToast.show();
    }

    public static class SafelyHandlerWarpper extends Handler {
        private Handler impl;

        public SafelyHandlerWarpper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
            }
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                impl.handleMessage(msg);//需要委托给原Handler执行
            } catch (Throwable throwable) {
            }
        }
    }
}
