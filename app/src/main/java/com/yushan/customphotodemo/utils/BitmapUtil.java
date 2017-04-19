package com.yushan.customphotodemo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

public class BitmapUtil {
	public static Bitmap getThumbnail(Context context, String url){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, options);

		float w = options.outWidth;
		float h = options.outHeight;
		float max = Math.max(w, h);

		BitmapFactory.Options bounds = new BitmapFactory.Options();
		bounds.inPurgeable=true;
		bounds.inInputShareable=true;
		bounds.inJustDecodeBounds=false;

		float size = max/800;
		if(size>1){
			bounds.inSampleSize = (int)size;
		}
		return BitmapFactory.decodeFile(url, bounds);
	}

	public static Bitmap getThumbnail(Context context, long origId){
		//BitmapFactory.Options bounds = new BitmapFactory.Options();
		//bounds.inPurgeable=true;
		//bounds.inInputShareable=true;
		Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), origId, MediaStore.Images.Thumbnails.MINI_KIND, /*bounds*/null);
		return bitmap;
	}

	public static Bitmap getBitmapFromPath(String imgPath) {
		Bitmap bm = null;
		bm = BitmapFactory.decodeFile(imgPath, getDefOption());
		return bm;
	}

	public static BitmapFactory.Options getDefOption() {
		BitmapFactory.Options bounds = new BitmapFactory.Options();
		bounds.inJustDecodeBounds = false;
		bounds.inPreferredConfig = Bitmap.Config.ARGB_4444;
		bounds.inPurgeable = true;
		bounds.inInputShareable = true;
		bounds.inSampleSize = 2;
		return bounds;
	}
}