package ee.ounapuu.herman.messenger.CustomObjects;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by toks on 3/23/17.
 */

@IgnoreExtraProperties
public class Message {


    public boolean isImage;
    public String textContent;
    public String imgRef;
    public long timestamp;
    public String posterName;

    public Message() {
    }

    public Message(Boolean isImage, String textContent, String imgRef, long timestamp, String posterName) {
        this.isImage = isImage;
        this.textContent = textContent;
        this.imgRef = imgRef;
        this.timestamp = timestamp;
        this.posterName = posterName;
    }
}

