/**
 * ListHierarchyFragment - Simple fragment with title and list of data
 * 
 * @author Michal Kostic
 * 
 * (c) Michal Kostic, All rights reserved, 2012
 *       
 **/

package com.michalkostic.fragmentpager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.michalkostic.fragmentpager.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ListHierarchyFragment extends LayoutedFragment {

	protected String[] path;
	protected ArrayList<Map<String, Object>> data;
	
	public ListHierarchyFragment() {
		// TODO Auto-generated constructor stub
		super();
		this.layoutResource = R.layout.list_hierarchy;
	}
	
	public static final String ARG_PATH = "path";
	public static final String ARG_DATA = "data";
	
	public static ListHierarchyFragment newInstance(String[] path, ArrayList<Map<String, Object>> data) {
		ListHierarchyFragment fragment = new ListHierarchyFragment();
		Bundle arguments = fragment.getArguments();
		if (arguments==null) {
			arguments = new Bundle();
		}
		arguments.putStringArray(ARG_PATH, path);
		
//		arguments.putParcelableArrayList(ARG_DATA, data);
		// TODO: not efficient, improve in the future
		arguments.putSerializable(ARG_DATA, data);
		fragment.setArguments(arguments);
		
		return fragment;
	}
	
	public static ArrayList<Map<String, Object>> getTestData() {
		return getTestData(new String[] {});
	}
	
	public static ArrayList<Map<String, Object>> getTestData(String[] path) {
		// expects path of size at least 1 (should always be such)
		ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		int testDataCount = 10;
		String pathSoFar = "";
		if (path.length>1) {
			pathSoFar = path[path.length-1] + ".";
		}

		for (int i=0; i<testDataCount; i++) {
			HashMap<String, Object> currData = new HashMap<String, Object>();
			currData.put(DATA_CAPTION, pathSoFar + i);
			data.add(currData);			
		}
		return data;		
	}
	
	public static final String DATA_CAPTION = "caption";
	
	protected void initFromArgs() {
		if (getArguments()!=null) {
			path = (String[])getArguments().get(ARG_PATH);
			data = (ArrayList<Map<String, Object>>)getArguments().getSerializable(ARG_DATA);
			
		} else {
			path = new String[] {};
			data = new ArrayList<Map<String,Object>>();
		}
	}
	
	// joins members of array into string - would have used Apache Commons for this, but wanted to keep minimal dependencies
	public static String join(String[] arr, String separator) {
		return join(arr, separator, 0);
	}

	public static String join(String[] arr, String separator, int indexFrom) {
		StringBuilder sb = new StringBuilder();
		for (int i=indexFrom; i<arr.length; i++) {
			sb.append(arr[i]);
			if (i<arr.length-1) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		
		initFromArgs();
		
		View view = inflater.inflate(layoutResource, container, false);
		
		// init header
		String headingText = join(path, " > ");
		TextView txtHeading = (TextView)view.findViewById(R.id.txtHeading);
		txtHeading.setText(headingText);
		
		// init list view (data + on click event)
		ListView listView = (ListView)view.findViewById(R.id.list);				
		
		ListAdapter listAdapter = new SimpleAdapter(this.getActivity(), data, android.R.layout.simple_list_item_1, new String[] {DATA_CAPTION}, new int[] {android.R.id.text1});
		listView.setAdapter(listAdapter);
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// open new fragment
				Object item = ((SimpleAdapter)parent.getAdapter()).getItem(position);
				Map<String, Object> typedItem = (Map<String, Object>)item;
				String caption = (String)typedItem.get(DATA_CAPTION);	
				// fairly dirty way to get modifiable List from array 
				List<String> pathList = new ArrayList<String>(Arrays.asList(path)); 
				pathList.add(caption);
				String[] newPath = new String[] {};
				newPath = pathList.toArray(newPath);

				// TODO: if this was not quick and dirty prototype, I could use the event handler as per http://developer.android.com/guide/components/fragments.html#EventCallbacks
				MainActivity activity = ((MainActivity)getActivity()); 
				activity.addFragment(ListHierarchyFragment.newInstance(newPath, getTestData(newPath)));
			}
		});
		
		return view;
	}
	
}
