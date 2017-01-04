package com.huorongliang.onetouchdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.FileNotFoundException;

/**
 * Created by huorong.liang on 2017/1/4.
 */

public class ShowImgActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);
        imageView = (ImageView) findViewById(R.id.iv_show);
        Intent intent = getIntent();
        String uriStr = intent.getStringExtra("uri");
        Uri uri = Uri.parse(uriStr);
        Bitmap bit = decodeUriAsBitmap(uri);
        if (bit != null){
            imageView.setImageBitmap(bit);
        }

    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
}
