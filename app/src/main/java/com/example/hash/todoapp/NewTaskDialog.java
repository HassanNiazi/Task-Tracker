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
    DatabaseReference mUserRef = mDatabase.child("user");

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_task_dialog,null);
        titleTB = (EditText) view.findViewById(R.id.TitleEditText);
        descrTB = (EditText) view.findViewById(R.id.DescEditText);
     //   final Firebase firebase =new Firebase("https://todo-2b5b4.firebaseio.com/");

        final DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();

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
                firebase.push().setValue(unitTask);

            }
        });

        Dialog dialog = builder.create();
        return dialog;
    }



}
