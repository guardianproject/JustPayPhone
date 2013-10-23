package info.guardianproject.justpayphone.app.views;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.utils.FontManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomFontButton extends Button {

	public CustomFontButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(attrs);
	}

	public CustomFontButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs);
	}

	public CustomFontButton(Context context)
	{
		super(context);
		init(null);
	}
	
	private void init(AttributeSet attrs)
	{
		if (attrs != null)
		{
			TypedArray a = getContext().obtainStyledAttributes(attrs, new int[] { R.attr.asset_font });
			String fontName = a.getString(0);
			if (fontName != null && !isInEditMode())
			{
				Typeface font = FontManager.getFontByName(getContext(), fontName);
				if (font != null)
					this.setTypeface(font);
			}
			a.recycle();
		}
	}
}
