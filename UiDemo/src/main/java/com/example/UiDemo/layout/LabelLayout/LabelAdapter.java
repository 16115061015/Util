package com.example.UiDemo.layout.LabelLayout;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class LabelAdapter<T> {
    private List<T> datas;

    public LabelAdapter(List<T> datas) {
        this.datas = datas;
    }

    public abstract View getView(ViewGroup parent, int position, T t);

    public T getItem(int position) {
        return datas.get(position);
    }

    public int getCount() {
        return datas.size();
    }
}
