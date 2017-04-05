package ee.ounapuu.herman.messenger.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ee.ounapuu.herman.messenger.ChatActivity;
import ee.ounapuu.herman.messenger.CustomObjects.Topic;
import ee.ounapuu.herman.messenger.LoginActivity;
import ee.ounapuu.herman.messenger.MainActivity;
import ee.ounapuu.herman.messenger.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by toks on 3/19/17.
 */

public class CreateTopicFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;

    private Bitmap uploadReadyImage;

    private ImageView newTopicImage;
    private EditText newTopicName;
    private Button addTopicButton;
    private BottomNavigationView bottomNavigationView;

    public static CreateTopicFragment newInstance() {
        CreateTopicFragment fragment = new CreateTopicFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();
        //dbRef.child("topics").child("thisistopic").child("somevaluesinlistorsth").setValue("dunno some stuff i guess");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_topic, container, false);

        newTopicImage = (ImageView) view.findViewById(R.id.newTopicImageView);
        newTopicName = (EditText) view.findViewById(R.id.new_topic_name);
        addTopicButton = (Button) view.findViewById(R.id.button_add_new_topic);
        addTopicButton.setOnClickListener(this);

        //bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.navigation);

        Button cameraImagePickerButton = (Button) view.findViewById(R.id.chooseImageButton);
        cameraImagePickerButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void chooseImageFromCamera(View view) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePhotoIntent, REQUEST_CAMERA);
        //Toast.makeText(getContext(), "Choosing img from camera", Toast.LENGTH_SHORT).show();
    }

    public void chooseImageFromGallery(View view) {
        //Toast.makeText(getContext(), "Choose img from gallery", Toast.LENGTH_SHORT).show();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            openPhotoSelect();
        } else {
            openPhotoSelect();
        }


    }


    /* Pastebin code example stuff below */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openPhotoSelect();
            } else {
                Toast.makeText(getContext(), "No permissions to read storage!", Toast.LENGTH_SHORT).show();
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
                uploadReadyImage = image;
                newTopicImage.setImageBitmap(image);
                // uploadImageToStorage(image);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String imagePath = getRealPathFromUri(selectedImageUri);
                Bitmap image = BitmapFactory.decodeFile(imagePath);
                uploadReadyImage = image;
                newTopicImage.setImageBitmap(image);
                // uploadImageToStorage(image);
            }
        }
    }

    private String getRealPathFromUri(Uri contentUri) {
        String selectedImagePath = null;
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, projection, null, null, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            selectedImagePath = cursor.getString(column_index);
        }
        cursor.close();
        return selectedImagePath;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chooseImageButton:
                chooseImage(view);
                break;
            case R.id.button_add_new_topic:
                createNewTopic(uploadReadyImage, newTopicName.getText().toString());
                break;
        }
    }

    public void chooseImage(View view) {
        final String[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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

    private void createNewTopic(final Bitmap image, final String topicName) {
        dbRef.child("/topics/" + topicName + "/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getContext(), "exists", Toast.LENGTH_SHORT).show();
                    final String[] items = {"Join thread", "Cancel"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Topic already exists, do you want to join it?");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (items[which]) {
                                case "Join thread":
                                    Intent i = new Intent(getActivity(), ChatActivity.class);
                                    i.putExtra("topicName", topicName);
                                    startActivity(i);
                                    clearViewData();
                                    break;
                                case "Cancel":
                                    dialog.dismiss();
                                    break;
                                default:
                                    dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getContext(), "does not exist", Toast.LENGTH_SHORT).show();
                    if (image == null && topicName.equals("")) {
                        Toast.makeText(getContext(), "Please choose a topic name and image!", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (image == null) {
                        Toast.makeText(getContext(), "Please choose an image!", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (topicName.equals("")) {
                        Toast.makeText(getContext(), "Please choose a topic name!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //todo replace with topic name
                    //Toast.makeText(getContext(), "New topic create start", Toast.LENGTH_SHORT).show();
                    StorageReference uploadImageReference = mStorageRef.child(topicName + ".jpg");

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = uploadImageReference.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(getContext(), "upload failure", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(getContext(), "Upload done!", Toast.LENGTH_SHORT).show();

                            List<String> participants = new ArrayList<String>();
                            List<String> messages = new ArrayList<String>();
                            participants.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            Topic topic = new Topic(topicName, participants, messages, topicName + ".jpg", false);

                            dbRef.child("topics").child(topicName).setValue(topic);
                            changeTopicPicture(topicName);

                            DatabaseReference topicLastActivityTimeRef = database.getReference("/topics/" + topicName + "/lastActivity/");
                            topicLastActivityTimeRef.setValue(Calendar.getInstance().getTimeInMillis());

                            Intent i = new Intent(getActivity(), ChatActivity.class);
                            i.putExtra("topicName", topicName);
                            startActivity(i);
                            clearViewData();

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void changeTopicPicture(String topicName) {
        StorageReference uploadImageReference = mStorageRef.child(topicName + ".jpg");
        Glide.with(getContext()).using(new FirebaseImageLoader()).
                load(uploadImageReference).
                signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(newTopicImage);
    }

    private void clearViewData() {
        getFragmentManager()
                .beginTransaction()
                .detach(this)
                .attach(this)
                .commitAllowingStateLoss();
    }
}
