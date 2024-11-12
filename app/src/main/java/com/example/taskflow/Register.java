package com.example.taskflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class Register extends Fragment {

    private TextView textError;
    private EditText fieldFullName, fieldEmail, fieldPass, fieldConfirmPass;
    private AppCompatButton btnGoToLogin, btnRegister;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        textError = view.findViewById(R.id.text_error);
        fieldFullName = view.findViewById(R.id.fullNameFieldRegister);
        fieldEmail = view.findViewById(R.id.emailFieldRegister);
        fieldPass = view.findViewById(R.id.passFieldRegister);
        fieldConfirmPass = view.findViewById(R.id.confirmPassFieldRegister);
        btnGoToLogin = view.findViewById(R.id.btnGoLoginRegister);
        btnRegister = view.findViewById(R.id.btnRegister);


        btnGoToLogin.setOnClickListener(v -> openFragment(new Login()));
        btnRegister.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser() {
        String fullName = fieldFullName.getText().toString();
        String email = fieldEmail.getText().toString();
        String pass = fieldPass.getText().toString();
        String confirmPass = fieldConfirmPass.getText().toString();


        if (fullName.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            showError("Debes llenar todos los campos!");
            return;
        }
        if (!email.contains("@")) {
            showError("Email no valido");
            return;
        }
        if (!confirmPass.equals(pass)) {
            showError("Las contraseñas no coinciden!");
            return;
        }
        if (pass.length() < 8) {
            showError("La contraseña debe tener minimo 8 caracteres");
            return;
        }

        showError("");

        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Registro Exitoso", Toast.LENGTH_LONG).show();
                openFragment(new Login());
            } else {
                Toast.makeText(getContext(), "Error al registrar, intentalo nuevamente.", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void openFragment(Fragment fragment) {

//        Casting del metodo "loadFragment" en el MainActivity
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).loadFragment(fragment);
        }
    }

    public void showError(String error) {
        textError.setText(error);
    }
}