package edu.foothill.myflipbook;

import java.util.Collections;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

public class Squiggle extends AbstractShape {

	public Squiggle(PointF start, int color) {
		super(start, color, false);
	}

	public Squiggle(String spec) {
		super(spec);
	}

	@Override
	public void draw(Canvas canvas) {
		float strokeWidth = mPaint.getStrokeWidth();
		mPaint.setStrokeWidth(4);
		PointF start = getPoint(0);
		float x1 = start.x;
		float y1 = start.y;
		float x2 = x1;
		float y2 = y1;

		for (PointF point : mPoints) {
			x2 = point.x;
			y2 = point.y;
			canvas.drawLine(x1, y1, x2, y2, mPaint);
			x1 = x2;
			y1 = y2;
		}
		mPaint.setStrokeWidth(strokeWidth);

		if (isSelected()) {
			float hb = BOX_SIZE / 2;
			x1 = start.x;
			y1 = start.y;
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
		float x = point.x;
		float y = point.y;
		int r = SELECT_RADIUS;
		for (PointF p : mPoints) {
			if (Math.abs(p.x - x) < r && Math.abs(p.y - y) < r) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsPointInStretchBox(PointF point) {
		PointF start = getPoint(0);
		PointF end = getPoint(mPoints.size() - 1);
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
		}
		else if (x2 - hb <= x && x2 + hb >= x && y2 - hb <= y && y2 + hb >= y) {
			return true;
		}
		return false;
	}
	
	@Override
	public PointF getCenter() {
		PointF start = getPoint(0);
		float minX = start.x;
		float minY = start.y;
		float maxX = minX;
		float maxY = minY;
		
		for (PointF point : mPoints) {
			float x = point.x;
			float y = point.y;
			
			if (x < minX)
				minX = x;
			else if (x > maxX)
				maxX = x;
			
			if (y < minY) 
				minY = y;
			else if (y > maxY)
				maxY = y;
		}
		
		float cx = (minX + maxX) / 2;
		float cy = (minY + maxY) / 2;
		return new PointF(cx, cy);		
	}

	@Override
	public boolean isContainedBy(Rect rect) {
		PointF start = getPoint(0);
		float minX = start.x;
		float minY = start.y;
		float maxX = minX;
		float maxY = minY;
		
		int n = mPoints.size();
		for (int i = 0; i < n; i++) {
			PointF point = getPoint(i);
			float x = point.x;
			float y = point.y;
			
			if (x < minX)
				minX = x;
			else if (x > maxX)
				maxX = x;
			
			if (y < minY)
				minY = y;
			else if (y > maxY)
				maxY = y;			
		}
		
		PointF boxStart = new PointF(minX, minY);
		PointF boxEnd = new PointF(maxX, minX);
		return rect.containsPoint(boxStart) && rect.containsPoint(boxEnd);
	}

	@Override
	public void stretchTo(PointF point) {
		int n = mPoints.size();
		PointF start = getPoint(0);
		PointF end = getPoint(n - 1);
		
		float x = point.x;
		float x1 = start.x;
		float x2 = end.x;
		
		float dx1 = x2 - x1;
		if (dx1 >= 0 && dx1 < 0.01)
			dx1 = 0.01f;
		else if (dx1 < 0 && dx1 > -0.01)
			dx1 = -0.01f;
		
		float dx2 = x - x1;
		if (dx2 >= 0 && dx2 < 0.01)
			dx2 = 0.01f;
		else if (dx2 < 0 && dx2 > -0.01)
			dx2 = -0.01f;
		
		float dxr = dx2 / dx1;
		
		float y = point.y;
		float y1 = start.y;
		float y2 = end.y;
		
		float dy1 = y2 - y1;
		if (dy1 >= 0 && dy1 < 0.01)
			dy1 = 0.01f;
		else if (dy1 < 0 && dy1 > -0.01)
			dy1 = -0.01f;
		
		float dy2 = y - y1;
		if (dy2 >= 0 && dy2 < 0.01)
			dy2 = 0.01f;
		else if (dy2 < 0 && dy2 > -0.01)
			dy2 = -0.01f;
		
		float dyr = dy2 / dy1;
		
		for (int i = 1; i < n; i++) {
			PointF p = getPoint(i);
			float dx = p.x - x1;
			p.x = x1 + dx * dxr;
			float dy = p.y - y1;
			p.y = y1 + dy * dyr;
		}
		
	}

	@Override
	public String toString() {
		return "Squiggle " + super.toString();
	}
	
	@Override
	public Squiggle clone() {
		return new Squiggle(toString());
	}
}
