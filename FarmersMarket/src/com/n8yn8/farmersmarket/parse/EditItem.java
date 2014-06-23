package com.n8yn8.farmersmarket.parse;

import java.io.ByteArrayOutputStream;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.n8yn8.farmersmarket.AlbumStorageDirFactory;
import com.n8yn8.farmersmarket.BaseAlbumDirFactory;
import com.n8yn8.farmersmarket.Contract.FeedEntry;
import com.n8yn8.farmersmarket.FroyoAlbumDirFactory;
import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.fragments.NoNameAlertFragment;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class EditItem extends Activity implements NoNameAlertFragment.NoticeDialogListener {
	private static final String TAG = "EditItem";
	
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	EditText itemName;
	Spinner categorySpinner, vendorSpinner, startSpinner, endSpinner;
	ArrayAdapter<CharSequence> categoryAdapter, monthAdapter;
	ArrayAdapter<Vendor> vendorAdapter;
	String name, category, added, price, unit, vendorName, start, end, photo, vendorId, mRowId;
	private String mCurrentPhotoPath = "";
	EditText setPrice, setUnit;
	CheckBox isAdded;
	Item item;
	Vendor thisVendor;
	boolean itemLoaded, vendorsLoaded;

	//private LruCache<String, Bitmap> mMemoryCache;
	static int TAKE_PICTURE = 1;
	Uri outputFileUri = null;
	ParseImageView mImageView;
	Bitmap imageBitmap;
	ParseFile photoFile;
	Bitmap mImageBitmap;
	public String lastId;  
	public String picName;
	Boolean newItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");

		itemLoaded = false;
		vendorsLoaded = false;
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

		mImageView = (ParseImageView) findViewById(R.id.item_imageView);
		mImageView.setImageResource(R.drawable.ic_item_holder);
		mImageBitmap = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		
		initializeSpinners();
		
		Bundle extras = getIntent().getExtras();
		newItem = extras.get("new_item").equals("true");
		if (!newItem) {
			mRowId = extras.getString("item_id");
			ParseQuery<Item> query = ParseQuery.getQuery("item");
			query.include("vendor");
			query.getInBackground(mRowId, new GetCallback<Item>() {
				public void done(Item object, ParseException e) {
					if (e == null) {
						Log.i(TAG, "Item query finished successfully");
						item = object;
						itemLoaded = true;
						populateFields();
					} else {
						Toast.makeText(getApplicationContext(), "Items didn't load", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		
		if (!newItem){
			
		}

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Log.d(TAG, "itemName on Save button = "+itemName.getText().toString());
				if(saveState()) {
					setResult(Activity.RESULT_OK);
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
		categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(categoryAdapter);
		categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				category = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				

			}
		});
		
		ParseQuery<Vendor> query = ParseQuery.getQuery("vendor");
		query.findInBackground(new FindCallback<Vendor>() {

			@Override
			public void done(List<Vendor> vendors, ParseException e) {
				if (e == null) {
					Log.i(TAG, "Vendor list query finished successfully");
					loadVendorSpinner(vendors);
					
				} else {
					Log.e(TAG, "Error loading Vendor list" + e.getMessage());
				}
				
			}
			
		});

		
		
		monthAdapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_item);
		monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		startSpinner.setAdapter(monthAdapter);
		startSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				start = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				

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
				

			}
		});
	}
	
	private void loadVendorSpinner (List<Vendor> vendors) {
		Log.i(TAG, "loadVendorSpinner");
		vendorAdapter = new VendorSpinnerAdapter(this, android.R.id.text1, vendors);
		vendorSpinner.setAdapter(vendorAdapter);
		vendorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				Log.i(TAG, "vendor spinner onItemSelected");
				thisVendor = vendorAdapter.getItem(position);
				Log.d(TAG, "selected Vendor = " + thisVendor.getName());
				vendorId = thisVendor.getObjectId();
				vendorName = thisVendor.getName();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				Log.i(TAG, "vendorSpinner nothingSelected");
			}
		});
		vendorsLoaded = true;
	}
	
	private void populateFields() {
		Log.i(TAG, "populateFields");
			
			itemName.setText(item.getName());
			int cPosition = categoryAdapter.getPosition(item.getType());
			categorySpinner.setSelection(cPosition);

			setPrice.setText(item.getPrice());
			setUnit.setText(item.getUnit());
			
			//TODO set vendor spinner
			thisVendor = (Vendor) item.get("vendor");
			try {
				thisVendor.fetchIfNeeded();
			} catch (ParseException e1) {
				Log.e(TAG, "Error fetching vendor");
				e1.printStackTrace();
			}
			Log.d(TAG, "thisVendor = " + thisVendor.getName());
			
			int vPosition = vendorAdapter.getPosition(thisVendor);
			Log.d(TAG, "position of vendor = " + vPosition);
			vendorSpinner.setSelection(vPosition);
			
			int sPosition = monthAdapter.getPosition(item.getSeasonStart());
			startSpinner.setSelection(sPosition);
			
			int ePosition = monthAdapter.getPosition(item.getSeasonEnd());
			endSpinner.setSelection(ePosition);

			if(added!=null)
				isAdded.setChecked(item.isInGroceries());
			
			photoFile = item.getParseFile("photoFile");
			if (photoFile != null) {
				mImageView.setParseFile(photoFile);
				mImageView.loadInBackground(new GetDataCallback () {

					@Override
					public void done(byte[] data, ParseException e) {
						if (e != null) {
							Toast.makeText(getApplicationContext(), "Error loading image", Toast.LENGTH_SHORT).show();
						}
						
					}
					
				});
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
		//saveState();
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
		//initializeSpinners();
	}

	private boolean saveState() {
		Log.i(TAG, "saveState");
		name = itemName.getText().toString();
		price = setPrice.getText().toString();
		unit = setUnit.getText().toString();
		
		if (newItem) {
			item = new Item();
		}
		item.setName(name);
		item.setType(category);
		item.setPrice(price);
		item.setUnit(unit);
		item.setVendorId(vendorId);
		item.setVendorName(vendorName);
		item.put("vendor", thisVendor);
		item.setSeasonStart(start);
		item.setSeasonEnd(end);
		item.setInGroceries(isAdded.isChecked());
		item.setPhoto(mCurrentPhotoPath);
		
		if(imageBitmap != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
	        byte[] scaledData = bos.toByteArray();
	        photoFile = new ParseFile("meal_photo.jpg", scaledData);
			photoFile.saveInBackground(new SaveCallback() {

				public void done(ParseException e) {
					if (e != null) {
						Toast.makeText(getApplicationContext(),
								"Error saving photoFile: " + e.getMessage(),
								Toast.LENGTH_LONG).show();
						Log.e(TAG, "Error saving: " + e.getMessage());
					} else {
						Toast.makeText(getApplicationContext(),
								"Photo Saved",
								Toast.LENGTH_LONG).show();
					}
				}
			});
			item.setPhotoFile(photoFile);
		}
		item.saveInBackground(new SaveCallback() {
				 
		        @Override
		        public void done(ParseException e) {
		            if (e == null) {
		            	Toast.makeText(
		                        getApplicationContext(),
		                        "Item saved successfully",
		                        Toast.LENGTH_SHORT).show();
		            	
		            } else {
		                Toast.makeText(
		                        getApplicationContext(),
		                        "Error saving Item: " + e.getMessage(),
		                        Toast.LENGTH_SHORT).show();
		                Log.e(TAG, "Error saving: " + e.getMessage());
		            }
		        }
		 
		    });
		return (!name.equals(""));
	}
	
	private void deleteState() {
		Log.i(TAG, "deleteState");
		setResult(RESULT_CANCELED);
		if(mRowId != null){
			Log.d(TAG, "mRowId to delete = "+mRowId);
			//TODO
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
        Log.i(TAG, "Positive button pressed");
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
			//intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
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
		Log.i(TAG, "setUpPhotoFile");
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	private File createImageFile() throws IOException {
		Log.i(TAG, "createImageFile");
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "IMG_" + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, ".jpg", albumF);
		return imageF;
	}

	private File getAlbumDir() {
		Log.i(TAG, "getAlbumDir");
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
		Log.i(TAG, "getAlbumName");
		return getString(R.string.album_name);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		Log.i(TAG, "onActivityResult");
		if (requestCode == TAKE_PICTURE && resultCode==RESULT_OK){
			Bundle extras = data.getExtras();
	        imageBitmap = (Bitmap) extras.get("data");
	        mImageView.setImageBitmap(imageBitmap);
	        
			/*if (mCurrentPhotoPath != null) {
				saveBitmap();
				mCurrentPhotoPath = null;
			}*/
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
		Log.i(TAG, "saveBitmap");

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
		Log.i(TAG, "loadBitmap");
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
