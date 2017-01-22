package com.hipad.smarthome.utils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.TextView;

/**
 * 
 * @author guowei
 *
 */
public class MTextView extends TextView
{
	
	private static HashMap<String, SoftReference<MeasuredData>> measuredData = new HashMap<String, SoftReference<MeasuredData>>();
	private static int hashIndex = 0;
	
	ArrayList<LINE> contentList = new ArrayList<LINE>();
	private Context context;

	private TextPaint paint = new TextPaint();
	
//	private float lineSpacingMult = 0.5f;
	private int textColor = Color.BLACK;
	private float lineSpacing;
	private int lineSpacingDP = 5;
	private int maxWidth;
	private int oneLineWidth = -1;
	private float lineWidthMax = -1;
	private ArrayList<Object> obList = new ArrayList<Object>();
	private boolean useDefault = false;
	private CharSequence text = "";

	private int minHeight;
	private DisplayMetrics displayMetrics;
	private Paint textBgColorPaint = new Paint();
	private Rect textBgColorRect = new Rect();

	public MTextView(Context context)
	{
		super(context);
		this.context = context;
		paint.setAntiAlias(true);
		lineSpacing = dip2px(context, lineSpacingDP);
		minHeight = dip2px(context, 30);

		displayMetrics = new DisplayMetrics();
	}
	
	public MTextView(Context context,AttributeSet attrs)
	{
		super(context,attrs);
		this.context = context;
		paint.setAntiAlias(true);
		lineSpacing = dip2px(context, lineSpacingDP);
		minHeight = dip2px(context, 30);

		displayMetrics = new DisplayMetrics();
	}

