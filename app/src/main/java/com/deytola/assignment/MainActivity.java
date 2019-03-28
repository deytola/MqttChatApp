package com.deytola.assignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    public static final String chatTopic = "com.deytola.assignment.topic";
    private static final String TAG = MainActivity.class.getSimpleName();
    EditText userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userId = findViewById(R.id.editText_userId);

    }

    public void proceedToChat(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        String topicToPass = userId.getText().toString();
        intent.putExtra(chatTopic, topicToPass);
        startActivity(intent);
    }
}
