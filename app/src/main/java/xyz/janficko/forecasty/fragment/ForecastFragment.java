package xyz.janficko.forecasty.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import xyz.janficko.forecasty.R;
import xyz.janficko.forecasty.task.FetchWeatherTask;

/**
 * Created by Jan on 8. 08. 2016.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> forecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
            weatherTask.execute("Maribor,si");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        weatherTask.execute("Maribor,si");

        forecastAdapter = new ArrayAdapter<String>(
                // The current context (this fragment's parent activity)
                getActivity(),
                // ID of the list item layout
                R.layout.list_item_forecast,
                // ID of textview to populate
                R.id.list_item_forecast_textview,
                // Forecast data
                weatherTask.getForecastArrayA());

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);

        return rootView;

    }

}
