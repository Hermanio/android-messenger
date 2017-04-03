package ee.ounapuu.herman.messenger.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ee.ounapuu.herman.messenger.ChatActivity;
import ee.ounapuu.herman.messenger.MainActivity;
import ee.ounapuu.herman.messenger.R;
import ee.ounapuu.herman.messenger.customListAdapter.CustomListAdapter;

/**
 * Created by toks on 3/19/17.
 */

public class ViewTopicFragment extends Fragment implements View.OnClickListener {

    ListView list;
    CustomListAdapter adapter;
    ArrayList<String> itemname;

    private DatabaseReference mDatabase;
    private Query getAllTopicsQuery;
    private ValueEventListener dataUpdateListener;

    private String displayMode = "FEATURED";

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
        Button featured_button = (Button) view.findViewById(R.id.button_featured_choice);
        featured_button.setOnClickListener(this);
        Button usergen_button = (Button) view.findViewById(R.id.button_usergen_choice);
        usergen_button.setOnClickListener(this);
        SearchView searchView = (SearchView) view.findViewById(R.id.searchview_topic);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
               // Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
                //todo: filtering should be here, simple regex match probably?
                filterTopicsByString(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getContext(), "edit", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        getAllTopicsQuery = mDatabase.child("topics").orderByKey();
        setDataUpdateListener(view);
        setTopicsMode("featured");
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

    private void setDataUpdateListener(final View view){
        dataUpdateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemname = new ArrayList<>();
                Log.d("length", "Length is " + dataSnapshot.getChildrenCount());
                if (displayMode.equals("FEATURED")) {
                    for (DataSnapshot topicSnapShot : dataSnapshot.getChildren()) {
                        if (Boolean.parseBoolean(topicSnapShot.child("isStaticTopic").getValue().toString())) {
                                itemname.add(topicSnapShot.getKey());
                        }
                    }
                } else {
                    for (DataSnapshot topicSnapShot : dataSnapshot.getChildren()) {
                        if (!Boolean.parseBoolean(topicSnapShot.child("isStaticTopic").getValue().toString())) {
                                itemname.add(topicSnapShot.getKey());
                        }
                    }
                }


                if (itemname != null) {
                    Log.d("length", "Length is for array " + itemname.size());
                }

                if (itemname.size() > 0) {
                    adapter = new CustomListAdapter(getActivity(), itemname);

                    list = (ListView) view.findViewById(R.id.customlist);
                    list.setAdapter(adapter);

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            String selectedItem = itemname.get(position);
                            // Toast.makeText(getContext(), selectedItem, Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(getActivity(), ChatActivity.class);
                            i.putExtra("topicName", selectedItem);
                            startActivity(i);

                        }
                    });
                } else {
                    Log.d("adapter", "no items sent to adapter");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("loop", "loadPost:onCancelled", databaseError.toException());

            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_featured_choice:
                setTopicsMode("featured");
                break;
            case R.id.button_usergen_choice:
                setTopicsMode("usergen");

                break;
        }
    }

    private void setTopicsMode(String topicsMode) {
        getAllTopicsQuery.removeEventListener(dataUpdateListener);
        if (topicsMode.equals("featured")) {
            this.displayMode = "FEATURED";
            Toast.makeText(getContext(), "featured", Toast.LENGTH_SHORT).show();
            getAllTopicsQuery = mDatabase.child("topics").orderByValue();
            getAllTopicsQuery.addValueEventListener(dataUpdateListener);
        } else {
            this.displayMode = "USERGEN";

            Toast.makeText(getContext(), "usergen", Toast.LENGTH_SHORT).show();
            getAllTopicsQuery = mDatabase.child("topics").orderByValue();
            getAllTopicsQuery.addValueEventListener(dataUpdateListener);
        }
    }

    private void filterTopicsByString(String searchQuery) {
        final ArrayList<String> matchingTopicNames = new ArrayList<>();
        for (String item : itemname) {
            if (item.contains(searchQuery)) {
                matchingTopicNames.add(item);
            }
        }

        if (matchingTopicNames.size() > 0 ) {
            adapter = new CustomListAdapter(getActivity(), matchingTopicNames);

            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String selectedItem = matchingTopicNames.get(position);

                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("topicName", selectedItem);
                    startActivity(i);
                }
            });

            adapter.notifyDataSetChanged();
        }

    }

}
