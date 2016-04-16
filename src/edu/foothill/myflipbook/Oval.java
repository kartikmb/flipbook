package edu.foothill.myflipbook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;

public class Oval extends AbstractShape {

	public Oval(PointF start, int color, boolean filled) {
		super(start, color, filled);
	}

	public Oval(String spec) {
		super(spec);
	}

	@Override
	public void draw(Canvas canvas) {
		PointF start = mPoints.get(0);
		PointF end = mPoints.get(1);
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

		RectF rect = new RectF(x1, y1, x2, y2);
		canvas.drawOval(rect, mPaint);

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
	public void addPoint(PointF point) {
		mPoints.set(1, point);
	}

	@Override
	public boolean containsPoint(PointF point) {
		PointF start = mPoints.get(0);
		PointF end = mPoints.get(1);
		float semiXAxis = (end.x - start.x) / 2;
		float semiYAxis = (end.y - start.y) / 2;
		float centerX = start.x + semiXAxis;
		float centerY = start.y + semiYAxis;
		float tempX = point.x - centerX;
		float tempY = point.y - centerY;
		return (tempX * tempX) / (semiXAxis * semiXAxis) + (tempY * tempY)
				/ (semiYAxis * semiYAxis) <= 1;
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
	public String toString() {
		return "Oval " + super.toString();
	}

	@Override
	public Oval clone() {
		return new Oval(toString());
	}
}
