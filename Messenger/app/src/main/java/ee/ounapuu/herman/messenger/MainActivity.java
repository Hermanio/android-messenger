package ee.ounapuu.herman.messenger;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ee.ounapuu.herman.messenger.fragment.ChatFragment;
import ee.ounapuu.herman.messenger.fragment.ProfileViewFragment;
import ee.ounapuu.herman.messenger.fragment.ViewTopicFragment;
import ee.ounapuu.herman.messenger.fragment.CreateTopicFragment;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(MainActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();
                    //all is OK
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        setNavigationBarListener();
        openLastFragment();
    }

    private void setNavigationBarListener() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int selectedMenuOption = item.getItemId();
                Fragment fragment = getFragmentManager().findFragmentById(selectedMenuOption);

                if (fragment == null) {
                    switch (selectedMenuOption) {
                        case R.id.navigation_chat:
                            fragment = new ChatFragment();
                            break;
                        case R.id.navigation_create_topic:
                            fragment = new CreateTopicFragment();
                            break;
                        case R.id.navigation_view_topic:
                            fragment = new ViewTopicFragment();
                            break;
                        case R.id.navigation_profile:
                            fragment = new ProfileViewFragment();
                            break;
                    }
                }

                getFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
                setLastOpenedFragment(item.getItemId());
                return true;
            }
        });
    }

    private void openLastFragment() {
        Fragment fragment = getFragmentManager().findFragmentById(getLastOpenedFragment());
        if (fragment == null) {
            switch (getLastOpenedFragment()) {
                case R.id.navigation_chat:
                    fragment = new ChatFragment();
                    bottomNavigationView.getMenu().getItem(1).setChecked(true);
                    break;
                case R.id.navigation_create_topic:
                    fragment = new CreateTopicFragment();
                    bottomNavigationView.getMenu().getItem(2).setChecked(true);
                    break;
                case R.id.navigation_view_topic:
                    fragment = new ViewTopicFragment();
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                    break;
                case R.id.navigation_profile:
                    fragment = new ProfileViewFragment();
                    bottomNavigationView.getMenu().getItem(3).setChecked(true);
                    break;
                default:
                    fragment = new ViewTopicFragment();
                    break;
            }
            getFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        }
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signOut() {
        Toast.makeText(this, "signout in activity", Toast.LENGTH_SHORT).show();
        mAuth.signOut();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        signOut();
        setLastOpenedFragment(-1);
        return true;
    }
}
