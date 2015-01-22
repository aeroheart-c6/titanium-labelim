package com.aeroheart.ti.labelim;

import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannedString;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;


/**
 * Based very heavily (if not already completely copied) from the EllipsizingTextView class from
 * barone (https://github.com/triposo/barone)
 * 
 * @author aeroheart-c6
 */
public class LabelTextView extends TextView {
    protected static final String LCAT = "Labelim.Label";
    protected static final String ELLIPSIS = "\u2026";
    
    protected TiViewProxy proxy;
    protected LayoutParams tiLayout;
    protected CharSequence text;
    
    protected boolean wordWrap;
    protected boolean ellipsized;
    protected boolean internalChange;
    protected boolean stale;
    protected int lines;
    
    public LabelTextView(Context context,
                         LayoutParams layoutParams, boolean wordWrap, boolean ellipsize, TiViewProxy proxy) {
        this(context, null, 0, layoutParams, wordWrap, ellipsize, proxy);
    }
    
    public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr,
                         LayoutParams layoutParams, boolean wordWrap, boolean ellipsize, TiViewProxy proxy) {
        super(context, attrs, defStyleAttr);
        super.setEllipsize(null);
        
        TypedArray array = context.obtainStyledAttributes(attrs, new int[] {
            android.R.attr.maxLines
        });
        
        this.proxy = proxy;
        this.tiLayout = layoutParams;
        
        this.setMaxLines(array.getInt(0, Integer.MAX_VALUE));
        this.setWordWrap(wordWrap);
        this.setEllipsized(ellipsize);
        
        array.recycle();
    }
    
    public void setWordWrap(boolean wordWrap) {
        this.setSingleLine(!wordWrap);
        
        this.stale = true; 
        this.wordWrap = wordWrap;
    }
    
    public void setEllipsized(boolean ellipsize) {
        if (ellipsize)
            this.setEllipsize(TextUtils.TruncateAt.END);
        else
            this.setEllipsize(null);

        this.ellipsized = ellipsize;        
        this.stale = true;
    }
    
    public boolean getEllipsized() {
        return this.ellipsized;
    }
    
    public boolean isEllipsized() {
        return this.ellipsized;
    }
    
    public boolean isEllipsizingLastFullyVisibleLine() {
        return this.lines == Integer.MAX_VALUE;
    }
    
    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        this.stale = true;
    }
    
    @Override
    public void setMaxLines(int lines) {
        super.setMaxLines(lines);
        this.lines = lines;
        this.stale = true;
    }
    
    public int getMaxLines() {
        return this.lines;
    }
    
    @Override
    public void setEllipsize(TextUtils.TruncateAt where) {
        // disrespect ellipsize settings
    }
    
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        this.stale = true;
    }
    
    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        super.setText(text, type);
        this.text = text;
        this.stale = true;
    }
    
    /**
     * Returns the number of lines of text that is allowed for display
     */
    protected int getDisplayableLineCount() {
        if (this.isEllipsizingLastFullyVisibleLine()) {
            int visibleLinesCount;
            
            visibleLinesCount = this.getFullyVisibleLinesCount();
            visibleLinesCount = visibleLinesCount == -1 ? 1 : visibleLinesCount;
            
            return visibleLinesCount;
        }
        else
            return this.lines;
    }
    
    /**
     * Returns the number of lines of text that displays in their full height
     */
    protected int getFullyVisibleLinesCount() {
        Layout layout = this.getWorkingLayout("");
        int height, lineHeight;
        
        height = this.getHeight() - (this.getPaddingTop() + this.getPaddingBottom());
        lineHeight = layout.getLineBottom(0);
        
        return height / lineHeight;
    }
    
    protected Layout getWorkingLayout(CharSequence text) {
        return new StaticLayout(
            text,
            this.getPaint(),
            this.getWidth() - (this.getPaddingLeft() + this.getPaddingRight()),
            Layout.Alignment.ALIGN_NORMAL,
            1.0f, 0.0f,
            false
        );
    }
    
    protected void ellipsizeText() {
        if (!this.ellipsized)
            return;
        
        String text = this.text.toString();
        Layout layout = this.getWorkingLayout(text);
        int displayableLines = this.getDisplayableLineCount();
        
        if (layout.getLineCount() - 1 > displayableLines) {
            text = this.text.toString()
                       .substring(0, layout.getLineEnd(displayableLines - 1))
                       .trim();
            
            while (this.getWorkingLayout(text + ELLIPSIS).getLineCount() - 1> displayableLines)
                text = text.substring(0, text.length() - 2);
            
            
            text = text + ELLIPSIS;
        }
        
        if (!text.equals(this.getText())) {
            this.internalChange = true;
            
            try {
                this.setText(text);
            }
            finally {
                this.internalChange = false;
            }
        }
        
        this.stale = false;
    }
    
    /*
     ***********************************************************************************************
     * TextView callback overrides
     ***********************************************************************************************
     */
    @Override
    protected void onTextChanged(CharSequence text, int start, int beforeLen, int afterLen) {
        super.onTextChanged(text, start, beforeLen, afterLen);
        
        if (!this.internalChange) {
            this.text = text.toString();
            this.stale = true;
        }
    }
    
