package si.virag.bicikel;

import si.virag.bicikel.data.Station;
import si.virag.bicikel.data.StationInfo;
import si.virag.bicikel.map.MapActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainActivity extends FragmentActivity implements LoaderCallbacks<StationInfo>
{
	private static final int INFO_LOADER_ID = 1;
	
	private static final int MENU_REFRESH = 1;
	private static final int MENU_ABOUT = 2;
	
	private ViewFlipper viewFlipper;
	private ListView stationList;
	private TextView loadingText;
	private ProgressBar throbber;
	
	private StationInfo stationInfo;
	private Location currentLocation;
	
	private GPSManager gpsManager;
	private Handler gpsLocationHandler;
	
	private boolean loadInProgress;
	private boolean waitingForLocation;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        viewFlipper = (ViewFlipper) findViewById(R.id.main_flipper);
        stationList = (ListView) findViewById(R.id.station_list);
        loadingText = (TextView) findViewById(R.id.txt_loading);
        throbber = (ProgressBar) findViewById(R.id.loading_progress);
        
        // Set view flipper animations
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        
        // Setup delayed GPS location handler
        gpsManager = new GPSManager();
        gpsLocationHandler = new Handler()
        {
			@Override
			public void handleMessage(Message msg)
			{
				if (msg.what == GPSManager.GPS_LOCATION_OK && waitingForLocation)
				{
					sortDataByLocation();
				}
				
				gpsManager.cancelSearch();
			}
        };
        
        gpsManager.findCurrentLocation(this, gpsLocationHandler);        
        loadingText.setText(getString(R.string.loading));
        throbber.setVisibility(View.VISIBLE);
        
        loadInProgress = true;
        waitingForLocation = true;
        
        getSupportLoaderManager().initLoader(INFO_LOADER_ID, null, this);
        
    }
    
	@Override
	public Loader<StationInfo> onCreateLoader(int id, Bundle args)
	{
		JSONInformationDataLoader infoLoader = new JSONInformationDataLoader(this);
		return infoLoader;
	}

	@Override
	public void onLoadFinished(Loader<StationInfo> loader, StationInfo result)
	{
		loadInProgress = false;
		
		stationInfo = result;
		
		// Check for error
		if (result == null)
		{
			throbber.setVisibility(View.GONE);
			loadingText.setText(getString(R.string.connection_error));
			loadingText.setGravity(Gravity.CENTER_HORIZONTAL);
			return;
		}
		
		currentLocation = gpsManager.getCurrentLocation();
		
		if (currentLocation != null)
		{
			stationInfo.calculateDistances(currentLocation);
			waitingForLocation = false;
		}
		
		StationListAdapter adapter = new StationListAdapter(this, R.layout.station_list_item, stationInfo.getStations());
		stationList.setAdapter(adapter);
		viewFlipper.showNext();
		
		stationList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Station station = stationInfo.getStations().get(position);
				
				Intent newActivity = new Intent(MainActivity.this, MapActivity.class);
				newActivity.putExtra("lng", station.getLocation().getLongitude());
				newActivity.putExtra("lat", station.getLocation().getLatitude());
				
				startActivity(newActivity);
			}
		});
	}
	
	@Override
	public void onLoaderReset(Loader<StationInfo> loader)
	{
		// Nothing TBD
	}
    
	private void refreshData()
	{
		viewFlipper.setDisplayedChild(1);
		waitingForLocation = true;
		loadInProgress = true;
        gpsManager.findCurrentLocation(this, gpsLocationHandler);        
        loadingText.setText(getString(R.string.loading));
        throbber.setVisibility(View.VISIBLE);
        
        getSupportLoaderManager().initLoader(INFO_LOADER_ID, null, this).forceLoad();
	}
	
	
	private void sortDataByLocation()
	{
		// We might not yet have valid data
		if (stationInfo == null)
			return;
		
		Location currentLocation = gpsManager.getCurrentLocation();
		
		if (currentLocation != null)
		{
			waitingForLocation = false;
			stationInfo.calculateDistances(currentLocation);
			stationList.setAdapter(new StationListAdapter(this, R.layout.station_list_item, stationInfo.getStations()));
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case MENU_ABOUT:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getString(R.string.app_name) + " " + getString(R.string.app_ver));
				builder.setMessage(getString(R.string.app_about));
				
				AlertDialog alert = builder.create();
				alert.show();
				break;
				
			case MENU_REFRESH:
				if (!loadInProgress)
				{
					this.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							refreshData();
						}
					});
				}
				break;
		}
		
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, MENU_REFRESH, 0, getString(R.string.menu_refresh)).setIcon(R.drawable.refresh);
		menu.add(0, MENU_ABOUT, 1, getString(R.string.menu_about)).setIcon(R.drawable.info);
		return super.onCreateOptionsMenu(menu);
	}
    
}