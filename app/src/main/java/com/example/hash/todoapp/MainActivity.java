package com.example.hash.todoapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
//import com.google.firebase.quickstart.database.models.Post;
//import com.google.firebase.quickstart.database.models.User;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    RecyclerView recyclerView;
ListView listView;

    String userName = "Anonymous";
    String userId = "unknown@unknown.com";
    String imageUrl = "@android:drawable/star_big_on";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        userId = intent.getStringExtra("userId");
        imageUrl = intent.getStringExtra("imageUrl");

        Firebase.setAndroidContext(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewTaskDialog newTaskDialog =new NewTaskDialog();
                newTaskDialog.show(getFragmentManager(),"NewTask");
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
//        listView = (ListView) findViewById(R.id.taskList);

        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(header);

        TextView userNameNavHeaderTV = (TextView) header.findViewById(R.id.userNameNavHeader);
        userNameNavHeaderTV.setText(userName);

        TextView userIdNavHeaderTV = (TextView) header.findViewById(R.id.userEmailNavHeader);
        userIdNavHeaderTV.setText(userId);

        Drawable drawable = LoadImageFromWebOperations(imageUrl);

        ImageView userImageNavHeader = (ImageView) header.findViewById(R.id.userImageNavHeader);
        userImageNavHeader.setImageDrawable(drawable);



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerAdapter<UnitTask,TaskViewHolder> adapter =
                new FirebaseRecyclerAdapter<UnitTask, TaskViewHolder>(UnitTask.class,R.layout.task,TaskViewHolder.class,firebase) {
                    @Override
                    protected void populateViewHolder(TaskViewHolder viewHolder, UnitTask model, int position) {
                        viewHolder.title.setText(model.getTitle());
                        viewHolder.description.setText(model.getDescription());
                        viewHolder.checkBox.setChecked(model.isCompleted());
                    }
                };

//        final List<UnitTask> unitTasks = new LinkedList<>();
//
//        final ArrayAdapter<UnitTask> adapter = new ArrayAdapter<UnitTask>(this,R.layout.task,unitTasks){
//            @Override
//            public View getView(int position, View view, ViewGroup parent) {
//
//                if (view==null)
//                {
//                    view = getLayoutInflater().inflate(R.layout.task,parent);
//                }
//
//                UnitTask unitTask = unitTasks.get(position);
//
//                ((TextView)view.findViewById(R.id.Description)).setText(unitTask.getDescription());
//                ((TextView)view.findViewById(R.id.title)).setText(unitTask.getTitle());
//                ((CheckBox)view.findViewById(R.id.checkboxTaskRow)).setChecked(unitTask.isCompleted());
//
//                return view;
//            }
//        };
//
//
//
//
        recyclerView.setAdapter(adapter);
////        listView.setAdapter(adapter);
//
//        firebase.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                UnitTask unitTask = dataSnapshot.getValue(UnitTask.class);
//                unitTasks.add(unitTask);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        firebase.keepSynced(true);
    }
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_out) {
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder{

        TextView title,description;
        CheckBox checkBox;


        public TaskViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            description= (TextView) v.findViewById(R.id.Description);
             checkBox = (CheckBox) v.findViewById(R.id.checkboxTaskRow);
        }
    }


    @Override
    public void onClick(View v) {


        switch (v.getId())
        {
             }

    }


}
