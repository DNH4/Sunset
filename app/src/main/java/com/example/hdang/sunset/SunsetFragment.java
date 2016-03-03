package com.example.hdang.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by hdang on 2/26/2016.
 */
public class SunsetFragment extends Fragment {

    private View mSceneView;
    private View mSunView;
    private View mSkyView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    private boolean isReverse = false;
    //private boolean runOnce = false;

    // ini animation obj for seamless transition b/w going forward and reverse when click on the screen
    private AnimatorSet animatorSet = new AnimatorSet();
    private ObjectAnimator heightAnimator;
    private ObjectAnimator sunsetSkyAnimator;
    private ObjectAnimator nightSkyAnimator;

    public static SunsetFragment newInstance() {

        //Bundle args = new Bundle();

        SunsetFragment fragment = new SunsetFragment();
        //fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSceneView = v;
        mSunView = v.findViewById(R.id.sun);
        mSkyView = v.findViewById(R.id.sky);

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);

        mSkyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation(isReverse);
                isReverse = !isReverse;
            }
        });

        return v;
    }

    private void startAnimation(boolean isReverse){
        float sunYStart = mSunView.getTop();// top is 0
        float sunYEnd = mSkyView.getHeight();// get bottom of the sky
        float sunYCurrent = sunYStart;
        int skyColorCurrent = mBlueSkyColor;

        // if animatorSet already ran once and still running in the middlewonder
        //if(animatorSet.isRunning()){
        if(animatorSet.isRunning()){
            //update current value for seamless transition
            sunYCurrent = (float)heightAnimator.getAnimatedValue("y"); //save the current running height
            if(sunsetSkyAnimator.isRunning()){
                skyColorCurrent = (int) sunsetSkyAnimator.getAnimatedValue("backgroundColor");
            } else {
                skyColorCurrent =  (int) nightSkyAnimator.getAnimatedValue("backgroundColor");
            }
            animatorSet.end();
        }else if(isReverse){
            sunYCurrent = sunYEnd;
            skyColorCurrent = mNightSkyColor;
        }



        ObjectAnimator sunAnimatorX = ObjectAnimator
                .ofFloat(mSunView, "scaleX", 0.9f, 1.0f);
        sunAnimatorX.setDuration(1000);
        sunAnimatorX.setRepeatCount(ValueAnimator.INFINITE);
        sunAnimatorX.setRepeatMode(ValueAnimator.REVERSE);
        sunAnimatorX.start();

        ObjectAnimator sunAnimatorY = ObjectAnimator
                .ofFloat(mSunView, "scaleY", 0.9f, 1.0f);
        sunAnimatorY.setDuration(1000);
        sunAnimatorY.setRepeatCount(ValueAnimator.INFINITE);
        sunAnimatorY.setRepeatMode(ValueAnimator.REVERSE);
        sunAnimatorY.start();

        heightAnimator = ObjectAnimator //moving down
            .ofFloat(mSunView, "y", sunYCurrent, sunYEnd);
        heightAnimator.setDuration(3000);

        heightAnimator.setInterpolator(new AccelerateInterpolator());

        //Doesn't work correctly because color is not one simple number
        sunsetSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", skyColorCurrent, mSunsetSkyColor)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator()); // This will tell it how to interpolate color

        nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", skyColorCurrent, mNightSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());


        animatorSet = new AnimatorSet(); // start animatorSet
        //Reverse sunset process
        if(!isReverse){
            //animatorSet = new AnimatorSet();
            animatorSet
                    .play(heightAnimator)
                    .with(sunsetSkyAnimator)
                    .before(nightSkyAnimator);
            animatorSet.start();
        } else {

            heightAnimator = ObjectAnimator
                    .ofFloat(mSunView, "y", sunYCurrent, sunYStart)
                    .setDuration(3000);
            heightAnimator.setInterpolator(new AccelerateInterpolator());

            sunsetSkyAnimator = ObjectAnimator
                    .ofInt(mSkyView, "backgroundColor", skyColorCurrent, mBlueSkyColor)
                    .setDuration(3000);
            sunsetSkyAnimator.setEvaluator(new ArgbEvaluator()); // This will tell it how to interpolate color

            nightSkyAnimator = ObjectAnimator
                    .ofInt(mSkyView, "backgroundColor", skyColorCurrent, mSunsetSkyColor)
                    .setDuration(1500);
            nightSkyAnimator.setEvaluator(new ArgbEvaluator());

            //animatorSet = new AnimatorSet();
            animatorSet
                    .play(nightSkyAnimator)
                    .before(sunsetSkyAnimator)
                    .with(heightAnimator);
            animatorSet.start();
        }

    }

}
