package edu.foothill.myflipbook;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class PlayActivity extends Activity {
	
	private AnimationDrawable mDrawable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mDrawable = new AnimationDrawable();
		mDrawable.setOneShot(false);
		ImageView imageView = (ImageView) findViewById(R.id.flipbook_play_view);
		Flipbook flipbook = Flipbook.getInstance();
		setTitle(flipbook.getName());
		DrawCanvas drawCanvas = flipbook.getDrawCanvas();
		List<AbstractShape> oldShapes = drawCanvas.getShapes();
		List<Page> pages = flipbook.getPages();
		
		for (Page page : pages) {
			drawCanvas.setShapes(page.getShapes());
			Bitmap bitmap = drawCanvas.getBitmap();
			Drawable draw = new BitmapDrawable(getResources(), bitmap);
			mDrawable.addFrame(draw, 100);
		}
		
		imageView.setImageDrawable(mDrawable);
		mDrawable.start();
		drawCanvas.setShapes(oldShapes);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mDrawable.stop();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mDrawable.start();
	}	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.play, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
