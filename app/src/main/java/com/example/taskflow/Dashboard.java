package com.example.taskflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends Fragment {
    TextView userNameText;
    ImageButton btnLogout;
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        userNameText = view.findViewById(R.id.userNameText);
        firebaseAuth = FirebaseAuth.getInstance();
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logOut());

        displayName();

        return view;
    }

    private void displayName() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null){
             String nameUser = currentUser.getDisplayName();

             if (nameUser != null && !nameUser.isEmpty()){
                 userNameText.setText(nameUser);
             }
             else {
                 userNameText.setText("Usuario");
             }
        }
    }

    private void logOut() {
        firebaseAuth.signOut();

        GoogleSignIn.getClient(
                requireContext(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();

        openFragment(new Login());
        Toast.makeText(getContext(), "Sesion cerrada.", Toast.LENGTH_LONG).show();
    }


    private void openFragment (Fragment fragment) {
        if (requireActivity() instanceof MainActivity){
            ((MainActivity) requireActivity()).loadFragment(fragment);
        }
    }
}