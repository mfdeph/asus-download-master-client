package com.insolence.admclient;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class ProgressBarTextView extends TextView {
    // Максимальное значение шкалы
    private int mMaxValue = 100; 

    // Конструкторы
    public ProgressBarTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ProgressBarTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressBarTextView(Context context) {
        super(context);
    }
    
    // Установка максимального значения
    public void setMaxValue(int maxValue){
        mMaxValue = maxValue;
    }
    
    // Установка значения
    public synchronized void setValue(int value, String text) {
        // Установка новой надписи
        this.setText(text == null ? String.valueOf(value) + "%" : text);
        
        // Drawable, отвечающий за фон
        LayerDrawable background = (LayerDrawable) this.getBackground();
        
        // Достаём Clip, отвечающий за шкалу, по индексу 1
        ClipDrawable barValue = (ClipDrawable) background.getDrawable(1);
        
        // Устанавливаем уровень шкалы
        int newClipLevel = (int) (value * 10000 / mMaxValue);
        barValue.setLevel(newClipLevel);
        
        // Уведомляем об изменении Drawable
        drawableStateChanged();
    }
    
    public void setValue(String value){
    	
    }

}