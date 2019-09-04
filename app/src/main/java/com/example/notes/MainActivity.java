package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton makeNote;
    private RecyclerView recyclerView;
    private AdapterNote adapterNote;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private TextView noItem;
    private ArrayList<NotesModel> notesModels = new ArrayList<>();
    Map map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // calling a method to initalize all the views.
        initViews("Main");

        // method which handles all the click event
        clickEvent();
    }

    private void clickEvent() {

        // handles the click for the floating action button.
        makeNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews(final String TAG) {

        noItem = findViewById(R.id.no_text);
        makeNote = findViewById(R.id.add_notes);
        recyclerView = findViewById(R.id.notes_recycler);
        recyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();

        // this line is used to generate an unique id for every device.

        String unique_id = android.provider.Settings.Secure.getString(getContentResolver()
                , android.provider.Settings.Secure.ANDROID_ID);

        // this line refers to the particular point in the database ( or node)
        reference = firebaseDatabase.getReference(unique_id).child("Notes");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notesModels.clear();
                noItem.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (dataSnapshot.hasChildren()) {
                    // if child is present we iterate through the child to get all the data abd store
                    // it in the array list through the model class.
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        NotesModel notes = new NotesModel();
                        Log.e(TAG, "onDataChange: " + dataSnapshot1.child("title"));
                        //NotesModel notes = dataSnapshot1.getValue(NotesModel.class);
                        map = (Map) dataSnapshot1.getValue();
                        String title = (String) map.get("title");
                        String note = (String) map.get("note");
                        String date = (String) map.get("time");
                        String id = (String) map.get("id");
                        notes.setDescNotes(note);
                        notes.setNoteDates(date);
                        notes.setTitleText(title);
                        notes.setId(id);

                        // adding the object to the arraylist
                        notesModels.add(notes);
                    }

                    // setting up the adapter .
                    adapterNote = new AdapterNote(MainActivity.this, notesModels);
                    recyclerView.setAdapter(adapterNote);
                }else {

                    // if no text is present we hide the recycler view and show the text..

                    noItem.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    SharedPreferences.Editor editCount = MainActivity.this.getSharedPreferences("Notes", Context.MODE_PRIVATE).edit();
                    editCount.putString("count", "0");
                    editCount.apply();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*SharedPreferences prefs = MainActivity.this.getSharedPreferences("Notes", Context.MODE_PRIVATE);
        String title = prefs.getString("title", "Title...");
        String notes = prefs.getString("note","Notes......");*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }


    // this is the adapter class this class is used to populate the recycler view.

    private class AdapterNote extends RecyclerView.Adapter<AdapterNote.ViewHolder> {


        private ArrayList<NotesModel> notesModels ;
        public AdapterNote(MainActivity mainActivity, ArrayList<NotesModel> notesModels) {
            this.notesModels = notesModels;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_notes, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

            // populating the views and making click events happen on them.

            holder.titleNote.setText(notesModels.get(position).getTitleText());
            holder.noteDesc.setText(notesModels.get(position).getDescNotes());
            holder.noteDate.setText(notesModels.get(position).getNoteDates());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                    intent.putExtra("tag", true);
                    intent.putExtra("id", notesModels.get(position).getId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return notesModels.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView titleNote;
            public TextView noteDesc;
            public TextView noteDate;
            public CardView cardView;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                titleNote = itemView.findViewById(R.id.title_note);
                noteDate = itemView.findViewById(R.id.note_time);
                noteDesc = itemView.findViewById(R.id.note_desc);
                cardView = itemView.findViewById(R.id.card_view);
            }
        }
    }
}
