package com.kazzinc.checklist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

public class equipmentType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_type);
        ListView list = (ListView) findViewById(R.id.list);

        // получаем ресурс
        final String[] spisok = getResources().getStringArray(R.array.equipmentTypeOR);

        list.setAdapter(new ArrayAdapter<String>(this, R.layout.listtext, R.id.list_content, spisok));

        /*// создаем адаптер
        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, spisok);
        // устанавливаем для списка адаптер
        list.setAdapter(adapter);*/

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // по позиции получаем выбранный элемент
                String selectedItem = spisok[position];
                // установка текста элемента TextView
                Intent intent = new Intent(equipmentType.this, GSMEquipment.class);
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

                //adapter.getFilter().filter(text);

                return false;
            }
        });
    }
}