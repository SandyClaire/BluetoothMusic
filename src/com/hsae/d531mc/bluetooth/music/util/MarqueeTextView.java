package com.hsae.d531mc.bluetooth.music.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MarqueeTextView extends TextView {

	private boolean isMarquee = true;
	
	public MarqueeTextView(Context context) {
		this(context, null);
	}
	
	public MarqueeTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override  
    public boolean isFocused() {  
        return isMarquee;  
    }  

	public void setIsMarquee(boolean isMarquee) {
		this.isMarquee = isMarquee;
	}
	
}
