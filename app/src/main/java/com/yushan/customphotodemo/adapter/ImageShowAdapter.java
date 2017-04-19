package com.yushan.customphotodemo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.yushan.customphotodemo.AsyncImageLoaderImpl;
import com.yushan.customphotodemo.entity.ImageEntity;
import com.yushan.customphotodemo.view.ImagePost;
import com.yushan.customphotodemo.ImagesBaseActivity;
import com.yushan.customphotodemo.R;

import java.util.ArrayList;
import java.util.List;

public class ImageShowAdapter extends BaseAdapter {
	private ImagePost postCallBack;
	private Context mContext;
	private ArrayList<ImageEntity> mData;
	private View mListView;
	private List<ImageEntity> mResult;

	
	public ImageShowAdapter(Context context, View view, List<ImageEntity> result, ArrayList<ImageEntity> list) {
		this.mContext = context;
		mData = list;
		mListView = view;
		mResult = result;
	}

	@Override
	public int getCount() {
		return mData.size();
	}
	
	public ArrayList<ImageEntity> getData(){
		return mData;
	}

	@Override
	public ImageEntity getItem(int arg0) {
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return getItem(arg0).getId();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ImageEntity element = this.getItem(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.image_item_grid_element,null);
		}
		holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.image = (ImageView)convertView.findViewById(R.id.image_grid);
			holder.imageMark = (ImageView)convertView.findViewById(R.id.image_mark);
			holder.check = (ImageView)convertView.findViewById(R.id.check);
			convertView.setTag(holder);
		}
		
		holder.check.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(!element.isCheck()){
					int size = mResult.size();
					if(size>= ImagesBaseActivity.MAX_SEND){
						String format = mContext.getString(R.string.format_warn_max_send);
						String warn = String.format(format, ImagesBaseActivity.MAX_SEND);
						Toast.makeText(mContext, warn, Toast.LENGTH_SHORT).show();
						return;
					}
					element.setCheck(true);
					mResult.add(element);
				}else{
					element.setCheck(false);
					mResult.remove(element);
				}
				notifyDataSetChanged();
				if(postCallBack!=null){
					postCallBack.onPost();
				}
			}
		});

		if(element!=null) {
			AsyncImageLoaderImpl asyncImageLoader = new AsyncImageLoaderImpl(mContext);
			asyncImageLoader.loadImageLocal(holder.image, element, R.drawable.image_empty, mListView);
			boolean hasChecked = element.isCheck();
			if (hasChecked) {
				holder.check.setImageResource(R.drawable.image_check_bg_on);
			} else {
				holder.check.setImageResource(R.drawable.image_check_bg);
			}
			holder.imageMark.setVisibility(hasChecked ? View.VISIBLE : View.INVISIBLE);
		}
		return convertView; 
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
	public void update(List<ImageEntity> data){
		//checkPosition = position;
		if(data==null || data.size()==0){
			mData.clear();
		}else{
			if(mData.size()==0){
				mData.addAll(data);
			}else{
				mData.clear();
				mData.addAll(data);
			}
		}
		notifyDataSetChanged();
	}
	
	final class ViewHolder {
		ImageView image;
		ImageView imageMark;
		ImageView check;
	}
	
	public ImagePost getPostCallBack() {
		return postCallBack;
	}

	public void setPostCallBack(ImagePost postCallBack) {
		this.postCallBack = postCallBack;
	}

}
