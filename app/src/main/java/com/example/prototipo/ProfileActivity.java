package com.example.prototipo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    TextView profileName, profileEmail, profileUsername, profilePassword;
    TextView titleName, titleUsername;
    Button editProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        bottomNavigationView.setOnItemSelectedListener(item ->{
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_cal) {
                startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                return true;
            }
            return false;
        });

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        profilePassword = findViewById(R.id.profilePassword);
        titleName = findViewById(R.id.titleName);
        titleUsername = findViewById(R.id.titleUsername);
        editProfile = findViewById(R.id.editButton);

        showAllUserData();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });
    }

    private void showAllUserData(){
        Intent intent = getIntent();
        if (intent == null) {
            Log.e("ProfileActivity", "Intent nula");
            Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");

        // Logs para depuração
        Log.d("ProfileData", "Nome: " + name);
        Log.d("ProfileData", "Email: " + email);
        Log.d("ProfileData", "Usuário: " + username);
        Log.d("ProfileData", "Senha: " + password);

        // Valide se os dados existem
        if (name == null || email == null || username == null || password == null) {
            Toast.makeText(this, "Dados incompletos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Atualizar UI
        titleName.setText(name);
        titleUsername.setText(username);
        profileName.setText(name);
        profileEmail.setText(email);
        profileUsername.setText(username);
        profilePassword.setText(password);
    }

    public void passUserData(){
        Intent intent = new Intent(this, EditProfileActivity.class);

        intent.putExtra("name", profileName.getText().toString());
        intent.putExtra("email", profileEmail.getText().toString());
        intent.putExtra("username", profileUsername.getText().toString());
        intent.putExtra("password", profilePassword.getText().toString());

        startActivity(intent);
    }
}
