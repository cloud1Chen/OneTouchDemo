package com.huorongliang.onetouchdemo;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import static com.huorongliang.onetouchdemo.ScreenshotContentObserver.SORT_ORDER;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static String MAIN_TAG = "LHRTAG";
    private Button open_everything;
    private Button open_everything_2;
    private Button open_everything_3;

    private Context mContext;
    private ScreenshotContentObserver mScreenObserver;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x110) {
                Log.i(MAIN_TAG, "message is back! " + msg.obj.toString());
            }
            if (msg.what == 0x111){
                Log.i(MAIN_TAG, "message is back! " + msg.obj.toString());
                Intent intent = new Intent(MainActivity.this, ShowImgActivity.class);
                intent.putExtra("uri", msg.obj.toString());
                startActivity(intent);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        open_everything = (Button) findViewById(R.id.open_everything);
        open_everything_2 = (Button) findViewById(R.id.open_everything_2);
        open_everything_3 = (Button) findViewById(R.id.open_everything_3);

        open_everything.setOnClickListener(this);
        open_everything_2.setOnClickListener(this);
        open_everything_3.setOnClickListener(this);
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) mContext, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, SORT_ORDER);
        }
        listenDB();
    }

    private void listenDB() {
        mScreenObserver = new ScreenshotContentObserver(mContext, mHandler);
        registerContentObserver();
    }

    private void registerContentObserver() {
        /*之前说过，非常关键的Uri*/
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        getContentResolver().registerContentObserver(imageUri, false, mScreenObserver);
        Log.i(MAIN_TAG, "registered!---------------------------");
    }

    @Override
    public void onClick(View view) {
        Log.d("LHRTAG", "onclick()");
        switch (view.getId()) {
            case R.id.open_everything:
                launchCamera();
                break;
            case R.id.open_everything_2:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra("camerasensortype", Camera.CameraInfo.CAMERA_FACING_FRONT); // 调用前置摄像头
                intent.putExtra("autofocus", true); // 自动对焦
                intent.putExtra("fullScreen", false); // 全屏
                intent.putExtra("showActionIcons", false);
                startActivity(intent);
                break;

//                startActivityForResult(intent, PICK_FROM_CAMERA);
//                int cameraCount = 0;
//                Camera cam = null;
//
//                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//                cameraCount = Camera.getNumberOfCameras(); // get cameras number
//
//                for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
//                    Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
//                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) { // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
//                        cam = Camera.open(camIdx);
//                    }
//                    break;
//                }
                case R.id.open_everything_3:
                    launchCamera();
                    break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mScreenObserver);
        Log.i(MAIN_TAG, "onDestroy()!---------------------------");
        Log.i(MAIN_TAG, "unregistered!---------------------------");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == 1)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else
            {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void launchCamera()
    {
        try{
            //获取相机包名
            Intent infoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ResolveInfo res = mContext.getPackageManager().
                    resolveActivity(infoIntent, 0);
            if (res != null)
            {
                String packageName=res.activityInfo.packageName;
                if(packageName.equals("android"))
                {
                    infoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
                    res = mContext.getPackageManager().
                            resolveActivity(infoIntent, 0);
                    if (res != null)
                        packageName=res.activityInfo.packageName;
                }
                //启动相机
                startApplicationByPackageName(packageName);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    //通过包名启动应用
    private void startApplicationByPackageName(String packName)
    {
        PackageInfo packageInfo = null;
        try{
            packageInfo = mContext.getPackageManager().getPackageInfo(packName, 0);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(null == packageInfo){
            return;
        }
        Intent resolveIntent = new Intent();
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageInfo.packageName);
        List<ResolveInfo> resolveInfoList =mContext.getPackageManager().queryIntentActivities(resolveIntent, 0);
        if(null == resolveInfoList){
            return;
        }
        Iterator<ResolveInfo> iter = resolveInfoList.iterator();
        while(iter.hasNext()){
            ResolveInfo resolveInfo=(ResolveInfo) iter.next();
            if(null==resolveInfo){
                return;
            }
            String packageName = resolveInfo.activityInfo.packageName;
            String className = resolveInfo.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            mContext.startActivity(intent);
        }
    }
}
