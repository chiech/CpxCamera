package com.seu.magicfilter.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.utils.MagicParams;

public class SavePictureTask extends AsyncTask<Bitmap, Integer, String>{
	
	private OnPictureSaveListener onPictureSaveListener;
	private File file;
	private int Angle;
	private static final String TAG = "SavePictureTask";

	public SavePictureTask(File file, OnPictureSaveListener listener,int angle){
		this.onPictureSaveListener = listener;
		this.file = file;
		this.Angle = angle;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(final String result) {
		if(result != null)
			MediaScannerConnection.scanFile(MagicParams.context,
	                new String[] {result}, null,
	                new MediaScannerConnection.OnScanCompletedListener() {
	                    @Override
	                    public void onScanCompleted(final String path, final Uri uri) {
	                        if (onPictureSaveListener != null)
                                onPictureSaveListener.onSaved(result);
							Log.e(TAG, "onScanCompleted: "+result );
	                    }
            	});

	}

	@Override
	protected String doInBackground(Bitmap... params) {
		if(file == null)
			return null;
		return saveBitmap(params[0]);
	}
	
	private String saveBitmap(Bitmap bitmap) {
		if (file.exists()) {
			file.delete();
		}
		try {
				Matrix matrix = new Matrix();
				matrix.setRotate(Angle);

				FileOutputStream out = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
				out.flush();
				out.close();
				bitmap.recycle();


				return file.toString();

		} catch (FileNotFoundException e) {
		   e.printStackTrace();
		} catch (IOException e) {
		   e.printStackTrace();
		}
		return null;
	}
	
	public interface OnPictureSaveListener{
		void onSaved(final String result);
	}

	public void setOnPictureSaveListener(OnPictureSaveListener onPictureSaveListener) {
		this.onPictureSaveListener = onPictureSaveListener;
	}
}
