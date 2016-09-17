package com.example.hash.todoapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by hash on 9/8/16.
 */
public class NewTaskDialog extends DialogFragment {

    public String taskTitle;
    public String taskDescription;
    EditText titleTB,descrTB;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_task_dialog,null);
        titleTB = (EditText) view.findViewById(R.id.TitleEditText);
        descrTB = (EditText) view.findViewById(R.id.DescEditText);

        FirebaseAuth mAuth;

        mAuth = FirebaseAuth.getInstance();

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

        final DatabaseReference userRef = rootRef.child("users/" + firebaseUser.getUid());

        builder.setView(view);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
           // Return Nothing
            }
        });

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//              TODO Send Data to Firebase .... For Future we will move the update Firebase DB code to main activity if necessary
//                Not using becasue we are not sending data back to main activity we will update firebase Db from here
//                intercom.respond(titleTB.getText().toString(),descrTB.getText().toString(),false);
                UnitTask unitTask = new UnitTask(titleTB.getText().toString(),descrTB.getText().toString(),false);
                userRef.push().setValue(unitTask);

            }
        });

        Dialog dialog = builder.create();
        return dialog;
    }



}
