package ee.ounapuu.herman.messenger.fragment;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ee.ounapuu.herman.messenger.R;
import ee.ounapuu.herman.messenger.customListAdapter.CustomListAdapter;

/**
 * Created by toks on 3/19/17.
 */

public class ViewTopicFragment extends Fragment {
    ListView list;
    String[] itemname ={
            "Safari",
            "Camera",
            "Safari",
            "Camera",
            "Safari",
            "Camera",
            "Safari",
            "Safari",
            "Camera",
            "Safari",
            "Camera",
            "Safari",
            "Camera",
            "Safari",
            "Safari",
            "Camera",
            "Safari",
            "Camera",
            "Safari",
            "Camera",
            "Safari",
            "Camera"
    };

    Integer[] imgid={
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic1,
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic1,
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic1,
            R.drawable.pic2
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
        CustomListAdapter adapter=new CustomListAdapter(getActivity(), itemname, imgid);
        list=(ListView)view.findViewById(R.id.customlist);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String Slecteditem= itemname[+position];
                Toast.makeText(getContext(), Slecteditem, Toast.LENGTH_SHORT).show();

            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getContext(), "on destroy view", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
