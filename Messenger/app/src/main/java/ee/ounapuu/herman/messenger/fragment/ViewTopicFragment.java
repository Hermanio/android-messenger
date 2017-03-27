package ee.ounapuu.herman.messenger.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ee.ounapuu.herman.messenger.ChatActivity;
import ee.ounapuu.herman.messenger.MainActivity;
import ee.ounapuu.herman.messenger.R;
import ee.ounapuu.herman.messenger.customListAdapter.CustomListAdapter;

/**
 * Created by toks on 3/19/17.
 */

public class ViewTopicFragment extends Fragment {

    ListView list;
    String[] itemname;

    private DatabaseReference mDatabase;
    private Query getAllTopicsQuery;
    private ValueEventListener dataUpdateListener;

    public static ViewTopicFragment newInstance() {
        ViewTopicFragment fragment = new ViewTopicFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_topic, container, false);
        getAllTopicsQuery = mDatabase.child("topics").orderByKey();
        setDataUpdateListener(view);
        getAllTopicsQuery.addValueEventListener(dataUpdateListener);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getAllTopicsQuery.removeEventListener(dataUpdateListener);
        //Toast.makeText(getContext(), "on destroy view", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setDataUpdateListener(final View view) {
        dataUpdateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("length", "Length is " + dataSnapshot.getChildrenCount());
                int iterator = 0;
                itemname = new String[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot topicSnapShot : dataSnapshot.getChildren()) {
                    Log.d("loop", topicSnapShot.getKey());
                    itemname[iterator] = topicSnapShot.getKey();
                    iterator++;
                }

                if (itemname != null) {
                    Log.d("length", "Length is for array " + itemname.length);

                }


                CustomListAdapter adapter = new CustomListAdapter(getActivity(), itemname);

                list = (ListView) view.findViewById(R.id.customlist);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String selectedItem = itemname[+position];
                       // Toast.makeText(getContext(), selectedItem, Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(getActivity(), ChatActivity.class);
                        i.putExtra("topicName", selectedItem);
                        startActivity(i);

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("loop", "loadPost:onCancelled", databaseError.toException());

            }
        };
    }
}
