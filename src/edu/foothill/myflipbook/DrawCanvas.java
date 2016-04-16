package edu.foothill.myflipbook;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class DrawCanvas extends View {
	
	private List<AbstractShape> mShapes;

	public DrawCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setShapes(List<AbstractShape> shapes) {
		mShapes = shapes;
	}
	
	public List<AbstractShape> getShapes() {
		return mShapes;
	}
	
	public Bitmap getBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		draw(canvas);
		return bitmap;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if (mShapes == null)
			return;
		
		super.onDraw(canvas);
		
		for (AbstractShape shape : mShapes) {
			if (shape.isVisible()) {
				shape.draw(canvas);
			}
		}
		
	}
	
	public void unselectAllShapes() {
		if (mShapes == null) 
			return;
		
		for (AbstractShape shape : mShapes) {
			shape.setSelected(false);
		}
	}
}
