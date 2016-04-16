package edu.foothill.myflipbook;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.Toast;

public class EditActivity extends Activity implements OnTouchListener,
		OnItemSelectedListener, OnClickListener {

	private static final int BUTTON_SIZE = 38;
	private int MIN_MOVE_DISTANCE = 15;
	private int PAGE_ICON_HEIGHT_PORTRAIT = 60;
	private int PAGE_ICON_HEIGHT_LANDSCAPE = 60;

	private static final Integer[] COLORS = { Color.BLACK, Color.WHITE,
			Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW,
			Color.MAGENTA };

	private int buttonSize;
	private int minMoveDistance;
	private int pageIconHeightPortrait;
	private int pageIconHeightLandscape;

	private Handler mHandler;
	private LinearLayout mTransformationRow;
	private LinearLayout mPageRow;
	private LinearLayout mPageRowContainer;
	private HorizontalScrollView mScrollView;

	private DrawCanvas mDrawCanvas;
	private AbstractShape mShape;
	private AbstractShape mStretchShape;
	private boolean setupComplete;

	private int mColor;
	private int mColorIndex;
	private int mTool;

	private boolean insideSelectedShape;
	private boolean insideStretchBox;
	private boolean shapeMovedOrStretched;

	private ColorAdapter mColorAdapter;
	private Spinner mColorSpinner;

	private Flipbook mFlipbook;
	private Page mPage;
	private int mPageIndex;

	public static float convertDpToPx(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		buttonSize = (int) convertDpToPx(BUTTON_SIZE, this);
		minMoveDistance = (int) convertDpToPx(MIN_MOVE_DISTANCE, this);
		pageIconHeightPortrait = (int) convertDpToPx(PAGE_ICON_HEIGHT_PORTRAIT,
				this);
		pageIconHeightLandscape = (int) convertDpToPx(
				PAGE_ICON_HEIGHT_LANDSCAPE, this);

		mHandler = new Handler();
		mTransformationRow = (LinearLayout) findViewById(R.id.transformation_row);
		mPageRow = (LinearLayout) findViewById(R.id.page_row);
		mPageRowContainer = (LinearLayout) findViewById(R.id.page_row_container);
		mScrollView = (HorizontalScrollView) findViewById(R.id.scroll_view);

		mDrawCanvas = (DrawCanvas) findViewById(R.id.draw_canvas);
		mDrawCanvas.setOnTouchListener(this);
		setupComplete = false;

		mColor = COLORS[0];
		mColorIndex = 0;
		mTool = R.id.squiggle;
		findViewById(mTool).setSelected(true);

		mColorAdapter = new ColorAdapter(this, COLORS, buttonSize);
		mColorSpinner = (Spinner) findViewById(R.id.color_spinner);
		mColorSpinner.setAdapter(mColorAdapter);
		mColorSpinner.setOnItemSelectedListener(this);

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!setupComplete) {
			mFlipbook = Flipbook.getInstance();
			mFlipbook.setDrawCanvas(mDrawCanvas);
			int numPages = mFlipbook.getNumPages();
			if (numPages == 0) {
				mPage = mFlipbook.addPage();
				mPageIndex = 0;
				saveFlipbook(false);
			} else {
				mPageIndex = mFlipbook.getNumPages() - 1;
				mPage = mFlipbook.getPage(mPageIndex);
			}
			mDrawCanvas.setShapes(mPage.getShapes());
			if (mPage.getNumShapes() > 0)
				updatePageIcons();
			setupComplete = true;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putInt("colorIndex", mColorIndex);
		bundle.putInt("tool", mTool);
		int n = mPage.getNumShapes();
		bundle.putInt("nShapes", n);
		bundle.putInt("pageIndex", mPageIndex);
		for (int i = 0; i < n; i++) {
			bundle.putString("shape" + i, mPage.getShape(i).toString());
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle bundle) {
		mFlipbook = Flipbook.getInstance();
		mColorIndex = bundle.getInt("colorIndex");
		View child = mColorSpinner.getChildAt(mColorIndex);
		mColorSpinner.bringChildToFront(child);
		int num = bundle.getInt("tool");
		secretSelectTool(findViewById(num));
		mPageIndex = bundle.getInt("pageIndex");
		mPage = mFlipbook.getPage(mPageIndex);
		int n = bundle.getInt("nShapes");
		for (int i = 0; i < n; i++) {
			String s = bundle.getString("shape" + i);
			if (s.startsWith("Squiggle")) {
				mPage.addShape(new Squiggle(s));
			} else if (s.startsWith("Line")) {
				mPage.addShape(new Line(s));
			} else if (s.startsWith("Rect")) {
				mPage.addShape(new Rect(s));
			} else if (s.startsWith("Oval")) {
				mPage.addShape(new Oval(s));
			}
		}
		removeSelect();

		mDrawCanvas.invalidate();

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (view.getId() == mDrawCanvas.getId()) {
			PointF point = new PointF(event.getX(), event.getY());
			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				down(point);
				break;

			case MotionEvent.ACTION_MOVE:
				move(point);
				break;

			case MotionEvent.ACTION_UP:
				up();
				break;
			}

			mDrawCanvas.invalidate();
			return true;
		}
		return false;
	}

	private void down(PointF point) {
		switch (mTool) {

		case R.id.squiggle:
			mShape = new Squiggle(point, mColor);
			break;

		case R.id.line:
			mShape = new Line(point, mColor);
			break;

		case R.id.rect:
			mShape = new Rect(point, mColor, true);
			break;

		case R.id.oval:
			mShape = new Oval(point, mColor, true);
			break;

		case R.id.select:
			mShape = new Select(point);
			PointF p = mShape.getPoint(1);
			insideSelectedShape = false;
			insideStretchBox = false;
			shapeMovedOrStretched = false;
			int n = mPage.getNumShapes();
			for (int i = n - 1; i >= 0; i--) {
				AbstractShape shape = mPage.getShape(i);
				if (shape.isSelected()) {
					if (shape.containsPointInStretchBox(p)) {
						insideStretchBox = true;
						mStretchShape = shape;
						mShape.setVisible(false);
						break;
					} else if (shape.containsPoint(p)) {
						insideSelectedShape = true;
						mShape.setVisible(false);
						break;
					}
				}
			}
			break;
		}

		if (mShape != null) {
			mPage.addShape(mShape);
		}
	}

	private void move(PointF point) {
		if (mShape != null) {
			if (mTool != R.id.select) {
				mShape.addPoint(point);
			} else {
				PointF end = mShape.getPoint(1);
				float dx = point.x - end.x;
				float dy = point.y - end.y;
				float dist = (float) Math.sqrt(dx * dx + dy * dy);
				if (dist >= minMoveDistance) {
					mShape.addPoint(point);
					if (insideStretchBox) {
						shapeMovedOrStretched = true;
						mStretchShape.stretchTo(point);
					} else if (insideSelectedShape) {
						shapeMovedOrStretched = true;
						List<AbstractShape> shapes = mPage.getShapes();
						for (AbstractShape shape : shapes) {
							if (shape.isSelected()) {
								shape.translate(dx, dy);
							}
						}
					}
				}

			}
		}
	}

	private void up() {
		mShape = null;
		if (mTool == R.id.select) {

			int n = mPage.getNumShapes();

			if (n == 0)
				return;

			AbstractShape last = mPage.getShape(n - 1);
			if (last instanceof Select) {
				last = mPage.removeShape(n - 1);
				Select sel = (Select) last;
				if (!shapeMovedOrStretched) {

					boolean selectionChanged = false;
					List<AbstractShape> shapes = mPage.getShapes();
					for (AbstractShape shape : shapes) {

						if (shape.isSelectedBy(sel)) {
							shape.setSelected(!shape.isSelected());
							selectionChanged = true;
						}
					}
					if (!selectionChanged) {
						mDrawCanvas.unselectAllShapes();
					}
				}
			}
		}

		updatePageIcons();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		mColor = COLORS[position];
		mColorIndex = position;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (setupComplete) {

			removeSelect();
			mDrawCanvas.unselectAllShapes();

			switch (item.getItemId()) {

			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;

			case R.id.save_page:
				saveFlipbook(true);
				return true;

			case R.id.delete_flipbook:
				deleteFlipbook();
				return true;

			case R.id.download_flipbook:
				downloadFlipbook();
				return true;

			case R.id.play_flipbook:
				playFlipbook();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void secretSelectTool(View view) {
		removeSelect();
		int id = view.getId();

		if (id == mTool) {
			// Same tool was clicked
			if (mTool == R.id.select) {
				// Tool reclicked is select
				List<AbstractShape> shapes = mPage.getShapes();
				for (AbstractShape shape : shapes) {
					shape.setSelected(true);
				}
			}

		} else {
			// Different tool was clicked
			if (mTool == R.id.select) {
				// Current tool is select
				mPageRowContainer.setVisibility(View.VISIBLE);
				mTransformationRow.setVisibility(View.GONE);
				mDrawCanvas.unselectAllShapes();

			} else if (id == R.id.select) {
				// Next tool is select
				mPageRowContainer.setVisibility(View.GONE);
				mTransformationRow.setVisibility(View.VISIBLE);
			}

			findViewById(mTool).setSelected(false);
			mTool = id;
			findViewById(id).setSelected(true);
		}

		mDrawCanvas.invalidate();
	}

	public void onToolButtonClick(View view) {
		secretSelectTool(view);
		updatePageIcons();
	}

	public void onTransformationButtonClick(View view) {
		int id = view.getId();

		if (id == R.id.remove_shape) {

			int index = 0;
			int numShapes = mPage.getNumShapes();

			for (int i = 0; i < numShapes; i++) {

				AbstractShape shape = mPage.getShape(index);
				if (shape.isSelected())
					mPage.removeShape(index);
				else
					index++;
			}

		} else if (id == R.id.rotate_cw) {

			for (AbstractShape shape : mPage.getShapes()) {
				if (shape.isSelected())
					shape.rotate(5);
			}

		} else if (id == R.id.rotate_ccw) {

			for (AbstractShape shape : mPage.getShapes()) {
				if (shape.isSelected())
					shape.rotate(-5);
			}

		} else if (id == R.id.move_up) {

			for (AbstractShape shape : mPage.getShapes()) {
				if (shape.isSelected())
					shape.translate(0, -2);
			}

		} else if (id == R.id.move_down) {

			for (AbstractShape shape : mPage.getShapes()) {
				if (shape.isSelected())
					shape.translate(0, 2);
			}

		} else if (id == R.id.move_left) {

			for (AbstractShape shape : mPage.getShapes()) {
				if (shape.isSelected())
					shape.translate(-2, 0);
			}

		} else if (id == R.id.move_right) {

			for (AbstractShape shape : mPage.getShapes()) {
				if (shape.isSelected())
					shape.translate(2, 0);
			}

		} else if (id == R.id.zoom_in) {

			for (AbstractShape shape : mPage.getShapes()) {
				if (shape.isSelected())
					shape.zoom(1.1f, 1.1f);
			}

		} else if (id == R.id.zoom_out) {

			for (AbstractShape shape : mPage.getShapes()) {
				if (shape.isSelected())
					shape.zoom(0.9f, 0.9f);
			}
		}

		mDrawCanvas.invalidate();
		updatePageIcons();
	}

	/**
	 * Redraws every page icon in the bottom row, based off the flipbook's list
	 * of pages.
	 */
	private void updatePageIcons() {
		int pageIconHeight = 0;

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			pageIconHeight = pageIconHeightPortrait;
		} else {
			pageIconHeight = pageIconHeightLandscape;
		}

		mPageRow.removeAllViews();
		int numPages = mFlipbook.getNumPages();
		for (int i = 0; i < numPages; i++) {

			Page page = mFlipbook.getPage(i);
			mDrawCanvas.setShapes(page.getShapes());
			Bitmap bitmap = mDrawCanvas.getBitmap();

			float ratio = bitmap.getHeight() / pageIconHeight;
			float newWidth = bitmap.getWidth() / ratio;

			ImageView image = new ImageView(this);
			LayoutParams params = new LayoutParams((int) newWidth,
					pageIconHeight);
			params.setMargins(5, 0, 5, 0);
			image.setLayoutParams(params);
			image.setScaleType(ScaleType.CENTER_INSIDE);
			image.setImageBitmap(bitmap);
			image.setBackgroundResource(R.drawable.tool_button);
			image.setOnClickListener(this);
			image.setTag(i);
			mPageRow.addView(image);
		}

		mDrawCanvas.setShapes(mPage.getShapes());
		mPageRow.getChildAt(mPageIndex).setSelected(true);
	}

	/**
	 * Click call back for each icon in the page row
	 */
	@Override
	public void onClick(View view) {
		removeSelect();
		mPageRow.getChildAt(mPageIndex).setSelected(false);
		view.setSelected(true);

		mDrawCanvas.unselectAllShapes();
		mPageIndex = (Integer) view.getTag();
		mPage = mFlipbook.getPage(mPageIndex);
		mDrawCanvas.setShapes(mPage.getShapes());
		mDrawCanvas.invalidate();
	}

	/**
	 * Adds a new page to the list and redraws the page row
	 * 
	 * @param view
	 *            the add button which was clicked
	 */
	public void addPage(View view) {
		List<AbstractShape> shapes = new ArrayList<AbstractShape>();
		List<AbstractShape> prevShapes = mPage.getShapes();
		for (AbstractShape shape : prevShapes) {
			shapes.add(shape.clone());
		}
		mPage = mFlipbook.addPage(shapes);
		mPageIndex = mFlipbook.getNumPages() - 1;
		updatePageIcons();
		mDrawCanvas.invalidate();
		mHandler.postDelayed(new Runnable() {
			public void run() {
				mScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			}
		}, 100L);
	}

	public void removePage(View view) {
		if (mFlipbook.getNumPages() > 1) {
			// If the page to be deleted isn't the last one

			mFlipbook.removePage(mPageIndex);
			mPageIndex = mPageIndex == 0 ? 0 : mPageIndex - 1;
			mPage = mFlipbook.getPage(mPageIndex);

		} else {
			// If the page to be deleted is the last one

			mPage.clearShapes();
			// Clear the page's list of shapes instead
		}

		// Redraw the page row and the canvas
		updatePageIcons();
		mDrawCanvas.invalidate();
	}

	private void removeSelect() {
		if (mTool != R.id.select)
			return;

		int n = mPage.getNumShapes();
		if (n > 0) {
			AbstractShape shape = mPage.getShape(n - 1);
			if (shape instanceof Select) {
				mPage.removeShape(n - 1);
			}
		}
	}

	private void saveFlipbook(boolean show) {
		try {

			mFlipbook.save();
			if (show) {
				Toast.makeText(EditActivity.this, "Saved!", Toast.LENGTH_SHORT)
						.show();
			}

		} catch (FileNotFoundException e) {

			Toast.makeText(EditActivity.this, R.string.flipbook_saving_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void deleteFlipbook() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete_flipbook);

		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						boolean success = mFlipbook.delete();
						if (success) {
							mFlipbook = null;
							Toast.makeText(EditActivity.this, "Deleted!",
									Toast.LENGTH_SHORT).show();
							finish();
						} else {
							Toast.makeText(EditActivity.this,
									R.string.flipbook_deleting_error,
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		builder.setNegativeButton(android.R.string.cancel, null);
		builder.show();
	}

	private void downloadFlipbook() {
		int res = 0;
		if (mFlipbook.download()) {
			res = R.string.download_complete;
		} else {
			res = R.string.flipbook_downloading_error;
		}
		Toast.makeText(this, res, Toast.LENGTH_LONG).show();
	}

	private void playFlipbook() {
		Intent intent = new Intent(this, PlayActivity.class);
		startActivity(intent);
	}

}
