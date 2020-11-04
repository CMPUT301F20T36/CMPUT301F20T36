package com.example.book_master.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.book_master.LoginActivity;
import com.example.book_master.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

/**
 * Connect local data to Firebase.
 * Notice that the data retrieving is asynchronous with the main process.
 * Therefore we create listener and send the fetched data into static classes.
 * (UserList, BookList, MessageList)
 * o(*≧▽≦)ツ┏━┓
 */
public class DBHelper {
    private static final String TAG = DBHelper.class.getSimpleName();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Create an new user account through Firebase Authentication
     * @param email
     * @param password
     * @param username
     * @param contactInfo
     * @param context
     */
    public static void createAccount(final String email,
                              final String password,
                              final String username,
                              final String contactInfo,
                              final Context context) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmailAndPassword:success");
                            Toast.makeText(context, "Authentication succeeded.",
                                    Toast.LENGTH_SHORT).show();

                            // include the user in the Firebase
                            FirebaseUser user = mAuth.getCurrentUser();
                            setUserDoc(user.getUid(), new User(email, password, username, contactInfo), context);
                        } else {
                            Log.w(TAG, "createUserWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Delete the current user account
     * @param context
     */
    public static void deleteAccount(final Context context) {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "delete account:success");
                            Toast.makeText(context, "User account deleting succeeded.",
                                    Toast.LENGTH_SHORT).show();

                            // also, delete the correspond User instance in Firebase
                            deleteUserDoc(user.getUid(), context);
                            // TODO: direct UI to login activity
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                        } else {
                            Log.w(TAG, "delete account:failure", task.getException());
                            Toast.makeText(context, "User account deleting failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Sign in the user account if it is recorded
     * @param email
     * @param password
     * @param context
     */
    public static void signIn(final String email, final String password, final Context context) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmailAndPassword:success");
                            Toast.makeText(context, "Authentication succeeded.",
                                    Toast.LENGTH_SHORT).show();

                            // TODO: direct UI to main menu activity
                            Intent intent = new Intent(context, ProfileActivity.class);
                            context.startActivity(intent);
                        } else {
                            Log.w(TAG, "signInWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Sign out the current user account
     * @param context
     */
    public static void signOut(final Context context) {
        mAuth.signOut();

        // TODO: direct UI to login activity
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * Create or modify one User instance in Firebase
     * @param doc: the user id generated by FirebaseAuth,
     *           retrieved via FirebaseAuth.getInstance().getCurrentUser().getUid()
     * @param user
     * @param context
     */
    static void setUserDoc(final String doc, final User user, final Context context) {
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        mDB.collection("User")
                .document(doc)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "set(user):success");
                        Toast.makeText(context, "User info updating succeeded.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "set(user):failure", e);
                        Toast.makeText(context, "User info updating failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Create or modify one Book instance in Firebase
     * @param doc: the unique ID identifying the book
     * @param book
     * @param context
     */
    static void setBookDoc(final String doc, final Book book, final Context context) {
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        mDB.collection("Book")
                .document(doc)
                .set(book)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "set(book):success");
                        Toast.makeText(context, "Book info updating succeeded.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "set(book):failure", e);
                        Toast.makeText(context, "Book info updating failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Create or modify one Message instance in Firebase
     * @param doc: the unique ID identifying the message
     * @param msg
     * @param context
     */
    static void setMessageDoc(final String doc, final Message msg, final Context context) {
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        mDB.collection("Message")
                .document(doc)
                .set(msg)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "set(msg):success");
                        Toast.makeText(context, "Message info updating succeeded.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "set(msg):failure", e);
                        Toast.makeText(context, "Message info updating failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Delete one User instance from Firebase
     * @param doc: the user id generated by FirebaseAuth
     * @param context
     */
    public static void deleteUserDoc(final String doc, final Context context) {
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        mDB.collection("User")
                .document(doc)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "delete User:success");
                        Toast.makeText(context, "User instance deleting succeeded.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "delete User:failure", e);
                        Toast.makeText(context, "User instance deleting failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Delete one Book instance from Firebase
     * @param doc: the unique ID identifying the book
     * @param context
     */
    public static void deleteBookDoc(final String doc, final Context context) {
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        mDB.collection("Book")
                .document(doc)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "delete Book:success");
                        Toast.makeText(context, "Book instance deleting succeeded.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "delete Book:failure", e);
                        Toast.makeText(context, "Book instance deleting failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Delete one Message instance from Firebase
     * @param doc: the unique ID identifying the message
     * @param context
     */
    public static void deleteMessageDoc(final String doc, final Context context) {
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        mDB.collection("Message")
                .document(doc)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "delete Message:success");
                        Toast.makeText(context, "Message instance deleting succeeded.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "delete Message:failure", e);
                        Toast.makeText(context, "Message instance deleting failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Listen to the change in Firebase and update userList in local
     * Should be called when application starts, i.e., in MainActivity
     */
    public static void userCollectionListener() {
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        CollectionReference mCollectionRef = mDB.collection("User");
        mCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                UserList.clearList();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    String email = (String) doc.getData().get("email");
                    String password = (String) doc.getData().get("password");
                    String username = (String) doc.getData().get("username");
                    String contactInfo = (String) doc.getData().get("contactInfo");
                    UserList.addUser(new User(email, password, username, contactInfo));
                }
            }
        });
    }

    /**
     * Listen to the change in Firebase and update bookList in local
     * Should be called when application starts, i.e., in MainActivity
     */
    public static void bookCollectionListener() {
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        CollectionReference mCollectionRef = mDB.collection("Book");
        mCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                BookList.clearList();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {

                    // TODO: Add Book instance here
//                    String title = (String) doc.getData().get("title");
//                    BookList.addBook(new Book(title));
                }
            }
        });
    }

    /**
     * Listen to the change in Firebase and update messageList in local
     * Should be called when application starts, i.e., in MainActivity
     */
    public static void messageCollectionListener() {
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        CollectionReference mCollectionRef = mDB.collection("Message");
        mCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                // TODO: Create MessageList
//                MessageList.clearList();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {

                    // TODO: Add Message instance here
//                    String content = (String) doc.getData().get("content");
//                    BookList.addBook(new Message(content));
                }
            }
        });
    }
}