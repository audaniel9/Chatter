package com.daniel.chat.chatter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {
    private TextView profileFullName, profileUsername, profileEmail;
    private Button profileDelete;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        profileFullName = (TextView) getView().findViewById(R.id.profile_full_name);
        profileUsername = (TextView) getView().findViewById(R.id.profile_username);
        profileEmail = (TextView) getView().findViewById(R.id.profile_email);
        profileDelete = (Button) getView().findViewById(R.id.profile_delete);

        profileUsername.setText(user.getDisplayName());
        profileEmail.setText(user.getEmail());

        //FirebaseDatabase.getInstance().getReference().push().setValue

        profileDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDelete = new AlertDialog.Builder(getContext());
                alertDelete.setTitle("Delete Account");
                alertDelete.setMessage("This account will be deleted permanently. Are you sure you want to continue?");
                alertDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(user != null)
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    startActivity(new Intent(getActivity(),LoginActivity.class));
                                    getActivity().finish();
                                    Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_SHORT).show();
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
}