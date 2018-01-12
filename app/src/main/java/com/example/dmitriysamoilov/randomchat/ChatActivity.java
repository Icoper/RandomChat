package com.example.dmitriysamoilov.randomchat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmitriysamoilov.randomchat.database.SaveMsgHelper;
import com.example.dmitriysamoilov.randomchat.model.ChatMessage;
import com.example.dmitriysamoilov.randomchat.model.ChatRoom;
import com.example.dmitriysamoilov.randomchat.user.UserData;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Random;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = " ChatActivity";
    private Button sendMsgBtn;
    private EditText msgEt;

    private String chatName;
    private String mUsername;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<ChatMessage, FirechatMsgViewHolder> mFBAdapter;
    private RecyclerView mMsgRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUid;
    private SaveMsgHelper saveMsgHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("Free chat");

        Intent intent = getIntent();
        mUsername = intent.getStringExtra(UserData.USER_NAME_INTETN);
        saveMsgHelper = new SaveMsgHelper(this);

        // setup view elements
        sendMsgBtn = (Button) findViewById(R.id.sendButton);
        msgEt = (EditText) findViewById(R.id.msgEditText);
        mMsgRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setStackFromEnd(true);

        // FireBase setup
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mUid = mFirebaseUser.getUid();

        checkFreeRoom();
    }

    private void checkFreeRoom() {
        chatName = "";
        Toast.makeText(getApplicationContext(), "Wait to connect", Toast.LENGTH_SHORT).show();

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    ChatRoom room = data.getValue(ChatRoom.class);
                    // [START_EXCLUDE]
                    if (room.isRoomStatus()) {
                        chatName = room.getRoomName();
                        Toast.makeText(getApplicationContext(), "Free chat found", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "found room");
                        setmFBAdapterUn();
                        break;
                    }
                }
                initialize();
                // [END_EXCLUDE]

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(getApplicationContext(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });
    }

    private void initialize() {


        if (chatName.isEmpty()) {
            chatName = mUsername + randomNumb();
            createNewChat();

        } else {
            mDatabaseReference.child(chatName).push();
        }

        setmFBAdapterUn();

        sendMsgBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                ChatMessage frendlyMsg;
                if (!msgEt.getText().toString().isEmpty()) {
                    frendlyMsg = new ChatMessage(msgEt.getText().toString(), mUsername, mUid);
                    mDatabaseReference.child(chatName).push().setValue(frendlyMsg);
                    msgEt.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Enter msg first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mMsgRecyclerView.setLayoutManager(mLayoutManager);
        mMsgRecyclerView.setAdapter(mFBAdapter);
    }

    private void createNewChat() {
        Toast.makeText(getApplicationContext(), "Free chat not found, wait user", Toast.LENGTH_SHORT).show();

        String chatToken = mDatabaseReference.push().getKey();
        ChatRoom chatRoom = new ChatRoom(chatName, true);
        mDatabaseReference.child(chatToken).setValue(chatRoom);
    }

    // генерируем случайное имя комнаты
    private String randomNumb() {
        Random random = new Random();
        String result = "";

        for (int i = 0; i < 5; i++) {
            result += String.valueOf(random.nextInt());
        }

        return result;
    }

    private void setmFBAdapterUn() {
        mFBAdapter = new FirebaseRecyclerAdapter<ChatMessage, FirechatMsgViewHolder>(
                ChatMessage.class,
                R.layout.message,
                FirechatMsgViewHolder.class,
                mDatabaseReference.child(chatName).getRef()
        ) {
            @Override
            protected void populateViewHolder(FirechatMsgViewHolder firechatMsgViewHolder, ChatMessage chatMessage, int i) {

                if (chatMessage != null) {
                if (saveMsgHelper == null){
                    saveMsgHelper = new SaveMsgHelper(ChatActivity.this);
                }
                    if (chatMessage.getUid().equals(mUid)) {
                        firechatMsgViewHolder.setIsSender(true);
                        saveMsgHelper.saveToSenderHistory(chatMessage.getText());
                    } else {
                        firechatMsgViewHolder.setIsSender(false);
                    }
                    firechatMsgViewHolder.msgText.setText(chatMessage.getText());
                    firechatMsgViewHolder.userText.setText(chatMessage.getName());
                }
                saveMsgHelper.saveToGlobalHistory(chatMessage.getText(),chatMessage.getName());


            }

            @Override
            public ChatMessage getItem(int position) {
                return super.getItem(position);
            }
        };

        mFBAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            int mCurrentItemsCount = 0;

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatMsgCount = mFBAdapter.getItemCount();
                int lastVisiblePosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (chatMsgCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMsgRecyclerView.scrollToPosition(positionStart);
                }

                mCurrentItemsCount = chatMsgCount;
            }

            @Override
            public void onChanged() {
                super.onChanged();

                mCurrentItemsCount = mFBAdapter.getItemCount();
            }
        });

    }

    /**
     * Кастомный ВьюХолдер. Отвечает за показ сообщения
     */

    public static class FirechatMsgViewHolder extends RecyclerView.ViewHolder {
        public TextView msgText;
        public TextView userText;
        private final FrameLayout mLeftArrow;
        private final FrameLayout mRightArrow;
        private final RelativeLayout mMessageContainer;
        private final LinearLayout mMessage;
        private final int mGreen300;
        private final int mWhite300;

        public FirechatMsgViewHolder(View view) {
            super(view);

            userText = (TextView) view.findViewById(R.id.name_text);
            msgText = (TextView) view.findViewById(R.id.message_text);
            mLeftArrow = (FrameLayout) view.findViewById(R.id.left_arrow);
            mRightArrow = (FrameLayout) view.findViewById(R.id.right_arrow);
            mMessageContainer = (RelativeLayout) view.findViewById(R.id.message_container);
            mMessage = (LinearLayout) view.findViewById(R.id.message);
            mGreen300 = ContextCompat.getColor(view.getContext(), R.color.material_green_300);
            mWhite300 = ContextCompat.getColor(view.getContext(), R.color.material_white_300);
        }

        public void setIsSender(boolean isSender) {
            final int color;
            if (isSender) {
                color = mGreen300;
                msgText.setTextColor(Color.WHITE);
                mLeftArrow.setVisibility(View.INVISIBLE);
                mRightArrow.setVisibility(View.VISIBLE);
                mMessageContainer.setGravity(Gravity.END);
            } else {
                color = mWhite300;
                msgText.setTextColor(Color.BLACK);
                mLeftArrow.setVisibility(View.VISIBLE);
                mRightArrow.setVisibility(View.INVISIBLE);
                mMessageContainer.setGravity(Gravity.START);
            }

            ((GradientDrawable) mMessage.getBackground()).setColor(color);
            ((RotateDrawable) mLeftArrow.getBackground()).getDrawable()
                    .setColorFilter(color, PorterDuff.Mode.SRC);
            ((RotateDrawable) mRightArrow.getBackground()).getDrawable()
                    .setColorFilter(color, PorterDuff.Mode.SRC);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO
        // Автоудаление сообщений из чата
    }
}
