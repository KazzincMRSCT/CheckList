package com.kazzinc.checklist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnswerResultActivity extends AppCompatActivity {

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(this);

    SharedPreferences sPref;
    String UserId;
    private String UserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_result);

        Bundle arguments = getIntent().getExtras();

        String userId = "";
        String date ="";
        String workplace = "";

        try {
            userId = arguments.get("UserId").toString();
            date = arguments.get("Date").toString();
            workplace = arguments.get("WorkPlace").toString();
            getSupportActionBar().setTitle("Чек-лист:  " + workplace);
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }

        LinearLayout rContainer = (LinearLayout) findViewById(R.id.resultAnswerContainer);

        rContainer.removeAllViews();

        try {

            int id = 1;

            sqlLiteDatabase.open(this);
            String selectQuery = "SELECT AnswerId, QuesText, AnswerText, AnswerUserId, AnswerDate, AnswerShift, AnswerWorkPlaceName, AnswerQuesId, AnswerDateTime  FROM Answer INNER JOIN Question ON Answer.AnswerQuesId = Question.QuesId WHERE AnswerUserId='" + userId + "' AND Answerdate='" + date + "' AND AnswerWorkPlaceName='" + workplace + "' ORDER BY AnswerQuesId, AnswerDateTime";
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    final String AnsId = cursor.getString(0);
                    String quesText = cursor.getString(1);
                    String answerText = cursor.getString(2);
                    final String answerUserId = cursor.getString(3);
                    final String answerDate = cursor.getString(4);
                    final String answerShift = cursor.getString(5);
                    final String answerWorkPlaceName = cursor.getString(6);
                    final int answerQuesId = cursor.getInt(7);
                    final String answerDateTime = cursor.getString(8);

                    String ansColor = "#E1E2E5";
                    String textColor = "#E1E2E5";//"#979797";
                    String bgColor = "#444446";
                    //int isHaveAnswer = 1;
                    switch (answerText)
                    {
                        case "Хорошо":
                            ansColor="#7DCEA0";//"#28B463";
                            break;
                        case "Плохо":
                            ansColor="#F1948A";//"#E74C3C";
                            break;
                        case "Неприменимо":
                            ansColor="#979797";
                            //bgColor ="#979797";
                            //textColor = "#444446";
                            //isHaveAnswer = 0;
                            break;
                    }

                    String sBegin="";
                    String sEnd="";
                    boolean isRepeat = false;
                    try {
                        cursor.moveToNext();
                        int answerQuesIdNext = cursor.getInt(7);
                        cursor.moveToPrevious();
                        if (answerQuesId==answerQuesIdNext) {
                            sBegin="<s>";
                            sEnd="</s>";
                            ansColor = textColor = "#979797";
                            isRepeat = true;
                        }
                    }
                    catch (Exception e){
                        Log.d("Alexey", e.getMessage());
                    }

                    final CardView cw = new CardView(this);
                    cw.setId(id);
                    //cw.setTransitionName(date + ";" + workplace);
                    cw.setCardBackgroundColor(Color.parseColor(bgColor));

                    cw.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(QuestionsActivityPSO.this, "Нажатие кнопки", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AnswerResultActivity.this, QuestionActivityEdit.class);

                            intent.putExtra("AnsId", AnsId);
                            intent.putExtra("UserId", answerUserId);
                            intent.putExtra("Date", answerDate);
                            intent.putExtra("WorkPlace", answerWorkPlaceName);
                            intent.putExtra("Shift", answerShift);
                            intent.putExtra("QuestId", answerQuesId);
                            finish();
                            startActivity(intent);
                        }
                    });

                    LinearLayout ll = new LinearLayout(this);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.setPadding(20,20,20,20);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(10, 10, 10, 0);

                    TextView twDate = new TextView(this);
                    //twDate.setText(Html.fromHtml("<font color='#E1E2E5'>Вопрос " + answerQuesId + " :</font><font color='" + textColor + "'> "+quesText + "</font>"), TextView.BufferType.SPANNABLE);
                    twDate.setText(Html.fromHtml("<font color='" + textColor + "'> "+ sBegin + quesText + sEnd + "</font>"), TextView.BufferType.SPANNABLE);
                    twDate.setTypeface(ResourcesCompat.getFont(this, R.font.exo_2_light));
                    twDate.setTextSize(16);
                    ll.addView(twDate);

                    TextView twWorkplace = new TextView(this);
                    twWorkplace.setText(Html.fromHtml("<font color='" + ansColor + "'> " + sBegin + answerText + sEnd + "</font>"), TextView.BufferType.SPANNABLE);
                    twWorkplace.setTypeface(ResourcesCompat.getFont(AnswerResultActivity.this, R.font.cuprum));
                    twWorkplace.setTextSize(16);
                    ll.addView(twWorkplace);

                    TextView twDateTime = new TextView(this);
                    twDateTime.setText(Html.fromHtml("<font color='#979797'> " + answerDateTime + "</font>"), TextView.BufferType.SPANNABLE);
                    twDateTime.setTypeface(ResourcesCompat.getFont(AnswerResultActivity.this, R.font.exo_2_light));
                    twDateTime.setTextSize(12);
                    ll.addView(twDateTime);

                    cw.addView(ll);
                    rContainer.addView(cw, layoutParams);

                    id++;

                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey",e.getMessage());
        }
    }

    private void loadUserInfo()
    {
        sPref = getSharedPreferences("CheckList", MODE_MULTI_PROCESS);
        UserId = sPref.getString("UserId","");
        String userName = sPref.getString("UserName","");
        UserRole = sPref.getString("UserRole","");
        getSupportActionBar().setTitle(userName);
    }
}
