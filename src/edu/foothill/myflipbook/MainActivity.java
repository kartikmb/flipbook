package edu.foothill.myflipbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private ArrayAdapter<String> mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			List<String> flipbookNames = Flipbook.getFlipbookNames();
			mAdapter = new ArrayAdapter<String>(this,
					R.layout.flipbook_item, new ArrayList<String>(flipbookNames));
			setListAdapter(mAdapter);
		} catch (IOException e) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.sdcard_unavailable);
			builder.setNeutralButton(android.R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					MainActivity.this.finish();					
				}
			});
			
			builder.setCancelable(false);
			builder.show();
		}
	}

	@Override
	public void onListItemClick(ListView parent, View view, int position,
			long id) {
		String name = mAdapter.getItem(position);
		try {
			Flipbook.load(this, name);
			Intent intent = new Intent(MainActivity.this, EditActivity.class);
			startActivity(intent);
		} catch (IOException e) {
			Toast.makeText(MainActivity.this,
					R.string.flipbook_loading_error, Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_flipbook:
			showCreateFlipbookDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showCreateFlipbookDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.new_flipbook);
		builder.setMessage("");

		final EditText prompt = new EditText(this);
		prompt.setHint(R.string.flipbook_name_prompt);
		prompt.setSingleLine(true);
		builder.setView(prompt);

		builder.setPositiveButton(R.string.create, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String name = prompt.getText().toString();
				try {
					Flipbook.load(MainActivity.this, name);
					mAdapter.add(name);
					refreshList();
					Intent intent = new Intent(MainActivity.this, EditActivity.class);
					startActivity(intent);
				} catch (IOException e) {
					Toast.makeText(MainActivity.this,
							R.string.flipbook_loading_error, Toast.LENGTH_LONG)
							.show();
				}

			}
		});

		builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {}
		});

		final AlertDialog dialog = builder.create();
		dialog.show();
		
		final TextView textView = (TextView) dialog.findViewById(android.R.id.message);
		textView.setTextSize(15);

		final Button createButton = dialog
				.getButton(AlertDialog.BUTTON_POSITIVE);
		createButton.setEnabled(false);
		
		prompt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {
				String str = s.toString().trim();
				boolean emptyString = str.length() == 0;
				if (emptyString) {
					createButton.setEnabled(false);
				} else if (flipbookNameExists(str)) {
					textView.setText(str + " " + getString(R.string.flipbook_exists));
					createButton.setEnabled(false);
				} else if (textView.getText().toString().length() > 0) {
					textView.setText("");
					createButton.setEnabled(true);
				} else {
					createButton.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int before) {}

			@Override
			public void afterTextChanged(Editable s) {}
		});

	}
	
	private boolean flipbookNameExists(String name) {	
		for (int i = 0; i < mAdapter.getCount(); i++) {
			String str = mAdapter.getItem(i);
			if (str.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public void removeFlipbookName(String name) {
		mAdapter.remove(name);
		refreshList();
		
	}
	
	private void refreshList()
	{
		Intent refresh = new Intent(this, MainActivity.class);
		startActivity(refresh);
		finish();
	}
}
