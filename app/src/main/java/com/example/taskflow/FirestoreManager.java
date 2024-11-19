package com.example.taskflow;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FirestoreManager {
    private static final String TASKS_COLLECTION = "task";
    private final FirebaseFirestore firestore;

    public FirestoreManager() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public CollectionReference getTaskCollection(){
        return firestore.collection(TASKS_COLLECTION);
    }
}
