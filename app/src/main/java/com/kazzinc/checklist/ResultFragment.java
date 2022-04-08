package com.kazzinc.checklist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ResultFragment extends Fragment {

    private ResultViewModel mViewModel;
    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(getActivity());

    String UserId;
    private String UserRole;
    SharedPreferences sPref;

    public static ResultFragment newInstance() {
        return new ResultFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.result_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ResultViewModel.class);
        // TODO: Use the ViewModel

        Do();
    }

    @Override
    public void onResume() {
        super.onResume();

        Do();
    }

    private void loadUserInfo()
    {
        sPref = getContext().getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        String userName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");
    }

    public void Do () {

        loadUserInfo();

        LinearLayout rContainer = (LinearLayout) getView().findViewById(R.id.resultContainer);

        rContainer.removeAllViews();

        String selectQuery = "SELECT DISTINCT AnswerDate, AnswerWorkPlaceName, CASE (SELECT QuesType From Question WHERE QuesId=AnswerQuesId) WHEN 1 THEN 'Проверка рабочего места' WHEN 2 THEN 'Проверка оборудования' WHEN 3 THEN 'ПСО-1' WHEN 4 THEN 'ПСО-6' END QuesType, (SELECT QuesType From Question WHERE QuesId=AnswerQuesId) as QuesTypeId FROM Answer WHERE AnswerUserId=" + UserId;

        sqlLiteDatabase.open(getActivity());

        try {
            int id = 1;

            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);
            if(null != cursor) {
                if (cursor.moveToFirst()) {
                    do {
                        String date = cursor.getString(0);
                        String workplace = cursor.getString(1);
                        String qType = cursor.getString(2);
                        String qTypeId = cursor.getString(3);

                        int isHaveEmpty = 0;

                        /*Дополнительный запрос на проверку пропущенных вопросов*/
                        String selectQuery1 = "SELECT count(AnswerText) FROM Answer WHERE AnswerUserId='" + UserId + "' AND AnswerText='---' AND AnswerWorkPlaceName='" + workplace +  "' AND AnswerDate='" + date + "' AND AnswerQuesId IN (SELECT QuesId FROM Question WHERE QuesType='" + qTypeId + "')";

                        Cursor cursor1 = sqlLiteDatabase.database.rawQuery(selectQuery1, null);
                        if (null != cursor1) {
                            if (cursor1.moveToFirst()) {
                                do {
                                    isHaveEmpty =  cursor1.getInt(0);
                                } while (cursor1.moveToNext());
                            }
                        }
                        /////

                        final CardView cw = new CardView(getActivity());
                        cw.setId(id);
                        cw.setTransitionName(date + ";" + workplace);



                        cw.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(QuestionsActivityPSO.this, "Нажатие кнопки", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getContext(), AnswerResultActivity.class);

                                String[] separated = cw.getTransitionName().split(";");

                                intent.putExtra("UserId", UserId);
                                intent.putExtra("Date", separated[0]);
                                intent.putExtra("WorkPlace", separated[1]);

                                startActivity(intent);
                                //intent.setDataAndType(Uri.parse(path + iv.getTransitionName()), "image/*");
                            }

                        });

                        LinearLayout ll = new LinearLayout(getActivity());
                        ll.setOrientation(LinearLayout.VERTICAL);
                        ll.setPadding(20,20,20,20);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams.setMargins(0, 0, 0, 20);

                        TextView twDate = new TextView(getActivity());

                        twDate.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                        twDate.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.cuprum));
                        twDate.setText("Дата: " + date + " | Чек-лист: " + qType );
                        twDate.setTextSize(18);

                        TextView twWorkplace = new TextView(getActivity());
                        twWorkplace.setText(Html.fromHtml("<font color='#444446'>" + workplace + "</font>"));
                        twWorkplace.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.exo_2_light));
                        twWorkplace.setTextSize(18);


                        if (isHaveEmpty>0) {
                            cw.setCardBackgroundColor(getResources().getColor(R.color.colorDarkLight3));
                            twDate.setText(Html.fromHtml("<strong><font color='#88271D'>НЕ ЗАПОЛНЕН!</font></strong><font color='#444446'> Дата: " + date + " | Чек-лист: " + qType +"</font>"));
                        }
                        else {
                            cw.setCardBackgroundColor(getResources().getColor(R.color.colorDarkLight1));
                            twWorkplace.setText(Html.fromHtml("<font color='#E1E2E5'>" + workplace + "</font>"));
                            twDate.setTextColor(getResources().getColor(R.color.colorDarkLight3));
                        }

                        ll.addView(twDate);
                        ll.addView(twWorkplace);
                        cw.addView(ll);
                        rContainer.addView(cw, layoutParams);

                        id++;

                    } while (cursor.moveToNext());
                }
            }

            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

    }

}
