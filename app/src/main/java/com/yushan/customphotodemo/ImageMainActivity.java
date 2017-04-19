package com.yushan.customphotodemo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yushan.customphotodemo.adapter.ImageFolderAdapter;
import com.yushan.customphotodemo.adapter.ImageShowAdapter;
import com.yushan.customphotodemo.entity.ImageEntity;
import com.yushan.customphotodemo.entity.ImageFolderEntity;
import com.yushan.customphotodemo.utils.PreferencesUtil;
import com.yushan.customphotodemo.view.ImageFolderWindows;
import com.yushan.customphotodemo.view.ImagePost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ImageMainActivity extends ImagesBaseActivity implements View.OnClickListener {

    private GridView mGridView;
    private ImageShowAdapter mGridAdapter;
    private TextView mTextSend;
    private TextView mTextPreview;
    private TextView mTextFolder;
    private ImageFolderWindows mMenuWindows;
    private ImageFolderAdapter mFolderAdapter;
    private static int REQUEST_CODE_NUM = 10010;

    public static ArrayList<ImageEntity> tempData = new ArrayList<ImageEntity>();
    private boolean isEmpty;
    private ImageView back;
    private ListView folderList;
    private View maskView;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_main);

        PreferencesUtil.init(this, "config");

        initView();
        initData();
        setViewListener();

        loadForTask(0);
    }

    private void initData(){
        mGridAdapter = new ImageShowAdapter(this, mGridView, sResult, tempData);
        mGridAdapter.setPostCallBack(new ImagePost() {
            @Override
            public void onPost() {
                updateBtn();
            }
        });

        mGridView.setAdapter(mGridAdapter);
        mFolderAdapter = new ImageFolderAdapter(this);
        mFolderAdapter.setListView(folderList);
        folderList.setAdapter(mFolderAdapter);
    }

    private void setViewListener(){
        mTextSend.setOnClickListener(this);
        mTextPreview.setOnClickListener(this);
        mTextFolder.setOnClickListener(this);
        back.setOnClickListener(this);

        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                ImageShowAdapter adapter = (ImageShowAdapter) mGridView.getAdapter();
                Intent intent = new Intent(ImageMainActivity.this, PreviewActivity.class);
                intent.putParcelableArrayListExtra("action-data", tempData);
                sPosition = position;
                startActivityForResult(intent, REQUEST_CODE_NUM);
            }
        });

        folderList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                PreferencesUtil.putInt("folder-last-position", position);
                ImageFolderEntity entity = mFolderAdapter.getItem(position);

                mGridAdapter.update(entity.getList());
                mFolderAdapter.setSelection(position);
                String photoName = entity.getName();
                mTextFolder.setText(photoName);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMenuWindows.dismiss();
                    }
                }, 200);
            }
        });

        mMenuWindows.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                maskView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initView(){
        mTextSend = (TextView) findViewById(R.id.send);
        mTextPreview = (TextView) findViewById(R.id.preview);
        mTextFolder = (TextView) findViewById(R.id.text_folder);
        mGridView = (GridView) findViewById(R.id.grid);
        back = (ImageView)findViewById(R.id.back);
        maskView = findViewById(R.id.view_mask);
        layout = (RelativeLayout) View.inflate(this, R.layout.image_layout_file, null);
        folderList = (ListView) layout.findViewById(R.id.lv_dialog);
        mMenuWindows = new ImageFolderWindows(this, layout);
    }

    public void updateBtn() {
        isEmpty = sResult.size() == 0;
        mTextPreview.setEnabled(!isEmpty);
        mTextSend.setEnabled(!isEmpty);
        if (isEmpty) {
            mTextSend.setText(R.string.complete);
            mTextPreview.setText(R.string.preview);
        } else {
            String format = getString(R.string.format_send_count);
            String sendCount = String.format(format, sResult.size(), ImagesBaseActivity.MAX_SEND);
            mTextSend.setText(sendCount);

            String formatPre = getString(R.string.format_preview);
            String preCount = String.format(formatPre, sResult.size());
            mTextPreview.setText(preCount);
        }

    }

    public void loadForTask(int type) {
        HashMap<String, ImageFolderEntity> mDataFolder = new HashMap<String, ImageFolderEntity>();

        Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ImageEntity.STORE_IMAGES, null, null, MediaStore.Images.Media.DATE_TAKEN + " desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ImageEntity entity = new ImageEntity();
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                if (TextUtils.isEmpty(name)) {
                    continue;
                }
                String path = cursor.getString(2);
                long size = cursor.getLong(3);
                long timeAdd = cursor.getLong(4);
                long timeModify = cursor.getLong(5);
                long timeToken = cursor.getLong(6);

                entity.setId(id);
                entity.setName(name);
                entity.setUrl(path);
                entity.setSize(size);
                entity.setTimeAdd(timeAdd);
                entity.setTimeModify(timeModify);
                entity.setTimeToken(timeToken);

                if (entity.isRang7()) {
                    String fileName = getString(R.string.folder_recent);
                    ImageFolderEntity folder = mDataFolder.get(fileName);
                    if (folder != null) {
                        folder.addFile(entity);
                    } else {
                        ImageFolderEntity newFolder = new ImageFolderEntity(fileName);
                        newFolder.setPath(path.replace(name, ""));
                        newFolder.setName(fileName);
                        newFolder.addFile(entity);
                        mDataFolder.put(fileName, newFolder);
                    }
                }

                String folderName = getFolderName(path);
                ImageFolderEntity folder = mDataFolder.get(folderName);
                if (folder != null) {
                    folder.addFile(entity);
                } else {
                    ImageFolderEntity newFolder = new ImageFolderEntity();
                    newFolder.setPath(path.replace(name, ""));
                    newFolder.setName(folderName);
                    newFolder.addFile(entity);
                    mDataFolder.put(folderName, newFolder);
                }
            }
            cursor.close();
        }

        List<ImageFolderEntity> mDataDDR = new ArrayList<ImageFolderEntity>();
        if (!mDataFolder.isEmpty()) {
            mDataDDR.addAll(mDataFolder.values());
            Collections.sort(mDataDDR);
            mFolderAdapter.update(mDataDDR);

            int lastPosition = PreferencesUtil.getInt("folder-last-position", 0);
            if (lastPosition >= mFolderAdapter.getCount()) {
            }
            mGridAdapter.update(mFolderAdapter.getItem(lastPosition).getList());
            mFolderAdapter.setSelection(lastPosition);
            String photoName = mFolderAdapter.getItem(lastPosition).getName();
            mTextFolder.setText(photoName);
            mMenuWindows.setListSize(mFolderAdapter.getCount());
        }
    }

    private static String getFolderName(String path) {
        String folderName = "";
        String folders[] = path.split("/");
        if (folders != null) {
            int size = folders.length;

            if (size == 1) {
                folderName = folders[0];
            } else if (size > 1) {
                folderName = folders[size - 2];
            } else {
                folderName = "";
            }
        }
        return folderName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NUM) {
            if (resultCode == RESULT_OK) {
                mGridAdapter.notifyDataSetChanged();
                updateBtn();

                if (data != null) {
                    setResult(RESULT_OK, data);
                    finishAndClear();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAndClear();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send:
                submit();
                break;
            case R.id.back:
                finishAndClear();
                break;
            case R.id.preview:
                Intent intent = new Intent(ImageMainActivity.this, PreviewActivity.class);
                intent.putExtra("preview", true);
                startActivityForResult(intent, REQUEST_CODE_NUM);
                break;
            case R.id.text_folder:
                maskView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        maskView.setVisibility(View.VISIBLE);
                    }
                }, 500);
                mMenuWindows.showAtLocation2(view, Gravity.LEFT | Gravity.BOTTOM, 0, getResources().getDimensionPixelSize(R.dimen.image_head_height));
                break;
        }
    }
}
