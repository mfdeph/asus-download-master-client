package com.insolence.admclient;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class ProgressBarTextView extends TextView {
    // ������������ �������� �����
    private int mMaxValue = 100; 

    // ������������
    public ProgressBarTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ProgressBarTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressBarTextView(Context context) {
        super(context);
    }
    
    // ��������� ������������� ��������
    public void setMaxValue(int maxValue){
        mMaxValue = maxValue;
    }
    
    // ��������� ��������
    public synchronized void setValue(int value) {
        // ��������� ����� �������
        this.setText(String.valueOf(value) + "%");
        this.setTextColor(android.R.color.white);
        
        // Drawable, ���������� �� ���
        LayerDrawable background = (LayerDrawable) this.getBackground();
        
        // ������ Clip, ���������� �� �����, �� ������� 1
        ClipDrawable barValue = (ClipDrawable) background.getDrawable(1);
        
        // ������������� ������� �����
        int newClipLevel = (int) (value * 10000 / mMaxValue);
        barValue.setLevel(newClipLevel);
        
        // ���������� �� ��������� Drawable
        drawableStateChanged();
    }
}