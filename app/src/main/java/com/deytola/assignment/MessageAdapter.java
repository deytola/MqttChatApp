package com.deytola.assignment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
    List<Message> messages = new ArrayList<>(); //data source of the list adapter
    Context context; //context

    // public constructor
    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();  // notification is required to render updated chat
    }

    @Override
    public int getCount() {
        return messages.size();
    } //returns total number of items in the list

    @Override
    public Object getItem(int i) {
        return messages.get(i);  // returns list item at specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Create single ListView row/template i.e chat bubble
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();

        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        Message message = messages.get(position);
        //  inflate the layout for each list row
        if (message.isUserPublisher()) { // message was created by user, so create a right side bubble
            convertView = messageInflater.inflate(R.layout.publish_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.pub_message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
        } else {
            convertView = messageInflater.inflate(R.layout.subscribe_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.sub_message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
        }
        return convertView;
    }

    private class MessageViewHolder {
        public TextView messageBody;
    }

    public void removeMessage(int position){
        messages.remove(position);
        notifyDataSetChanged();
    }
}
