package com.sgxy.hzy.photoselector.avoidonresult;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

public class AvoidOnResultFragment extends Fragment {
    private SparseArray<AvoidOnResult.Callback> mCallbacks = new SparseArray<>();

    public AvoidOnResultFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void startForResult(Intent intent, AvoidOnResult.Callback callback) {
        mCallbacks.put(callback.hashCode(), callback);
        startActivityForResult(intent, callback.hashCode());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callback方式的处理
        AvoidOnResult.Callback callback = mCallbacks.get(requestCode);
        if (callback != null) {
            mCallbacks.remove(requestCode);
            callback.onActivityResult(resultCode, data);
        }
    }
}
