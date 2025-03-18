package com.example.prototipo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;
    TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializa os componentes da interface
        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        // Ação do botão de login
        loginButton.setOnClickListener(view -> {
            if (!validateUsername() | !validatePassword()) {
                return;
            }
            checkUser();
        });

        // Redireciona para a tela de cadastro
        signupRedirectText.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    // Valida o campo de nome de usuário
    private Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Usuário não pode estar vazio");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    // Valida o campo de senha
    private Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Senha não pode estar vazia");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    // Verifica as credenciais do usuário no Firebase
    private void checkUser() {
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        // Autentique o usuário via Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Task<AuthResult> authResultTask = mAuth.signInWithEmailAndPassword(userUsername, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login bem-sucedido, agora busque os dados no Realtime Database
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

                        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // Obtém o primeiro (e único) usuário retornado pela query
                                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();

                                    // Recupera os dados do usuário
                                    String passwordFromDB = userSnapshot.child("password").getValue(String.class);
                                    String nameFromDB = userSnapshot.child("name").getValue(String.class);
                                    String emailFromDB = userSnapshot.child("email").getValue(String.class);
                                    String usernameFromDB = userSnapshot.child("username").getValue(String.class);

                                    // Valida a senha
                                    if (passwordFromDB.equals(userPassword)) {
                                        // Dados válidos: redireciona para MainActivity
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                        intent.putExtra("name", nameFromDB);
                                        intent.putExtra("email", emailFromDB);
                                        intent.putExtra("username", usernameFromDB);
                                        intent.putExtra("password", passwordFromDB);

                                        startActivity(intent);
                                        finish();
                                    } else {
                                        loginPassword.setError("Senha incorreta");
                                        loginPassword.requestFocus();
                                    }
                                } else {
                                    loginUsername.setError("Usuário não encontrado");
                                    loginUsername.requestFocus();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("FirebaseError", "Erro ao acessar o Firebase: " + error.getMessage());
                                Toast.makeText(LoginActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }
}

