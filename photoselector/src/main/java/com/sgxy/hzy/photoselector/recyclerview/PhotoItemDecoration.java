package com.sgxy.hzy.photoselector.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class PhotoItemDecoration extends RecyclerView.ItemDecoration {
    private int gap;
    private RecyclerView.Adapter adapter;
    private int column;

    public PhotoItemDecoration(int column, RecyclerView.Adapter adapter, Context context) {
        this.column = column;
        this.gap = dip2px(context, 1f);
        this.adapter = adapter;
    }

    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (adapter == null) {
            return;
        }
        int position = parent.getChildAdapterPosition(view);
        if (position < 0 || position >= adapter.getItemCount()) {
            return;
        }
        int remainder = position % column;
        if(remainder == 0){
            outRect.left = 0;
            outRect.right = gap;
        }else if(remainder == column - 1){
            outRect.left = gap;
            outRect.right = 0;
        }else{
            outRect.left = gap;
            outRect.right = gap;
        }
        outRect.top = gap;
        outRect.bottom = gap;
    }
}
