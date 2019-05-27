package com.example.shoppingassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingassistant.Model.Data;
import com.example.shoppingassistant.Model.ItemType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private FloatingActionButton fab_btn;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;

    private TextView totalSumResult;

    // Global vars
    private ItemType type;
    private int amount;
    private String name;
    private String post_key;
    private Boolean checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        totalSumResult = findViewById(R.id.total_amount);

        getSupportActionBar().setTitle("ShoppingList");

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("ShoppingList").child(uId);

        mDatabase.keepSynced(true);

        recyclerView = findViewById(R.id.recycler_home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        // Total sum number
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalAmount = 0;

                for(DataSnapshot snap: dataSnapshot.getChildren()) {
                    Data data = snap.getValue(Data.class);

                    totalAmount += data.getAmount();

                    String total = totalAmount + ".0";

                    totalSumResult.setText(total);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab_btn = findViewById(R.id.fab);
        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });
    }

    private void customDialog() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myview = inflater.inflate(R.layout.input_data, null);

        final AlertDialog dialog = mydialog.create();

        dialog.setView(myview);

        final Spinner type = myview.findViewById(R.id.edt_type);
        final EditText amount = myview.findViewById(R.id.edt_amount);
        final EditText name = myview.findViewById(R.id.edt_name);
        Button btn_save = myview.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemType mType =  ItemType.valueOf(type.getSelectedItem().toString());
                String mAmount = amount.getText().toString().trim();
                String mName = name.getText().toString().trim();
                Boolean mChecked = false;

                if(TextUtils.isEmpty(mAmount)){
                    amount.setError("Required Field...");
                    return;
                }
                if(TextUtils.isEmpty(mName)){
                    name.setError("Required Field...");
                    return;
                }

                String id = mDatabase.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());
                int amount = Integer.parseInt(mAmount);

                Data data = new Data(mType, amount, mName, date, id, mChecked);

                mDatabase.child(id).setValue(data);

                Toast.makeText(getApplicationContext(), "Data add", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.item_data,
                MyViewHolder.class,
                mDatabase.orderByChild("checked")
        ) {

            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, final Data data, final int position) {
                myViewHolder.setType(data.getType().toString());
                myViewHolder.setName(data.getName());
                myViewHolder.setDate(data.getDate());
                myViewHolder.setChecked(data.getChecked());
                myViewHolder.setAmount(String.valueOf(data.getAmount()));

                final TextView update_name = myViewHolder.myView.findViewById(R.id.name);
                final TextView update_type = myViewHolder.myView.findViewById(R.id.type);
                final TextView update_amount = myViewHolder.myView.findViewById(R.id.amount);
                final CheckBox update_checked = myViewHolder.myView.findViewById(R.id.checked);

                myViewHolder.myView.findViewById(R.id.checked).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(position).getKey();
                        type = ItemType.valueOf(update_type.getText().toString());
                        String mAmount = update_amount.getText().toString().trim();
                        name = update_name.getText().toString().trim();

                        String date = DateFormat.getDateInstance().format(new Date());
                        int amount = Integer.parseInt(mAmount);

                        Data data = new Data(type, amount, name, date, post_key, update_checked.isChecked());

                        mDatabase.child(post_key).setValue(data);
                    }
                });

                myViewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(position).getKey();
                        type = data.getType();
                        name = data.getName();
                        amount = data.getAmount();
                        checked = data.getChecked();

                        updateData();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View myView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;
        }

        void setType(String type) {
            TextView mType = myView.findViewById(R.id.type);
            mType.setText(type);
        }

        public void setName(String name) {
            TextView mName = myView.findViewById(R.id.name);
            mName.setText(name);
        }

        void setDate(String date) {
            TextView mDate = myView.findViewById(R.id.date);
            mDate.setText(date);
        }

        void setAmount(String amount) {
            TextView mAmount = myView.findViewById(R.id.amount);
            mAmount.setText(String.valueOf(amount));
        }

         void setChecked(Boolean checked) {
            CheckBox mChecked = myView.findViewById(R.id.checked);
            mChecked.setChecked(checked);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.log_out:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateData() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

        View mView = inflater.inflate(R.layout.update_inputfield, null);

        final AlertDialog dialog = myDialog.create();

        dialog.setView(mView);

        final Spinner edtType = mView.findViewById(R.id.edt_type_upd);
        final EditText edtAmount = mView.findViewById(R.id.edt_amount_upd);
        final EditText edtName = mView.findViewById(R.id.edt_name_upd);

        edtType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ItemType.values()));

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        edtName.setText(name);
        edtName.setSelection(name.length());

        Button btnUpdate = mView.findViewById(R.id.btn_update);
        Button btnDelete = mView.findViewById(R.id.btn_delete);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = ItemType.valueOf(edtType.getSelectedItem().toString());
                String mAmount = edtAmount.getText().toString().trim();
                name = edtName.getText().toString().trim();

                String date = DateFormat.getDateInstance().format(new Date());
                int amount = Integer.parseInt(mAmount);

                Data data = new Data(type, amount, name, date, post_key, checked);

                mDatabase.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mDatabase.child(post_key).removeValue();

                // maps
                Intent myIntent = new Intent(HomeActivity.this, ShopsMapActivity.class);
                HomeActivity.this.startActivity(myIntent);

                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
