package com.aeroheart.ti.labelim;

import java.util.HashMap;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;
import android.graphics.Color;
import android.text.Html;
import android.text.InputType;
import android.text.util.Linkify;
import android.view.Gravity;
import android.widget.TextView;


public class Label extends TiUIView {
    protected static final float DEFAULT_SHADOW_RADIUS = 0.5f;

    protected boolean ellipsize = true;
    protected boolean wordWrap = true;
    protected int defaultColor;
    protected int shadowColor = Color.TRANSPARENT;
    protected float shadowRadius = Label.DEFAULT_SHADOW_RADIUS;
    protected float shadowX = 0f;
    protected float shadowY = 0f;
    
    public Label(final TiViewProxy proxy) {
        super(proxy);
        
        Activity activity;
        TextView label;
        
        activity = this.getProxy().getActivity();
        label = new LabelTextView(activity, this.layoutParams, this.wordWrap, this.ellipsize, proxy);
        label.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        label.setPadding(0, 0, 0, 0);
        label.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        label.setFocusable(false);
        label.setSingleLine(false);
        
        TiUIHelper.styleText(label, null);
        
        this.defaultColor = label.getCurrentTextColor();
        this.setNativeView(label);
    }
    
    @Override
    public void processProperties(KrollDict dict) {
        super.processProperties(dict);
        
        LabelTextView view = (LabelTextView)this.getNativeView();
        boolean needShadow = true;
        
        // Only accept one, html has priority
        if (dict.containsKey(TiC.PROPERTY_HTML)) {
            String html = TiConvert.toString(dict, TiC.PROPERTY_HTML);
            if (html == null) {
                //If html is null, set text if applicable
                if (dict.containsKey(TiC.PROPERTY_TEXT))
                    view.setText(TiConvert.toString(dict,TiC.PROPERTY_TEXT));
                else
                    view.setText(Html.fromHtml(""));
            }
            else {
                view.setMovementMethod(null);
                view.setText(Html.fromHtml(html));
            }
        }
        else if (dict.containsKey(TiC.PROPERTY_TEXT))
            view.setText(TiConvert.toString(dict,TiC.PROPERTY_TEXT));
        else if (dict.containsKey(TiC.PROPERTY_TITLE))
            // For table view rows
            view.setText(TiConvert.toString(dict,TiC.PROPERTY_TITLE));

        if (dict.containsKey(TiC.PROPERTY_INCLUDE_FONT_PADDING))
            view.setIncludeFontPadding(TiConvert.toBoolean(dict, TiC.PROPERTY_INCLUDE_FONT_PADDING, true));

        if (dict.containsKey(TiC.PROPERTY_COLOR)) {
            Object color = dict.get(TiC.PROPERTY_COLOR);
            if (color == null)
                view.setTextColor(defaultColor);
            else
                view.setTextColor(TiConvert.toColor(dict, TiC.PROPERTY_COLOR));
        }
        if (dict.containsKey(TiC.PROPERTY_HIGHLIGHTED_COLOR))
            view.setHighlightColor(TiConvert.toColor(dict, TiC.PROPERTY_HIGHLIGHTED_COLOR));
        
        if (dict.containsKey(TiC.PROPERTY_FONT))
            TiUIHelper.styleText(view, dict.getKrollDict(TiC.PROPERTY_FONT));
        
        if (dict.containsKey(TiC.PROPERTY_TEXT_ALIGN) || dict.containsKey(TiC.PROPERTY_VERTICAL_ALIGN)) {
            String textAlign = dict.optString(TiC.PROPERTY_TEXT_ALIGN, "left");
            String verticalAlign = dict.optString(TiC.PROPERTY_VERTICAL_ALIGN, "middle");
            TiUIHelper.setAlignment(view, textAlign, verticalAlign);
        }
        
        if (dict.containsKey(TiC.PROPERTY_ELLIPSIZE)) {
            this.ellipsize = TiConvert.toBoolean(dict, TiC.PROPERTY_ELLIPSIZE, false);
            view.setEllipsized(this.ellipsize);
        }
        
        if (dict.containsKey(TiC.PROPERTY_WORD_WRAP)) {
            this.wordWrap = TiConvert.toBoolean(dict, TiC.PROPERTY_WORD_WRAP, true);
            view.setWordWrap(this.wordWrap);
        }
        
        if (dict.containsKey(TiC.PROPERTY_SHADOW_OFFSET)) {
            Object value = dict.get(TiC.PROPERTY_SHADOW_OFFSET);
            
            if (value instanceof HashMap) {
                @SuppressWarnings("rawtypes")
                HashMap hash = (HashMap)value;
                needShadow = true;
                
                this.shadowX = TiConvert.toFloat(hash.get(TiC.PROPERTY_X), 0);
                this.shadowY = TiConvert.toFloat(hash.get(TiC.PROPERTY_Y), 0);
            }
        }
        
        if (dict.containsKey(TiC.PROPERTY_SHADOW_RADIUS)) {
            needShadow = true;
            this.shadowRadius = TiConvert.toFloat(dict.get(TiC.PROPERTY_SHADOW_RADIUS), DEFAULT_SHADOW_RADIUS);
        }
        
        if (dict.containsKey(TiC.PROPERTY_SHADOW_COLOR)) {
            needShadow = true;
            this.shadowColor = TiConvert.toColor(dict, TiC.PROPERTY_SHADOW_COLOR);
        }
        
        if (dict.containsKey(LabelimConstants.PROPERTY_MAX_LINES))
            view.setMaxLines(TiConvert.toInt(dict.get(LabelimConstants.PROPERTY_MAX_LINES), -1));
        
        if (dict.containsKey(LabelimConstants.PROPERTY_SCROLL_HORIZONTALLY))
            view.setHorizontallyScrolling(TiConvert.toBoolean(dict.get(LabelimConstants.PROPERTY_SCROLL_HORIZONTALLY), false));
        
        if(dict.containsKey(LabelimConstants.PROPERTY_LINE_SPACING_EXTRA) && 
           dict.containsKey(LabelimConstants.PROPERTY_LINE_SPACING_MULTIPLIER))
            view.setLineSpacing(TiConvert.toFloat(dict.get(LabelimConstants.PROPERTY_LINE_SPACING_EXTRA)),
                                TiConvert.toFloat(dict.get(LabelimConstants.PROPERTY_LINE_SPACING_MULTIPLIER)));
            
        if (needShadow)
            view.setShadowLayer(this.shadowRadius, this.shadowX, this.shadowY, this.shadowColor);
        
        TiUIHelper.linkifyIfEnabled(view, dict.get(TiC.PROPERTY_AUTO_LINK));
        view.invalidate();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        LabelTextView view = (LabelTextView)this.getNativeView();
        
        if (key.equals(TiC.PROPERTY_HTML)) {
            view.setText(Html.fromHtml(TiConvert.toString(newValue)));
            TiUIHelper.linkifyIfEnabled(view, proxy.getProperty(TiC.PROPERTY_AUTO_LINK));
            view.requestLayout();
        }
        else if (key.equals(TiC.PROPERTY_TEXT) || key.equals(TiC.PROPERTY_TITLE)) {
            view.setText(TiConvert.toString(newValue));
            TiUIHelper.linkifyIfEnabled(view, proxy.getProperty(TiC.PROPERTY_AUTO_LINK));
            view.requestLayout();
        }
        else if (key.equals(TiC.PROPERTY_INCLUDE_FONT_PADDING))
            view.setIncludeFontPadding(TiConvert.toBoolean(newValue, true));
        else if (key.equals(TiC.PROPERTY_COLOR)) {
            if (newValue == null)
                view.setTextColor(defaultColor);
            else
                view.setTextColor(TiConvert.toColor((String) newValue));
        }
        else if (key.equals(TiC.PROPERTY_HIGHLIGHTED_COLOR))
            view.setHighlightColor(TiConvert.toColor((String) newValue));
        else if (key.equals(TiC.PROPERTY_TEXT_ALIGN)) {
            TiUIHelper.setAlignment(view, TiConvert.toString(newValue), null);
            view.requestLayout();
        }
        else if (key.equals(TiC.PROPERTY_VERTICAL_ALIGN)) {
            TiUIHelper.setAlignment(view, null, TiConvert.toString(newValue));
            view.requestLayout();
        }
        else if (key.equals(TiC.PROPERTY_FONT)) {
            @SuppressWarnings("rawtypes")
            HashMap hash = (HashMap)newValue;
            
            TiUIHelper.styleText(view, hash);
            view.requestLayout();
        }
        else if (key.equals(TiC.PROPERTY_ELLIPSIZE)) {
            ellipsize = TiConvert.toBoolean(newValue, false);
            view.setEllipsized(ellipsize);
        }
        else if (key.equals(TiC.PROPERTY_WORD_WRAP)) {
            wordWrap = TiConvert.toBoolean(newValue, true);
            view.setWordWrap(wordWrap);
        }
        else if (key.equals(TiC.PROPERTY_AUTO_LINK))
            Linkify.addLinks(view, TiConvert.toInt(newValue));
        else if (key.equals(TiC.PROPERTY_SHADOW_OFFSET)) {
            if (newValue instanceof HashMap) {
                @SuppressWarnings("rawtypes")
                HashMap dict = (HashMap)newValue;
                
                this.shadowX = TiConvert.toFloat(dict.get(TiC.PROPERTY_X), 0);
                this.shadowY = TiConvert.toFloat(dict.get(TiC.PROPERTY_Y), 0);
                
                view.setShadowLayer(this.shadowRadius, this.shadowX, this.shadowY, this.shadowColor);
            }
        }
        else if (key.equals(TiC.PROPERTY_SHADOW_RADIUS)) {
            this.shadowRadius = TiConvert.toFloat(newValue, DEFAULT_SHADOW_RADIUS);
            view.setShadowLayer(shadowRadius, shadowX, shadowY, shadowColor);
        }
        else if (key.equals(TiC.PROPERTY_SHADOW_COLOR)) {
            this.shadowColor = TiConvert.toColor(TiConvert.toString(newValue));
            view.setShadowLayer(shadowRadius, shadowX, shadowY, shadowColor);
        }
        else if (key.equals(LabelimConstants.PROPERTY_MAX_LINES))
            view.setMaxLines(TiConvert.toInt(newValue));
        else if (key.equals(LabelimConstants.PROPERTY_SCROLL_HORIZONTALLY))
            view.setHorizontallyScrolling(TiConvert.toBoolean(newValue));
        else
            super.propertyChanged(key, oldValue, newValue, proxy);
    }
}
