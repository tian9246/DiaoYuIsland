package com.clov4r.android.recommend.view;

import java.util.ArrayList;

import com.clov4r.android.recommend.RecommendActivity;
import org.mummy.activity.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.renderscript.Font.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class RecommendTitleView extends ImageView {
	ArrayList<Bitmap> iconStack = new ArrayList<Bitmap>();
	ArrayList<String> titleStack = new ArrayList<String>();
	Bitmap indicator = null;// , device_pc = null
	Bitmap background = null;
	int arrowWidth = 0;
	int viewWidth = 0;
	int viewHeight = 0;
	int textSize = 21;
	int lineCount = 1;
	int topMargin = 0;
	Paint textPaint = null;

	DisplayMetrics dm = null;

	public RecommendTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		indicator = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.dlna_arrow);
		background = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.title_bg);
		// device_pc = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.dlna_device_pc);
		// if (device_pc != null)
		// viewHeight = device_pc.getHeight();
		if (indicator != null) {
			viewHeight = background.getHeight();// indicator.getHeight() * 3;
			arrowWidth = indicator.getWidth();
		}

		dm = new DisplayMetrics();
		((RecommendActivity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;

		viewWidth = width;//
		textPaint = new Paint();
		textPaint.setTextSize(textSize);
		textPaint.setColor(Color.BLACK);
		textPaint.setShadowLayer(1, 1, 1, Color.WHITE);// setStyle(style.ShadowStyleForWhite);

		String familyName = "楷体";
		Typeface font = Typeface.create(familyName, Typeface.BOLD);
		textPaint.setTypeface(font);
		FontMetrics fm = textPaint.getFontMetrics();

		viewHeight = (int) (Math.ceil(fm.descent - fm.top) + 2) * 2;//
		topMargin = (viewHeight - arrowWidth) / 2;

		setBackgroundResource(R.drawable.title_bg);

	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(viewWidth, viewHeight * lineCount);
	}

	public void setWidth(int width) {
		viewWidth = width;
	}

	/**
	 * ������һ��
	 * 
	 * @param type
	 * @param text
	 */
	public void enterNext(int type, String text) {
		if (type == 0) {
			iconStack.clear();
			titleStack.clear();
			iconStack.add(null);
		} else
			iconStack.add(indicator);
		titleStack.add(text);
		invalidate();
	}

	/**
	 * �ص���һ��
	 */
	public void backPrevious() {
		if (iconStack.size() > 0)
			iconStack.remove(iconStack.size() - 1);
		if (titleStack.size() > 0)
			titleStack.remove(titleStack.size() - 1);
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = 0;
		int left = 10;
		lineCount = 1;
		for (int i = 0; i < iconStack.size(); i++) {
			String title = titleStack.get(i) + "";
			Bitmap tempMap = iconStack.get(i);
			// if (tempMap == null)
			// tempMap = indicator;
			if (tempMap != null) {
				int mapWidth = tempMap.getWidth();
				int topMargin = (viewHeight - tempMap.getHeight()) / 2;
				if (mapWidth > viewWidth - width) {
					lineCount++;
					left = 10;
					width = 0;
					measure(MeasureSpec.makeMeasureSpec(getWidth(),
							MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
							getHeight(), MeasureSpec.EXACTLY));
				} else {
					width += mapWidth;
				}
				canvas.drawBitmap(tempMap, left, (lineCount - 1) * viewHeight
						+ topMargin, null);
				left += mapWidth;
			}

			if (title != null && !"".equals(title)) {
				int textTopMargin = (viewHeight - textSize) / 2;
				for (int j = 0; j < title.length(); j++) {
					String tempTitle = title.substring(j, j + 1);
					float tempWidth = textPaint.measureText(tempTitle);
					// int topMargin = (int) (viewHeight - tempWidth);
					if (tempWidth > viewWidth - width) {
						lineCount++;
						left = 10;
						width = 0;
					} else {
						width += tempWidth;
					}
					canvas.drawText(tempTitle, left, (lineCount - 1)
							* viewHeight + textTopMargin * 2, textPaint);
					// (int) ((lineCount - 0.5)
					// * viewHeight + 0)
					left += tempWidth;
				}
			}
		}
	}
}
