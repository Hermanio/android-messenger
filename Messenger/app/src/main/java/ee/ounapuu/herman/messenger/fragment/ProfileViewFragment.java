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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import ee.ounapuu.herman.messenger.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by toks on 3/19/17.
 */

public class ProfileViewFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private StorageReference mStorageRef;
    private FirebaseUser user;


    private String userName;
    private String userEmail;
    private Uri photoUrl;
    private String userId;

    private TextView profileName;
    private ImageView profileImage;

    private Bitmap uploadReadyImage;

    public static ProfileViewFragment newInstance() {
        ProfileViewFragment fragment = new ProfileViewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        loadUserData();
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveProfilePicture();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        profileName = (TextView) view.findViewById(R.id.profileUsernameText);
        profileName.setText(userEmail);

        //loadingSpinner = (ProgressBar) view.findViewById(R.id.loadingSpinner);
        //loadingSpinner.setVisibility(View.GONE);


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
                uploadImageToStorage(image);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String imagePath = getRealPathFromUri(selectedImageUri);
                Bitmap image = BitmapFactory.decodeFile(imagePath);
                uploadReadyImage = image;

                uploadImageToStorage(image);

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
        }
    }

    private void uploadImageToStorage(Bitmap image) {
        //todo replace with topic name
        //Toast.makeText(getContext(), userId, Toast.LENGTH_SHORT).show();
        StorageReference uploadImageReference = mStorageRef.child(userId + ".jpg");

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
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                profileImage.setImageBitmap(uploadReadyImage);

                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Toast.makeText(getContext(), downloadUrl.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void retrieveProfilePicture() {
        StorageReference uploadImageReference = mStorageRef.child(userId + ".jpg");
        Glide.with(getContext()).using(new FirebaseImageLoader()).
                load(uploadImageReference).
                signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(profileImage);
    }


    private void loadUserData() {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Name, email address, and profile photo Url
            userName = user.getDisplayName();
            userEmail = user.getEmail();
            photoUrl = user.getPhotoUrl();
            userId = user.getUid();
        }
    }


}
