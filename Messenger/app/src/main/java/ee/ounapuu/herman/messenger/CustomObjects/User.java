package ee.ounapuu.herman.messenger.CustomObjects;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by toks on 3/23/17.
 */

@IgnoreExtraProperties
public class User {


    public String uid;
    public String name;
    public String email;


    public User() {
    }

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = "for god sakes replace me";
        this.email = email;
    }

}

