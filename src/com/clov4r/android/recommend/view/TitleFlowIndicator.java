/*
 * Copyright (C) 2011 Patrik �kerfeldt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clov4r.android.recommend.view;

import java.util.ArrayList;

import org.mummy.activity.R;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * A TitleFlowIndicator is a FlowIndicator which displays the title of left view
 * (if exist), the title of the current select view (centered) and the title of
 * the right view (if exist). When the user scrolls the ViewFlow then titles are
 * also scrolled.
 * 
 */
public class TitleFlowIndicator extends TextView implements FlowIndicator {

	private static final int TITLE_PADDING = 10;
	private static final int SELECTED_COLOR = 0xFFFFC445;
	private static final int TEXT_COLOR = 0xff060606;// AAAAAA;
	private static final int TEXT_SIZE = 20;
	private static final int FOOTER_LINE_HEIGHT = 2;
	private static final int FOOTER_COLOR = 0xFFFFC445;//
	private static final int FOOTER_TRIANGLE_HEIGHT = 10;
	private static final int FOOTER_BAR_HEIGH = 70;
	private ViewFlow viewFlow;
	private int currentScroll = 0;
	private TitleProvider titleProvider = null;
	private int currentPosition = 0;
	private Paint paintText;
	private Paint paintSelected;
	private Path path;
	private Paint paintFooterLine;
	private Paint paintFooterBar;
	private Paint paintFooterTriangle;
	private int footerTriangleHeight;
	private int titlePadding;
	private int footerLineHeight;
	private int footerBarHeight;

	int viewHeight = 0;

	/**
	 * Default constructor
	 */
	public TitleFlowIndicator(Context context) {
		super(context);
		initDraw(TEXT_COLOR, TEXT_SIZE, SELECTED_COLOR, FOOTER_LINE_HEIGHT,
				FOOTER_COLOR, FOOTER_BAR_HEIGH);
	}

