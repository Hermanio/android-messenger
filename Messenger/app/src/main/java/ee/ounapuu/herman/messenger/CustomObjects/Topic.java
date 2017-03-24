package ee.ounapuu.herman.messenger.CustomObjects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Topic {


    public String title;
    public List<String> participants;
    public List<String> messageIds;
    public String imgRef;


    public Topic() {}

    public Topic(String title, List<String> participants, List<String> messageIds, String imgRef) {
        this.title = title;
        this.participants = participants;
        this.messageIds = messageIds;
        this.imgRef = imgRef;
    }

}


