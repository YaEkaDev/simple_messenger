package com.example.messenger;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<List<Message>> messages = new MutableLiveData<>();
    private FirebaseAuth mAuth;
    private MutableLiveData<NewUser> otherUser = new MutableLiveData<>();
    private MutableLiveData<Boolean> msgSent = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private String currentUserId;
    private String my_name;
    private String SERVER_KEY = "AAAA9XLXhZ0:APA91bGcfMkRLI01AjIHiY1hxBvasCXILOqWaJ9p7XrLM2nPkl3x95632WUYBc_pFuNTZbMA5uk2uYpWnmBbPWZ3Kewnt5trxpB9rxNTT3kXZb2LUyJMpn0lfi3DcimT4CKbngjE5JAq";
    private String otherUserId;
    private final String url = "https://chat-gkt-default-rtdb.europe-west1.firebasedatabase.app/";
    private FirebaseDatabase database = FirebaseDatabase.getInstance(url);
    private DatabaseReference referenceUsers = database.getReference("users");
    private DatabaseReference referenceMessages = database.getReference("Messages");


    public ChatViewModel(String currentUserId, String otherUserId) {
        this.currentUserId = currentUserId;
        this.otherUserId = otherUserId;
        mAuth = FirebaseAuth.getInstance();
        referenceUsers.child(otherUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NewUser user = snapshot.getValue(NewUser.class);
                otherUser.setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError err) {
                error.setValue(err.getMessage());
                Log.d("MyLog", err.getMessage());
            }
        });

        referenceMessages.child(otherUserId).child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Message> messageList = new ArrayList<>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);

                    messageList.add(message);

                }
                messages.setValue(messageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        referenceUsers.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                my_name = snapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public LiveData<NewUser> getOtherUser() {
        return otherUser;
    }

    public LiveData<Boolean> getMsgSent() {
        return msgSent;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void setUserOnline(boolean isOnline){
        referenceUsers.child(currentUserId).child("online").setValue(isOnline);
    }


    public void sendMessage(Message message) {

        referenceMessages
                .child(message.getSenderId())
                .child(message.getRecieverId())
                .child(message.getId())
                .setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        referenceMessages
                                .child(message.getRecieverId())
                                .child(message.getSenderId())
                                .child(message.getId())
                                .setValue(message)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        msgSent.setValue(true);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        error.setValue(e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error.setValue(e.getMessage());
                    }
                });

    }

    void getToken(Message message, ChatActivity chatActivity) {

        referenceUsers.child(message.getRecieverId())
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                //String name = snapshot.child("name").getValue().toString();
                Log.d("MyLog",token);
                JSONObject to = new JSONObject();
                JSONObject data = new JSONObject();
                try {
                    data.put("title",my_name);
                    data.put("message", message.getText());
                    //data.put("otherId", message.getRecieverId());
                    //data.put("currentId", message.getSenderId());
                    data.put("otherId",message.getSenderId() );
                    data.put("currentId", message.getRecieverId());

                    to.put("to", token);
                    to.put("data", data);

                   sendNotification(to, chatActivity);

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(JSONObject to, Context context) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", to, response -> {
            Log.d("notification", "sendNotification: " + response);
        }, error -> {
            Log.d("notification", "err sendNotification: " + error);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "key=" + SERVER_KEY);
                map.put("Content-Type", "application/json");
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    public String getIdMsg() {

        referenceMessages.child(currentUserId).child(otherUserId);
        String chatID = referenceMessages.push().getKey();
        return chatID;

    }
}
