package si.virag.bicikelj.station_map;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import si.virag.bicikelj.R;

public class StationMapActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null)
            return;

        StationMapFragment fragment = new StationMapFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList("stations", getIntent().getExtras().getParcelableArrayList("stations"));
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().add(R.id.map_container, fragment, "MapFragment").commit();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
