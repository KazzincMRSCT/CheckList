package com.kazzinc.checklist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

public class GSMArea extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_s_m_area);
        setTitle("Выбор участка");

        ListView list = (ListView) findViewById(R.id.list);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listtext, R.id.list_content, getArea());
        list.setAdapter (adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(GSMArea.this, GSMEquipment.class);
                intent.putExtra("inputArea", selectedItem);
                startActivity(intent);
            }
        });

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SearchView sv = (SearchView) findViewById(R.id.search);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {

                adapter.getFilter().filter(text);

                return false;
            }
        });
    }

    private ArrayList<String> getArea() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT DISTINCT EquipmentArea FROM Equipment ORDER BY EquipmentArea";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                Intent intent = new Intent (GSMArea.this, MenuActivity.class);
                intent.putExtra("inputPage", "gsm");
                startActivity (intent);
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed()
    {
        Intent intent = new Intent (GSMArea.this, MenuActivity.class);
        intent.putExtra("inputPage", "gsm");
        startActivity (intent);
        finish();
    }

    public class ActivityName extends FragmentActivity {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .add(android.R.id.content, new GSMFragment()).commit();}
        }
    }

}
