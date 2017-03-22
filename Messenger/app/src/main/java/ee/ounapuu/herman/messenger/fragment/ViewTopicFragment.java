package ee.ounapuu.herman.messenger.fragment;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import ee.ounapuu.herman.messenger.R;

/**
 * Created by toks on 3/19/17.
 */

public class ViewTopicFragment extends Fragment {
    String[] itemname ={
            "Safari",
            "Camera",
            "Global",
            "FireFox",
            "UC Browser",
            "Android Folder",
            "VLC Player",
            "Safari",
            "Camera",
            "Global",
            "FireFox",
            "UC Browser",
            "Android Folder",
            "VLC Player",
            "Safari",
            "Camera",
            "Global",
            "FireFox",
            "UC Browser",
            "Android Folder",
            "VLC Player",
            "Cold War"
    };

    public static ViewTopicFragment newInstance() {
        ViewTopicFragment fragment = new ViewTopicFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_topic, container, false);
        ListView list = (ListView) view.findViewById(R.id.customlist);

        list.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.custom_list, R.id.Itemname,itemname));
        return view;
    }
}
