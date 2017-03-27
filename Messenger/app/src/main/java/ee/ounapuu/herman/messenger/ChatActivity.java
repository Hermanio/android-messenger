package ee.ounapuu.herman.messenger;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.bassaer.chatmessageview.models.Message;
import com.github.bassaer.chatmessageview.models.User;
import com.github.bassaer.chatmessageview.views.ChatView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by toks on 27.03.17.
 */

public class ChatActivity extends AppCompatActivity {


    private ChatView mChatView;


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef;

    private StorageReference mStorageRef;


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private String chatTopic;
    private TextView chatTitle;
    private Bitmap myIcon;
    private Bitmap otherIcon;


    private String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mStorageRef = FirebaseStorage.getInstance().getReference();


        String newString;
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            newString = "Chat not available";
        } else {
            newString = extras.getString("topicName");
            chatTopic = newString;
        }
        chatTitle = (TextView) findViewById(R.id.chat_topic_textview);
        chatTitle.setText(newString);

        setupChatView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatView.setOnClickSendButtonListener(null);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("topicName", chatTopic);
    }


    private void setupChatView() {


        if (mChatView == null) {
            mChatView = (ChatView) findViewById(R.id.chat_view);
        }

        mChatView.setRightBubbleColor(ContextCompat.getColor(this, R.color.green500));
        mChatView.setLeftBubbleColor(Color.WHITE);
        mChatView.setBackgroundColor(ContextCompat.getColor(this, R.color.blueGray200));
        mChatView.setSendButtonColor(ContextCompat.getColor(this, R.color.blueGray500));
        mChatView.setSendIcon(R.drawable.ic_action_send);
        mChatView.setRightMessageTextColor(Color.WHITE);
        mChatView.setLeftMessageTextColor(Color.BLACK);
        mChatView.setUsernameTextColor(Color.WHITE);
        mChatView.setSendTimeTextColor(Color.WHITE);
        mChatView.setDateSeparatorColor(Color.WHITE);
        mChatView.setInputTextHint("new message...");
        mChatView.setMessageMarginTop(5);
        mChatView.setMessageMarginBottom(5);


        //Click Send Button
        mChatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new message
                sendMessage();

            }

        });


    }

    private void sendMessage() {
        if (chatTopic == null) {
            Toast.makeText(this, "Messaging is unavailable.", Toast.LENGTH_SHORT).show();
            return;
        }

        //User id
        //todo: see if this needs replacing
        int myId = 0;

        //User icon
        getImageForUser(mAuth.getCurrentUser().getUid());

        //todo: replace with user display name
        String myName = mAuth.getCurrentUser().getEmail();

        // int yourId = 1;
        // Bitmap yourIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);
        //String yourName = "Emily";

        final User me = new User(myId, myName, myIcon);
        //final User you = new User(yourId, yourName, yourIcon);

        if (mChatView.getInputText() != null) {
            Message message = new Message.Builder()
                    .setUser(me)
                    .setRightMessage(true)
                    .setMessageText(mChatView.getInputText())
                    .hideIcon(true)
                    .build();
            //Set to chat view
            mChatView.send(message);
            sendMessageToDB(mChatView.getInputText());
            //Reset edit text
            mChatView.setInputText("");

            receiveMessage();
        } else {
            Toast.makeText(this, "Please enter a message!", Toast.LENGTH_SHORT).show();
        }

    }

    private void receiveMessage() {
        //create using db listener, if own message, ignore
        getImageForChatMember("vodka");
        final User otherUser = new User(1, "vodka", otherIcon);

        Message message = new Message.Builder()
                .setUser(otherUser)
                .setRightMessage(false)
                .setMessageText("test message")
                .hideIcon(false)
                .build();
        mChatView.receive(message);
    }

    private void getImageForUser(String username) {
        String imagePath = username + ".jpg";
        StorageReference storageReference = mStorageRef.child((imagePath));

        Glide.with(this).using(new FirebaseImageLoader()).
                load(storageReference).asBitmap().into(new SimpleTarget<Bitmap>(100, 100) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                myIcon = resource; // Possibly runOnUiThread()
            }
        });
    }

    private void getImageForChatMember(String username) {
        //String imagePath = username + ".jpg";
        //todo: replace with something else when user info GET operations are ready, this is for testing only
        String imagePath = "tfIBzXWgD4XnyG3y2IjD08SgHeq1.jpg";
        StorageReference storageReference = mStorageRef.child((imagePath));

        Glide.with(this).using(new FirebaseImageLoader()).
                load(storageReference).asBitmap().into(new SimpleTarget<Bitmap>(100, 100) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                otherIcon = resource; // Possibly runOnUiThread()
            }
        });
    }

    private void sendMessageToDB(String message) {
        //send message to messages, get id, then send message id to chat messages
        ee.ounapuu.herman.messenger.CustomObjects.Message completeMessage =
                new ee.ounapuu.herman.messenger.CustomObjects.Message(
                        false,
                        message,
                        "",
                        System.currentTimeMillis(),
                        mAuth.getCurrentUser().getUid());
        String messageID;
        DatabaseReference dbRefMessages = database.getReference("messages");
        DatabaseReference newRef = dbRefMessages.push();
        messageID = newRef.getKey();
        //Toast.makeText(getContext(), messageID, Toast.LENGTH_SHORT).show();
        newRef.setValue(completeMessage);

        DatabaseReference dbRefTopic = database.getReference("/topics/" + chatTopic + "/");
        dbRefTopic.child("messages").push().setValue(messageID);
    }

}
