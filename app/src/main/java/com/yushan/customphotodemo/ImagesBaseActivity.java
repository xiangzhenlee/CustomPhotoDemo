package com.yushan.customphotodemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.yushan.customphotodemo.entity.ImageEntity;
import com.yushan.customphotodemo.utils.BitmapUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImagesBaseActivity extends FragmentActivity {

	protected static int sPosition;
	public static final int MAX_SEND = 6;
	protected static List<ImageEntity> sResult = new ArrayList<ImageEntity>();
	protected static boolean isOriginal;
	
	protected Context mContext;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mContext = this;
	}
	
	public void submit(){
		ArrayList<Uri> respUris = new ArrayList<Uri>();
		if(isOriginal){
			for(int i=0;i<sResult.size();i++){
				ImageEntity data = sResult.get(i);
				Uri uri = Uri.parse("file:///"+data.getUrl());
				respUris.add(uri);
			}
		}else{
			for(int i=0;i<sResult.size();i++){
				ImageEntity data = sResult.get(i);
				Bitmap bm = BitmapUtil.getThumbnail(this, data.getId());
				String path = getCachePath();
				if(saveCacheDir(path, bm)){
					Uri uri = Uri.parse("file:///"+path);
					respUris.add(uri);
				}
			}
		}
		sResult.clear();
		
		if(respUris!=null && respUris.size()>0){
			Intent intent = new Intent();
			intent.putParcelableArrayListExtra("result", respUris);
			intent.putExtra("isOriginal", isOriginal);
			setResult(RESULT_OK, intent);
		}else{
			setResult(RESULT_CANCELED);
		}
		finish();
	}
	
	public boolean saveCacheDir(String filename, Bitmap bitmap) {
		File f = new File(filename);
		try {
			if(f.exists()){
				f.deleteOnExit();
			}
			f.createNewFile();
			
		    FileOutputStream out = new FileOutputStream(f);
		    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		    out.flush();
		    out.close();
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private String getCachePath(){
		String fileName = String.valueOf(System.currentTimeMillis())+".jpg";
		String folder = this.getCacheDir().getPath();
		String fullPath = folder+"/"+fileName;
		return fullPath;
	}
	
	protected void finishAndClear(){
		//isOriginal = false;
		sPosition = 0;
		sResult.clear();
		finish();
	}
}
