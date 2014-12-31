package com.kai.uguide;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import com.edmodo.cropper.CropImageView;
import com.kai.uguide.utils.PicShrink;


public class CropImageActivity extends Activity {

    static final int MARGIN = 16;
    CropImageView cropImageView;
    Bitmap bitmap;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        mCurrentPhotoPath = (String) getIntent().getStringExtra("filePath");
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);

        setPic();
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
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = cropImageView.getWidth();
        int targetH = cropImageView.getHeight();

        if (targetW * targetH == 0) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            targetW = size.x - MARGIN * 2;
            targetH = size.y - MARGIN * 2;
        }

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
        cropImageView.setImageBitmap(bitmap);
    }
}
