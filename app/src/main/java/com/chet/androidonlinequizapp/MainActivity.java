package com.chet.androidonlinequizapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.chet.androidonlinequizapp.Fragments.Home;
import com.chet.androidonlinequizapp.Fragments.Page2;
import com.chet.androidonlinequizapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.zip.Inflater;

public class MainActivity extends FragmentActivity {
    MaterialEditText edtNewUser,edtNewPassword,edtNewEmail; //for sing up
    MaterialEditText edtUser,edtPassword; //for sign in;
    Button btnSignUp,btnSignIn;
    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        //firebase
       database = FirebaseDatabase.getInstance();
       users = database.getReference("Users");
       edtUser = (MaterialEditText)findViewById(R.id.edtUser);
       edtPassword = (MaterialEditText)findViewById(R.id.edtPassword);

       btnSignIn = (Button)findViewById(R.id.btn_sign_in);
       btnSignUp = (Button)findViewById(R.id.btn_sign_up);

       btnSignUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               showSignUpDialog();
           }


       });
       btnSignIn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               signIn(edtUser.getText().toString(),edtPassword.getText().toString());
           }

           private void signIn(final String user, final String pwd) {
               users.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(user).exists())
                    {
                        if (!user.isEmpty())
                        {
                            User login = dataSnapshot.child(user).getValue(User.class);
                            if (login.getPassword().equals(pwd))
                            {
                                Intent homeActivity = new Intent(MainActivity.this,Home.class);
                                startActivity(homeActivity);
                                finish();

                            }
                            else
                                Toast.makeText(MainActivity.this,"Wrong password", Toast.LENGTH_SHORT).show();
                        }
                        else
                          {
                            Toast.makeText(MainActivity.this,"Please enter your user name",Toast.LENGTH_SHORT).show();
                          }

                    }
                    else
                        Toast.makeText(MainActivity.this,"User is not exists !", Toast.LENGTH_SHORT).show();

                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
           }
       });
    }

    private void showSignUpDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Sign Up");
        alertDialog.setMessage("Please fill full information");
        LayoutInflater inflater =this.getLayoutInflater();
        View sign_up_layout = inflater.inflate(R.layout.sign_up_layout,null);

        edtNewUser = (MaterialEditText)sign_up_layout.findViewById(R.id.edtNewUserName);
        edtNewPassword = (MaterialEditText)sign_up_layout.findViewById(R.id.edtNewPassword);
        edtNewEmail = (MaterialEditText)sign_up_layout.findViewById(R.id.edtNewEmail);


        alertDialog.setView(sign_up_layout);
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final User user= new User(edtNewUser.getText().toString(),
                  edtNewPassword.getText().toString(),
                  edtNewEmail.getText().toString());

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(user.getUsername()).exists())
                            Toast.makeText(MainActivity.this,"User already exists !",Toast.LENGTH_SHORT).show();
                        else
                        {
                            users.child(user.getUsername())
                                    .setValue(user);
                            Toast.makeText(MainActivity.this,"User registration success !",Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
}
