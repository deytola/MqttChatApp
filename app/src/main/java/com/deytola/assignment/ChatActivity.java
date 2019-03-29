package com.deytola.assignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
    private static final String TAG = MainActivity.class.getSimpleName();
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
        Intent intent = getIntent();
        final String newTopic = intent.getStringExtra(MainActivity.chatTopic); //create topic to subscribe to from userId entered on MainActivity


        final int qos = 1;   // qos is chosen to be 1 so messages are delivered at least once
        final String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883", clientId);

        try {

            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    // After successful connection, subscribe to specified topic on broker

                    subscribe(client, newTopic, qos);

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
            // publish new message
            public void onClick(View v) {
                publish(client, newTopic);

            }
        });

        // remove long pressed message
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                messageAdapter.removeMessage(position);
                return true;
            }
        });

    }

    private void subscribe(MqttAndroidClient client, String topic, int qos) {
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

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

    private void publish(MqttAndroidClient client, String topic) {
        try {

            byte[] encodedPayload = new byte[0];
            myPayload = payloadInput.getText().toString();

            Message publisherMessage = new Message(myPayload, true);
            messageAdapter.addMessage(publisherMessage);

            Message subscriberMessage = new Message(myPayload, false);
            messageAdapter.addMessage(subscriberMessage);

            if (myPayload.length() > 0) {
                encodedPayload = myPayload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
                payloadInput.getText().clear();
                Log.d(TAG, "Publish successful");
            }



        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }


}
