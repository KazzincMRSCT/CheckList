package com.kazzinc.checklist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DialogCheckboxActivity extends AppCompatActivity {

    String value;
    String[] listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_checkbox);

        /*//SearchableSpinner subAreaSpinner = (SearchableSpinner) findViewById(R.id.area_list_spinner);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_alert_dialog);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(DialogCheckboxActivity.this,
                R.array.pnvr_instruction_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        listItems = getResources().getStringArray(R.array.pnvr_instruction_tb);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                *//*if (spinner.getSelectedItem().equals("Инструкции ОТиТБ"))
                    listItems = getResources().getStringArray(R.array.pnvr_instruction_tb);
                if (spinner.getSelectedItem().equals("Рабочие инструкции"))
                    listItems = getResources().getStringArray(R.array.pnvr_insraction_ri);
                if (spinner.getSelectedItem().equals("ПВР и НШС"))
                    listItems = getResources().getStringArray(R.array.pnvr_instruction_pvr);*//*

                switch (position) {
                    case 0:
                        listItems = getResources().getStringArray(R.array.pnvr_instruction_tb);
                        break;
                    case 1:
                        listItems = getResources().getStringArray(R.array.pnvr_insraction_ri);
                        break;
                    case 2:
                        listItems = getResources().getStringArray(R.array.pnvr_instruction_pvr);
                        break;
                }


                // initiate a ListView
                ListView listView = (ListView) findViewById(R.id.listview_alert_dialog);
                // set the adapter to fill the data in ListView
                CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), listItems);
                listView.setAdapter(customAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });*/


    }
}


