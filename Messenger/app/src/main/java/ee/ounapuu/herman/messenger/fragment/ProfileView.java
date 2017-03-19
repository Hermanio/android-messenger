package ee.ounapuu.herman.messenger.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ee.ounapuu.herman.messenger.MainActivity;
import ee.ounapuu.herman.messenger.R;

/**
 * Created by toks on 3/19/17.
 */

public class ProfileView extends Fragment {
    public static ProfileView newInstance() {
        ProfileView fragment = new ProfileView();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getActivity(), "ondestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getActivity(), "on destroy view", Toast.LENGTH_SHORT).show();
    }
}
