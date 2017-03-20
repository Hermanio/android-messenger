package ee.ounapuu.herman.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

/**
 * Created by toks on 3/19/17.
 */

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    public void login(View view) {

        Toast.makeText(this, "Add actual login", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void startRegistration(View view) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
        //finish();
    }
}
