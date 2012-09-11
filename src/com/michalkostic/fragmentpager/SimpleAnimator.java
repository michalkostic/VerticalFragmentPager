/**
 * SimpleAnimator - poor man's ValueAnimator (which is not available below API level 11)
 * 
 * Triggers animation event every n-miliseconds. Basically just wrapper for interpolator with timer.
 * 
 * @author Michal Kostic
 * 
 * (c) Michal Kostic, All rights reserved, 2012
 *       
 **/

package com.michalkostic.fragmentpager;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class SimpleAnimator {
	// poor man's ValueAnimator (available from API level 11)
	// normally would use TranslateAnimation but didn't want to spend time working around odd clipping
	private Timer timer;
	private Handler handler = null;
	
	protected long startTime;
	protected long duration;
	protected Interpolator interpolator;
	
	public void start(final OnAnimationListener listener) {
		interpolator = new DecelerateInterpolator();
		startTime = new Date().getTime();
		timer = new Timer();
		int animationFrequencyMillis = 40;
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				long currMillis = (new Date()).getTime();
				long currDuration = currMillis-startTime;
				float ratio = (currDuration*1f)/duration;
				if (currMillis-startTime<duration) {
					float progress = interpolator.getInterpolation(ratio);
					listener.onAnimation(progress);
				} else {
					// time out
					timer.cancel();
					listener.onFinish();
				}
			}
		}, 0, animationFrequencyMillis);
	}	
		
	public interface OnAnimationListener {
		public void onAnimation(float progress);
		public void onFinish();
	}
}
