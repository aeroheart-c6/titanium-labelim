package com.aeroheart.ti.labelim;

import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.TextUtils.TruncateAt;
import android.text.SpannedString;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;


public class LabelTextView extends TextView {
    protected TiViewProxy proxy;
    protected LayoutParams tiLayout;
    protected boolean wordWrap;
    protected boolean ellipsize;
    
    public LabelTextView(Context context, LayoutParams layoutParams, boolean wordWrap, boolean ellipsize, TiViewProxy proxy) {
        super(context);
        
        this.proxy = proxy;
        this.tiLayout = layoutParams;
        
        this.setWordWrap(wordWrap);
        this.setEllipsize(ellipsize);
    }
    
    public void setWordWrap(boolean wordWrap) { 
        this.wordWrap = wordWrap;
    }
    
    public void setEllipsize(boolean ellipsize) {
        this.ellipsize = ellipsize;
        
        if (this.ellipsize)
            this.setEllipsize(TruncateAt.END);
        else
            this.setEllipsize(null);
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        
        if (this.proxy != null && proxy.hasListeners(TiC.EVENT_POST_LAYOUT))
            proxy.fireEvent(TiC.EVENT_POST_LAYOUT, null, false);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        CharSequence text = this.getText();
        
        // For html texts, we will manually detect url clicks.
        if (text instanceof SpannedString) {
            SpannedString spanned = (SpannedString) text;
            Spannable buffer = Factory.getInstance().newSpannable(spanned.subSequence(0, spanned.length()));
            
            int action = event.getAction();
            
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                Layout layout = this.getLayout();
                int x = (int) event.getX();
                int y = (int) event.getY();
                
                x -= this.getTotalPaddingLeft();
                y -= this.getTotalPaddingTop();
                
                x += this.getScrollX();
                y += this.getScrollY();
                
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);
                ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
                
                if (link.length != 0) {
                    ClickableSpan cSpan = link[0];
                    if (action == MotionEvent.ACTION_UP)
                        cSpan.onClick(this);
                    else if (action == MotionEvent.ACTION_DOWN)
                        Selection.setSelection(buffer, buffer.getSpanStart(cSpan), buffer.getSpanEnd(cSpan));
                }
            }
        }
        
        return super.onTouchEvent(event);
    }
}