	public static int px2sp(Context context, float pxValue)
	{
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	public static int dip2px(Context context, float dpValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	@Override
	public void setMaxWidth(int maxpixels)
	{
		super.setMaxWidth(maxpixels);
		maxWidth = maxpixels;
	}

	@Override
	public void setMinHeight(int minHeight)
	{
		super.setMinHeight(minHeight);
		this.minHeight = minHeight;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		if (useDefault)
		{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}
		int width = 0, height = 0;

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		switch (widthMode)
		{
		case MeasureSpec.EXACTLY:
			width = widthSize;
			break;
		case MeasureSpec.AT_MOST:
			width = widthSize;
			break;
		case MeasureSpec.UNSPECIFIED:

			((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			width = displayMetrics.widthPixels;
			break;
		default:
			break;
		}
		if (maxWidth > 0)
			width = Math.min(width, maxWidth);

		paint.setTextSize(this.getTextSize());
		paint.setColor(textColor);
		int realHeight = measureContentHeight((int) width);

		int leftPadding = getCompoundPaddingLeft();
		int rightPadding = getCompoundPaddingRight();
		width = Math.min(width, (int) lineWidthMax + leftPadding + rightPadding);

		if (oneLineWidth > -1)
		{
			width = oneLineWidth;
		}
		switch (heightMode)
		{
		case MeasureSpec.EXACTLY:
			height = heightSize;
			break;
		case MeasureSpec.AT_MOST:
			height = realHeight;
			break;
		case MeasureSpec.UNSPECIFIED:
			height = realHeight;
			break;
		default:
			break;
		}

		height += getCompoundPaddingTop() + getCompoundPaddingBottom();

		height = Math.max(height, minHeight);

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if (useDefault)
		{
			super.onDraw(canvas);
			return;
		}
		if (contentList.isEmpty())
			return;
		int width;

		Object ob;

		int leftPadding = getCompoundPaddingLeft();
		int topPadding = getCompoundPaddingTop();

		float height = 0 + topPadding + lineSpacing;
		if (oneLineWidth != -1)
		{
			height = getMeasuredHeight() / 2 - contentList.get(0).height / 2;
		}

		for (LINE aContentList : contentList)
		{
			float realDrawedWidth = leftPadding;
			for (int j = 0; j < aContentList.line.size(); j++)
			{
				ob = aContentList.line.get(j);
				width = aContentList.widthList.get(j);

				if (ob instanceof String)
				{
					canvas.drawText((String) ob, realDrawedWidth, height + aContentList.height - paint.getFontMetrics().descent, paint);
					realDrawedWidth += width;
				}
				else if (ob instanceof SpanObject)
				{
					Object span = ((SpanObject) ob).span;
					if(span instanceof ImageSpan)
					{
						ImageSpan is = (ImageSpan) span;
						Drawable d = is.getDrawable();
	
						int left = (int) (realDrawedWidth);
						int top = (int) height;
						int right = (int) (realDrawedWidth + width);
						int bottom = (int) (height + aContentList.height);
						d.setBounds(left, top, right, bottom);
						d.draw(canvas);
						realDrawedWidth += width;
					}
					else if(span instanceof BackgroundColorSpan)
					{
						
						textBgColorPaint.setColor(((BackgroundColorSpan) span).getBackgroundColor());
						textBgColorPaint.setStyle(Style.FILL);
						textBgColorRect.left = (int) realDrawedWidth;
						int textHeight = (int) getTextSize();
						textBgColorRect.top = (int) (height + aContentList.height - textHeight - paint.getFontMetrics().descent);
						textBgColorRect.right = textBgColorRect.left+width;
						textBgColorRect.bottom = (int) (height + aContentList.height + lineSpacing  - paint.getFontMetrics().descent);
    					canvas.drawRect(textBgColorRect, textBgColorPaint);
						canvas.drawText(((SpanObject) ob).source.toString(), realDrawedWidth, height + aContentList.height - paint.getFontMetrics().descent, paint);
						realDrawedWidth += width;
					}
					else
					{
						canvas.drawText(((SpanObject) ob).source.toString(), realDrawedWidth, height + aContentList.height - paint.getFontMetrics().descent, paint);
						realDrawedWidth += width;
					}
				}

			}
			height += aContentList.height + lineSpacing;
		}

	}

	@Override
	public void setTextColor(int color)
	{
		super.setTextColor(color);
		textColor = color;
	}

	private int measureContentHeight(int width)
	{
		int cachedHeight = getCachedData(text.toString(), width);

		if (cachedHeight > 0)
		{
			return cachedHeight;
		}

		float obWidth = 0;
		float obHeight = 0;

		float textSize = this.getTextSize();
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = fontMetrics.bottom - fontMetrics.top;
		float height = lineSpacing;

		int leftPadding = getCompoundPaddingLeft();
		int rightPadding = getCompoundPaddingRight();

		float drawedWidth = 0;
		
		boolean splitFlag = false;

		width = width - leftPadding - rightPadding;

		oneLineWidth = -1;

		contentList.clear();

		StringBuilder sb;

		LINE line = new LINE();

		for (int i = 0; i < obList.size(); i++)
		{
			Object ob = obList.get(i);

			if (ob instanceof String)
			{

				obWidth = paint.measureText((String) ob);
				obHeight = textSize;
			}
			else if (ob instanceof SpanObject)
			{
				Object span = ((SpanObject) ob).span;
				if(span instanceof ImageSpan)
				{
					Rect r = ((ImageSpan)span).getDrawable().getBounds();
					obWidth = r.right - r.left;
					obHeight = r.bottom - r.top;
					if (obHeight > lineHeight)
						lineHeight = obHeight;
				}
				else if(span instanceof BackgroundColorSpan)
				{
					String str = ((SpanObject) ob).source.toString();
					obWidth = paint.measureText(str);
					obHeight = textSize;
					
					int k= str.length()-1;
					while(width - drawedWidth < obWidth)
					{
						obWidth = paint.measureText(str.substring(0,k--));
					}
					if(k < str.length()-1)
					{
						splitFlag = true;
						SpanObject so1 = new SpanObject();
						so1.start = ((SpanObject) ob).start;
						so1.end = so1.start + k;
						so1.source = str.substring(0,k+1);
						so1.span = ((SpanObject) ob).span;
						
						SpanObject so2 = new SpanObject();
						so2.start =  so1.end;
						so2.end = ((SpanObject) ob).end;
						so2.source = str.substring(k+1,str.length());
						so2.span = ((SpanObject) ob).span;
						
						ob = so1;
						obList.set(i,so2);
						i--;
					}
				}
				else
				{
					String str = ((SpanObject) ob).source.toString();
					obWidth = paint.measureText(str);
					obHeight = textSize;
				}
			}

			if (width - drawedWidth < obWidth || splitFlag)
			{
				splitFlag = false;
				contentList.add(line);

				if (drawedWidth > lineWidthMax)
				{
					lineWidthMax = drawedWidth;
				}
				drawedWidth = 0;
				height += line.height + lineSpacing;

				lineHeight = obHeight;

				line = new LINE();
			}

			drawedWidth += obWidth;

			if (ob instanceof String && line.line.size() > 0 && (line.line.get(line.line.size() - 1) instanceof String))
			{
				int size = line.line.size();
				sb = new StringBuilder();
				sb.append(line.line.get(size - 1));
				sb.append(ob);
				ob = sb.toString();
				obWidth = obWidth + line.widthList.get(size - 1);
				line.line.set(size - 1, ob);
				line.widthList.set(size - 1, (int) obWidth);
				line.height = (int) lineHeight;

			}
			else
			{
				line.line.add(ob);
				line.widthList.add((int) obWidth);
				line.height = (int) lineHeight;
			}

		}
		
		if (drawedWidth > lineWidthMax)
		{
			lineWidthMax = drawedWidth;
		}
		
		if (line != null && line.line.size() > 0)
		{
			contentList.add(line);
			height += lineHeight + lineSpacing;
		}
		if (contentList.size() <= 1)
		{
			oneLineWidth = (int) drawedWidth + leftPadding + rightPadding;
			height = lineSpacing + lineHeight + lineSpacing;
		}

		cacheData(width, (int) height);
		return (int) height;
	}

	@SuppressWarnings("unchecked")
	private int getCachedData(String text, int width)
	{
		SoftReference<MeasuredData> cache = measuredData.get(text);
		if (cache == null)
			return -1;
		MeasuredData md = cache.get();
		if (md != null && md.textSize == this.getTextSize() && width == md.width)
		{
			lineWidthMax = md.lineWidthMax;
			contentList = (ArrayList<LINE>) md.contentList.clone();
			oneLineWidth = md.oneLineWidth;

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < contentList.size(); i++)
			{
				LINE line = contentList.get(i);
				sb.append(line.toString());
			}
			return md.measuredHeight;
		}
		else
			return -1;
	}

	@SuppressWarnings("unchecked")
	private void cacheData(int width, int height)
	{
		MeasuredData md = new MeasuredData();
		md.contentList = (ArrayList<LINE>) contentList.clone();
		md.textSize = this.getTextSize();
		md.lineWidthMax = lineWidthMax;
		md.oneLineWidth = oneLineWidth;
		md.measuredHeight = height;
		md.width = width;
		md.hashIndex = ++hashIndex;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < contentList.size(); i++)
		{
			LINE line = contentList.get(i);
			sb.append(line.toString());
		}

		SoftReference<MeasuredData> cache = new SoftReference<MeasuredData>(md);
		measuredData.put(text.toString(), cache);
	}

	public void setMText(CharSequence cs)
	{
		text = cs;

		obList.clear();

		ArrayList<SpanObject> isList = new ArrayList<MTextView.SpanObject>();
		useDefault = false;
		if (cs instanceof SpannableString)
		{
			SpannableString ss = (SpannableString) cs;
			CharacterStyle[] spans = ss.getSpans(0, ss.length(), CharacterStyle.class);
			for (int i = 0; i < spans.length; i++)
			{
				
				int s = ss.getSpanStart(spans[i]);
				int e = ss.getSpanEnd(spans[i]);
				SpanObject iS = new SpanObject();
				iS.span = spans[i];
				iS.start = s;
				iS.end = e;
				iS.source = ss.subSequence(s, e);
				isList.add(iS);
			}
		}
		
		SpanObject[] spanArray = new SpanObject[isList.size()];
		isList.toArray(spanArray);
		Arrays.sort(spanArray,0,spanArray.length,new SpanObjectComparator());
		isList.clear();
		for(int i=0;i<spanArray.length;i++)
		{
			isList.add(spanArray[i]);
		}
		
		String str = cs.toString();

		for (int i = 0, j = 0; i < cs.length(); )
		{
			if (j < isList.size())
			{
				SpanObject is = isList.get(j);
				if (i < is.start)
				{
					Integer cp = str.codePointAt(i);
					if (Character.isSupplementaryCodePoint(cp))
					{
						i += 2;
					}
					else
					{
						i++;
					}

					obList.add(new String(Character.toChars(cp)));

				}
				else if (i >= is.start)
				{
					obList.add(is);
					j++;
					i = is.end;
				}
			}
			else
			{
				Integer cp = str.codePointAt(i);
				if (Character.isSupplementaryCodePoint(cp))
				{
					i += 2;
				}
				else
				{
					i++;
				}

				obList.add(new String(Character.toChars(cp)));
			}
		}

		requestLayout();
	}

	public void setUseDefault(boolean useDefault)
	{
		this.useDefault = useDefault;
		if (useDefault)
		{
			this.setText(text);
			this.setTextColor(textColor);
		}
	}
	public void setLineSpacingDP(int lineSpacingDP)
	{
		this.lineSpacingDP = lineSpacingDP;
		lineSpacing = dip2px(context, lineSpacingDP);
	}
	public int getLineSpacingDP()
	{
		return lineSpacingDP;
	}
	class SpanObject
	{
		public Object span;
		public int start;
		public int end;
		public CharSequence source;
	}
	class SpanObjectComparator implements Comparator<SpanObject>
	{
		@Override
		public int compare(SpanObject lhs, SpanObject rhs)
		{
			
			return lhs.start - rhs.start;
		}
		
	}
	class LINE
	{
		public ArrayList<Object> line = new ArrayList<Object>();
		public ArrayList<Integer> widthList = new ArrayList<Integer>();
		public int height;

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("height:" + height + "   ");
			for (int i = 0; i < line.size(); i++)
			{
				sb.append(line.get(i) + ":" + widthList.get(i));
			}
			return sb.toString();
		}

	}

	class MeasuredData
	{
		public int measuredHeight;
		public float textSize;
		public int width;
		public float lineWidthMax;
		public int oneLineWidth;
		public int hashIndex;
		ArrayList<LINE> contentList;

	}

}