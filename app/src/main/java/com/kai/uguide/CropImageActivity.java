package com.kai.uguide;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.edmodo.cropper.CropImageView;
import com.enrique.stackblur.StackBlurManager;
import com.kai.uguide.utils.PicShrink;


public class CropImageActivity extends Activity {

    private final int MARGIN = 16;
    private CropImageView cropImageView;
    private Bitmap bitmap;
    private Drawable d;
    private String mCurrentPhotoPath;

    //for background blur
    //private StackBlurManager _stackBlurManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLoadingLayout();
        startLoadingThread();

//        setContentView(R.layout.activity_crop_image);
//
//        mCurrentPhotoPath = (String) getIntent().getStringExtra("filePath");
//        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
//
//        threadSetPic();
    }

    private void setCropLayout() {
        setContentView(R.layout.activity_crop_image);
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);

        if (bitmap != null)
            cropImageView.setImageBitmap(bitmap);
        if (d != null)
            getWindow().setBackgroundDrawable(d);
    }

    private void setLoadingLayout() {
        setContentView(R.layout.layout_loading);
        View img = findViewById(R.id.loading_logo);

        float density  = getResources().getDisplayMetrics().density;
        Animation an = new RotateAnimation(0.0f, 360.0f, 32 * density, 32 * density);

        an.setDuration(2000);
        an.setRepeatCount(Animation.INFINITE);
        an.setRepeatMode(Animation.INFINITE);
        //an.setFillAfter(false);              // DO NOT keep rotation after animation
        //an.setFillEnabled(true);             // Make smooth ending of Animation
//        an.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {}
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {}
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                img.setRotation(180.0f);      // Make instant rotation when Animation is finished
//            }
//        });

        img.startAnimation(an);
    }

    private void startLoadingThread () {
        Thread thread = new Thread() {
            public void run() {
                setPic();
                setBackground();

                CropImageActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCropLayout();
                    }
                });
            }
        };

        thread.setDaemon(true);
        thread.start();
    }

    private void setPic() {
        mCurrentPhotoPath = (String) getIntent().getStringExtra("filePath");

        // Get the dimensions of the View
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int targetW = size.x - MARGIN * 2;
        int targetH = size.y - MARGIN * 2;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        cropImageView.setImageBitmap(bitmap);
    }

    private void setBackground() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int x = 0;
        int y = 0;
        double screenRatio = 1.0 * displayMetrics.widthPixels / displayMetrics.heightPixels;
        double imgRatio = 1.0 * bitmap.getWidth() / bitmap.getHeight();
        if (imgRatio > screenRatio) {
            width = (int) (bitmap.getWidth() / imgRatio);
            x = (int) ((bitmap.getWidth() - width)/2);
        } else if (imgRatio < screenRatio) {
            height  = (int) (bitmap.getHeight() * imgRatio);
            y = (int) ((bitmap.getHeight() - height)/2);
        }
        Bitmap fitImg = Bitmap.createBitmap(bitmap, x, y, width, height);

        StackBlurManager _stackBlurManager = new StackBlurManager(fitImg);
        d = new BitmapDrawable(getResources(),  _stackBlurManager.process(30) );//processRenderScript(this, 20) );
    }

    public void go_to_search(View view) {
        Intent i = new Intent(this, MainActivity.class);

        Bitmap compressed;
        Bitmap bm = cropImageView.getCroppedImage();

        if (bm != null) {
            compressed = PicShrink.compress(bm);
        } else {
            compressed = PicShrink.compress(bitmap);
        }

        i.putExtra("Image", compressed);
        startActivity(i);

        finish();
    }

}
