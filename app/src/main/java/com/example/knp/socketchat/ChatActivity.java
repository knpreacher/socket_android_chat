package com.example.knp.socketchat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import br.com.goncalves.pugnotification.notification.PugNotification;
import io.socket.emitter.Emitter;

/**
 * Created by knp on 2/8/17.
 */

public class ChatActivity extends AppCompatActivity {

    static boolean inActivity = true;

    private static final String TAG = "LOGIIIIIIIIIIIIII";
    private static Bundle mBundle = null;
    SharedPreferences sp;
    RecyclerView recyclerView;
    ChatRecyclerAdapter chatRecyclerAdapter;
    Intent intent;
    Button btnSend;
    EditText etMessage;
    Handler typingHandler = new Handler();
    ArrayList<Message> messages;
    MSocket mSocket;
    Toolbar toolbar;
    TextView tvIsTyping;
    boolean isTyping=false;
    boolean isFirstOpened=true;

    String se = "Online: ";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.chat_layout);

        if(savedInstanceState!=null){
            Log.i(TAG, "onCreate: sis " + savedInstanceState.toString());
            messages = savedInstanceState.getParcelableArrayList("MESS");
            isFirstOpened = false;
        } else {
            messages = new ArrayList<>();
            isFirstOpened = true;
        }

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        if(sp.getString("activeLogin","")==null || sp.getString("activeRoom","")==null){
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        intent = getIntent();
        Log.i(TAG, "onCreate: "+intent.getStringExtra("login"));

        mSocket = new MSocket();

        tvIsTyping = (TextView)findViewById(R.id.tvIsTyping);
        btnSend = (Button)findViewById(R.id.btnSend);
        etMessage = (EditText)findViewById(R.id.etMessege);
        recyclerView = (RecyclerView)findViewById(R.id.rvForMessages);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getStringExtra("room"));
        toolbar.setBackgroundColor(Color.BLACK);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitle(se + intent.getIntExtra("count",0));
        toolbar.setSubtitleTextColor(Color.GREEN);
        toolbar.setLogo(R.drawable.icon);
        toolbar.setTitleMarginStart(35);
        setSupportActionBar(toolbar);



        chatRecyclerAdapter = new ChatRecyclerAdapter(this,messages);

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isTyping && etMessage.getText().length()!=0){
                    JSONObject data = new JSONObject();
                    try {
                        data.put("login",intent.getStringExtra("login"));
                        data.put("room",intent.getStringExtra("room"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            //TODO: server!!!!!!!!!!!!!!!!!!!!!

                    isTyping = true;
                    mSocket.getSocket().emit("typing",data);
                }

                typingHandler.removeCallbacks(onTypingTimeout);
                typingHandler.postDelayed(onTypingTimeout,500);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//-----------------------------------
        //SOCKET.ON
//-----------------------------------
        if(isFirstOpened) {
            Log.i(TAG, "onCreate: first opened");
            
            mSocket.getSocket().on("newMes", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        Log.i(TAG, "call: newMes calld : " + args[0]);
                        JSONObject data = new JSONObject(args[0].toString());
                        Log.i(TAG, "call: 1");
                        Message m = new Message(1);
                        m.setSender(data.getString("login"));
                        m.setText(data.getString("text"));
                        m.setTime(data.getString("time"));
                        if (!inActivity) {
                            PugNotification
                                    .with(ChatActivity.this)
                                    .load()
                                    .identifier(4898)
                                    .title(data.getString("login"))
                                    .message(data.getString("text"))
                                    .smallIcon(R.drawable.icon)
                                    .largeIcon(R.drawable.icon)
                                    .flags(Notification.DEFAULT_ALL)
                                    .click(ChatActivity.class, savedInstanceState)
                                    .simple()
                                    .build();
                        }
                        messages.add(m);
                        update();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            mSocket.getSocket().on("newUserCon", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject data = new JSONObject(args[0].toString());
                        Message m = new Message(2);
                        m.setSender(data.getString("login"));
                        Log.i(TAG, "call: 24442 " + args[0]);
                        messages.add(m);
                        update(data.getInt("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            mSocket.getSocket().on("closeCon", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject data = new JSONObject(args[0].toString());
                        Message m = new Message(3);
                        m.setSender(data.getString("login"));
                        Log.i(TAG, "call: 34443 " + args[0]);
                        messages.add(m);
                        update(data.getInt("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            mSocket.getSocket().on("typing", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    final JSONObject data = (JSONObject) args[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tvIsTyping.setText(data.getString("login") + " is typing...");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            mSocket.getSocket().on("stop typing", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvIsTyping.setText("");
                        }
                    });
                }
            });

        } else {
            Log.i(TAG, "onCreate: not first opened");
            chatRecyclerAdapter.notifyDataSetChanged();
        }

//-----------------------------------
        //SOCKET.ON
//-----------------------------------
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMessage.getText().length()>0) {
                    Message m = new Message(0);
                    m.setText(etMessage.getText().toString());
                    Date date = new Date();

                    m.setTime(Integer.toString(date.getHours()) + ":" + Integer.toString(date.getMinutes()) + ":" + Integer.toString(date.getSeconds()));

                    JSONObject data = new JSONObject();
                    try {
                        data.put("login", intent.getStringExtra("login"));
                        data.put("text", m.getText());
                        data.put("time", m.getTime());
                        Log.i(TAG, "onClick: i send : " + data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mSocket.getSocket().emit("newMes", data);

                    messages.add(m);
                    chatRecyclerAdapter.notifyDataSetChanged();
                    etMessage.setText("");
                    if (chatRecyclerAdapter.getItemCount() > 1) {
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, chatRecyclerAdapter.getItemCount() - 1);
                    }
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatRecyclerAdapter);


    }
    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!isTyping) return;

            isTyping = false;
            JSONObject data = new JSONObject();
            try {
                data.put("login",intent.getStringExtra("login"));
                data.put("room",intent.getStringExtra("room"));
                mSocket.getSocket().emit("stop typing",data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    void update(final int count){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatRecyclerAdapter.notifyDataSetChanged();
                if(chatRecyclerAdapter.getItemCount()>1){
                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,null,chatRecyclerAdapter.getItemCount()-1);
                }
                toolbar.setSubtitle(se+Integer.toString(count));
            }
        });
    }

    void update(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatRecyclerAdapter.notifyDataSetChanged();
                if(chatRecyclerAdapter.getItemCount()>1){
                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,null,chatRecyclerAdapter.getItemCount()-1);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
        inActivity = false;
        /*
        mBundle = new Bundle();
        mBundle.putParcelableArrayList("MS",messages);
        */
        //TODO: loooooooool
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: ");
        outState.putParcelableArrayList("MESS",messages);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i(TAG, "onPostResume: ");
        inActivity = true;
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(4898);
        /*
        if(mBundle!=null) {
            messages = mBundle.getParcelableArrayList("MS");
            chatRecyclerAdapter = new ChatRecyclerAdapter(this,messages);
        }
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(this, "asd", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSocket.getSocket().disconnect();
        mSocket.getSocket().close();
    }
}
