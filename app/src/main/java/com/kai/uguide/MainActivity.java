package com.kai.uguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kai.uguide.utils.PicShrink;
import com.qq.wx.img.imgsearcher.ImgListener;
import com.qq.wx.img.imgsearcher.ImgResult;
import com.qq.wx.img.imgsearcher.ImgSearcher;
import com.qq.wx.img.imgsearcher.ImgSearcherState;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends Activity implements ImgListener {

    final static String TAG = "MainActivity";

    //Home Page Variable
    private static final String screKey = "eca6dc6c8f426ffe568ab9328c4be095e3ad91883bc4cd68";
    final int TAKE_PICTURE = 1;
    final int FROM_ALBUM = 2;
    int mInitSucc = 0;
    //Result Page
    private Button mCancelBtn;
    private TextView mTextView;
    private Bitmap bm = null;

    private String imgFileName = null;

    //About Result
    private String mResUrl;
    private String mResMD5;
    private String mResPicDesc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bm = getIntent().getParcelableExtra("Image");

        //initMainUI();
        preInitImg();

        initResultUI();
    }

    private void preInitImg() {
        ImgSearcher.shareInstance().setListener(this);
        mInitSucc = ImgSearcher.shareInstance().init(this, screKey);

        if (mInitSucc != 0) {
            Toast.makeText(this, "Failed to initialize",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initMainUI() {
        int foo = 1;
    }

//	private void initMainUI() {
//		setContentView(R.layout.img_demo);
//
//		mCameraBtn = (Button) findViewById(R.id.camera_start);
//		mCameraBtn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				String filepath = null;
//				boolean sdCardExist = Environment.getExternalStorageState()
//						.equals(android.os.Environment.MEDIA_MOUNTED); //Judge SD card is present
//				if (!sdCardExist) {
//					/**
//					 * Write mobile phone's internal storage
//					 */
//					@SuppressWarnings("deprecation")
//					File mediaFilesDir = getDir("mediaFiles", Context.MODE_WORLD_READABLE);
//					filepath = mediaFilesDir.getPath();
//				}
//				else {
//					filepath = Environment.getExternalStorageDirectory().getPath()
//							+ "/Tencent/mm";
//					File outputpath = new File(filepath);
//					if (!outputpath.exists()) {
//						outputpath.mkdirs();
//					}
//				}
//
//				//**For test
//				Date date = new Date();
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//				String dateStr = dateFormat.format(date);
//				String imgType = ".jpg";
//				imgFileName = filepath + "/" + dateStr + imgType;
//				File imgFile = new File(imgFileName);
//
//				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//				Uri imageUri = Uri.fromFile(imgFile);
//				//Specify a storage path(in SD card), workupload.jpg is a temp file, will be replaced after taking a picture.
//				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//				cameraIntent.putExtra("imgFilename", imgFileName);
//				startActivityForResult(cameraIntent, TAKE_PICTURE);
//			}
//		});
//
//		mAlbumBtn = (Button) findViewById(R.id.album);
//		mAlbumBtn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
//				openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//				startActivityForResult(openAlbumIntent, FROM_ALBUM);
//			}
//		});
//
//		mTextView = (TextView) findViewById(R.id.start_searching);
//	}

    @SuppressWarnings("deprecation")
    private void initResultUI() {
        setContentView(R.layout.search_demo);

        LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.staff);

        Drawable mDrawable = new BitmapDrawable(getResources(), bm);
        mLinearLayout.setBackgroundDrawable(mDrawable);
        mCancelBtn = (Button) findViewById(R.id.cancel);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int ret = ImgSearcher.shareInstance().cancel();
                if (0 != ret) {
                    initMainUI();
                }
            }
        });

        mTextView = (TextView) findViewById(R.id.start_searching);

        byte[] imgByte = getJpg(bm);
        int ret = startImgSearching(imgByte);
        if (0 != ret) {
            initMainUI();
        }
    }

    private int startImgSearching(byte[] img) {
        if (mInitSucc != 0) {
            mInitSucc = ImgSearcher.shareInstance().init(this, screKey);
        }
        if (mInitSucc != 0) {
            Toast.makeText(this, "Failed to initialize",
                    Toast.LENGTH_SHORT).show();
            return -1;
        }

        int ret = ImgSearcher.shareInstance().start(img);
        if (0 == ret) {
            return 0;
        } else {
            Toast.makeText(this, "ErrorCode = " + ret, Toast.LENGTH_LONG).show();
            ;
            return -1;
        }
    }

    @Override
    public void onGetError(int errorCode) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "ErrorCode = " + errorCode, Toast.LENGTH_LONG).show();
        initMainUI();
    }

    @Override
    public void onGetResult(ImgResult result) {
        // TODO Auto-generated method stub
        boolean ret = true;
        if (result != null) {
            if (1 == result.ret && result.res != null) {
                int resSize = result.res.size();
                for (int i = 0; i < resSize; ++i) {
                    ImgResult.Result res = (ImgResult.Result) result.res.get(i);
                    if (res != null) {
                        mResMD5 = res.md5;
                        mResUrl = res.url;
                        mResPicDesc = res.picDesc;
                    }
                }
                ret = true;
            } else {
                ret = false;
            }
        }
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        turnToResultActivity(ret);
    }

    public void turnToResultActivity(boolean isFound) {
        Intent it = new Intent(this, ResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("ret", isFound);
        bundle.putString("url", mResUrl);
        bundle.putString("md5", mResMD5);
        bundle.putString("picDesc", mResPicDesc);
        it.putExtras(bundle);
        startActivity(it);
        finish();
    }

    @Override
    public void onGetState(ImgSearcherState state) {
        if (ImgSearcherState.Canceling == state) {
            mTextView.setText("Cancelling...");
        } else if (ImgSearcherState.Canceled == state) {
            initMainUI();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("imgFileName", imgFileName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {//result is not correct
            return;
        } else {

            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
            }
            if (TAKE_PICTURE == requestCode) {
                bm = PicShrink.compress(imgFileName);
                File file = new File(imgFileName);
                file.delete();
            } else {
                Uri uri = data.getData();
                if (null != uri) {
                    bm = PicShrink.compress(this, uri);
                    if (null == bm) {
                        return;
                    }
                } else {
                    return;
                }
            }
            initResultUI();
        }
    }

    public byte[] getJpg(Bitmap bitmap) {
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outs);

        return outs.toByteArray();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
            }
            // Monitor the return key
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != bm && !bm.isRecycled()) {
            bm.recycle();
        }
        ImgSearcher.shareInstance().destroy();
    }
}