package com.hp.app.twitterClient;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.twitterClient.R;
import com.codepath.apps.twitterClient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends Activity {
	
	private PullToRefreshListView lvTweets;
	public static final int SETTINGS_REQUEST = 123;
	private TweetsAdapter adapter;
	private ArrayList<Tweet> tweets = new ArrayList<Tweet>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		
		lvTweets = (PullToRefreshListView) findViewById(R.id.lv_tweets);
		adapter = new TweetsAdapter(this, tweets);
		lvTweets.setAdapter(adapter);
		
		
		makeTwitterCall(0);
		
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
		       	makeTwitterCall(0); 
			}
		});
		
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
	        @Override
	        public void onLoadMore(int page, int totalItemsCount) {
	                // Triggered only when new data needs to be appended to the list
	                // Add whatever code is needed to append new items to your AdapterView
	        	Tweet tweet = (Tweet) lvTweets.getItemAtPosition(totalItemsCount - 1);
	        	if(tweet != null){
	        		makeTwitterCall(tweet.getId()); 
	        	}
	                // or customLoadMoreDataFromApi(totalItemsCount); 
	        }
	    });
		
	}

	private void makeTwitterCall(long max_id) {
		TwitterClientApp.getRestClient().getHomeTimeLine(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONArray jsonTweets) {
				tweets = Tweet.fromJson(jsonTweets);
				
				Log.d("DEBUG", tweets.toString());
				
				adapter.addAll(tweets);				
				lvTweets.onRefreshComplete();
			}
			
			 public void onFailure(Throwable e) {
	                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
	         }
			 
		}, max_id);
	}
	
	public void onComposePress(MenuItem miCompose){
		Intent i = new Intent(this, ComposeActivity.class);
//		i.putExtra(FOO_KEY, "bar");
//		i.putExtra(GOO_KEY, "baz");
//		i.putExtra(SETTINGS_KEY, settings);
		startActivityForResult(i, SETTINGS_REQUEST);
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}

}
