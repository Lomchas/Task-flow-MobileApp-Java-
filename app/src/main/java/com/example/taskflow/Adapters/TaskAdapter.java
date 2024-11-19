package com.example.taskflow.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskflow.Models.Task;
import com.example.taskflow.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private OnTaskInteractionListener listener;

    public TaskAdapter(List<Task> taskList, OnTaskInteractionListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, descTextView, statusTextView, tagTextView;
        ImageButton taskDeleteBtn, taskCheckBtn;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.task_title);
            descTextView = itemView.findViewById(R.id.task_desc);
            statusTextView = itemView.findViewById(R.id.task_status);
            tagTextView = itemView.findViewById(R.id.task_tag);
            taskDeleteBtn = itemView.findViewById(R.id.task_delete_btn);
            taskCheckBtn = itemView.findViewById(R.id.task_check_btn);


        }

        public void bind(Task task){
            titleTextView.setText(task.getTitle());
            descTextView.setText(task.getDescription());
//            statusTextView.setText(task.isCompleted() ? "Completada" : "Pendiente");
            tagTextView.setText(task.getLabel());

            if (task.isCompleted()){
                statusTextView.setText("Completada");
                statusTextView.setBackgroundResource(R.drawable.background_status_task);
            }else {
                statusTextView.setText("Pendiente");
                statusTextView.setBackgroundResource(R.drawable.background_pending_task);
            }


            if (!task.isCompleted()){
                taskCheckBtn.setBackgroundResource(R.drawable.background_complete_btn);
                taskCheckBtn.setImageResource(R.drawable.baseline_check_circle_24);
            }
            else {
                taskCheckBtn.setBackgroundResource(R.drawable.background_incompleted_btn);
                taskCheckBtn.setImageDrawable(null);
            }

            taskCheckBtn.setOnClickListener(v -> {
                if (listener != null){
                    listener.onTaskCheck(task);
                }
            });


            taskDeleteBtn.setOnClickListener(v -> {
                if (listener != null){
                    listener.onTaskDelete(task);
                }
            });

        }
    }

    public interface OnTaskInteractionListener {
        void onTaskCheck(Task task);
        void onTaskDelete(Task task);
    }
}
