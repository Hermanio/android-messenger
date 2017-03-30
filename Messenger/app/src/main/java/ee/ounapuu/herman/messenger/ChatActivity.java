package ee.ounapuu.herman.messenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.bassaer.chatmessageview.models.Message;
import com.github.bassaer.chatmessageview.models.User;
import com.github.bassaer.chatmessageview.views.ChatView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


public class ChatActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private ChatView mChatView;


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef;

    private StorageReference mStorageRef;


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private String chatTopic;
    private TextView chatTitle;
    private Bitmap myIcon;
    private Bitmap otherIcon;

    public ArrayList<DataSnapshot> messageIDList = new ArrayList<>();
    public ArrayList<DataSnapshot> messageList = new ArrayList<>();
    public ArrayList<DataSnapshot> usersList = new ArrayList<>();


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
            getMessages(chatTopic);

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
        mChatView.setOnClickOptionButtonListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                chooseImage(view);
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

        if (!mChatView.getInputText().equals("")) {
            Message message = new Message.Builder()
                    .setUser(me)
                    .setRightMessage(true)
                    .setMessageText(mChatView.getInputText())
                    .hideIcon(true)
                    .build();
            //Set to chat view

            //mChatView.send(message);
            sendMessageToDB(mChatView.getInputText());
            //Reset edit text
            mChatView.setInputText("");

            //buildMessage();
        } else {
            Toast.makeText(this, "Please enter a message!", Toast.LENGTH_SHORT).show();
        }

    }

    private void buildMessage(boolean isImage, String username, String timestamp, String content) {
        //create using db listener, if own message, ignore
        getImageForChatMember(username);
        final User otherUser;
        final Message message;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(Long.parseLong(timestamp)));


        StorageReference imageReference;

   /*
            always:
            setuser
            setcreated
            build

            if image
            setType picture
            setpicture onload then receive

            if text
            setMessageText

            if self
            setrightmessage true
            hideicon true

            if other
            setrightmessage false
            hideicon false
            * */

        final Message.Builder messageBuilder = new Message.Builder()
                .setCreatedAt(calendar);

        if (username.equals(mAuth.getCurrentUser().getEmail())) {
            otherUser = new User(0, username, myIcon);
            messageBuilder.setRightMessage(true);
            messageBuilder.hideIcon(true);
        } else {
            otherUser = new User(1, username, otherIcon);
            messageBuilder.setRightMessage(false);
            messageBuilder.hideIcon(false);
        }

        messageBuilder.setUser(otherUser);

        if (isImage) {
            messageBuilder.setType(Message.Type.PICTURE);
            imageReference = mStorageRef.child(content);
            Glide.with(this).using(new FirebaseImageLoader()).
                    load(imageReference).asBitmap().into(new SimpleTarget<Bitmap>(500, 500) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                    messageBuilder.setPicture(resource);
                    mChatView.receive(messageBuilder.build());

                }
            });
        } else {
            messageBuilder.setMessageText(content);
            mChatView.receive(messageBuilder.build());
        }


    }

    private void sendImageMessage() {
        openPhotoSelect();
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
                        mAuth.getCurrentUser().getEmail());
        String messageID;
        DatabaseReference dbRefMessages = database.getReference("/messages/" + chatTopic);
        DatabaseReference newRef = dbRefMessages.push();
        //Toast.makeText(getContext(), messageID, Toast.LENGTH_SHORT).show();
        newRef.setValue(completeMessage);

        //DatabaseReference dbRefTopic = database.getReference("/topics/" + chatTopic + "/");
        //dbRefTopic.child("messages").push().setValue(messageID);
    }

    private void sendImageMessageToDB(String imageRef) {
        //send message to messages, get id, then send message id to chat messages
        ee.ounapuu.herman.messenger.CustomObjects.Message completeMessage =
                new ee.ounapuu.herman.messenger.CustomObjects.Message(
                        true,
                        "",
                        imageRef,
                        System.currentTimeMillis(),
                        mAuth.getCurrentUser().getEmail());
        String messageID;
        DatabaseReference dbRefMessages = database.getReference("/messages/" + chatTopic);
        DatabaseReference newRef = dbRefMessages.push();
        //Toast.makeText(getContext(), messageID, Toast.LENGTH_SHORT).show();
        newRef.setValue(completeMessage);

    }

    private void getMessages(String chatName) {

        Query getAllPosts = database.getReference("/messages/" + chatName);

        getAllPosts.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                addMessageIDtoList(dataSnapshot);
                boolean isImage = Boolean.parseBoolean(dataSnapshot.child("isImage").getValue().toString());
                String content;
                String username = dataSnapshot.child("posterName").getValue().toString();
                String timestamp = dataSnapshot.child("timestamp").getValue().toString();
                if (isImage) {
                    content = dataSnapshot.child("imgRef").getValue().toString();
                } else {
                    content = dataSnapshot.child("textContent").getValue().toString();

                }
                Log.d("messages", dataSnapshot.child("textContent").getValue().toString());
                Log.d("messages", dataSnapshot.child("posterName").getValue().toString());
                Log.d("messages", dataSnapshot.child("timestamp").getValue().toString());

                buildMessage(isImage, username, timestamp, content);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addMessageIDtoList(DataSnapshot messageIDSnapshot) {
        this.messageIDList.add(messageIDSnapshot);
    }

    public void addMessageToList(DataSnapshot messageSnapshot) {
        this.messageList.add(messageSnapshot);
    }

    public void addUserToList(DataSnapshot userSnapshot) {
        this.usersList.add(userSnapshot);
    }


    //testing below
    /* Pastebin code example stuff below */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openPhotoSelect();
            } else {
                Toast.makeText(this, "No permissions to read storage!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openPhotoSelect() {
        Intent choosePhotoIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        choosePhotoIntent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(choosePhotoIntent, "Select file"),
                SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                uploadImageToStorage(image);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String imagePath = getRealPathFromUri(selectedImageUri);
                Bitmap image = BitmapFactory.decodeFile(imagePath);
                uploadImageToStorage(image);

            }
        }
    }

    private String getRealPathFromUri(Uri contentUri) {
        String selectedImagePath = null;
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = this.getContentResolver().query(contentUri, projection, null, null, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            selectedImagePath = cursor.getString(column_index);
        }
        cursor.close();
        return selectedImagePath;
    }


    private void uploadImageToStorage(Bitmap image) {
        //todo replace with topic name
        //Toast.makeText(getContext(), userId, Toast.LENGTH_SHORT).show();
        final String imageName = UUID.randomUUID().toString() + ".jpg";
        StorageReference uploadImageReference = mStorageRef.child(imageName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = uploadImageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                //Toast.makeText(getContext(), "upload failure", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                //profileImage.setImageBitmap(uploadReadyImage);

                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Toast.makeText(getContext(), downloadUrl.toString(), Toast.LENGTH_SHORT).show();
                sendImageMessageToDB(imageName);

            }
        });

    }

    private void retrieveProfilePicture() {
        // StorageReference uploadImageReference = mStorageRef.child(userEmail + ".jpg");
        // Glide.with(this).using(new FirebaseImageLoader()).
        //       load(uploadImageReference).
        //       signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
        //       .into(profileImage);
    }

    public void chooseImage(View view) {
        final String[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (items[which]) {
                    case "Take Photo":
                        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePhotoIntent, REQUEST_CAMERA);
                        break;
                    case "Choose from Library":
                        if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_STORAGE_PERMISSION);
                            dialog.dismiss();
                        } else {
                            openPhotoSelect();
                        }
                        break;
                    default:
                        dialog.dismiss();
                }
            }
        });
        builder.show();
    }

}
