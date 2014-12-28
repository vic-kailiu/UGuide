package com.kai.uguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;


public class HomeActivity extends Activity {

    ImageView img;
    Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        img = (ImageView) findViewById(R.id.lands1);
//        anim = AnimationUtils.loadAnimation(getApplicationContext(),
//                R.anim.blink);
//        img.startAnimation(anim);
        ImageView svgView = (ImageView) findViewById(R.id.SvgView);


        int foo = 1;
    }

    public void on_start_click(View view) {
        Intent i = new Intent(this, SelectImageActivity.class);
        startActivity(i);
    }
}
