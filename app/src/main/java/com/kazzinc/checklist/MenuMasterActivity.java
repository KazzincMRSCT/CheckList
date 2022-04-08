package com.kazzinc.checklist;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ScrollView;

public class MenuMasterActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView bottomNavigationView;
    AuditFragment auditFragment = new AuditFragment();
    MasterChecklistFragment checklistFragment = new MasterChecklistFragment();
    ScrollView scrollView;

    SharedPreferences sPref;
    String UserId;
    private String UserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_master);

        scrollView = findViewById(R.id.sclollViewMaster);
        bottomNavigationView = findViewById(R.id.bottomMenuMaster);
        //bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.audit);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.userprofile);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadUserInfo();
    }

    private void loadUserInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        String userName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");
        getSupportActionBar().setTitle(userName);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.checkList:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, checklistFragment).commit();
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                return true;
            case R.id.audit:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, auditFragment).commit();
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                return true;
        }
        return false;
    }
}
