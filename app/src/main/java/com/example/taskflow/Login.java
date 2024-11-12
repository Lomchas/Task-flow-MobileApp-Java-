package com.example.taskflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;


public class Login extends Fragment {


    private FirebaseAuth firebaseAuth;

    //    GOOGLE
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100;

    //    FACEBOOK
    private CallbackManager callbackManager;
    private LoginButton loginButton;


    TextView textError;
    EditText email, pass;
    AppCompatButton btnGoRegister, btnLogin;
    ImageButton btnSignInWithGoogle, btnSignInWithFacebook;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

//        CONFIGURACION GOOGLE SIGN-IN
        googleSignInClient = GoogleSignIn.getClient(requireContext(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.client_id)).requestEmail().build());


//        INSTANCIAR CALLBACK-MANAGER
        callbackManager = CallbackManager.Factory.create();


        btnSignInWithFacebook = view.findViewById(R.id.facebookSignInButton);


        email = view.findViewById(R.id.emailFieldLogin);
        pass = view.findViewById(R.id.passFieldLogin);
        textError = view.findViewById(R.id.text_error);
        btnGoRegister = view.findViewById(R.id.btnGoRegisterLogin);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnSignInWithGoogle = view.findViewById(R.id.googleSignInButton);


//        HANDLE ONCLICK BUTTONS EVENTS
        btnGoRegister.setOnClickListener(v -> openFragment(new Register()));
        btnLogin.setOnClickListener(v -> logIn());
        btnSignInWithGoogle.setOnClickListener(v -> signInWithGoogle());
        btnSignInWithFacebook.setOnClickListener(v -> signInWithFacebook());

        return view;
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                openFragment(new Dashboard());
                Toast.makeText(getContext(), "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error al iniciar sesion con Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Toast.makeText(getContext(), "Error al autenticar con Facebook: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    //    SIGN-IN BY GOOGLE
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //    GET INTO ACCOUNT BY GOOGLE
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                openFragment(new Dashboard());
                Toast.makeText(getContext(), "Inicio de sesion exitoso!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Error al autenticar con Firebase", Toast.LENGTH_LONG).show();
            }
        });
    }

    //    HANDLE RESULT GOOGLE SIGN-IN
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        GOOGLE RESULT REQUEST
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getContext(), "Error en inicio de sesion con Google", Toast.LENGTH_LONG).show();
            }
        }

//        FACEBOOK RESULT REQUEST
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    //    SIGN-IN BY EMAIL AND PASS
    private void logIn() {
        String emailText = email.getText().toString();
        String passText = pass.getText().toString();

        if (emailText.isEmpty() || passText.isEmpty()) {
            Toast.makeText(getContext(), "Debes llenar todos los campos", Toast.LENGTH_LONG).show();
            return;
        }
        if (!emailText.contains("@")) {
            Toast.makeText(getContext(), "Email no valido", Toast.LENGTH_LONG).show();
            return;
        }
        if (passText.length() < 8) {
            Toast.makeText(getContext(), "La contraseña debe tener minimo 8 caracteres", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(emailText, passText).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                openFragment(new Dashboard());
                Toast.makeText(getContext(), "Bienvenido", Toast.LENGTH_LONG).show();
                textError.setText("");
            }
        }).addOnFailureListener(e -> {
            String errorMessage;

            if (e instanceof FirebaseAuthInvalidUserException) {
                errorMessage = "El email ingresado no está registrado. Por favor, verifica.";
            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                errorMessage = "La contraseña es incorrecta. Por favor, inténtalo de nuevo.";
            } else {
                errorMessage = "Error al iniciar sesión. Inténtalo de nuevo más tarde.";
            }

            textError.setText(errorMessage);
        });

    }

    //    CHANGE IU
    private void openFragment(Fragment fragment) {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).loadFragment(fragment);
        }
    }
}