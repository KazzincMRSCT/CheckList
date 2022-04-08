package com.kazzinc.checklist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

public class GSMEquipment extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_s_m_equipment);

        setTitle("Выбор оборудования");

        String area = "";
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            area = "";
        } else {
            area = extras.getString("inputArea");
        }

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow32);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView list = (ListView) findViewById(R.id.lvGSMEquipment);

        adapter = new ArrayAdapter<String>(this, R.layout.listtext, R.id.list_content, getEquipment(area));
        list.setAdapter(adapter);

        final String finalArea = area;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(GSMEquipment.this, GSMReffil.class);
                intent.putExtra("inputEquipment", selectedItem.replace("№ ", "№"));
                intent.putExtra("inputArea", finalArea);
                startActivity(intent);
            }
        });

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

    private ArrayList<String> getEquipment(String area) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT EquipmentName FROM Equipment WHERE EquipmentArea='" + area +"' ORDER BY EquipmentName";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(0).replace("№", "№ "));
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
                Intent intent = new Intent (GSMEquipment.this, GSMArea.class);
                startActivity (intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed()
    {
        Intent intent = new Intent (GSMEquipment.this, GSMArea.class);
        startActivity (intent);
        finish();
    }
}


