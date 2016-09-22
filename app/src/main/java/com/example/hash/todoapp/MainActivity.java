package com.example.hash.todoapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

//import com.google.firebase.quickstart.database.models.Post;
//import com.google.firebase.quickstart.database.models.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    RecyclerView recyclerView;
    ListView listView;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {

//        FirebaseRecyclerAdapter firebaseRecyclerAdapter = (FirebaseRecyclerAdapter) recyclerView.getAdapter();
//
//        firebaseRecyclerAdapter.notifyDataSetChanged();

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth = FirebaseAuth.getInstance();
                firebaseUser = mAuth.getCurrentUser();
                NewTaskDialog newTaskDialog = new NewTaskDialog();
                newTaskDialog.show(getFragmentManager(), "NewTask");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.taskList);

        try {


            View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
            navigationView.addHeaderView(header);

            TextView userNameNavHeaderTV = (TextView) header.findViewById(R.id.userNameNavHeader);
            userNameNavHeaderTV.setText(firebaseUser.getDisplayName());

            TextView userIdNavHeaderTV = (TextView) header.findViewById(R.id.userEmailNavHeader);
            userIdNavHeaderTV.setText(firebaseUser.getEmail());
//        Drawable drawable = LoadImageFromWebOperations(firebaseUser.getPhotoUrl());

            ImageView userImageNavHeader = (ImageView) header.findViewById(R.id.userImageNavHeader);
            Uri personPhoto = firebaseUser.getPhotoUrl();
            if (personPhoto != null) {
                // Download photo and set to image
                Context context = userImageNavHeader.getContext();
                Picasso.with(context).load(personPhoto).into(userImageNavHeader);
            }

        } catch (Exception e) {

            Toast.makeText(MainActivity.this, "Unable to load user data", Toast.LENGTH_SHORT).show();

        }

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference userRef = rootRef.child("users/" + firebaseUser.getUid());

        final FirebaseRecyclerAdapter<UnitTask, TaskViewHolder> adapter =
                new FirebaseRecyclerAdapter<UnitTask, TaskViewHolder>(UnitTask.class, R.layout.task, TaskViewHolder.class, userRef) {
                    @Override
                    protected void populateViewHolder(final TaskViewHolder viewHolder, UnitTask model, int position) {


                        final DatabaseReference taskRef = getRef(position);

                        mAuth = FirebaseAuth.getInstance();
                        firebaseUser = mAuth.getCurrentUser();
                        viewHolder.checkBox.setChecked(model.isCompleted());

                        if (model.isCompleted()) {

                            SpannableString content = new SpannableString(model.title);
                            content.setSpan(new StrikethroughSpan(), 0, content.length(), 0);
                            viewHolder.title.setText(content);
                            content = new SpannableString(model.description);
                            content.setSpan(new StrikethroughSpan(), 0, content.length(), 0);
                            viewHolder.description.setText(content);
                        } else {
                            viewHolder.title.setText(model.getTitle());
                            viewHolder.description.setText(model.getDescription());
                        }
                        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Map<String, Object> stringBooleanMap = new HashMap<String, Object>();
                                stringBooleanMap.put("completed", viewHolder.checkBox.isChecked());
                                userRef.child(taskRef.getKey()).updateChildren(stringBooleanMap);

                            }
                        });
                        viewHolder.deleteTaskButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                userRef.child(taskRef.getKey()).removeValue();
                            }
                        });


                    }


                };


        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.refreshDrawableState();

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        // Code Snippet Added Still Needs to be understood !!! Maybe it
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_out) {
            mAuth.signOut();
            Intent myIntent = new Intent(MainActivity.this, SignIn.class);
            MainActivity.this.startActivity(myIntent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

        }

    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView title, description;
        CheckBox checkBox;
        Button deleteTaskButton;
        public TaskViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            description = (TextView) v.findViewById(R.id.Description);
            checkBox = (CheckBox) v.findViewById(R.id.checkboxTaskRow);
            deleteTaskButton = (Button) v.findViewById(R.id.deleteRow);

        }

    }


}
