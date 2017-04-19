package com.yushan.customphotodemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yushan.customphotodemo.entity.ImageEntity;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PreviewActivity extends ImagesBaseActivity {
	private ViewPager mPager;
	private TextView mTextTitle;
	private CheckBox mCheckOrigrnal;
	private CheckBox mCheckOn;
	private TextView mTextSend;
	private ArrayList<ImageEntity> mDatas;
	private boolean isPerViewMode;
	private int mPositionInAll;

	private LinearLayout ll_preview;
	private TextView tv_picNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_preview);

		mTextTitle = (TextView) findViewById(R.id.pic_number);
		mTextSend = (TextView) this.findViewById(R.id.send);
		mCheckOrigrnal = (CheckBox) findViewById(R.id.select_original1);
		mCheckOn = (CheckBox) findViewById(R.id.select_on);
		mPager = (ViewPager) findViewById(R.id.pager);

		ll_preview = (LinearLayout) findViewById(R.id.ll_preview);
		tv_picNumber = (TextView) findViewById(R.id.tv_picNumber);

		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		ll_preview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
			}
		});
		//		mTextSend.setOnClickListener(new View.OnClickListener() {
		//			@Override
		//			public void onClick(View arg0) {
		//				submit();
		//			}
		//		});

		Intent intent = this.getIntent();
		if (intent != null) {
			isPerViewMode = intent.getBooleanExtra("preview", false);
		}
		if (isPerViewMode) {
			mDatas = new ArrayList<ImageEntity>(sResult);
			mPositionInAll = 0;
		} else {

			mDatas = ImageMainActivity.tempData;
			mPositionInAll = sPosition;
		}

		updateTitle();
		updateCheckStatus();
		updateSendBtn();
		updateOrgSize();
		mCheckOrigrnal.setChecked(isOriginal);

		mCheckOrigrnal.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton view, boolean isChecked) {
				isOriginal = isChecked;
				updateOrgSize();
			}
		});

		mPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageScrollStateChanged(int arg0) {
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageSelected(int arg0) {
				mPositionInAll = arg0;
				updateTitle();
				updateCheckStatus();
				updateOrgSize();
			}
		});

		mPager.setCurrentItem(mPositionInAll);
		mCheckOn.setOnCheckedChangeListener(lisChecked);
	}

	OnCheckedChangeListener lisChecked = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton view, boolean isChecked) {
			setResult(RESULT_OK);
			ImageEntity element = mDatas.get(mPositionInAll);
			if (!element.isCheck()) {
				int size = sResult.size();
				if (size >= ImagesBaseActivity.MAX_SEND) {
					String format = mContext.getString(R.string.format_warn_max_send);
					String warn = String.format(format, ImagesBaseActivity.MAX_SEND);
					Toast.makeText(mContext, warn, Toast.LENGTH_SHORT).show();
					mCheckOn.setChecked(false);
					return;
				}
				element.setCheck(true);
				sResult.add(element);
			} else {
				element.setCheck(false);
				sResult.remove(element);
			}

			updateSendBtn();
			updateCheckStatus();
		}
	};

	private void updateCheckStatus() {
		boolean checked = mDatas.get(mPositionInAll).isCheck();
		mCheckOn.setOnCheckedChangeListener(null);
		mCheckOn.setChecked(checked);
		mCheckOn.setOnCheckedChangeListener(lisChecked);
	}

	private void updateSendBtn() {
		boolean isEmpty = sResult.size() == 0;
		//		mTextSend.setEnabled(!isEmpyt);
		tv_picNumber.setEnabled(!isEmpty);

		if (isEmpty) {
			mTextSend.setTextColor(Color.WHITE);
			mTextSend.setText(R.string.complete);
			tv_picNumber.setVisibility(View.GONE);
		} else {
			String format = getString(R.string.format_send_count);
			String sendCount = String.format(format, sResult.size(), ImagesBaseActivity.MAX_SEND);
			tv_picNumber.setVisibility(View.VISIBLE);
			tv_picNumber.setText(sResult.size()+"");
			mTextSend.setTextColor(getResources().getColor(R.color.headColor));
		}
	}
	/**
	 * 更新显示图片的大小 
	 */
	private void updateOrgSize() {
		if (isOriginal) {
			mCheckOrigrnal.setText(getFileSize());
		} else {
			mCheckOrigrnal.setText(R.string.original);
		}

	}

	private void updateTitle() {
		int all = mDatas.size();
		String newTitle = mPositionInAll + 1 + "/" + all;
		mTextTitle.setText(newTitle);
	}

	private String getFileSize() {
		ImageEntity image = mDatas.get(mPositionInAll);
		String showSize = getString(image.getSize());
		return showSize;
	}

	public String getAllFileSize() {
		double totalByte = 0;
		if (mDatas.size() > 0) {
			for (ImageEntity data : sResult) {
				totalByte += data.getSize();
			}
		}
		return getString(totalByte);
	}

	private String getString(double totalbyte) {
		if (totalbyte == 0) {
			return getString(R.string.original);
		} else {
			if (totalbyte > 1024 * 1024) {
				double tms = totalbyte / 1024 / 1024;
				return getString(R.string.original)+"(" + sizeFormat(tms) + "M)";
			} else {
				double tms = totalbyte / 1024;
				if (tms == 0) {
					return getString(R.string.original);
				}
				return getString(R.string.original)+"(" + sizeFormat(tms) + "K)";
			}
		}
	}

	private String sizeFormat(double value) {
		DecimalFormat fnum = new DecimalFormat("##0.0");
		return fnum.format(value);
	}

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {
		public ImagePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public Fragment getItem(int position) {
			ImageEntity image = mDatas.get(position);
			return ImageDetailFragment.newInstance(image);
		}
	}

}
