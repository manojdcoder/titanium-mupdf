package com.artifex.mupdflib;

import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;

@SuppressLint("NewApi")
public class SafeAnimatorInflater
{
	private View mView;

	public SafeAnimatorInflater(Activity activity, int animation, View view)
	{
		AnimatorSet set = null;
		try{
			set = (AnimatorSet) AnimatorInflater.loadAnimator(activity, TiRHelper.getResource("anim.info"));
		} catch (ResourceNotFoundException exp) {
			Log.e("SageAnimatorInflater", "XML resouce not found!");
		}
		mView = view;
		set.setTarget(view);
		set.addListener(new Animator.AnimatorListener() {
			public void onAnimationStart(Animator animation) {
				mView.setVisibility(View.VISIBLE);
			}

			public void onAnimationRepeat(Animator animation) {
			}

			public void onAnimationEnd(Animator animation) {
				mView.setVisibility(View.INVISIBLE);
			}

			public void onAnimationCancel(Animator animation) {
			}
		});
		set.start();
	}
}
