package com.example.prototipo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class MainActivity extends AppCompatActivity {
    private String name, email, username, password; // Variáveis para armazenar dados

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView (R.layout.activity_main);

        // Recuperar os dados do Intent
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
            username = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
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
                //startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);

                profileIntent.putExtra("name", getIntent().getStringExtra("name"));
                profileIntent.putExtra("email", getIntent().getStringExtra("email"));
                profileIntent.putExtra("username", getIntent().getStringExtra("username"));
                profileIntent.putExtra("password", getIntent().getStringExtra("password"));

                startActivity(profileIntent);

                finish();
                return true;
            }

            return false;
        });
    }
}