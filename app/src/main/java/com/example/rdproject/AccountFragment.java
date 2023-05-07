package com.example.rdproject;

import static android.app.Activity.RESULT_OK;

import android.Manifest;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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
        String[] options = {"Select photo from gallery", "Select photo from camera", "Edit description", "Change username", "Change password", "Log Out"};
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
                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    final String currentDescription = "";
                    input.setText(currentDescription);
                    input.setSelection(currentDescription.length());
                    final DatabaseReference descriptionRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("description");
                    descriptionRef.setValue(input.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DatabaseReference ref = descriptionRef.getRef();
                                    // Do something with the DatabaseReference object
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle the error
                                }
                            });


                    AlertDialog.Builder descriptionDialog = new AlertDialog.Builder(getActivity());
                    descriptionDialog.setTitle("Edit Description");
                    descriptionDialog.setView(input);
                    descriptionDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newDescription = input.getText().toString().trim();
                            descriptionRef.setValue(newDescription);
                            // Update the profile description on the page here.
                        }
                    });
                    descriptionDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog descriptionAlert = descriptionDialog.create();

                    input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            descriptionAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!s.toString().trim().isEmpty());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    descriptionAlert.show();
                } else if (which == 3) {
                    pd.setMessage("Changing username");
                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    final String currentUsername = "";
                    input.setText(currentUsername);
                    input.setSelection(currentUsername.length());
                    final DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("username");
                    usernameRef.setValue(input.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DatabaseReference ref = usernameRef.getRef();
                                    // Do something with the DatabaseReference object
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle the error
                                }
                            });


                    AlertDialog.Builder usernameDialog = new AlertDialog.Builder(getActivity());
                    usernameDialog.setTitle("Edit Username");
                    usernameDialog.setView(input);
                    usernameDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newUsername = input.getText().toString().trim();
                            usernameRef.setValue(newUsername);
                            // Update the profile username on the page here.
                        }
                    });
                    usernameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog usernameAlert = usernameDialog.create();

                    input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            usernameAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!s.toString().trim().isEmpty());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    usernameAlert.show();

                } else if (which == 4){
                    pd.setMessage("Changing password");
                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    // Get the current password of the user and set it as the initial text of the EditText
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String currentPassword = ""; // Replace this with the user's current password
                    input.setText(currentPassword);
                    input.setSelection(currentPassword.length());

                    AlertDialog.Builder passwordDialog = new AlertDialog.Builder(getActivity());
                    passwordDialog.setTitle("Edit Password");
                    passwordDialog.setView(input);
                    passwordDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newPassword = input.getText().toString().trim();

                            // Make sure the user is authenticated before updating their password
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), "Password has been successfully updated", Toast.LENGTH_SHORT).show();
                                                    final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                                                    userRef.child("password").setValue(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // The password has been updated in the database
                                                        }
                                                    });

                                                } else {
                                                    Toast.makeText(getActivity(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    });
                    passwordDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog passwordAlert = passwordDialog.create();

                    input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            passwordAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!s.toString().trim().isEmpty());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    passwordAlert.show();

                }else {
                    pd.setTitle("Logging out");
                    startActivity(new Intent(getActivity(), LogIn.class));
                }
            }
        });
        builder.create().show();
    }




}
