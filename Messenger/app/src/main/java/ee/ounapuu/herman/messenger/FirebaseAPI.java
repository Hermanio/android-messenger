package ee.ounapuu.herman.messenger;

/**
 * Created by toks on 3/23/17.
 */

public class FirebaseAPI {

    private static FirebaseAPI firebaseAPI = new FirebaseAPI();

    private FirebaseAPI() {

    }

    public static FirebaseAPI getInstance() {
        return firebaseAPI;
    }

    protected static void demoMethod() {
        System.out.println("demoMethod for singleton");
    }


}
