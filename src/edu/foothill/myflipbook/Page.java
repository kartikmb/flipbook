package edu.foothill.myflipbook;

import java.util.ArrayList;
import java.util.List;

public class Page {

	private List<AbstractShape> mShapes;

	public Page() {
		mShapes = new ArrayList<AbstractShape>();
	}
	
	public Page(List<AbstractShape> shapes) {
		 mShapes = shapes;
	}
	
	public void addShape(AbstractShape shape) {
		mShapes.add(shape);
	}
	
	public AbstractShape removeShape(int index) {
		return mShapes.remove(index);
	}
	
	public void clearShapes() {
		mShapes.clear();
	}
	
	public AbstractShape getShape(int index) {
		return mShapes.get(index);
	}
	
	public List<AbstractShape> getShapes() {
		return mShapes;
	}
	
	public int getNumShapes() {
		return mShapes.size();
	}
	
	public void setShapes(List<AbstractShape> shapes) {
		mShapes = shapes;
	}
}
