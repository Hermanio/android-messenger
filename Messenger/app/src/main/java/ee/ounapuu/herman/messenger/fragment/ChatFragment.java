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

public class ChatFragment extends Fragment{

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
}
