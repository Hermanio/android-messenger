package ee.ounapuu.herman.messenger.CustomObjects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Topic {


    public String title;
    public List<String> participants;
    public List<Message> messages;
    public String imgRef;


    public Topic() {}

    public Topic(String title, List<String> participants, List<Message> messages, String imgRef) {
        this.title = title;
        this.participants = participants;
        this.messages = messages;
        this.imgRef = imgRef;
    }

}


