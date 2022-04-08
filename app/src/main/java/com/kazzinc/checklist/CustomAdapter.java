package com.kazzinc.checklist;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import static android.content.Context.MODE_PRIVATE;

public class CustomAdapter extends BaseAdapter {
    SharedPreferences sPref;
    String[] names;
    Context context;
    LayoutInflater inflter;
    String value;

    public CustomAdapter(Context context, String[] names) {
        this.context = context;
        this.names = names;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.checkbox_row, null);
        final CheckedTextView simpleCheckedTextView = (CheckedTextView) view.findViewById(R.id.simpleCheckedTextView);
        simpleCheckedTextView.setText(names[position]);

        simpleCheckedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (simpleCheckedTextView.isChecked()) {

                    simpleCheckedTextView.setCheckMarkDrawable(0);
                    simpleCheckedTextView.setChecked(false);
                    sPref = context.getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
                    String checkItemText = sPref.getString("CheckItemText","");
                    checkItemText.replace(simpleCheckedTextView.getText() + ";  ", "");
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("CheckItemText", String.valueOf(checkItemText));
                    ed.commit();

                } else {

                    //simpleCheckedTextView.setCheckMarkDrawable(R.drawable.checked_item_24);
                    simpleCheckedTextView.setChecked(true);

                    sPref = context.getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
                    String checkItemText = sPref.getString("CheckItemText","");
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("CheckItemText", String.valueOf(checkItemText+simpleCheckedTextView.getText() + "; "));
                    ed.commit();
                }
            }
        });
        return view;
    }
}