package xyz.janficko.forecasty.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import xyz.janficko.forecasty.WeatherDataParser;

/**
 * Created by Jan on 10. 08. 2016.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

    private Context context; //field in your AsyncTask
    private static final String APPID = "7f5abb431c205025e73ea0ceb1651e0d";
    private static final int numDays = 7;

    private ArrayList<String> forecastArrayA = new ArrayList<String>();

    public FetchWeatherTask() {}

    public FetchWeatherTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

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

            WeatherDataParser weatherDataPraser = new WeatherDataParser();

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

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        if (strings != null) {
            for(String dayForecastStr : strings) {
                forecastArrayA.add(dayForecastStr);
            }
        }

    }

    public ArrayList<String> getForecastArrayA() {
        return forecastArrayA;
    }

    public void setForecastArrayA(ArrayList<String> forecastArrayA) {
        this.forecastArrayA = forecastArrayA;
    }
}