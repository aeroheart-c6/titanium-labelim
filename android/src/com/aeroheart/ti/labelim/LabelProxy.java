package com.aeroheart.ti.labelim;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;


@Kroll.proxy(creatableInModule=LabelimModule.class, propertyAccessors={
    TiC.PROPERTY_AUTO_LINK,
    TiC.PROPERTY_COLOR,
    TiC.PROPERTY_ELLIPSIZE,
    TiC.PROPERTY_FONT,
    TiC.PROPERTY_HIGHLIGHTED_COLOR,
    TiC.PROPERTY_HTML,
    TiC.PROPERTY_TEXT,
    TiC.PROPERTY_TEXT_ALIGN,
    TiC.PROPERTY_TEXTID,
    TiC.PROPERTY_WORD_WRAP,
    TiC.PROPERTY_VERTICAL_ALIGN,
    TiC.PROPERTY_SHADOW_OFFSET,
    TiC.PROPERTY_SHADOW_COLOR,
    TiC.PROPERTY_SHADOW_RADIUS,
    TiC.PROPERTY_INCLUDE_FONT_PADDING,
    LabelimConstants.PROPERTY_MAX_LINES,
    LabelimConstants.PROPERTY_SCROLL_HORIZONTALLY
})
public class LabelProxy extends TiViewProxy {
    public LabelProxy() {
        super();
        
    }
    public LabelProxy(TiContext context) {
        this();
    }
    
    @Override
    protected KrollDict getLangConversionTable() {
        KrollDict table;
        
        table = new KrollDict();
        table.put(TiC.PROPERTY_TEXT, TiC.PROPERTY_TEXTID);
        
        return table;
    }
    
    @Override
    public TiUIView createView(Activity activity) {
        return new Label(this);
    }
    
    @Override
    public String getApiName() {
        return "Aeroheart.Ti.Labelim";
    }
}
