/**
 * LayoutedFragment - fragment that can fill itself from the layout resource.
 * 
 * This class is not really needed, just stayed here from previous experiments. Extended by ListHierarchyFragment. 
 * 
 * (c) Michal Kostic, All rights reserved, 2012
 *  
 * @author Michal Kostic
 *       
 **/

package com.michalkostic.fragmentpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LayoutedFragment extends Fragment {
	
	protected int layoutResource = -1;
	
//	public LayoutedFragment() {
//		super();
//	}
	
//	public LayoutedFragment(int layoutResource) {
//		this.layoutResource = layoutResource;
//	}
	
	public static LayoutedFragment newInstance(int layoutResource) {
		LayoutedFragment fragment = new LayoutedFragment();
		
		Bundle args = new Bundle();
	    args.putInt(ARG_LAYOUT_RESOURCE, layoutResource);
	    fragment.setArguments(args);
	    
	    return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
	}
	
	public static final String ARG_LAYOUT_RESOURCE = "layoutResource";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//return super.onCreateView(inflater, container, savedInstanceState);
		if (layoutResource<0) {
			layoutResource = getArguments().getInt(ARG_LAYOUT_RESOURCE, -1);
		}
		if (layoutResource>=0) {
			return inflater.inflate(layoutResource, container, false);
		} else {
			return null;
		}
	}
}
