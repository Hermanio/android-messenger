package ee.ounapuu.herman.messenger;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import ee.ounapuu.herman.messenger.fragment.ChatFragment;
import ee.ounapuu.herman.messenger.fragment.ProfileViewFragment;
import ee.ounapuu.herman.messenger.fragment.ViewTopicFragment;
import ee.ounapuu.herman.messenger.fragment.CreateTopicFragment;


public class MainActivity extends AppCompatActivity {

    private Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNavigationBarListener();
        openLastFragment();


    }

    private void setNavigationBarListener() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
                        Fragment fragment = getFragmentManager().findFragmentById(R.id.navigation_view_topic);
                        if (fragment == null) {
                            fragment = new ViewTopicFragment();
                            getFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment).commit();
                        }
                        selectedFragment = ViewTopicFragment.newInstance();
                        break;
                    case R.id.navigation_profile:
                        selectedFragment = ProfileViewFragment.newInstance();
                        break;
                }

                setLastOpenedFragment(item.getItemId());

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                //transaction.remove(selectedFragment);
                //transaction.add(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });
    }

    private void openLastFragment() {
        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch (getLastOpenedFragment()) {
            case R.id.navigation_view_topic:
                Fragment fragment = getFragmentManager().findFragmentById(R.id.navigation_view_topic);
                if (fragment == null) {
                    fragment = new ViewTopicFragment();
                    getFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment).commit();
                }
                //transaction.replace(R.id.frame_layout, ViewTopicFragment.newInstance());
                break;
            case R.id.navigation_create_topic:
                transaction.replace(R.id.frame_layout, CreateTopicFragment.newInstance());

                break;
            case R.id.navigation_profile:
                transaction.replace(R.id.frame_layout, ProfileViewFragment.newInstance());

                break;
            case R.id.navigation_chat:
                transaction.replace(R.id.frame_layout, ChatFragment.newInstance());
                break;
            default:
                transaction.replace(R.id.frame_layout, ViewTopicFragment.newInstance());

                break;

        }

        //transaction.replace(R.id.frame_layout, ViewTopicFragment.newInstance());
        transaction.commit();


        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }

    private void setLastOpenedFragment(int fragmentId) {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("lastOpenedFragment", fragmentId);
        editor.apply();
    }

    private int getLastOpenedFragment() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        return prefs.getInt("lastOpenedFragment", R.id.navigation_view_topic);
    }


}
