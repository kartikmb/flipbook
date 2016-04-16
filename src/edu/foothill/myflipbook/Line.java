package edu.foothill.myflipbook;

import java.util.Collections;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

public class Line extends AbstractShape {

	public Line(PointF start, int color) {
		super(start, color, false);
	}

	public Line(String spec) {
		super(spec);
	}

	@Override
	public void draw(Canvas canvas) {
		float strokeWidth = mPaint.getStrokeWidth();
		mPaint.setStrokeWidth(4);
		PointF start = mPoints.get(0);
		PointF end = mPoints.get(1);
		float x1 = start.x;
		float y1 = start.y;
		float x2 = end.x;
		float y2 = end.y;
		canvas.drawLine(x1, y1, x2, y2, mPaint);
		mPaint.setStrokeWidth(strokeWidth);

		if (isSelected()) {
			float hb = BOX_SIZE / 2;
			int color = getColor();

			setColor(Color.WHITE);
			setFilled(true);
			canvas.drawRect(x1 - hb, y1 - hb, x1 + hb, y1 + hb, mPaint);
			canvas.drawRect(x2 - hb, y2 - hb, x2 + hb, y2 + hb, mPaint);

			setColor(Color.LTGRAY);
			setFilled(false);
			canvas.drawRect(x1 - hb, y1 - hb, x1 + hb, y1 + hb, mPaint);
			canvas.drawRect(x2 - hb, y2 - hb, x2 + hb, y2 + hb, mPaint);

			setColor(color);
		}
	}

	@Override
	public boolean containsPoint(PointF point) {
		PointF start = mPoints.get(0);
		PointF end = mPoints.get(1);
		float x1 = start.x;
		float y1 = start.y;
		float x2 = end.x;
		float y2 = end.y;
		float x = point.x;
		float y = point.y;

		float m1 = (y2 - y1) / (x2 - x1);
		float b1 = y1 - (m1 * x1);
		float m2 = -1 / m1;
		float b2 = y - (m2 * x);
		float x3 = (b2 - b1) / (m1 - m2);
		float y3 = (m1 * x3) + b1;
		float a = x3 - x;
		float b = y3 - y;
		float dist = (float) Math.sqrt((a * a) + (b * b));
		return dist <= SELECT_RADIUS;
	}

	@Override
	public boolean containsPointInStretchBox(PointF point) {
		PointF start = getPoint(0);
		PointF end = getPoint(1);
		float x1 = start.x;
		float y1 = start.y;
		float x2 = end.x;
		float y2 = end.y;
		float x = point.x;
		float y = point.y;
		float hb = BOX_SIZE / 2;

		if (x1 - hb <= x && x1 + hb >= x && y1 - hb <= y && y1 + hb >= y) {
			Collections.reverse(mPoints);
			return true;
		} else if (x2 - hb <= x && x2 + hb >= x && y2 - hb <= y && y2 + hb >= y) {
			return true;
		}
		return false;
	}
	
	@Override
	public PointF getCenter() {
		PointF start = getPoint(0);
		PointF end = getPoint(1);
		float x = (start.x + end.x) / 2;
		float y = (start.y + end.y) / 2;
		return new PointF(x, y);
	}

	@Override
	public void addPoint(PointF point) {
		mPoints.set(1, point);
	}

	@Override
	public String toString() {
		return "Line " + super.toString();
	}
	
	@Override
	public Line clone() {
		return new Line(toString());
	}

}
