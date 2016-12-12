package com.mytrackerapp.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.util.LinkedList;

public class CustomAdapter extends ArrayAdapter{
        LinkedList <newUserModel> modelItems = null;
        Context context;

    public CustomAdapter(Context context, LinkedList<newUserModel> resource) {
                super(context,R.layout.row,resource);
                this.context = context;
                this.modelItems = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                convertView = inflater.inflate(R.layout.row, parent, false);
                TextView name = (TextView) convertView.findViewById(R.id.textView1);
                CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
                /*cb.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        newUserModel user = (newUserModel) cb.getTag();
                        if(cb.isChecked())
                            user.setValue(0);
                        else
                            user.setValue(1);
                        if(user.getValue() == 1)
                            System.out.println(user.getName());

                    }
                });*/
            name.setText(modelItems.get(position).getName());
                if(modelItems.get(position).isChecked() == true) {
                        cb.setChecked(true);
                } else {
                        cb.setChecked(false);
                }
                return convertView;
        }
}

