package edu.foothill.myflipbook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

public class Rect extends AbstractShape {

	public Rect(PointF start, int color, boolean filled) {
		super(start, color, filled);
	}

	public Rect(String spec) {
		super(spec);
	}

	@Override
	public void draw(Canvas canvas) {
		PointF start = getPoint(0);
		PointF end = getPoint(1);
		float x1 = start.x;
		float y1 = start.y;
		float x2 = end.x;
		float y2 = end.y;

		if (x1 > x2) {
			float temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (y1 > y2) {
			float temp = y1;
			y1 = y2;
			y2 = temp;
		}

		canvas.drawRect(x1, y1, x2, y2, mPaint);

		if (isSelected()) {
			float hb = BOX_SIZE / 2;
			int color = getColor();
			boolean filled = isFilled();

			setColor(Color.WHITE);
			setFilled(true);
			canvas.drawRect(x1 - hb, y1 - hb, x1 + hb, y1 + hb, mPaint);
			canvas.drawRect(x2 - hb, y1 - hb, x2 + hb, y1 + hb, mPaint);
			canvas.drawRect(x2 - hb, y2 - hb, x2 + hb, y2 + hb, mPaint);
			canvas.drawRect(x1 - hb, y2 - hb, x1 + hb, y2 + hb, mPaint);

			setColor(Color.LTGRAY);
			setFilled(false);
			canvas.drawRect(x1 - hb, y1 - hb, x1 + hb, y1 + hb, mPaint);
			canvas.drawRect(x2 - hb, y1 - hb, x2 + hb, y1 + hb, mPaint);
			canvas.drawRect(x2 - hb, y2 - hb, x2 + hb, y2 + hb, mPaint);
			canvas.drawRect(x1 - hb, y2 - hb, x1 + hb, y2 + hb, mPaint);

			setColor(color);
			setFilled(filled);
		}
	}

	@Override
	public boolean containsPoint(PointF point) {
		assertValidRectangle();
		PointF start = getPoint(0);
		PointF end = getPoint(1);
		float x = point.x;
		float y = point.y;
		return x > start.x && x < end.x && y > start.y && y < end.y;
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

	public float getWidth() {
		return getPoint(1).x - getPoint(0).x;
	}

	public float getHeight() {
		return getPoint(1).y - getPoint(0).y;
	}

	@Override
	public String toString() {
		return "Rect " + super.toString();
	}

	@Override
	public Rect clone() {
		return new Rect(toString());
	}
}
