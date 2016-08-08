package xyz.janficko.forecasty.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.janficko.forecasty.R;

/**
 * Created by Jan on 8. 08. 2016.
 */
public class PlaceholderFragment extends Fragment {

    public PlaceholderFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }
}
