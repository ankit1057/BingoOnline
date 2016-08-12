package com.sid.bingotry;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Game extends AppCompatActivity {

    private DatabaseReference root;
    private int count = 0 , move_count = 0, line_count = 0;
    private boolean mFirst = false , ready = false , lost = false;
    private TextView tv_cmd;
    private int btn_count = 1;
    private Typeface myTypeFace;
    private ValueEventListener vel;
    private Map<Integer, TextView> map;
    private Map<String , String> map2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tv_cmd = (TextView) findViewById(R.id.tv_cmd);
        String room_name = getIntent().getExtras().getString("room_name");
        if (room_name != null) {
            root = FirebaseDatabase.getInstance().getReference().child(room_name);
        }
        myTypeFace = Typeface.createFromAsset(getAssets() , "stripped.ttf");
        map = new HashMap<>();
        map2 = new HashMap<>();

        tv_cmd.setText(R.string.place_your_numbers);

        init();


    }

    private void init() {
        final DatabaseReference ref_count = root.getRef().child("Count");
        ref_count.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator i = dataSnapshot.getChildren().iterator();
                if (i.hasNext()) {
                    count = 2;
                } else {
                    count = 1;
                    mFirst = true;
                }
                ref_count.child("count").setValue(String.valueOf(count));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void Touched(View view) {
        TextView textView = (TextView) view;
        if (btn_count < 26 && textView.getText().toString().isEmpty()) {
            textView.setText(String.valueOf(btn_count));
            map.put(btn_count , textView);
            map2.put(textView.getText().toString() , textView.getTag().toString());
            btn_count++;
            if (btn_count == 26) {
                root.child("complete").child(String.valueOf(mFirst)).setValue("yes");
                tv_cmd.setText("Waiting For Opponent To Complete...");
                btn_count = 27;
                check_win2();
                getComplete();
            }
        }
        if (btn_count == 27 && ready && !textView.getTypeface().equals(myTypeFace)){
            textView.setTypeface(myTypeFace);
            ready = false;
            move_count++;
            tv_cmd.setText(R.string.op_turn);
            updateMove(textView.getText().toString());
        }
    }

    private void updateMove(String tag) {
        DatabaseReference ref_move = root.getRef().child("move");
        ref_move.child(String.valueOf(move_count)).setValue(tag);
        map2.remove(tag);
        check_win();
    }

    private void getComplete() {
        final DatabaseReference ref_complete = root.getRef().child("complete");
        ref_complete.addValueEventListener(vel =  new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator i = dataSnapshot.getChildren().iterator();
                while(i.hasNext()){
                    Object o = i.next();
                    String temp = ((DataSnapshot) o).getKey();
                    if (temp.equals(String.valueOf(!mFirst))){
                        if (mFirst){
                            tv_cmd.setText(R.string.your_turn);
                            ready = true;
                        }
                        else {
                            tv_cmd.setText(R.string.op_turn);
                        }
                        getMove();
                        ref_complete.removeEventListener(vel);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
            }

    private void getMove() {
        DatabaseReference ref_move = root.getRef().child("move");
        ref_move.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator i = dataSnapshot.getChildren().iterator();
                while(i.hasNext()){
                    Object o = i.next();
                    String temp = ((DataSnapshot) o).getKey();
                    if (temp.equals(String.valueOf(move_count + 1))){
                        int temp_count = Integer.parseInt(((DataSnapshot) o).getValue().toString());
                        TextView t = map.get(temp_count);
                        t.setTypeface(myTypeFace);
                        ready = true;
                        move_count++;
                        tv_cmd.setText(R.string.your_turn);
                        map2.remove(String.valueOf(temp_count));
                        check_win();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void check_win() {
        line_count = 0;
        boolean row1 = !map2.containsValue("1") && !map2.containsValue("2") && !map2.containsValue("3") && !map2.containsValue("4") && !map2.containsValue("5");
        boolean row2 = !map2.containsValue("6") && !map2.containsValue("7") && !map2.containsValue("8") && !map2.containsValue("9") && !map2.containsValue("10");
        boolean row3 = !map2.containsValue("11") && !map2.containsValue("12") && !map2.containsValue("13") && !map2.containsValue("14") && !map2.containsValue("15");
        boolean row4 = !map2.containsValue("16") && !map2.containsValue("17") && !map2.containsValue("18") && !map2.containsValue("19") && !map2.containsValue("20");
        boolean row5 = !map2.containsValue("21") && !map2.containsValue("22") && !map2.containsValue("23") && !map2.containsValue("24") && !map2.containsValue("25");
        boolean col1 = !map2.containsValue("1") && !map2.containsValue("6") && !map2.containsValue("11") && !map2.containsValue("16") && !map2.containsValue("21");
        boolean col2 = !map2.containsValue("2") && !map2.containsValue("7") && !map2.containsValue("12") && !map2.containsValue("17") && !map2.containsValue("22");
        boolean col3 = !map2.containsValue("3") && !map2.containsValue("8") && !map2.containsValue("13") && !map2.containsValue("18") && !map2.containsValue("23");
        boolean col4 = !map2.containsValue("4") && !map2.containsValue("9") && !map2.containsValue("14") && !map2.containsValue("19") && !map2.containsValue("24");
        boolean col5 = !map2.containsValue("5") && !map2.containsValue("10") && !map2.containsValue("15") && !map2.containsValue("20") && !map2.containsValue("25");

        if (row1){
            line_count++;
        }
        if (row2){
            line_count++;
        }
        if (row3){
            line_count++;
        }
        if (row4){
            line_count++;
        }
        if (row5){
            line_count++;
        }
        if (col1){
            line_count++;
        }
        if (col2){
            line_count++;
        }
        if (col3){
            line_count++;
        }
        if (col4){
            line_count++;
        }
        if (col5){
            line_count++;
        }

        Log.d("line cont" , String.valueOf(line_count));
        if ( line_count >= 5){
            root.child("win").child(String.valueOf(mFirst)).setValue("yes");
        }
        }


    private void check_win2(){

        DatabaseReference ref_win = root.getRef().child("win");
        ref_win.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()){
                    if(((DataSnapshot)i.next()).getKey().equals(String.valueOf(!mFirst))){
                        lost = true;
                    }
                }
                if (lost && line_count >= 5){
                    tv_cmd.setText("Draw!!!");
                    disableAll();
                }
                else if (!lost && line_count >= 5){
                    tv_cmd.setText("You Won!!!");
                    disableAll();
                }
                else if (lost && line_count < 5){
                    tv_cmd.setText("You Lost!!!");
                    disableAll();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void disableAll() {
        for (int i = 1 ; i<= 25 ; i++){
            TextView t = map.get(i);
            t.setEnabled(false);
        }
    }

}
