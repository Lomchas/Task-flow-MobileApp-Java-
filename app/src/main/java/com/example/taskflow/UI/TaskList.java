package com.example.taskflow.UI;

import android.app.DownloadManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskflow.Adapters.TaskAdapter;
import com.example.taskflow.FirestoreManager;
import com.example.taskflow.MainActivity;
import com.example.taskflow.Models.Task;
import com.example.taskflow.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class TaskList extends Fragment implements TaskAdapter.OnTaskInteractionListener {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;

    TextView placeholder;
    AppCompatButton goToAddTaskFragment;
    AppCompatToggleButton togglePendingTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

//        RECYCLERVIEW SETTINGS
        recyclerView = view.findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(adapter);


        placeholder = view.findViewById(R.id.empty_placeholder);
        goToAddTaskFragment = view.findViewById(R.id.btnGoToAddTask);
        togglePendingTask = view.findViewById(R.id.togglePendingTasks);

        goToAddTaskFragment.setOnClickListener(v -> {
            switchDashboardFragment(new AddTask());
        });

        togglePendingTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            loadTasksFromFirestore(isChecked);
        });

//        LOAD DATA FROM FIRESTORE
        loadTasksFromFirestore(false);

        return view;
    }

    private void loadTasksFromFirestore(boolean pendingTask) {
        FirestoreManager firestoreManager = new FirestoreManager();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = firestoreManager.getTaskCollection().whereEqualTo("userId", userId);

        if(pendingTask){
            query = query.whereEqualTo("completed", false);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                taskList.clear();
                queryDocumentSnapshots.forEach(documentSnapshot -> {
                    Task task = documentSnapshot.toObject(Task.class);
                    task.setId(documentSnapshot.getId());
                    taskList.add(task);
                });
                adapter.notifyDataSetChanged();
            } else {
                taskList.clear();
                Toast.makeText(requireContext(), pendingTask ? "No hay tareas pendientes." : "No hay tareas para mostrar.", Toast.LENGTH_SHORT).show();
            }
            togglePlaceholder();
        }).addOnFailureListener(e -> {
            Log.e("TaskList", "Error al cargar las tareas: " + e.getMessage());
        });
        adapter.notifyDataSetChanged();
    }

    private void switchDashboardFragment(Fragment fragment) {
        if (getActivity() instanceof MainActivity) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.dashboardFragment, fragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onTaskCheck(Task task) {
        task.setCompleted(!task.isCompleted());
        adapter.notifyDataSetChanged();
        updateTaskFromFirestore(task);
        Log.d("ID", "El id del elemento presionado es: " + task.getId());
    }

    @Override
    public void onTaskDelete(Task task) {
        int position = taskList.indexOf(task);
        if (position != -1) {
            taskList.remove(task);
            adapter.notifyItemRemoved(position);
            deleteTaskFromFirestore(task);
            togglePlaceholder();
        }
    }

    private void deleteTaskFromFirestore(Task task) {
        FirestoreManager firestoreManager = new FirestoreManager();

        firestoreManager.getTaskCollection().document(task.getId()).delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(requireContext(), "Tarea eliminada!", Toast.LENGTH_SHORT).show();
            Log.d("Firestore", "Tarea elimininada!");
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "No se pudo eliminar la tarea, Intenta nuevamente!", Toast.LENGTH_SHORT).show();
            Log.e("Firestore", "Error al intentar eliminar la tarea: " + e.getMessage());
        });
    }

    private void updateTaskFromFirestore(Task task) {
        FirestoreManager firestoreManager = new FirestoreManager();

        firestoreManager.getTaskCollection().document(task.getId()).set(task).addOnSuccessListener(e -> {
            Toast.makeText(requireContext(), "Tarea actualizada!", Toast.LENGTH_SHORT).show();
            Log.d("UpdateTask", "Estado actualizado en Firestore");
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Error al actualizar tarea", Toast.LENGTH_SHORT).show();
            Log.e("UpdateTask", "Error al actualizar tarea: " + e.getMessage());
        });
    }

    private void togglePlaceholder() {
        if (taskList.isEmpty()) {
            placeholder.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            placeholder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}