package com.yushan.customphotodemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yushan.customphotodemo.entity.ImageEntity;
import com.yushan.customphotodemo.photoview.PhotoViewAttacher;
import com.yushan.customphotodemo.utils.BitmapUtil;


public class ImageDetailFragment extends Fragment {
    private ImageEntity mImageUrl;
    private ImageView mImageView;
    private Bitmap mBitmap;

    public static ImageDetailFragment newInstance(ImageEntity imageUrl) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putParcelable("data-image",imageUrl);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments().getParcelable("data-image");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mImageView = (ImageView) inflater.inflate(R.layout.image_info_fragment, container, false);
        mBitmap = BitmapUtil.getThumbnail(getActivity(), mImageUrl.getUrl());
        mImageView.setImageBitmap(mBitmap);
        new PhotoViewAttacher(mImageView);
        return mImageView;
    }

    @Override
    public void onDestroy() {
        mImageView.setImageBitmap(null);
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        super.onDestroy();
    }
}