//  @Override
//  public void setText(CharSequence text, TextView.BufferType type) {
//      Log.d(LabelimModule.LCAT, "Setting  text. Yep");
//      Log.d(LabelimModule.LCAT, text.toString());
//      Log.d(LabelimModule.LCAT, String.valueOf(this.lines));
//      if (this.ellipsize) {
//          SpannableStringBuilder builder;
//          CharSequence truncated;
//          Layout layout
//          ;
//          int lastLineStart,
//              lastLineEnd;
//          
//          layout = this.getLayout();
//          
//          if (layout == null)
//              layout = new StaticLayout(
//                  text, 0, text.length(),
//                  this.getPaint(),
//                  this.getWidth() - this.getCompoundPaddingLeft() - this.getCompoundPaddingRight(),
//                  Layout.Alignment.ALIGN_NORMAL,
//                  1.0f, 0f, false
//              );
//          
//          lastLineStart = layout.getLineStart(this.lines - 1);
//          lastLineEnd = layout.getLineEnd(this.lines - 1);
//          truncated = TextUtils.ellipsize(
//              text.subSequence(lastLineStart, text.length()),
//              this.getPaint(),
//              (float)(lastLineEnd - lastLineStart),
//              this.getEllipsize()
//          );
//          
//          builder = new SpannableStringBuilder();
//          builder.append(text, 0, lastLineStart);
//          builder.append(truncated);
//          
//          // Reassemble spans
//          
//          if (text instanceof Spanned) {
//              Spanned spanned = (Spanned)text;
//              Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);
//              int destLen = builder.length();
//              
//              for (Object span : spans) {
//                  int start = spanned.getSpanStart(span),
//                      end = spanned.getSpanEnd(span);
//                  int flags = spanned.getSpanFlags(span);
//                  
//                  if (start <= destLen)
//                      builder.setSpan(span, start, Math.min(end, destLen), flags);
//              }
//          }
//      }
//      
//      super.setText(text, type);
//  }
    
    /*
     ***********************************************************************************************
     * View callback overrides
     ***********************************************************************************************
     */
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        // Only allow label to exceeed the size of parent when it's size behaviour with both
//        // wordwrap and ellipsize disabled
//        if (!this.wordWrap && !this.ellipsized
//            && this.tiLayout.optionWidth == null && !this.tiLayout.autoFillsWidth) {
//            widthMeasureSpec = MeasureSpec.makeMeasureSpec(
//                MeasureSpec.getSize(widthMeasureSpec),
//                MeasureSpec.UNSPECIFIED
//            );
//            
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
//                MeasureSpec.getSize(heightMeasureSpec),
//                MeasureSpec.UNSPECIFIED
//            );
//        }
//        
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        
        if (this.proxy != null && proxy.hasListeners(TiC.EVENT_POST_LAYOUT))
            proxy.fireEvent(TiC.EVENT_POST_LAYOUT, null, false);
    }
    
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.stale = true;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        if (this.stale)
            this.ellipsizeText();
        
        super.onDraw(canvas);
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
