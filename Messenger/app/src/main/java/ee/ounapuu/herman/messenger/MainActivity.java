package ee.ounapuu.herman.messenger;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import ee.ounapuu.herman.messenger.fragment.ChatFragment;
import ee.ounapuu.herman.messenger.fragment.ProfileView;
import ee.ounapuu.herman.messenger.fragment.ViewTopicActivity;
import ee.ounapuu.herman.messenger.fragment.CreateTopicFragment;

import static java.security.AccessController.getContext;


public class MainActivity extends Activity {

    private Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.navigation_chat:
                                selectedFragment = ChatFragment.newInstance();
                                break;
                            case R.id.navigation_create_topic:
                                selectedFragment = CreateTopicFragment.newInstance();
                                break;
                            case R.id.navigation_view_topic:
                                selectedFragment = ViewTopicActivity.newInstance();
                                break;
                            case R.id.navigation_profile:
                                selectedFragment = ProfileView.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ViewTopicActivity.newInstance());
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }


}
