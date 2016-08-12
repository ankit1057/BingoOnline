package com.sid.bingotry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> arrayAdapter;
    private Button btn_add_room, btn_search_rooms;
    private EditText et_add_a_room;
    private DatabaseReference root;
    private ProgressBar pBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lv = (ListView) findViewById(R.id.lv);
        btn_search_rooms = (Button) findViewById(R.id.btn_search_rooms);
        btn_add_room = (Button) findViewById(R.id.btn_add_room);
        et_add_a_room = (EditText) findViewById(R.id.et_add_room);
        pBar = (ProgressBar) findViewById(R.id.pBar);

        root = FirebaseDatabase.getInstance().getReference().getRoot();

        arrayAdapter = new ArrayAdapter<>(this , android.R.layout.simple_list_item_1);
        lv.setAdapter(arrayAdapter);
        init();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView t = (TextView) view;
                Intent i = new Intent(MainActivity.this , Game.class);
                i.putExtra("room_name" , t.getText().toString());
                startActivity(i);
            }
        });





    }

    private void init() {
        btn_search_rooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pBar.setVisibility(View.VISIBLE);
                Log.d("clicked" , "search rooms clicked");
                root.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("value added" , "called");
                        Set<String> set = new HashSet<>();
                        Iterator i = dataSnapshot.getChildren().iterator();
                        while (i.hasNext()){
                            set.add(((DataSnapshot) i.next()).getKey());
                            Log.d("added" , "added");
                        }
                        arrayAdapter.clear();
                        arrayAdapter.addAll(set);
                        arrayAdapter.notifyDataSetChanged();
                        pBar.setVisibility(View.GONE);
                        et_add_a_room.setVisibility(View.VISIBLE);
                        btn_add_room.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                            }
        });

    }

    public void btn_add_room_clicked(View v) {
        String temp = et_add_a_room.getText().toString();
        if (!temp.isEmpty()) {
            root.child(temp).setValue("");
            et_add_a_room.setText("");
        }
    }
}
