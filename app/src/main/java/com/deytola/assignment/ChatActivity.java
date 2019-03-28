package com.deytola.assignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    Button sendButton;
    EditText payloadInput;
    String myPayload;
    private boolean checkPublish;
    private static final String TAG = "MainActivity";
    ListView listView;
    MessageAdapter messageAdapter;
    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        payloadInput = findViewById(R.id.createPayload);
        sendButton = findViewById(R.id.sendPayload);
        listView = findViewById(R.id.message_view);
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages);
        listView.setAdapter(messageAdapter);

        final String topic = "seamfix/test";
        final int qos = 1;
        final String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883", clientId);


        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Successful connection

                    subscribe(client, topic, qos);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //unsuccessful connection
                    Log.d(TAG, "connection failed!");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


        sendButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                publish(client, clientId);

            }
        });

    }

    private void subscribe(MqttAndroidClient client, String topic, int qos) {
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    checkPublish = false;
                    Message subscriberMessage = new Message(myPayload, checkPublish);
                    Log.d(TAG, "subscription successful!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "subscription unsuccessful");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publish(MqttAndroidClient client, String clientId) {
        try {
            String topic = "seamfix/test";
            byte[] encodedPayload = new byte[0];
            myPayload = payloadInput.getText().toString();
            checkPublish = true;
            Message publisherMessage = new Message(myPayload, checkPublish);
            messageAdapter.addMessage(publisherMessage);
            if (myPayload.length() > 0) {
                encodedPayload = myPayload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
                Log.d(TAG, myPayload);
                payloadInput.getText().clear();
            }

            Log.d(TAG, clientId);
            Log.d(TAG, "Publish successful");


        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
}
