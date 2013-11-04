package info.guardianproject.justpayphone.app.views;

import info.guardianproject.justpayphone.R;
import info.guardianproject.justpayphone.utils.FontManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.Region.Op;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class BubbleView extends TextView {

	private int mPointerSize;
	private Path mPath;
	private Paint mPaint;

	public BubbleView(Context context) {
		super(context);
		init(null);
	}

	public BubbleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public BubbleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	private void init(AttributeSet attrs)
	{
		mPointerSize = 40;
		
		int bubbleColor = Color.TRANSPARENT;
		if (isInEditMode())
			bubbleColor = 0x80ffffff;
		
		if (attrs != null)
		{
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleView);
			mPointerSize = a.getDimensionPixelSize(R.styleable.BubbleView_pointer_size, mPointerSize);
			bubbleColor = a.getColor(R.styleable.BubbleView_bubble_color, bubbleColor);
			a.recycle();
		}
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(bubbleColor);
		mPaint.setStyle(Paint.Style.FILL);
		
		this.setBackgroundColor(Color.TRANSPARENT);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w > 0 && h > 0)
		{
			int pointerStartOffset = (getWidth() / 2) - (mPointerSize / 2);

			double pHeight = Math.sqrt(((double)mPointerSize * mPointerSize) / 2.0);
			
			mPath = new Path();
			mPath.moveTo(0, (float) pHeight);
			mPath.lineTo(pointerStartOffset, (float) pHeight);
			mPath.lineTo(pointerStartOffset + (mPointerSize / 2), 0);
			mPath.lineTo(pointerStartOffset + mPointerSize, (float) pHeight);
			mPath.lineTo(w, (float) pHeight);
			mPath.lineTo(w, h);
			mPath.lineTo(0, h);
			mPath.close();			
		}
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		canvas.drawPath(mPath, mPaint);
		super.onDraw(canvas);
	}
}
