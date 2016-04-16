package edu.foothill.myflipbook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.PointF;

public class Select extends Rect {

	public Select(PointF start) {
		super(start, Color.parseColor("#222222"), false);
	}
	
	@Override
	public void draw(Canvas canvas) {
		PointF start = getPoint(0);
		PointF end = getPoint(1);
		float x1 = start.x;
		float y1 = start.y;
		float x2 = end.x;
		float y2 = end.y;
		mPaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
		canvas.drawRect(x1, y1, x2, y2, mPaint);
	}
	
	@Override
	public String toString() {
		return "Select" + super.toString();
	}
}
