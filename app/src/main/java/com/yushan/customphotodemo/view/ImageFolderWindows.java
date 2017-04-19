package com.yushan.customphotodemo.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.yushan.customphotodemo.R;


public class ImageFolderWindows extends PopupWindow {
	private View mMenuView;
	private Context mContext;
	
	public ImageFolderWindows(Activity context, View menuView) {
		super(context);
		mContext = context;
		this.mMenuView = menuView;
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.popwin_anim_style);
		this.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.transparent));
		this.setContentView(mMenuView);
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}
	
	public void showAtLocation2(View parent, int gravity, int x, int y) {
		showAtLocation(parent, gravity, x, y);
	}
	public void setListSize(int size) {
		float max = 5;
		if(size<=max){
			this.setHeight(LayoutParams.WRAP_CONTENT);
		}else{
			float lineHeight = mContext.getResources().getDimension(R.dimen.image_folder_height);
			int totalHeight = Math.round(lineHeight * max);
			this.setHeight(totalHeight);
		}
	}
}
