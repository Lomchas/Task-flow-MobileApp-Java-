package com.example.taskflow.UI;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;

import com.example.taskflow.FirestoreManager;
import com.example.taskflow.MainActivity;
import com.example.taskflow.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class AddTask extends Fragment {

    private EditText fieldTaskTitle, fieldTaskDesc;
    private AppCompatCheckBox checkBoxCompleted;
    private ImageButton goBackTaskListBtn;
    private Spinner spinnerCategory;
    private AppCompatButton btnSubmitTask;
    private FirestoreManager firestoreManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        firestoreManager = new FirestoreManager();

        fieldTaskTitle = view.findViewById(R.id.fieldTaskTitle);
        fieldTaskDesc = view.findViewById(R.id.fieldTaskDescription);
        checkBoxCompleted = view.findViewById(R.id.checkboxCompleted);
        spinnerCategory = view.findViewById(R.id.spinnerLabels);
        goBackTaskListBtn = view.findViewById(R.id.goBackTaskListBtn);
        btnSubmitTask = view.findViewById(R.id.btnSubmitTask);


//        SPINNER SETTINGS
        String[] categories = getResources().getStringArray(R.array.task_categories);
        ArrayAdapter<String> adapterSpinnerCategory = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapterSpinnerCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterSpinnerCategory);

        goBackTaskListBtn.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        btnSubmitTask.setOnClickListener(v -> {
            addTaskToFirestore();
        });


        return view;
    }

    private void addTaskToFirestore() {
        String title = fieldTaskTitle.getText().toString().trim();
        String desc = fieldTaskDesc.getText().toString().trim();
        String label = spinnerCategory.getSelectedItem().toString();
        Boolean isCompleted = checkBoxCompleted.isChecked();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (TextUtils.isEmpty(title)) {
            fieldTaskTitle.setError("El titulo es obligatorio");
            return;
        }

        if (TextUtils.isEmpty(desc)) {
            fieldTaskDesc.setError("Debes poner una descripcion");
            return;
        }

        Map<String, Object> task = new HashMap<>();
        task.put("title", title);
        task.put("description", desc);
        task.put("label", label);
        task.put("completed", isCompleted);
        task.put("userId", userId);

        firestoreManager.getTaskCollection().add(task).addOnSuccessListener(documentReference -> {
//            AGREGAR ID
            String idTask = documentReference.getId();
            firestoreManager.getTaskCollection()
                    .document(idTask)
                    .update("id", idTask)
                    .addOnSuccessListener(e -> {
                        Toast.makeText(requireContext(), "Tarea agregada con exito", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error al actualizar el ID de la tarea: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Error al agregar la tarea!", Toast.LENGTH_SHORT).show();
            Log.e("Firestore", "Error al añadir la tarea: " + e.getMessage());
        });
    }

    private void switchDashboardFragment(Fragment fragment) {
        if (getActivity() instanceof MainActivity) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.dashboardFragment, fragment).addToBackStack(null).commit();
        }
    }
}