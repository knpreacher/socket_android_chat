package com.example.knp.socketchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LGIIIIIIIIIIIIIIIIIIIII";
    EditText etLogin;
    EditText etRoom;
    Button btnLogin;
    TextView tvSAH;
    TextView tvv;
    CheckBox cbRM;
    String login;

    MSocket mSocket;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.right_in,R.anim.left_out);

        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<>();

        View page = inflater.inflate(R.layout.activity_main,null);

        etLogin = (EditText)page.findViewById(R.id.input_login);
        etRoom = (EditText)page.findViewById(R.id.input_room);
        tvSAH = (TextView)page.findViewById(R.id.tvSAH);
        cbRM = (CheckBox)page.findViewById(R.id.cbRM);
        tvv = (TextView)page.findViewById(R.id.textView);
        btnLogin = (Button)page.findViewById(R.id.btnLogin);

        pages.add(page);

        page = inflater.inflate(R.layout.reg_layout,null);
        pages.add(page);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        if(sp.getString("login","")!=null && sp.getString("room","")!=null){
            etLogin.setText(sp.getString("login",""));
            etRoom.setText(sp.getString("room",""));
            connect();
            cbRM.setChecked(true);
            btnLogin.setEnabled(true);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etRoom.getTextSize()>0 && etLogin.getTextSize()>0) {
                    connect();
                } else {
                    Toast.makeText(MainActivity.this, "Please input login and room", Toast.LENGTH_SHORT).show();
                }


            }
        });

        tvSAH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PrefActivity.class);

                startActivity(intent);

            }
        });

        VPAdapter adapter = new VPAdapter(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        setContentView(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvv.setText(sp.getString("address",""));
        Log.i(TAG, "onClick: sp.address"+sp.getString("address",""));
        Log.i(TAG, "onClick: sp.port"+sp.getString("port",""));
    }

    private void connect(){
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.show();
        if (cbRM.isChecked()) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("login", etLogin.getText().toString());
            editor.putString("room", etRoom.getText().toString());
            editor.commit();
        }
        try {
            Log.i(TAG, "onClick: sp.address" + sp.getString("address", ""));
            Log.i(TAG, "onClick: sp.port" + sp.getString("port", ""));

            mSocket = new MSocket();
            mSocket.connect("http://" + sp.getString("address", "") + ":8080");
            //mSocket.connect("http://192.168.0.222:8080");
            final JSONObject data = new JSONObject();
            data.put("login", etLogin.getText().toString());
            login = etLogin.getText().toString();
            data.put("room", etRoom.getText().toString());
            mSocket.getSocket().on("gon", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mSocket.getSocket().emit("checkMyData", data);
                }
            });
            mSocket.getSocket().on("welcome", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    intent.putExtra("room", etRoom.getText().toString());
                    intent.putExtra("login", etLogin.getText().toString());
                    intent.putExtra("count", (int) args[0]);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("activeLogin",etLogin.getText().toString());
                    editor.putString("activeRoom",etRoom.getText().toString());
                    editor.commit();
                    startActivity(intent);
                    pd.dismiss();
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
            pd.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
