package com.xboxcollectorsplace.utils;

import com.xboxcollectorsplace.App;
import com.xboxcollectorsplace.R;
import com.xboxcollectorsplace.bl.BLParameters;
import com.xboxcollectorsplace.ui.SynopsisDetailActivity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Overriden WebView used to show justified text paragraphs.
 */
public class JustifiedTextView extends WebView
{
    private String _core = "<html><body style='text-align:justify;color:rgba(%s);font-family: sans-serif-light;font-size:%dpx;margin: 0px 0px 0px 0px;'>%s</body></html>";
    private String _textColor = "0,0,0,255";
    private String _completeText = "";
    private String _text = "";
    private int _textSize = 13;

    public JustifiedTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.setWebChromeClient(new WebChromeClient(){});
    }

    public void setTextSize(int textSize)
    {
	    this._textSize = textSize;
    }
    
    public void setText(String s, int truncatePosition)
    {
        this._text = s;
        this.reloadData(truncatePosition);
    }
    
    private void reloadData(int truncatePosition)
    {
        this.getSettings().setDefaultTextEncodingName(BLParameters.GENERAL_PARAMETERS.CODIFICATION_UTF_8);

        // If the device is a tablet, the text is set bigger and the position is truncated in a
        // further position
        int textSize = this._textSize;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
	    if (isTablet)
	    {
	    	truncatePosition = truncatePosition * 2;
	    	textSize = textSize + 2;
	    }
	    
	    // If the text is too long, its truncated, a text is added at the end indicated that
	    // it can be touched to expand, and a listener is added to call "SynopsisDetailActivity"
	    // in order to show the full text in a new window
        if (truncatePosition != 0 && this._text.length() > truncatePosition)
        {
        	this._completeText = this._text;
        	this._text = this._text.substring(0, truncatePosition - 25) + "... <i>" + App.getContext().getResources().getString(R.string.detail_read_more).replace("á", "&aacute;").replace("ü", "&uuml;") + "</i>";
        	
        	this.setOnTouchListener(new OnTouchListener() 
        	{
				public boolean onTouch(View view, MotionEvent event) 
				{
					if (event.getAction() == MotionEvent.ACTION_UP)
					{
						Intent intent = new Intent(App.getContext(), SynopsisDetailActivity.class);
						intent.putExtra(BLParameters.PARAMETERS.SYNOPSIS, _completeText);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    		App.getContext().startActivity(intent);
					}
					
					return false;
				}
			});
        }
        
        this.loadData(String.format(this._core, this._textColor, textSize, this._text), "text/html", BLParameters.GENERAL_PARAMETERS.CODIFICATION_UTF_8);
	}

    public void setTextColor(int hex, int truncatePosition)
    {
        String h = Integer.toHexString(hex);
        int a = Integer.parseInt(h.substring(0, 2),16);
        int r = Integer.parseInt(h.substring(2, 4),16);
        int g = Integer.parseInt(h.substring(4, 6),16);
        int b = Integer.parseInt(h.substring(6, 8),16);
        this._textColor = String.format("%d,%d,%d,%d", r, g, b, a); 
        this.reloadData(truncatePosition);
    }

    public void setTextSize(int textSize, int truncatePosition)
    {
        this._textSize = textSize;
        this.reloadData(truncatePosition);
    }
}