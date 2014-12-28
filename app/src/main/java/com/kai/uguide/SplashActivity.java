package com.kai.uguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_LogoOut = 5000;
    private static int SPLASH_TIME_OUT = 1000;

    ImageView img;
    Animation animFadein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
//        img = (ImageView) findViewById(R.id.logo_fore_hp);
//        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
//                R.anim.fade_in);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                img.startAnimation(animFadein);
////                YoYo.with(Techniques.FadeOutDown)
////                        .duration(1700)
////                        .playOn(findViewById(R.id.logo_fore_hp));
//            }
//        }, SPLASH_TIME_LogoOut);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(i);

                finish();
            }
        }, SPLASH_TIME_OUT);

        //img.startAnimation(animFadein);
    }
}
