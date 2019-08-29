package com.seu.magiccamera.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.service.autofill.Transformation;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seu.magiccamera.R;
import com.seu.magiccamera.adapter.FilterAdapter;
import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.camera.CameraEngine;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.helper.SavePictureTask;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.widget.MagicCameraView;
import com.squareup.picasso.Picasso;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Date;
import java.util.Locale;

/**
 * Created by chiech on 2019/8/26.
 */
public class CameraActivity extends Activity implements SavePictureTask.OnPictureSaveListener {
    private LinearLayout mFilterLayout,Ll_photo,Ll_vedio,mFilterLayoutBeauty;
    private RecyclerView mFilterListView;
    private FilterAdapter mAdapter;
    private MagicEngine magicEngine;
    private boolean isRecording = false,isPhoto = true;
    private final int MODE_PIC = 1;
    private final int MODE_VIDEO = 2;
    private int mode = MODE_PIC;

    private ImageView btn_shutter,Iv_Album;
    private ImageView btn_mode;
    private Chronometer chronometer;
    private IndicatorSeekBar seekBar;
    private int level = 1;
    private final int IMAGE = 1;

    private TextView Tv_photo,Tv_vedio,Tv_photoline,Tv_vedioline,Tv_appname;

    private ObjectAnimator animator;
    private static String TAG = "CameraActivity";

