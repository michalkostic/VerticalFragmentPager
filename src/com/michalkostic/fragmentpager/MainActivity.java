/**
 * MainActivity - main activity of the app 
 * 
 * Keeps track of fragments in the stack - this functionality could be extracted to custom component if needed to improve reuse.
 *
 * @author Michal Kostic 
 * 
 * (c) Michal Kostic, All rights reserved, 2012      
 **/

package com.michalkostic.fragmentpager;

import java.util.ArrayList;

import com.michalkostic.fragmentpager.R;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

/*
 * Main activity of the app 
 * Space for improvement:
 * - extract fragment pager into self contained component
 * - handling of orientation change
 * - style the ListHierarchyFragment so it looks prettier
 * - improve animation constants
 */
public class MainActivity extends FragmentActivity {
	private static final String TAG = "MainActivity";

	protected GestureDetector gestureDetector;
	protected RelativeLayout fragmentHolder;
	protected View fragmentCover;
	
	protected ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	protected FragmentManager fragmentManager = getSupportFragmentManager();
	
	// fragment bookkeeping methods
	public void addFragment(Fragment fragment) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.add(R.id.fragmentHolder, fragment);		
		transaction.commit();
		fragmentManager.executePendingTransactions();
		fragmentHolder.removeView(fragmentCover);
		fragmentHolder.addView(fragmentCover, fragmentHolder.getChildCount()-1);		

		fragments.add(fragment);		
		
	}
	
	public void removeLast() {
		if (fragments.size()>0) {			
			fragmentHolder.removeView(fragmentCover);				
			fragmentHolder.addView(fragmentCover, fragmentHolder.getChildCount()-2);
			Fragment fragment = fragments.remove(fragments.size()-1);
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.remove(fragment);		
			transaction.commit();				
			//fragmentManager.executePendingTransactions();
			
		}		
	}
	
	public void removeLastButRoot() {
		if (fragments.size()>1) {
			removeLast();
		}
	}
	
	public Fragment getTopFragment() {
		if (fragments.size()>0) {
			return fragments.get(fragments.size()-1);
		} else {
			return null;
		}
	}
	
	// layout params helpers
	public static int getTopMargin(View view) {
		RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams)view.getLayoutParams());
		return params.topMargin;
	}

	public static void setTopMargin(View view, int margin) {
		RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams)view.getLayoutParams());
		params.topMargin = margin;
		view.setLayoutParams(params);
	}

	
	// fragment UI methods
    public void removeFragmentAnimated() {
    	if (fragments.size()<=1) { return; }
    	final View fragmentView = getTopFragment().getView();
    	final int originalTopMargin = getTopMargin(fragmentView);
    	final int distanceToBottom = fragmentView.getHeight(); //-originalTopMargin;
    	SimpleAnimator animator = new SimpleAnimator();
    	animator.duration = 500;
    	animator.start(new SimpleAnimator.OnAnimationListener() {
			
			@Override
			public void onAnimation(final float progress) {
				handler.post(new Runnable() {					
					@Override
					public void run() {
						//Log.i(TAG, "removeAnimation:" + progress + " " + distanceToBottom + " " + (originalTopMargin+distanceToBottom*progress));
						setTopMargin(fragmentView, Math.round(originalTopMargin+distanceToBottom*progress));						
					}
				});
			}

			@Override
			public void onFinish() {
				handler.post(new Runnable() {					
					@Override
					public void run() {						
						removeLastButRoot();
					}
				});				
			}
		});
    }

    Handler handler = new Handler(); 
    
    public void returnToTopAnimated() {
    	if (fragments.size()<=1) { return; }
    	final View fragmentView = getTopFragment().getView();
    	final int distanceToTop = getTopMargin(fragmentView);
    	SimpleAnimator animator = new SimpleAnimator();
    	animator.duration = 300;
    	animator.start(new SimpleAnimator.OnAnimationListener() {			
			@Override
			public void onAnimation(final float progress) {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						//Log.i(TAG, progress + " " + (distanceToTop*(1-progress)));
						setTopMargin(fragmentView, Math.round(distanceToTop*(1-progress)));						
					}
				});
			}

			@Override
			public void onFinish() {}
		});
    }
    
    public void returnToTopAnimated_translateAnimation() {    	
    	final View fragmentView = getTopFragment().getView();
    	//RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams)fragmentView.getLayoutParams());    	
    	TranslateAnimation animation = new TranslateAnimation(0f, 0f, 0f, -getTopMargin(fragmentView));
    	animation.setDuration(500);  
    	animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				setTopMargin(fragmentView, 0);
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}			
		});
		fragmentView.startAnimation(animation);
    	
    	//animation.start();    	
    }

    
	// activity lifecycle methods
	
    protected int viewHeight = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        

        setContentView(R.layout.slider_activity_main);
        
        fragmentHolder = (RelativeLayout)findViewById(R.id.fragmentHolder);
        fragmentCover = findViewById(R.id.fragmentCover);
        
        LayoutedFragment rootFragment = ListHierarchyFragment.newInstance(new String[] {"Main"}, ListHierarchyFragment.getTestData());
        addFragment(rootFragment);

        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {				
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
					float distanceY) {
				// TODO: check if pixel density dependent				
				if (fragments.size()>1) {
					// move fragment appropriately
					RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams)getTopFragment().getView().getLayoutParams()); 
					params.topMargin = Math.max(params.topMargin + (-1)*(int)distanceY, 0); //Math.min(0, (int)(e2.getY()-e1.getY()));
					getTopFragment().getView().setLayoutParams(params);
				}
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				//Log.i(TAG, "fling velocity: " + velocityY);
				// TODO: check if pixel density dependent
				if (velocityY>300) {
					//removeLastButRoot();
					removeFragmentAnimated();
					return true;
				} else if (velocityY<0) {
					returnToTopAnimated();
					return true;
				}
				return false;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});
        
        
    }
    

    @Override
    public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG, "touch event:" + event.getX() + " " + event.getY());
		boolean gestureConsumed = gestureDetector.onTouchEvent(event);
		
		if (event.getAction()==MotionEvent.ACTION_UP && !gestureConsumed) {
			View fragmentView = getTopFragment().getView();
			int VIEW_RETURN_THRESHOLD = fragmentHolder.getHeight()/2;	// if below half of screen => hide 
			// touch up event
			if (fragmentView.getTop()<VIEW_RETURN_THRESHOLD) {
				returnToTopAnimated();
			} else {
				removeFragmentAnimated();
			}
		}

    	return super.onTouchEvent(event);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
 
    // only since API level 5 (Android 2.0) - if needed lower version compatibility - use onKeyDown
    @Override
    public void onBackPressed() {
    	// TODO: consider detecting double tap on "back" to exit on the last fragment
    	removeLastButRoot();
    	//super.onBackPressed();
    }
    
}
