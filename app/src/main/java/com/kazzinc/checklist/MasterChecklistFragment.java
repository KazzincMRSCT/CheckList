package com.kazzinc.checklist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class  MasterChecklistFragment extends Fragment {

    private MasterChecklistViewModel mViewModel;

    private String QuesType;
    SharedPreferences sPref;
    private String Shift;
    String formattedDate;
    String UserId;
    private String UserRole;

    public static MasterChecklistFragment newInstance() {
        return new MasterChecklistFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.master_checklist_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MasterChecklistViewModel.class);
        // TODO: Use the ViewModel

        Shift = "1";

        Date c = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(c);


        int h = c.getHours();
        if ((h >= 6) && (h < 18))
            Shift = "2";
        if (h < 6) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            c = cal.getTime();
        }

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        formattedDate = df.format(c);

        loadUserInfo();

        CardView cardWorkPlace = (CardView) getView().findViewById(R.id.cardWorkPlace);
        CardView cardGCOM = (CardView) getView().findViewById(R.id.cardGCOM);
        CardView cardPNVR = (CardView) getView().findViewById(R.id.cardPNVR);

        // button on click listener
        View.OnClickListener handler = new View.OnClickListener() {
            public void onClick(View v) {

                Vibrator vibro = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibro.vibrate(30);

                switch (v.getId()) {

                    case R.id.cardWorkPlace:
                        QuesType = "5";
                        saveUserInfo();
                        try {
                            Intent intentWorkPlace = new Intent(getActivity(), WorkPlaceSelect.class);
                            //finish ();
                            startActivity(intentWorkPlace);
                        } catch (Exception e) {
                            Log.d("Alexey", e.getMessage());
                        }
                        break;
                    case R.id.cardGCOM:
                        QuesType = "6";
                        saveUserInfo();
                        try {
                            Intent intentWorkPlace = new Intent(getActivity(), WorkPlaceSelect.class);
                            //finish ();
                            startActivity(intentWorkPlace);
                        } catch (Exception e) {
                            Log.d("Alexey", e.getMessage());
                        }
                        break;
                    case R.id.cardPNVR:
                        saveUserInfo();
                        try {
                            Intent intentWorkPlace = new Intent(getActivity(), Pnvr1Activity.class);
                            //finish ();
                            startActivity(intentWorkPlace);
                        } catch (Exception e) {
                            Log.d("Alexey", e.getMessage());
                        }
                        break;
                }
            }
        };

        cardWorkPlace.setOnClickListener(handler);
        cardGCOM.setOnClickListener(handler);
        cardPNVR.setOnClickListener(handler);
    }

    private void saveUserInfo()
    {
        sPref = getContext().getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("UserDate", formattedDate);
        ed.putInt("UserShift", Integer.valueOf(Shift));
        ed.putString("QuesType", QuesType);
        ed.commit();
    }

    private void loadUserInfo()
    {
        sPref = getContext().getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        String userName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");
    }

}