	/**
	 * The contructor used with an inflater
	 * 
	 * @param context
	 * @param attrs
	 */
	public TitleFlowIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Retrieve styles attributs
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TitleFlowIndicator);
		// Retrieve the colors to be used for this view and apply them.
		int footerColor = a.getColor(
				R.styleable.TitleFlowIndicator_footerColor, FOOTER_COLOR);
		footerLineHeight = a.getInt(
				R.styleable.TitleFlowIndicator_footerLineHeight,
				FOOTER_LINE_HEIGHT);
		footerTriangleHeight = a.getInt(
				R.styleable.TitleFlowIndicator_footerTriangleHeight,
				FOOTER_TRIANGLE_HEIGHT);
		// footerBarHeight =
		// a.getInt(R.styleable.TitleFlowIndicator_footerBarHeight,
		// FOOTER_BAR_HEIGH);
		int selectedColor = a.getColor(
				R.styleable.TitleFlowIndicator_selectedColor, SELECTED_COLOR);
		// int textColor = a.getColor(R.styleable.TitleFlowIndicator_textColor,
		// TEXT_COLOR);
		int textColor = TEXT_COLOR;
		float textSize = a.getFloat(R.styleable.TitleFlowIndicator_textSize,
				TEXT_SIZE);
		titlePadding = a.getInt(R.styleable.TitleFlowIndicator_titlePadding,
				TITLE_PADDING);
		initDraw(textColor, textSize, selectedColor, footerLineHeight,
				footerColor, footerBarHeight);

	}

	/**
	 * Initialize draw objects
	 */
	private void initDraw(int textColor, float textSize, int selectedColor,
			int footerLineHeight, int footerColor, int footerBarHeight) {
		paintText = new Paint();
		paintText.setColor(textColor);
		paintText.setTextSize(textSize);
		paintText.setAntiAlias(true);
		paintSelected = new Paint();
		paintSelected.setColor(selectedColor);
		paintSelected.setTextSize(textSize);
		paintSelected.setAntiAlias(true);
		paintFooterLine = new Paint();
		paintFooterLine.setStyle(Paint.Style.FILL_AND_STROKE);
		paintFooterLine.setStrokeWidth(footerLineHeight);
		// paintFooterBar = new Paint();
		// paintFooterBar.setStyle(Paint.Style.FILL_AND_STROKE);
		// paintFooterBar.setStrokeWidth(footerBarHeight);
		paintFooterLine.setColor(FOOTER_COLOR);
		paintFooterTriangle = new Paint();
		paintFooterTriangle.setStyle(Paint.Style.FILL_AND_STROKE);
		paintFooterTriangle.setColor(footerColor);

		FontMetrics fm = paintText.getFontMetrics();

		viewHeight = (int) (Math.ceil(fm.descent - fm.top) + 2) * 3;//
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Calculate views bounds
		ArrayList<Rect> bounds = calculateAllBounds(paintText);

		if (bounds == null || bounds.size() == 0)
			return;

		// If no value then add a fake one
		int count = (viewFlow != null && viewFlow.getAdapter() != null) ? viewFlow
				.getAdapter().getCount() : 1;

		// Verify if the current view must be clipped to the screen
		Rect curViewBound = bounds.get(currentPosition);
		int curViewWidth = curViewBound.right - curViewBound.left;
		if (curViewBound.left < 0) {
			// Try to clip to the screen (left side)
			curViewBound.left = 10;
			curViewBound.right = curViewWidth;
		}
		if (curViewBound.right > getLeft() + getWidth()) {
			// Try to clip to the screen (right side)
			curViewBound.right = getLeft() + getWidth() - 10;
			curViewBound.left = curViewBound.right - curViewWidth;
		}

		// Left views starting from the current position
		if (currentPosition > 0) {
			for (int iLoop = currentPosition - 1; iLoop >= 0; iLoop--) {
				Rect bound = bounds.get(iLoop);
				int w = bound.right - bound.left;
				// Si left side is outside the screen
				if (bound.left < 0) {
					// Try to clip to the screen (left side)
					bound.left = 0;
					bound.right = w;
					// Except if there's an intersection with the right view
					if (iLoop < count - 1 && currentPosition != iLoop) {
						Rect rightBound = bounds.get(iLoop + 1);
						// Intersection
						if (bound.right + TITLE_PADDING > rightBound.left) {
							bound.left = rightBound.left - (w + titlePadding);
						}
					}
				}
			}
		}
		// Right views starting from the current position
		if (currentPosition < count - 1) {
			for (int iLoop = currentPosition + 1; iLoop < count; iLoop++) {
				Rect bound = bounds.get(iLoop);
				int w = bound.right - bound.left;
				// If right side is outside the screen
				if (bound.right > getLeft() + getWidth()) {
					// Try to clip to the screen (right side)
					bound.right = getLeft() + getWidth();
					bound.left = bound.right - w;
					// Except if there's an intersection with the left view
					if (iLoop > 0 && currentPosition != iLoop) {
						Rect leftBound = bounds.get(iLoop - 1);
						// Intersection
						if (bound.left - TITLE_PADDING < leftBound.right) {
							bound.left = leftBound.right + titlePadding;
						}
					}
				}
			}
		}

		// Now draw views
		for (int iLoop = 0; iLoop < count; iLoop++) {
			// Get the title
			String title = getTitle(iLoop);
			Rect bound = bounds.get(iLoop);
			// Only if one side is visible
			if ((bound.left > getLeft() && bound.left < getLeft() + getWidth())
					|| (bound.right > getLeft() && bound.right < getLeft()
							+ getWidth())) {
				Paint paint = paintText;
				// Change the color is the title is closed to the center
				int middle = (bound.left + bound.right) / 2;
				if (Math.abs(middle - (getWidth() / 2)) < 20) {
					paint = paintSelected;
				}
				int y = viewHeight * 2 / 3;
				if (bound.left == 0)
					bound.left = 10;
				if (bound.right == getLeft() + getWidth()) {
					// Try to clip to the screen (right side)
					bound.right = getLeft() + getWidth() - 10;
					bound.left = bound.right - curViewWidth;
				}
				canvas.drawText(title, bound.left, y, paint);// bound.bottom +
																// 50
			}
		}

		// Draw the footer line
		path = new Path();
		path.moveTo(0, getHeight() - footerLineHeight - FOOTER_BAR_HEIGH);
		path.lineTo(getWidth(), getHeight() - footerLineHeight
				- FOOTER_BAR_HEIGH);
		path.close();
		canvas.drawPath(path, paintFooterLine);
		// Draw the footer triangle
		path = new Path();
		path.moveTo(getWidth() / 2, getHeight() - footerLineHeight
				- (FOOTER_BAR_HEIGH - footerTriangleHeight));
		path.lineTo(getWidth() / 2 + footerTriangleHeight, getHeight()
				- footerTriangleHeight - footerLineHeight
				- (FOOTER_BAR_HEIGH - footerTriangleHeight));
		path.lineTo(getWidth() / 2 - footerTriangleHeight, getHeight()
				- footerTriangleHeight - footerLineHeight
				- (FOOTER_BAR_HEIGH - footerTriangleHeight));
		path.close();
		canvas.drawPath(path, paintFooterTriangle);

	}

	/**
	 * Calculate views bounds and scroll them according to the current index
	 * 
	 * @param paint
	 * @param currentIndex
	 * @return
	 */
	private ArrayList<Rect> calculateAllBounds(Paint paint) {
		ArrayList<Rect> list = new ArrayList<Rect>();
		// For each views (If no values then add a fake one)
		int count = (viewFlow != null && viewFlow.getAdapter() != null) ? viewFlow
				.getAdapter().getCount() : 1;
		for (int iLoop = 0; iLoop < count; iLoop++) {
			Rect bounds = calcBounds(iLoop, paint);
			int w = (bounds.right - bounds.left);
			int h = (bounds.bottom - bounds.top);
			bounds.left = (getWidth() / 2) - (w / 2) - currentScroll
					+ (iLoop * getWidth());
			bounds.right = bounds.left + w;
			bounds.top = 0;
			bounds.bottom = h;
			list.add(bounds);
		}

		return list;
	}

	/**
	 * Calculate the bounds for a view's title
	 * 
	 * @param index
	 * @param paint
	 * @return
	 */
	private Rect calcBounds(int index, Paint paint) {
		// Get the title
		String title = getTitle(index);
		// Calculate the text bounds
		Rect bounds = new Rect();
		bounds.right = (int) paint.measureText(title);
		bounds.bottom = (int) (paint.descent() - paint.ascent());
		return bounds;
	}

	/**
	 * Returns the title
	 * 
	 * @param pos
	 * @return
	 */
	private String getTitle(int pos) {
		// Set the default title
		// String title = "title " + pos;
		String title = " ";
		// If the TitleProvider exist
		if (titleProvider != null) {
			title = titleProvider.getTitle(pos);
		}
		return title;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.taptwo.android.widget.FlowIndicator#onScrolled(int, int, int,
	 * int)
	 */
	@Override
	public void onScrolled(int h, int v, int oldh, int oldv) {
		currentScroll = h;
		invalidate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.taptwo.android.widget.ViewFlow.ViewSwitchListener#onSwitched(android
	 * .view.View, int)
	 */
	@Override
	public void onSwitched(View view, int position) {
		currentPosition = position;
		invalidate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.taptwo.android.widget.FlowIndicator#setViewFlow(org.taptwo.android
	 * .widget.ViewFlow)
	 */
	@Override
	public void setViewFlow(ViewFlow view) {
		viewFlow = view;
		invalidate();
	}

	/**
	 * Set the title provider
	 * 
	 * @param provider
	 */
	public void setTitleProvider(TitleProvider provider) {
		titleProvider = provider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ViewFlow can only be used in EXACTLY mode.");
		}
		result = specSize;
		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// We were told how big to be
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		}
		// Measure the height
		else {
			// Calculate the text bounds
			// Rect bounds = new Rect();
			// bounds.bottom = (int) (paintText.descent() - paintText.ascent());
			// result = bounds.bottom - bounds.top + footerTriangleHeight
			// + footerLineHeight + 10;

			return viewHeight;
			// return result;
		}
		return result;
	}
}
