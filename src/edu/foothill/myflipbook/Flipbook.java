package edu.foothill.myflipbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class Flipbook {

	private static final String SDCARD_PATH = Environment
			.getExternalStorageDirectory().getPath();
	private static final String APP_PATH = SDCARD_PATH + File.separator
			+ "Flipbook";

	private static Flipbook mInstance;
	private static MainActivity mActivity;

	private DrawCanvas mDrawCanvas;

	private String mName;
	private List<Page> mPages;

	static {
		System.loadLibrary("gifflen");
	}

	private Flipbook(String name) throws IOException {
		File data = new File(APP_PATH, name);
		List<Page> pages = new ArrayList<Page>();
		if (data.exists()) {
			List<AbstractShape> shapes = new ArrayList<AbstractShape>();
			Scanner in = new Scanner(data);
			while (in.hasNextLine()) {
				String line = in.nextLine();
				if (line.length() > 0) {
					String prefix = line.substring(0, line.indexOf(" "));
					if (prefix.equals("Squiggle"))
						shapes.add(new Squiggle(line));
					else if (prefix.equals("Line"))
						shapes.add(new Line(line));
					else if (prefix.equals("Rect"))
						shapes.add(new Rect(line));
					else if (prefix.equals("Oval"))
						shapes.add(new Oval(line));
				} else if (!shapes.isEmpty()) {
					Page page = new Page(shapes);
					pages.add(page);
					//don't clear list, make a new one (pointers)
					shapes = new ArrayList<AbstractShape>();
				}
			}

		}

		mName = name;
		mPages = pages;
	}

	public static synchronized void load(MainActivity activity, String name)
			throws IOException {
		if (mActivity == null) {
			mActivity = activity;
		}

		if (mInstance == null || !name.equals(mInstance.mName)) {
			mInstance = new Flipbook(name);
		}
	}

	public static Flipbook getInstance() throws NullPointerException {
		if (mInstance == null)
			throw new NullPointerException("Flipbook not instantiated.");
		return mInstance;
	}

	public static List<String> getFlipbookNames() throws IOException {
		if (!isExternalStorageAvailable())
			throw new IOException("SD card unavailable");

		File appDir = new File(APP_PATH);
		if (appDir.mkdir()) {
			return new ArrayList<String>();
		} else {
			return Arrays.asList(appDir.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return !name.endsWith(".gif")
							&& new File(dir, name).isFile();
				}
			}));
		}
	}

	private static boolean isExternalStorageAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public native int Init(String gifName, int w, int h, int numColors,
			int quality, int frameDelay);

	public native void Close();

	public native int AddFrame(int[] inArray);

	public boolean download() {
		File file = new File(APP_PATH, mName + ".gif");
		String path = file.getPath();
		int width = mDrawCanvas.getWidth();
		int height = mDrawCanvas.getHeight();
		int result = Init(path, width, height, 256, 100, 4);

		if (result != 0)
			return false;

		List<AbstractShape> oldShapes = mDrawCanvas.getShapes();
		for (Page page : mPages) {
			mDrawCanvas.setShapes(page.getShapes());
			Bitmap bitmap = mDrawCanvas.getBitmap();
			int[] pixels = new int[width * height];
			bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
			for (int i = 0; i < pixels.length; i++)
			{
				if (pixels[i] == 0)
					pixels[i] = -1;
			}
			AddFrame(pixels);
		}
		mDrawCanvas.setShapes(oldShapes);
		Close();
		return true;
	}

	public void save() throws FileNotFoundException {
		File data = new File(APP_PATH, mName);
		PrintWriter printer = new PrintWriter(data);
		for (Page page : mPages) {
			List<AbstractShape> shapes = page.getShapes();
			for (AbstractShape shape : shapes) {
				printer.println(shape.toString());
			}
			printer.println();
		}
		printer.close();
	}

	public boolean delete() {
		File data = new File(APP_PATH, mName);
		boolean success = data.delete();
		if (!success)
			return false;

		mActivity.removeFlipbookName(mName);
		mInstance = null;
		mName = "";
		mPages = null;

		return true;
	}

	public void setDrawCanvas(DrawCanvas canvas) {
		mDrawCanvas = canvas;
	}

	public DrawCanvas getDrawCanvas() {
		return mDrawCanvas;
	}

	public Page addPage() {
		Page page = new Page();
		mPages.add(page);
		return page;
	}

	public Page addPage(List<AbstractShape> shapes) {
		Page page = addPage();
		page.setShapes(shapes);
		return page;
	}

	public boolean removePage(int index) {
		if (mPages.size() <= 1)
			return false;

		mPages.remove(index);
		return true;
	}

	public String getName() {
		return mName;
	}

	public Page getPage(int index) {
		return mPages.get(index);
	}

	public List<Page> getPages() {
		return mPages;
	}

	public int getNumPages() {
		return mPages.size();
	}

	public void setName(String name) {
		mName = name;
	}

	public void setPage(int index, Page page) {
		mPages.set(index, page);
	}

	public void setPages(List<Page> pages) {
		mPages = pages;
	}

}
