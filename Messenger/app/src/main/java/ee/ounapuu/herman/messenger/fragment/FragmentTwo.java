package ee.ounapuu.herman.messenger.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ee.ounapuu.herman.messenger.R;

/**
 * Created by toks on 3/19/17.
 */

public class FragmentTwo extends Fragment {
    public static FragmentTwo newInstance() {
        FragmentTwo fragment = new FragmentTwo();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_2, container, false);
    }
}
