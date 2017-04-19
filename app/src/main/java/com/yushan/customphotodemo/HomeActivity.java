package com.yushan.customphotodemo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class HomeActivity extends Activity implements View.OnClickListener {

    private Button btn_jump2custom;
    private TextView tv_filename;
    private static final int PHOTO_PICKED_WITH_DATA = 3021;
    private String fileName = "选取的照片：";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        btn_jump2custom = (Button) findViewById(R.id.btn_jump2custom);
        btn_jump2custom.setOnClickListener(this);
        tv_filename = (TextView) findViewById(R.id.tv_filename);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_jump2custom:
                fileName = "选取的照片：";
                Intent intent = new Intent(this, ImageMainActivity.class);
                intent.putExtra("action-original", true);
                intent.putExtra("fromWhere", "ClubBroadcastDetailActivity");
                startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == PHOTO_PICKED_WITH_DATA){
            ArrayList<Uri> images = data.getParcelableArrayListExtra("result");
            for (int i = 0; i < images.size(); i++) {
                sendPicByUri(images.get(i));
            }

            tv_filename.setText(fileName);
        }

    }

    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        String st8 = "找不到照片";
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            fileName = fileName + "\n" + picturePath ;
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }

            fileName = fileName + "\n" + file.getAbsolutePath() ;
        }

    }
}
