package com.example.cre8.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //listview
        final ListView listView = (ListView) findViewById(R.id.listView);
        //adapter for listview
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(adapter);
        //Firebase Init
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //Get the items from the database
        final DatabaseReference ref = database.getReference("todos");
        //Listen for new todos
        ref.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName){
                String value = dataSnapshot.getValue(String.class);
                adapter.add(value);
            }
            //Remove todoItem
            public void onChildRemoved(DataSnapshot dataSnapshot){
                String value = dataSnapshot.getValue(String.class);
                adapter.remove(value);
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName){}
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName){}
            @Override
            public void onCancelled(DatabaseError error){
                Log.w("TAG:", "Error", error.toException());
            }
        });
        final EditText input = (EditText) findViewById(R.id.input);
        final Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //generate key for database
                DatabaseReference childRef = ref.push();
                //add what the input has to todos
                childRef.setValue(input.getText().toString());
                // Clear input
                input.setText("");
            }
        });
        // Delete items when clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Query q = ref.orderByValue().equalTo((String)
                        listView.getItemAtPosition(position));
                q.addListenerForSingleValueEvent(new ValueEventListener(){
                    //Remove the todoItem from the database
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot){
                        if (dataSnapshot.hasChildren()){
                            DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                            firstChild.getRef().removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError){
                    }
                })
                ;}
        })
        ;}
}
