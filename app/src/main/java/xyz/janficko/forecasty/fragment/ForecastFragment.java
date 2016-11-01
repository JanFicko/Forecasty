package xyz.janficko.forecasty.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xyz.janficko.forecasty.R;
import xyz.janficko.forecasty.WeatherDataParser;
import xyz.janficko.forecasty.activity.DetailActivity;

/**
 * Created by Jan on 8. 08. 2016.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> forecastAdapter;

    public ForecastFragment() {}

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
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String location = settings.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
            updateWeather(location);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        forecastAdapter = new ArrayAdapter<String>(
                // The current context (this fragment's parent activity)
                getActivity(),
                // ID of the list item layout
                R.layout.list_item_forecast,
                // ID of textview to populate
                R.id.list_item_forecast_textview,
                // Forecast data
                new ArrayList<String>());

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, forecastAdapter.getItem(i));
                startActivity(intent);
            }
        });


        return rootView;

    }

    /**
     * Temporary method. Is called when user clicks refresh option.
     *
     * @param location
     */
    private void updateWeather(String location) {
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute(location);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = settings.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        updateWeather(location);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private static final String APPID = "7f5abb431c205025e73ea0ceb1651e0d";
        private static final int numDays = 7;

        /**
         * Get data from outside source.
         *
         * @param params
         * @return
         */
        @Override
        protected String[] doInBackground(String... params) {

            String[] result = {};
            String city = params[0];

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                final String OPENWEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String KEY_PARAM = "APPID";

                Uri.Builder builder = Uri.parse(OPENWEATHER_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, city)
                        .appendQueryParameter(FORMAT_PARAM, "json")
                        .appendQueryParameter(UNITS_PARAM, "metric")
                        .appendQueryParameter(DAYS_PARAM, "7")
                        .appendQueryParameter(KEY_PARAM, APPID);

                String baseUrl = builder.build().toString();
                URL url = new URL(baseUrl);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                WeatherDataParser weatherDataPraser = new WeatherDataParser(getActivity());

                try {
                    result = weatherDataPraser.getWeatherDataFromJson(forecastJsonStr, numDays);
                } catch (JSONException e) {
                    Log.e("FetchWeatherTask", e.getMessage(), e);
                    e.printStackTrace();
                }


            } catch (IOException e) {
                Log.e("FetchWeatherTask", "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("FetchWeatherTask", "Error closing stream", e);
                    }
                }
            }

            return result;
        }

        /**
         * Do something with data after it has been fetched.
         *
         * @param strings
         */
        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                forecastAdapter.clear();
                for(String dayForecastStr : strings) {
                    forecastAdapter.add(dayForecastStr);
                    //Log.v("ADAPTER", dayForecastStr);
                }
            }
        }
    }

    /**
     * Transform string array to ArrayList.
     *
     * @param result
     */
    private void formatToArrayList(String[] result){
        ArrayList<String> forecastArrayList = new ArrayList<String>();
        if (result != null) {
            for(String dayForecastStr : result) {
                forecastArrayList.add(dayForecastStr);
            }
        }
    }
}
