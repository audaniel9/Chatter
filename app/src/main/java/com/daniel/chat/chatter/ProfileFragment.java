package com.daniel.chat.chatter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private TextView profileUsername, profileEmail;
    private EditText profileFullName;
    private Button profileFullNameEdit, profileDelete;
    private ImageView profilePic;
    private MenuItem menuRefresh, menuSearch;
    private FirebaseAuth.AuthStateListener authListener;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Map<String, Object> updateValues = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menuRefresh = menu.findItem(R.id.menuRefresh);
        menuSearch = menu.findItem(R.id.menuSearch);

        menuRefresh.setVisible(false);
        menuSearch.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        profileFullName = (EditText) getView().findViewById(R.id.profile_full_name);
        profileUsername = (TextView) getView().findViewById(R.id.profile_username);
        profileEmail = (TextView) getView().findViewById(R.id.profile_email);
        profilePic = (ImageView) getView().findViewById(R.id.profile_pic);
        profileDelete = (Button) getView().findViewById(R.id.profile_delete);
        profileFullNameEdit = (Button) getView().findViewById(R.id.profile_full_name_edit);

        profileEditListeners();

        profileUsername.setText(user.getDisplayName());
        profileEmail.setText(user.getEmail());
        profileFullName.setText(profileFullName.getText().toString());

        profileDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDelete = new AlertDialog.Builder(getContext());
                alertDelete.setTitle("Delete Account");
                alertDelete.setMessage("This account will be deleted permanently. Are you sure you want to continue?");
                alertDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    authListener = new FirebaseAuth.AuthStateListener() {
                                        @Override
                                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                            startActivity(new Intent(getActivity(), LoginActivity.class));
                                            getActivity().finish();
                                            Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_SHORT).show();
                                            FirebaseDatabase.getInstance().getReference("Users").child(user.getDisplayName()).removeValue();
                                        }
                                    };
                                }
                                else {
                                    Toast.makeText(getActivity(), "Cannot delete", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                alertDelete.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDelete.show();
            }
        });
    }

    public void profileEditListeners() {
        profileFullNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileFullName.setEnabled(true);
                profileFullName.setFocusableInTouchMode(true);
                profileFullName.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(profileFullName, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        profileFullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    profileFullName.setEnabled(false);
                    profileFullName.setFocusableInTouchMode(false);
                    FirebaseDatabase.getInstance().getReference("Users").orderByChild("fullName").equalTo(user.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                    updateValues.put(childSnapshot.getKey()+"/fullName", profileFullName.toString());
                                }

                                FirebaseDatabase.getInstance().getReference("Users").updateChildren(updateValues);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    //FirebaseDatabase.getInstance().getReference("Users").child(user.getDisplayName()).child("fullName").setValue(new Users(profileFullName.getText().toString()));
                }
            }
        });
    }
}