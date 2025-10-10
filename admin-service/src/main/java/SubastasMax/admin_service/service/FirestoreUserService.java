package SubastasMax.admin_service.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;
import SubastasMax.admin_service.model.User;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreUserService {

    private static final String COLLECTION_NAME = "users";

    // ✅ Obtener todos los usuarios
    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<User> users = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documents) {
            User user = doc.toObject(User.class);
            try {
                user.setId(Long.parseLong(doc.getId()));
            } catch (NumberFormatException e) {
                user.setId(null);
            }
            users.add(user);
        }
        return users;
    }

    // ✅ Obtener un usuario por ID
    public User getUserById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();
        return document.exists() ? document.toObject(User.class) : null;
    }

    // ✅ Crear usuario
    public User createUser(User user) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        String docId = (user.getId() != null) ? String.valueOf(user.getId()) : UUID.randomUUID().toString();
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(docId)
                .set(user);
        future.get();
        return user;
    }

    // ✅ Actualizar usuario completo
    public User updateUser(String id, User user) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            return null;
        }

        ApiFuture<WriteResult> future = docRef.set(user);
        future.get();
        return user;
    }

    // ✅ Eliminar usuario
    public boolean deleteUser(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            return false;
        }

        ApiFuture<WriteResult> future = docRef.delete();
        future.get();
        return true;
    }

    // 🔹 NUEVO: Actualizar solo el rol de un usuario
    public User updateUserRole(String id, String newRole) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            return null;
        }

        ApiFuture<WriteResult> future = docRef.update("role", newRole);
        future.get();

        User updatedUser = document.toObject(User.class);
        if (updatedUser != null) {
            updatedUser.setRole(newRole);
        }
        return updatedUser;
    }

    // 🔹 NUEVO: Actualizar solo el estado de un usuario (activo/suspendido/etc.)
    public User updateUserStatus(String id, String newStatus) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        DocumentSnapshot document = docRef.get().get();

        if (!document.exists()) {
            return null;
        }

        ApiFuture<WriteResult> future = docRef.update("status", newStatus);
        future.get();

        User updatedUser = document.toObject(User.class);
        if (updatedUser != null) {
            updatedUser.setStatus(newStatus);
        }
        return updatedUser;
    }
}
