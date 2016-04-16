package edu.foothill.myflipbook;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

public abstract class AbstractShape {

	protected final static int BOX_SIZE = 40;
	protected final static int SELECT_RADIUS = 15;

	protected List<PointF> mPoints;
	protected Paint mPaint;
	protected boolean mSelected;
	protected boolean mVisible;

	public AbstractShape(PointF start, int color, boolean filled) {
		mPoints = new ArrayList<PointF>();
		mPoints.add(start);
		mPoints.add(new PointF(start.x + 1, start.y + 1));
		mPaint = new Paint();
		setColor(color);
		setFilled(filled);
		setSelected(false);
		setVisible(true);
	}

	public AbstractShape(String spec) {
		this(new PointF(0, 0), Color.BLACK, false);
		String[] tokens = spec.split(" ");
		try {
			float x = Float.parseFloat(tokens[1]);
			float y = Float.parseFloat(tokens[2]);
			int color = Integer.parseInt(tokens[3]);
			boolean filled = Boolean.parseBoolean(tokens[4]);
			boolean selected = Boolean.parseBoolean(tokens[5]);
			translate(x, y);
			setColor(color);
			setFilled(filled);
			setSelected(selected);

			for (int i = 6; i < tokens.length - 1; i += 2) {
				x = Float.parseFloat(tokens[i]);
				y = Float.parseFloat(tokens[i + 1]);
				addPoint(new PointF(x, y));
			}
		} catch (Exception e) {
		}
	}

	public abstract void draw(Canvas canvas);

	public abstract boolean containsPoint(PointF point);

	protected abstract PointF getCenter();

	@Override
	public abstract AbstractShape clone();

	public boolean containsPointInStretchBox(PointF point) {
		assertValidRectangle();
		PointF start = getPoint(0);
		PointF end = getPoint(1);
		float x1 = start.x;
		float y1 = start.y;
		float x2 = end.x;
		float y2 = end.y;
		float x = point.x;
		float y = point.y;
		float hb = BOX_SIZE / 2;

		if (x2 - hb <= x && x2 + hb >= x && y2 - hb <= y && y2 + hb >= y) {
			start.x = x1;
			start.y = y1;
			end.x = x2;
			end.y = y2;
		} else if (x1 - hb <= x && x1 + hb >= x && y1 - hb <= y && y1 + hb >= y) {
			start.x = x2;
			start.y = y2;
			end.x = x1;
			end.y = y1;
		} else if (x1 - hb <= x && x1 + hb >= x && y2 - hb <= y && y2 + hb >= y) {
			start.x = x2;
			start.y = y1;
			end.x = x1;
			end.y = y2;
		} else if (x2 - hb <= x && x2 + hb >= x && y1 - hb <= y && y1 + hb >= y) {
			start.x = x1;
			start.y = y2;
			end.x = x2;
			end.y = y1;
		} else {
			return false;
		}
		return true;
	}

	public boolean isSelectedBy(Rect rect) {
		PointF start = rect.getPoint(0);
		PointF end = rect.getPoint(1);
		return (containsPoint(start) && containsPoint(end))
				|| isContainedBy(rect);
	}

	public boolean isContainedBy(Rect rect) {
		PointF start = getPoint(0);
		PointF end = getPoint(1);
		return rect.containsPoint(start) && rect.containsPoint(end);
	}

	public void addPoint(PointF point) {
		mPoints.add(point);
	}

	public void stretchTo(PointF point) {
		addPoint(point);
	}

	public void translate(float dx, float dy) {
		for (PointF point : mPoints) {
			point.x += dx;
			point.y += dy;
		}
	}

	public void rotate(float deg) {
		PointF center = getCenter();
		Matrix matrix = new Matrix();
		matrix.preRotate(deg, center.x, center.y);
		float[] pts = getPointsArray();
		matrix.mapPoints(pts);
		storePointsArray(pts);
	}

	public void zoom(float sx, float sy) {
		PointF center = getCenter();
		Matrix matrix = new Matrix();
		matrix.preScale(sx, sy, center.x, center.y);
		float[] pts = getPointsArray();
		matrix.mapPoints(pts);
		storePointsArray(pts);
	}

	private float[] getPointsArray() {
		int n = mPoints.size() * 2;
		float[] pts = new float[n];
		int index = 0;

		for (PointF point : mPoints) {
			pts[index] = point.x;
			pts[index + 1] = point.y;
			index += 2;
		}

		return pts;
	}

	private void storePointsArray(float[] pts) {
		for (int i = 0; i < pts.length; i++) {
			float x = pts[i];
			float y = pts[++i];
			PointF point = new PointF(x, y);
			mPoints.set(i / 2, point);
		}
	}

	public void assertValidRectangle() {
		PointF start = getPoint(0);
		PointF end = getPoint(1);

		if (start.x > end.x) {
			float temp = start.x;
			start.x = end.x;
			end.x = temp;
		}

		if (start.y > end.y) {
			float temp = start.y;
			start.y = end.y;
			end.y = temp;
		}
	}

	@Override
	public String toString() {
		PointF start = getPoint(0);
		StringBuffer buffer = new StringBuffer();
		buffer.append(start.x + " " + start.y);
		buffer.append(" " + getColor() + " " + isFilled() + " " + isSelected());

		for (PointF point : mPoints) {
			buffer.append(" " + point.x + " " + point.y);
		}
		return buffer.toString();
	}

	public PointF getPoint(int index) {
		if (index < 0 || index >= mPoints.size())
			return null;
		return mPoints.get(index);
	}

	public List<PointF> getPoints() {
		return mPoints;
	}

	public int getColor() {
		return mPaint.getColor();
	}

	public boolean isFilled() {
		return mPaint.getStyle() == Paint.Style.FILL;
	}

	public boolean isSelected() {
		return mSelected;
	}

	public boolean isVisible() {
		return mVisible;
	}

	public void setColor(int color) {
		mPaint.setColor(color);
	}

	public void setFilled(boolean filled) {
		if (filled)
			mPaint.setStyle(Paint.Style.FILL);
		else
			mPaint.setStyle(Paint.Style.STROKE);
	}

	public void setSelected(boolean selected) {
		mSelected = selected;
	}

	public void setVisible(boolean visible) {
		mVisible = visible;
	}
}