package ee.ounapuu.herman.messenger.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

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

        Button cameraImagePickerButton = (Button) view.findViewById(R.id.chooseImageFromCameraButton);
        cameraImagePickerButton.setOnClickListener(this);

        Button galleryImagePickerButton = (Button) view.findViewById(R.id.chooseImageFromGalleryButton);
        galleryImagePickerButton.setOnClickListener(this);


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
        Toast.makeText(getContext(), "Choosing img from camera", Toast.LENGTH_SHORT).show();
    }

    public void chooseImageFromGallery(View view) {
        Toast.makeText(getContext(), "Choose img from gallery", Toast.LENGTH_SHORT).show();

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
            case R.id.chooseImageFromCameraButton:
                chooseImageFromCamera(view);
                break;
            case R.id.chooseImageFromGalleryButton:
                chooseImageFromGallery(view);
                break;
            case R.id.button_add_new_topic:
                //todo: create new topic here
                //first upload image
                //then save info to DB
                createNewTopic(uploadReadyImage, newTopicName.getText().toString());
                break;
        }
    }

    private void createNewTopic(Bitmap image, String topicName) {
        //todo replace with topic name
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
                //Toast.makeText(getContext(), downloadUrl.toString(), Toast.LENGTH_SHORT).show();
                //createNewTopic(newTopicName.getText().toString());
                //todo: push data to DB, if successful then go to that new topic


            }
        });

    }


}
