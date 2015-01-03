package com.kai.uguide;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.qq.wx.img.imgsearcher.ImgListener;
import com.qq.wx.img.imgsearcher.ImgResult;
import com.qq.wx.img.imgsearcher.ImgSearcher;
import com.qq.wx.img.imgsearcher.ImgSearcherState;

import java.io.ByteArrayOutputStream;

public class MainActivity extends Activity implements ImgListener {

    //Home Page Variable
    private static final String screKey = "eca6dc6c8f426ffe568ab9328c4be095e3ad91883bc4cd68";

    int mInitSucc = 0;
    //Result Page
    private TextView mTextView;
    private ImageView searchImageView;
    private Bitmap bm = null;

    private String imgFileName = null;

    //About Result
    private String mResUrl;
    private String mResMD5;
    private String mResPicDesc;

    static boolean animated = false;
    static boolean retrieved = false;
    static boolean searchRet = false;

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

    private void initResultUI() {
        // BEGIN_INCLUDE (inflate_set_custom_view)
        // Inflate a "Done/Cancel" custom action bar view.
        final LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(
                R.layout.action_bar_cancel, null);

        customActionBarView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTextView.setText("Cancelling...");
                        // "Cancel"
                        //int ret =
                                ImgSearcher.shareInstance().cancel();
//                        if (0 != ret) {
//                            finish();
//                        }
                    }
                });

        // Show the custom action bar view and hide the normal Home icon and title.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        // END_INCLUDE (inflate_set_custom_view)

        setContentView(R.layout.search_demo);

        mTextView = (TextView) findViewById(R.id.searchTextView);
        searchImageView = (ImageView) findViewById(R.id.searchImageView);
        searchImageView.setImageBitmap(bm);
        final View searchImageFrame = findViewById(R.id.searchImageFrame);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)searchImageFrame.getLayoutParams();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        params.height = displayMetrics.heightPixels / 2;
        params.width = displayMetrics.widthPixels / 2;
        searchImageFrame.setLayoutParams(params);

        ValueAnimator animator1 = ObjectAnimator.ofFloat(searchImageFrame, "alpha", (float)0.9, 1);
        ValueAnimator animator2 = ObjectAnimator.ofFloat(searchImageFrame, "alpha", 1, (float)0.9);
        //ValueAnimator animator3 = ObjectAnimator.ofFloat(searchImageFrame, "alpha", (float)0.9, 1);
        animator1.setDuration(1500);
        animator2.setDuration(1500);
        //animator3.setDuration(1200);

        final AnimatorSet set = new AnimatorSet();
        set.playSequentially(animator1, animator2);//, animator3);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                YoYo.with(Techniques.Landing)
                        .duration(1000)
                        .playOn(searchImageFrame);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        YoYo.with(Techniques.Flash)//Techniques.FadeOutUp)
//                                .duration(700)
//                                .playOn(searchImageFrame);
//                    }
//                }, 1700);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        YoYo.with(Techniques.ZoomOutUp)//Techniques.FadeOutUp)
                                .duration(1000)
                                .playOn(searchImageFrame);
                    }
                }, 1700);// * 2);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (retrieved) {
                    turnToResultActivity();
                } else {
                    animated = true;
                    set.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });

        set.start();

        byte[] imgByte = getJpg(bm);
        int ret = startImgSearching(imgByte);
        if (0 != ret) {
            finish();
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
            return -1;
        }
    }

    @Override
    public void onGetError(int errorCode) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "ErrorCode = " + errorCode, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onGetResult(ImgResult result) {
        // TODO Auto-generated method stub
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
                searchRet = true;
            } else {
                searchRet = false;
            }
        }
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);

        retrieved = true;

        if (animated)
            turnToResultActivity();
    }

    public void turnToResultActivity() {
        if (!searchRet) {
            noResultDialog();
            return;
        }
        Intent it = new Intent(this, ResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("ret", searchRet);
        bundle.putString("url", mResUrl);
        bundle.putString("md5", mResMD5);
        bundle.putString("picDesc", mResPicDesc);
        it.putExtras(bundle);
        startActivity(it);
        finish();
    }

    private void noResultDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(
                MainActivity.this).create();
        // Setting Dialog Title
        alertDialog.setTitle("No Result");
        // Setting Dialog Message
        alertDialog.setMessage("So sorry that no result actually found for your picture, you can try take a higher quality one or in other angles  :) \n\nClick 'OK' to redirect you to home page");
        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.earth_small);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(MainActivity.this, SelectImageActivity.class);
                startActivity(it);
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onGetState(ImgSearcherState state) {
//        if (ImgSearcherState.Canceling == state) {
//            mTextView.setText("Cancelling...");
//        } else
        if (ImgSearcherState.Canceled == state) {
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("imgFileName", imgFileName);
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
            return (ImgSearcher.shareInstance().cancel() == 0?
                    true:false) ;
            // Monitor the return key
            //finish();
        } else {
            return super.onKeyDown(keyCode, event);
        }
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