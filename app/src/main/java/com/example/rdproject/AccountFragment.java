package com.example.rdproject;

import static android.app.Activity.RESULT_OK;

import android.Manifest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int CAMERA_REQUEST_CODE = 100, STORAGE_REQUEST_CODE = 200;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference().child("images");

    private ProgressDialog pd;
    private StorageReference storageProfilePicsref;

    private ImageView user_profile_photo, test;
    private TextView username, email, description;
    private FloatingActionButton nice_button;


    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        storageProfilePicsref = FirebaseStorage.getInstance().getReference().child("Profile Pic");

        username = view.findViewById((R.id.username));
        email = view.findViewById(R.id.email);
        description = view.findViewById(R.id.description);
        user_profile_photo = view.findViewById(R.id.user_profile);
        nice_button = view.findViewById(R.id.nice_button);

        pd = new ProgressDialog(getActivity());

        Query checkemail = reference.orderByChild("email").equalTo(user.getEmail());
        checkemail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String username_text = "" + dataSnapshot.child("username").getValue();
                    String description_text = "" + dataSnapshot.child("description").getValue();
                    String image_text = "" + dataSnapshot.child("image").getValue();

                    username.setText(username_text);
                    email.setText(user.getEmail());
                    description.setText(description_text);

                    Glide.with(getActivity())
                            .load(image_text)
                            .apply(RequestOptions.circleCropTransform())
                            .into(user_profile_photo);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nice_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
        return view;
    }

    private void showEditProfileDialog() {
        String[] options = {"Select photo from gallery", "Select photo from camera", "Edit description", "Change username", "Change password"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    pd.setMessage("Updating profile photo");
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, STORAGE_REQUEST_CODE);
                } else if (which == 1) {
                    pd.setMessage("Updating profile photo");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                } else if (which == 2) {
                    pd.setMessage("Updating description");
                } else if (which == 3) {
                    pd.setMessage("Changing username");
                } else {
                    pd.setMessage("Changing password");
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == STORAGE_REQUEST_CODE) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    Glide.with(this).load(bitmap).apply(RequestOptions.circleCropTransform()).into(user_profile_photo);
                    // Get a reference to the user's node in the database
                    // Get a reference to the user's node in the database
                    DatabaseReference userRef = reference.child(user.getUid());

                    // Upload the photo to Firebase Storage and get a reference to the uploaded file
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference photoRef = storageRef.child("profile_photos/" + user.getUid() + ".jpg");
                    UploadTask uploadTask = photoRef.putFile(imageUri);

                    // Set up a listener to get the download URL of the uploaded image after the upload is complete
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL of the uploaded image from the task snapshot
                            photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Update the "image" field of the user's node in the database with the download URL
                                    userRef.child("image").setValue(uri.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors
                            Toast.makeText(getActivity(), "Failed to upload profile photo", Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                user_profile_photo.setImageBitmap(photo);

                // Get a reference to the Storage node where the photo will be uploaded
                StorageReference storageRef = storage.getReference().child("profile_photos/" + user.getUid() + ".jpg");

                // Upload the photo to Firebase Storage
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataBytes = baos.toByteArray();
                UploadTask uploadTask = storageRef.putBytes(dataBytes);

                // Add a listener to track the upload progress
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a reference to the user's node in the database
                        DatabaseReference userRef = reference.child(user.getUid());

                        // Get the download URL of the uploaded image from the Storage reference
                        storageRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Update the "image" field of the user's node in the database with the download URL
                                        userRef.child("image").setValue(uri.toString());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle any errors
                                        Toast.makeText(getActivity(), "Failed to upload profile photo", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }

        }
    }

}
