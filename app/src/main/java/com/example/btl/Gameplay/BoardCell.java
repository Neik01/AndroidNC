package com.example.btl.Gameplay;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BoardCell extends androidx.appcompat.widget.AppCompatImageView {


    public BoardCell(Context context) {
        super(context);

    }

    public BoardCell(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BoardCell(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
