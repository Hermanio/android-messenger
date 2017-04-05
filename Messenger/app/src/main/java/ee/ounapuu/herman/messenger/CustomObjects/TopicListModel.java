package ee.ounapuu.herman.messenger.CustomObjects;

/**
 * Created by toks on 5.04.17.
 */

public class TopicListModel {

    private String title;
    private long lastActivity;
    private long participantCount;
    public TopicListModel(String title, long lastActivity, long participantCount) {
        this.title = title;
        this.lastActivity = lastActivity;
        this.participantCount = participantCount;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public long getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(long participantCount) {
        this.participantCount = participantCount;
    }
}
