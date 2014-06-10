package com.n8yn8.farmersmarket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.n8yn8.farmersmarket.Contract.FeedEntry;
import com.n8yn8.farmersmarket.adapter.VendorSpinnerAdapter;
import com.n8yn8.farmersmarket.fragments.NoNameAlertFragment;
import com.n8yn8.farmersmarket.models.DatabaseHelper;
import com.n8yn8.farmersmarket.models.Item;
import com.n8yn8.farmersmarket.models.Vendor;

public class EditItem extends Activity implements NoNameAlertFragment.NoticeDialogListener {
	private static final String TAG = "EditItem";
	
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private DatabaseHelper db;
	EditText itemName;
	Spinner categorySpinner, vendorSpinner, startSpinner, endSpinner;
	String name, category, added, price, unit, vendorName, start, end, photo;
	long vendorId;
	private static String mCurrentPhotoPath;
	EditText setPrice, setUnit;
	CheckBox isAdded;
	private Long mRowId;

	//private LruCache<String, Bitmap> mMemoryCache;
	static int TAKE_PICTURE = 1;
	Uri outputFileUri = null;
	ImageView mImageView;
	Bitmap mImageBitmap;
	public String lastId;  
	public String picName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		db = new DatabaseHelper(this);

		setContentView(R.layout.activity_edit_item);
		itemName = (EditText) findViewById(R.id.itemName);
		categorySpinner = (Spinner) findViewById(R.id.category);
		vendorSpinner = (Spinner) findViewById(R.id.vendor);
		startSpinner = (Spinner) findViewById(R.id.season_start);
		endSpinner = (Spinner) findViewById(R.id.season_end);
		setPrice = (EditText) findViewById(R.id.price);
		setUnit = (EditText) findViewById(R.id.unit);
		isAdded = (CheckBox) findViewById(R.id.addToGroceries);
		added = "no";
		Button confirmButton = (Button) findViewById(R.id.saveItem);
		Button deleteButton = (Button) findViewById(R.id.deleteItem);
		
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    //final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    // Use 1/8th of the available memory for this memory cache.
	    //final int cacheSize = maxMemory / 2;
	    /*mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	        	int size = bitmap.getByteCount() / 1024;
	        	Log.d("cache size", ""+size);
	            return size;
	        }
	    };*/

		mImageView = (ImageView) findViewById(R.id.item_imageView);
		mImageView.setImageResource(R.drawable.ic_item_holder);
		mImageBitmap = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		

		mRowId = (savedInstanceState == null) ? null :
			(Long) savedInstanceState.getSerializable(FeedEntry._ID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(FeedEntry._ID)
					: null;
		}