    private final MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE,
            MagicFilterType.FAIRYTALE,
            MagicFilterType.SUNRISE,
            MagicFilterType.SUNSET,
            MagicFilterType.WHITECAT,
            MagicFilterType.BLACKCAT,
            MagicFilterType.SKINWHITEN,
            MagicFilterType.HEALTHY,
            MagicFilterType.SWEETS,
            MagicFilterType.ROMANCE,
            MagicFilterType.SAKURA,
            MagicFilterType.WARM,
            MagicFilterType.ANTIQUE,
            MagicFilterType.NOSTALGIA,
            MagicFilterType.CALM,
            MagicFilterType.LATTE,
            MagicFilterType.TENDER,
            MagicFilterType.COOL,
            MagicFilterType.EMERALD,
            MagicFilterType.EVERGREEN,
            MagicFilterType.CRAYON,
            MagicFilterType.SKETCH,
            MagicFilterType.AMARO,
            MagicFilterType.BRANNAN,
            MagicFilterType.BROOKLYN,
            MagicFilterType.EARLYBIRD,
            MagicFilterType.FREUD,
            MagicFilterType.HEFE,
            MagicFilterType.HUDSON,
            MagicFilterType.INKWELL,
            MagicFilterType.KEVIN,
            MagicFilterType.LOMO,
            MagicFilterType.N1977,
            MagicFilterType.NASHVILLE,
            MagicFilterType.PIXAR,
            MagicFilterType.RISE,
            MagicFilterType.SIERRA,
            MagicFilterType.SUTRO,
            MagicFilterType.TOASTER2,
            MagicFilterType.VALENCIA,
            MagicFilterType.WALDEN,
            MagicFilterType.XPROII
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = builder
                .build((MagicCameraView)findViewById(R.id.glsurfaceview_camera));
        initView();
    }



    private void initView(){
        mFilterLayout = (LinearLayout)findViewById(R.id.layout_filter);
        mFilterLayoutBeauty = (LinearLayout)findViewById(R.id.layout_filter_beauty);
        Ll_photo = (LinearLayout)findViewById(R.id.Ll_photo);
        Ll_vedio = (LinearLayout)findViewById(R.id.Ll_vedio);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);

        btn_shutter = (ImageView)findViewById(R.id.btn_camera_shutter);

        chronometer = (Chronometer)findViewById(R.id.camera_chronometer);

        Tv_appname = (TextView)findViewById(R.id.Tv_AppName);
        Tv_photo = (TextView)findViewById(R.id.Tv_photo);
        Tv_photoline = (TextView)findViewById(R.id.Tv_photo_line);
        Tv_vedio = (TextView)findViewById(R.id.Tv_vedio);
        Tv_vedioline = (TextView)findViewById(R.id.Tv_vedio_line);
        seekBar = (IndicatorSeekBar)findViewById(R.id.seekbar);
        Iv_Album = (ImageView)findViewById(R.id.Iv_Album);

        findViewById(R.id.btn_camera_filter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_closefilter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_beauty_close).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_shutter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_switch).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_beauty).setOnClickListener(btn_listener);
        findViewById(R.id.Ll_photo).setOnClickListener(btn_listener);
        findViewById(R.id.Ll_vedio).setOnClickListener(btn_listener);
        findViewById(R.id.Iv_Album).setOnClickListener(btn_listener);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterListView.setLayoutManager(linearLayoutManager);

        mAdapter = new FilterAdapter(this, types);
        mFilterListView.setAdapter(mAdapter);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        MagicCameraView cameraView = (MagicCameraView)findViewById(R.id.glsurfaceview_camera);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        params.width = screenSize.x;
        params.height = screenSize.x * 4 / 3;
        cameraView.setLayoutParams(params);

        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                level = seekParams.progress;
                magicEngine.setBeautyLevel(level);
                Log.e(TAG, "onSeeking: level "+level );
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });
    }

    private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener(){

        @Override
        public void onFilterChanged(MagicFilterType filterType) {
            magicEngine.setFilter(filterType);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(mode == MODE_PIC)
                takePhoto();
            else
                takeVideo();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }




    private View.OnClickListener btn_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_camera_shutter:
                    if (PermissionChecker.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(CameraActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                v.getId());
                    } else {
                        if(mode == MODE_PIC){
                            takePhoto();
                        }
                        else{
                            takeVideo();
                        }
                    }
                    break;
                case R.id.btn_camera_filter:
                    showFilters(1);
                    break;
                case R.id.btn_camera_switch:
                    magicEngine.switchCamera();
                    setCameraDisplayOrientation(CameraActivity.this,CameraEngine.getCameraId(),CameraEngine.getMyCamera());
                    break;
                case R.id.btn_camera_beauty:
                    showFilters(2);
                    break;
                case R.id.btn_camera_closefilter:
                    hideFilters(1);
                    break;
                case R.id.btn_camera_beauty_close:
                    hideFilters(2);
                    break;
                case R.id.Iv_Album:
                    Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivity(intent);
                    break;
                case R.id.Ll_photo:
                    if (!isPhoto){
                        Tv_photo.setTextColor(Color.parseColor("#FFFF06A6"));
                        Tv_photoline.setVisibility(View.VISIBLE);
                        Tv_vedio.setTextColor(Color.parseColor("#000000"));
                        Tv_vedioline.setVisibility(View.INVISIBLE);
                        isPhoto = !isPhoto;
                        mode = MODE_PIC;
                        btn_shutter.setImageResource(R.drawable.photo_btn);
                    }
                    break;
                case R.id.Ll_vedio:
                    if (isPhoto){
                        Tv_vedio.setTextColor(Color.parseColor("#FFFF06A6"));
                        Tv_vedioline.setVisibility(View.VISIBLE);
                        Tv_photo.setTextColor(Color.parseColor("#000000"));
                        Tv_photoline.setVisibility(View.INVISIBLE);
                        isPhoto = !isPhoto;
                        mode = MODE_VIDEO;
                        btn_shutter.setImageResource(R.drawable.video_btn);
                    }
            }
        }
    };


    public static int ReturnAngle(Activity activity){
        Camera.CameraInfo info = new Camera.CameraInfo();
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = -90;
                Log.e(TAG, "ReturnAngle: " + degrees );
                break;
            case Surface.ROTATION_90:
                degrees = -180;
                Log.e(TAG, "ReturnAngle: " + degrees );
                break;
            case Surface.ROTATION_180:
                degrees = -270;
                Log.e(TAG, "ReturnAngle: " + degrees );
                break;
            case Surface.ROTATION_270:
                degrees = -90;
                Log.e(TAG, "ReturnAngle: " + degrees );
                break;
        }
        return degrees;
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = -90;
                Log.e(TAG, "setCameraDisplayOrientation: "+degrees );
                break;
            case Surface.ROTATION_90:
                degrees = -180;
                Log.e(TAG, "setCameraDisplayOrientation: "+degrees );
                break;
            case Surface.ROTATION_180:
                degrees = -270;
                Log.e(TAG, "setCameraDisplayOrientation: "+degrees );
                break;
            case Surface.ROTATION_270:
                degrees = 0;
                Log.e(TAG, "setCameraDisplayOrientation: "+degrees );
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        Log.e(TAG, "setCameraDisplayOrientation result: "+result );
        camera.setDisplayOrientation(result);
    }


    private void takePhoto(){
        savePicture(getOutputMediaFile(),null,ReturnAngle(CameraActivity.this));

    }

    public void savePicture(final File file, SavePictureTask.OnPictureSaveListener listener, int angle){
        SavePictureTask savePictureTask = new SavePictureTask(file, listener,angle);
        MagicParams.magicBaseView.savePicture(savePictureTask);
        savePictureTask.setOnPictureSaveListener(new SavePictureTask.OnPictureSaveListener(){
            @Override
            public void onSaved( String result) {
                Log.e(TAG, "onSaved: "+result );
//                Picasso.with(CameraActivity.this)
//                        .load(result)
//                        .resize(45,45)
//                        .rotate(-90)
//                        .into(Iv_Album);

//                Matrix matrix = new Matrix();
//                matrix.setScale(0.1f,0.1f);
//                BitmapFactory.Options options =  new BitmapFactory.Options();
//                options.inSampleSize = 2;
//                options.inPreferredConfig = Bitmap.Config.ARGB_4444;
//                Bitmap bitmap = BitmapFactory.decodeFile(result,options);
//                Bitmap bmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
//                Iv_Album.setImageBitmap(bmp);
//                bitmap.recycle();
//                bmp.recycle();
//
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e(TAG, "run: " );
//                    }
//                },5000);
            }
        });

    }

    private void takeVideo(){
        if(isRecording) {
            chronometer.stop();
            Log.e(TAG, "takeVideo: stop record" );
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.setVisibility(View.INVISIBLE);
            Tv_appname.setVisibility(View.VISIBLE);
            btn_shutter.setImageResource(R.drawable.video_btn);
            magicEngine.stopRecord();
        }else {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            Log.e(TAG, "takeVideo: start record" );
            chronometer.setVisibility(View.VISIBLE);
            Tv_appname.setVisibility(View.INVISIBLE);
            btn_shutter.setImageResource(R.drawable.stop_video_btn);
            magicEngine.startRecord();
        }
        isRecording = !isRecording;
    }

    @Override
    public void onSaved(String result) {
        Toast.makeText(CameraActivity.this,"Image saved !",Toast.LENGTH_LONG).show();
        Log.e(TAG, "onSaved: "+result );
    }

    private void showFilters(int id){
        switch (id){
            case 1:
                ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", mFilterLayout.getHeight(), 0);
                animator.setDuration(200);
                animator.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        findViewById(R.id.btn_camera_shutter).setClickable(false);
                        mFilterLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }
                });
                animator.start();
                break;
            case 2:
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(mFilterLayoutBeauty, "translationY", mFilterLayoutBeauty.getHeight(), 0);
                animator1.setDuration(200);
                animator1.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        findViewById(R.id.btn_camera_shutter).setClickable(false);
                        mFilterLayoutBeauty.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }
                });
                animator1.start();
                break;
        }


    }

    private void hideFilters(int id){
        switch (id){
            case 1:
                ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", 0 ,  mFilterLayout.getHeight());
                animator.setDuration(200);
                animator.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // TODO Auto-generated method stub
                        mFilterLayout.setVisibility(View.INVISIBLE);
                        findViewById(R.id.btn_camera_shutter).setClickable(true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        // TODO Auto-generated method stub
                        mFilterLayout.setVisibility(View.INVISIBLE);
                        findViewById(R.id.btn_camera_shutter).setClickable(true);
                    }
                });
                animator.start();
                break;
            case 2:
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(mFilterLayoutBeauty, "translationY", 0 ,  mFilterLayoutBeauty.getHeight());
                animator1.setDuration(200);
                animator1.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // TODO Auto-generated method stub
                        mFilterLayoutBeauty.setVisibility(View.INVISIBLE);
                        findViewById(R.id.btn_camera_shutter).setClickable(true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        // TODO Auto-generated method stub
                        mFilterLayoutBeauty.setVisibility(View.INVISIBLE);
                        findViewById(R.id.btn_camera_shutter).setClickable(true);
                    }
                });
                animator1.start();
                break;
        }

    }

    public File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MagicCamera");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
}
