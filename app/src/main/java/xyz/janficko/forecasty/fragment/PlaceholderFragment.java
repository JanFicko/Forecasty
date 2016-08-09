package xyz.janficko.forecasty.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import xyz.janficko.forecasty.R;

/**
 * Created by Jan on 8. 08. 2016.
 */
public class PlaceholderFragment extends Fragment {

    public PlaceholderFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> vreme = new ArrayList<String>();
        vreme.add("Danes - Sončno - 25/17");
        vreme.add("Jutri - Sončno - 25/17");
        vreme.add("Sreda - Sončno - 25/17");
        vreme.add("Četrtek - Sončno - 25/17");
        vreme.add("Petek - Sončno - 25/17");

        ArrayAdapter<String> forecastAdapter = new ArrayAdapter<String>(
                // The current context (this fragment's parent activity)
                getActivity(),
                // ID of the list item layout
                R.layout.list_item_forecast,
                // ID of textview to populate
                R.id.list_item_forecast_textview,
                // Forecast data
                vreme);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);

        return rootView;
    }
}