		initializeSpinners();

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Log.d(TAG, "itemName on Save button = "+itemName.getText().toString());
				if(saveState()) {
					setResult(RESULT_OK);
					finish();
				} else {
					showNoticeDialog();
				}
			}

		});

		deleteButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View veiw){
				deleteState();
			}
		});
	}

	private void initializeSpinners() {
		Log.i(TAG, "initializeSpinners");
		ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(categoryAdapter);
		categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				category = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		List<Vendor> vendors = db.getAllVendors();
		final VendorSpinnerAdapter vendorAdapter = new VendorSpinnerAdapter(this,
                android.R.layout.simple_spinner_item, vendors);
		vendorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		vendorSpinner.setAdapter(vendorAdapter);
		vendorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				Vendor thisVendor = vendorAdapter.getVendor(position);
				vendorId = thisVendor.getId();
				vendorName = thisVendor.getName();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_item);
		monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		startSpinner.setAdapter(monthAdapter);
		startSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				start = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		endSpinner.setAdapter(monthAdapter);
		endSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				end = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		if (mRowId != null) {
			Item item = db.getItem(mRowId);
			
			itemName.setText(item.getName());
			int cPosition = categoryAdapter.getPosition(item.getType());
			categorySpinner.setSelection(cPosition);

			setPrice.setText(item.getPrice());
			setUnit.setText(item.getUnit());
			Vendor thisVendor = db.getVendor(item.getVendorId());
			int vPosition = vendorAdapter.getPosition(thisVendor);
			vendorSpinner.setSelection(vPosition);
			
			int sPosition = monthAdapter.getPosition(item.getSeasonStart());
			startSpinner.setSelection(sPosition);
			
			int ePosition = monthAdapter.getPosition(item.getSeasonEnd());
			endSpinner.setSelection(ePosition);

			if(added!=null)
				isAdded.setChecked(item.getAdded().equals("yes"));

			mCurrentPhotoPath = item.getPhoto();
			if(mCurrentPhotoPath!=null){
				Log.d(TAG,"on create has photo path " + mCurrentPhotoPath);
				loadBitmap();
			}
			
			
		}
	}

	public void newVendor(){
		Intent i = new Intent(this, EditVendor.class);
		startActivity(i);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState");
		saveState();
		outState.putSerializable(FeedEntry._ID, mRowId);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.i(TAG, "onRestoreInstanceState");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
		initializeSpinners();
	}

	private boolean saveState() {
		Log.i(TAG, "saveState");
		name = itemName.getText().toString();
		price = setPrice.getText().toString();
		unit = setUnit.getText().toString();
		
		if (isAdded.isChecked()){
			added = "yes";
		} else {
			added = "no";
		}
		
		Item item = new Item(name, category, price, unit, vendorId, vendorName, start, end, added, mCurrentPhotoPath);

		if (mRowId == null) {
			long id = db.createItem(item);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			item.set_ID(mRowId);
			db.updateItem(item);
		}
		return (!name.equals(""));
	}
	
	private void deleteState() {
		Log.i(TAG, "deleteState");
		setResult(RESULT_CANCELED);
		if(mRowId != null){
			Log.d(TAG, "mRowId to delete = "+mRowId);
			db.deleteItem(mRowId);
		}
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "onCreateOptionsMenu");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_item, menu);
		return true;
	}
	
	public void showNoticeDialog() {
		Log.i(TAG, "showNoticeDialog");
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoNameAlertFragment();
        dialog.show(getFragmentManager(), "NoticeDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.d(TAG, "Positive button pressed");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    	deleteState();
    }

	public void takePic(View v){
		Log.i(TAG, "takePic");
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			Log.d("takePic", "Absolute path created at "+mCurrentPhotoPath);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}
		startActivityForResult(intent, TAKE_PICTURE);

		/*File file = new File(Environment.getExternalStorageDirectory()+ File.separator + "FM" + File.separator, picName ); 
		outputFileUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri.toString());
		startActivityForResult(intent, TAKE_PICTURE); */


	}

	private File setUpPhotoFile() throws IOException {
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "IMG_" + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, ".jpg", albumF);
		return imageF;
	}

	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		Log.i(TAG, "onActivityResult");
		if (requestCode == TAKE_PICTURE && resultCode==RESULT_OK){
			if (mCurrentPhotoPath != null) {
				saveBitmap();
				mCurrentPhotoPath = null;
			}
		}
	}
	
	/*
	private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
}*/
	
private void saveBitmap() {
		

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		
		 /* Test compress */
	    File imageFile = new File(mCurrentPhotoPath);
	    try{
	        OutputStream out = null;
	        out = new FileOutputStream(imageFile);
	        //Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

	        bitmap.compress(Bitmap.CompressFormat.JPEG,80,out);
	        out.flush();
	        out.close();
	    }catch(Exception e){
	        Log.e("Damn","Didn't compress : "+e.toString());
	    }
		//addBitmapToMemoryCache(mCurrentPhotoPath, bitmap);
		//Log.d("setCache", "Number cache items = "+mMemoryCache.size());
	}

	public void loadBitmap(){
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
		mImageView.setImageBitmap(bitmap);
	}
	
/*	public void setPic(){
		final Bitmap bitmap = getBitmapFromMemCache(mCurrentPhotoPath);
	    if (bitmap != null) {
	    	Log.d("setPic","setting image");
	        mImageView.setImageBitmap(bitmap);
	    } else {
	    	Log.d("setPic","caching image");
	    	mImageView.setImageResource(android.R.drawable.ic_menu_gallery);
	    	setCache();
	    }
		
		 Associate the Bitmap to the ImageView 
		mImageView.setImageBitmap(getBitmapFromMemCache(mCurrentPhotoPath));
	}
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	    	Log.d("addBitmapToMemoryCache", "Bitmap at key is NOT in cache. Adding to cache. Items in cache before  = "+mMemoryCache.size());
	        mMemoryCache.put(key, bitmap);
	        Log.d("addBitmapToMemoryCache", "Bitmap added to cache? Items in cache after  = "+mMemoryCache.size());
	    }
	}
	
	public Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}*/

}
