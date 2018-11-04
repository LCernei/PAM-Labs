package com.labs.lc.lab4;

import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import uk.co.senab.photoview.PhotoView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private ViewGroup rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);
        rootLayout = findViewById(R.id.root);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        imageView.getLayoutParams().height = (int) (width*0.75f);
        imageView.requestLayout();

        imageView.setBackgroundResource(R.drawable.loading_bar_animation);
        AnimationDrawable animation = (AnimationDrawable) imageView.getBackground();
        animation.start();
    }
}
