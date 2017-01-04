package com.huorongliang.onetouchdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by huorong.liang on 2017/1/4.
 */

public class ScreenshotContentObserver extends ContentObserver {
    Context mContext;
    Handler mHandler;
    static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";

    public ScreenshotContentObserver(Context mContext, Handler mHandler) {
        super(mHandler);
        this.mContext = mContext;
        this.mHandler = mHandler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.i(MainActivity.MAIN_TAG, "observer onChange------------------------------------------");
        Log.i(MainActivity.MAIN_TAG, "database is changed!------------------------------------------");
//        onChange();
        sendUriWhenChange();
    }

    private void sendUriWhenChange() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) mContext, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            cursor = mContext.getContentResolver().query(uri, null, null, null, SORT_ORDER);
        }
        if (cursor != null){
            while(cursor.moveToNext()){
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            }
        }
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Log.i(MainActivity.MAIN_TAG, "observer onChange with uri------------------------------------------");
        Log.i(MainActivity.MAIN_TAG, "database is changed!------------------------------------------");
        mHandler.obtainMessage(0x111, uri).sendToTarget();
    }

    private void onChange(){
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) mContext, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            cursor = mContext.getContentResolver().query(uri, null, null, null, SORT_ORDER);
        }
        Log.i(MainActivity.MAIN_TAG, "cursor == null " + (cursor == null ? "true" : "false"));
        if (cursor != null) {
            Log.i(MainActivity.MAIN_TAG, "The number of data is:" + cursor.getCount());

            StringBuffer sb = new StringBuffer();

            while (cursor.moveToNext()) {
//                long addDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
//                long currentTime = System.currentTimeMillis();
//                if (Math.abs(currentTime - addDate) < 3000L){
//                    String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                }
                String fileName = cursor.getString(cursor.getColumnIndex("_data"));
                String[] a = fileName.split("/");
                Log.i(MainActivity.MAIN_TAG, a[a.length - 2] + a[a.length - 1]);  //观察输出地目录名/文件名
                sb.append("目录名称：" + a[a.length - 2]);
            }
            cursor.close();
            /*将消息传递给主线程，消息中绑定了目录信息*/
            mHandler.obtainMessage(0x110, sb.toString()).sendToTarget();
        }
    }


}
