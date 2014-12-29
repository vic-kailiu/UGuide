package com.kai.uguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_Logo = 1500;
    private static int SPLASH_TIME_LogoBackOut = 1000;
    private static int SPLASH_TIME_OUT = 10000;

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

        final AnimatorSet set = new AnimatorSet();
        ValueAnimator animator = ObjectAnimator.ofFloat(findViewById(R.id.logo), "translationX", 0, 95);
        set.playTogether(
                Glider.glide(Skill.QuadEaseIn, 1000, animator)
                );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.FadeOutDown)
                        .duration(1700)
                        .playOn(findViewById(R.id.logo_fore_hp_shadow));
            }
        }, SPLASH_TIME_LogoBackOut);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                set.setDuration(1000);
                set.start();
            }
        }, SPLASH_TIME_Logo);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(i);

                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
