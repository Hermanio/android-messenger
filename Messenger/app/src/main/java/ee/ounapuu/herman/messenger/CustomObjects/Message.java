package ee.ounapuu.herman.messenger.CustomObjects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by toks on 3/23/17.
 */

@IgnoreExtraProperties
public class Message {


    public boolean isImage;
    public String textContent;
    public String imgRef;
    public String timestamp;
    public String posterUID;

    public Message() {
    }

    public Message(Boolean isImage, String textContent, String imgRef, String timestamp, String posterUID) {
        this.isImage = isImage;
        this.textContent = textContent;
        this.imgRef = imgRef;
        this.timestamp = timestamp;
        this.posterUID = posterUID;
    }
}

