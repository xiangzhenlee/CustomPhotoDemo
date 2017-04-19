package com.yushan.customphotodemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.yushan.customphotodemo.entity.ImageEntity;
import com.yushan.customphotodemo.utils.BitmapUtil;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Vector;

public class AsyncImageLoaderImpl {

    private final static String TAG = "AsyncImageLoaderImpl";
    private static Context mContext;

    private HashMap<String, SoftReference<Bitmap>> mImageMap;
    private Vector<String> mTaskList;
    private static AsyncImageLoaderImpl instance;

    public AsyncImageLoaderImpl(Context mContext) {
        this.mContext = mContext;
        mImageMap = new HashMap<String, SoftReference<Bitmap>>();
        mTaskList = new Vector<String>();
    }

    public static AsyncImageLoaderImpl getInstance(Context mContext) {
        if (instance == null) {
            instance = new AsyncImageLoaderImpl(mContext);
        }
        return instance;
    }

    public Bitmap loadDrawable(LoadImageTask task) {
        String imageUrl = task.getUrl();
        if (mImageMap.containsKey(imageUrl)) {
            SoftReference<Bitmap> softReference = mImageMap.get(imageUrl);
            Bitmap drawable = softReference.get();
            if (drawable != null) {
                return drawable;
            }
        }
        if (!mTaskList.contains(imageUrl)) {
            mTaskList.add(imageUrl);
            LoadImageAsync aTask = new LoadImageAsync(task);
            aTask.execute(0);
        }
        return null;
    }

    class LoadImageAsync extends AsyncTask<Object, Void, Bitmap> {
        private LoadImageTask task;

        public LoadImageAsync(LoadImageTask task) {
            this.task = task;
        }

        @Override
        protected Bitmap doInBackground(Object... arg0) {
            Bitmap result = null;
            String url = task.getUrl();
            ImageEntity entity = task.getEntity();

            if (entity != null) {
                long time = System.currentTimeMillis();
                result = BitmapUtil.getThumbnail(mContext, entity.getId());
                Log.d("______", "id:"+entity.getUrl()+", time:"+(System.currentTimeMillis()-time));
            } else {
                result = BitmapUtil.getBitmapFromPath(url);
            }
            if (result != null) {
                // find at local
                return result;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mTaskList.remove(task.getUrl());
            if (result == null) {
                return;
            }
            mImageMap.put(task.getUrl(), new SoftReference<Bitmap>(result));
            task.back(result);
        }
    }

    public interface ImageCallback {
        void imageLoaded(Bitmap bitmap, String imageUrl);
    }

    public static void loadImageLocal(final ImageView img, ImageEntity entity, int res, final View mListView) {
        String url = entity.getUrl();
        img.setTag(url);
        LoadImageTask task = new LoadImageTask(url, null, new ImageCallback() {
            public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
                if (mListView == null) {
                    img.setImageBitmap(imageDrawable);
                    return;
                }
                ImageView imageViewByTag = (ImageView) mListView.findViewWithTag(imageUrl);
                if (imageViewByTag != null) {
                    img.setScaleType(ScaleType.CENTER_CROP);
                    imageViewByTag.setImageBitmap(imageDrawable);
                }
            }
        });

        task.setEntity(entity);

        Bitmap cachedImage = AsyncImageLoaderImpl.getInstance(mContext).loadDrawable(task);
        if (cachedImage == null) {
            if (res == 0) {
                img.setImageBitmap(null);
            } else {
                img.setScaleType(ScaleType.CENTER);
                img.setImageResource(res);
            }
        } else {
            img.setScaleType(ScaleType.CENTER_CROP);
            img.setImageBitmap(cachedImage);
        }
    }
}
