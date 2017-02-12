package com.example.knp.socketchat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import io.socket.emitter.Emitter;

/**
 * Created by knp on 2/8/17.
 */

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "LOGIIIIIIIIIIIIII";
    RecyclerView recyclerView;
    ChatRecyclerAdapter chatRecyclerAdapter;
    Intent intent;
    Button btnSend;
    EditText etMessage;
    ArrayList<Message> messages;
    MSocket mSocket;
    Toolbar toolbar;

    String se = "Online: ";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        intent = getIntent();
        Log.i(TAG, "onCreate: "+intent.getStringExtra("login"));
        mSocket = new MSocket();

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

        messages = new ArrayList<>();
        chatRecyclerAdapter = new ChatRecyclerAdapter(this,messages);

        mSocket.getSocket().on("newMes", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    Log.i(TAG, "call: newMes calld : "+args[0]);
                    JSONObject data = new JSONObject(args[0].toString());
                    Log.i(TAG, "call: 1" );
                    Message m = new Message(1);
                    m.setSender(data.getString("login"));
                    m.setText(data.getString("text"));
                    m.setTime(data.getString("time"));
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
                    Log.i(TAG, "call: 24442 "+ args[0]);
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
                    Log.i(TAG, "call: 34443 "+ args[0]);
                    messages.add(m);
                    update(data.getInt("count"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMessage.getTextSize()>0) {
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
}