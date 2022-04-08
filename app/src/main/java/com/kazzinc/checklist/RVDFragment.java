package com.kazzinc.checklist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class RVDFragment extends Fragment {

    private RVDViewModel mViewModel;

    public static RVDFragment newInstance() {
        return new RVDFragment();
    }

    private SqlLiteDatabase sqlLiteDatabase = new SqlLiteDatabase(getActivity());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.r_v_d_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RVDViewModel.class);
        // TODO: Use the ViewModel

        GetData();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("ResourceAsColor")
    private void GetData()
    {
        LinearLayout rContainer = (LinearLayout) getView().findViewById(R.id.resultContainerRVD);
        rContainer.removeAllViews();

        try {
            int id = 1;

            sqlLiteDatabase.open(this.getActivity());
            String selectQuery = "SELECT DateEvent, Equipment, ifnull(OldNumber,0), ifnull(NewNumber,0), ifnull(MotoHours,0), ifnull(SpecialHours,0), ifnull(Place,''), ifnull(Reason,'') FROM RVD WHERE Deleted IS NULL or Deleted=0 ORDER BY DateEvent DESC";
            Log.d("Alexey", "RVD() selectQuery: " + selectQuery);
            Cursor cursor = sqlLiteDatabase.database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    final String date = cursor.getString(0);
                    String[] splitedHour = date.split("\\s+");
                    String[] splitedDate = splitedHour[0].split("-");
                    final String Date = splitedDate[2]+"."+splitedDate[1]+"."+splitedDate[0]+" " + splitedHour[1].split(":")[0] + ":" + splitedHour[1].split(":")[1];
                    final String Equipment = cursor.getString(1);
                    final String OldNumber = cursor.getString(2);
                    final String NewNumber = cursor.getString(3);
                    final String MotoHours = cursor.getString(4);
                    final String SpecialHours = cursor.getString(5);
                    final String Place = cursor.getString(6);
                    final String Reason = cursor.getString(7);

                    String ansColor = "#E1E2E5";
                    String bgColor = "#444446";

                    final CardView cw = new CardView(this.getActivity());
                    cw.setId(id);

                    cw.setCardBackgroundColor(Color.parseColor(bgColor));

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(0, 20, 0, 0);

                    LinearLayout vl = new LinearLayout(this.getActivity());
                    vl.setOrientation(LinearLayout.VERTICAL);

                    TableLayout table = new TableLayout(this.getActivity());
                    table.setStretchAllColumns(true);
                    table.setShrinkAllColumns(true);
                    table.setPadding(20,10,10,0);

                    TableRow rowDate = new TableRow(this.getActivity());
                    TableRow rowEq = new TableRow(this.getActivity());
                    TableRow rowOldNumber = new TableRow(this.getActivity());
                    TableRow rowNewNumber = new TableRow(this.getActivity());
                    TableRow rowMotoHoura = new TableRow(this.getActivity());
                    TableRow rowSpecialHoura = new TableRow(this.getActivity());
                    TableRow rowPlace = new TableRow(this.getActivity());
                    TableRow rowReason = new TableRow(this.getActivity());

                    TextView twDate1 = new TextView(this.getActivity());
                    twDate1.setText(Html.fromHtml("<font color='#979797'>Дата/время</font>"), TextView.BufferType.SPANNABLE);
                    twDate1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twDate1.setTextSize(18);
                    twDate1.setHeight(50);

                    TextView twDate2 = new TextView(this.getActivity());
                    twDate2.setText(Html.fromHtml("<font color='" + ansColor + "'> "+ Date + "</font>"), TextView.BufferType.SPANNABLE);
                    twDate2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twDate2.setTextSize(16);

                    rowDate.addView(twDate1);
                    rowDate.addView(twDate2);

                    table.addView(rowDate);

                    TextView twEq1 = new TextView(this.getActivity());
                    twEq1.setText(Html.fromHtml("<font color='#979797'>Оборудование</font>"), TextView.BufferType.SPANNABLE);
                    twEq1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twEq1.setTextSize(18);
                    twEq1.setHeight(50);

                    TextView twEq2 = new TextView(this.getActivity());
                    twEq2.setText(Html.fromHtml("<font color='" + ansColor + "'> " + Equipment + "</font>"), TextView.BufferType.SPANNABLE);
                    twEq2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twEq2.setTextSize(16);

                    rowEq.addView(twEq1);
                    rowEq.addView(twEq2);

                    table.addView(rowEq);

                    TextView twOldNumber1 = new TextView(this.getActivity());
                    twOldNumber1.setText(Html.fromHtml("<font color='#979797'>№ бирки снятого РВД</font>"), TextView.BufferType.SPANNABLE);
                    twOldNumber1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twOldNumber1.setTextSize(18);
                    twOldNumber1.setHeight(50);


                    TextView twOldNumber2 = new TextView(this.getActivity());
                    twOldNumber2.setText(Html.fromHtml("<font color='" + ansColor + "'> " + OldNumber + "</font>"), TextView.BufferType.SPANNABLE);
                    twOldNumber2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twOldNumber2.setTextSize(18);

                    rowOldNumber.addView(twOldNumber1);
                    rowOldNumber.addView(twOldNumber2);

                    table.addView(rowOldNumber);

                    TextView twNewNumber1 = new TextView(this.getActivity());
                    twNewNumber1.setText(Html.fromHtml("<font color='#979797'>№ бирки нового РВД</font>"), TextView.BufferType.SPANNABLE);
                    twNewNumber1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twNewNumber1.setTextSize(18);
                    twNewNumber1.setHeight(50);


                    TextView twNewNumber2 = new TextView(this.getActivity());
                    twNewNumber2.setText(Html.fromHtml("<font color='" + ansColor + "'> " + NewNumber + "</font>"), TextView.BufferType.SPANNABLE);
                    twNewNumber2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twNewNumber2.setTextSize(18);

                    rowNewNumber.addView(twNewNumber1);
                    rowNewNumber.addView(twNewNumber2);

                    table.addView(rowNewNumber);

                    TextView twMotoHoura1 = new TextView(this.getActivity());
                    twMotoHoura1.setText(Html.fromHtml("<font color='#979797'>Моточасы</font>"), TextView.BufferType.SPANNABLE);
                    twMotoHoura1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twMotoHoura1.setTextSize(18);
                    twMotoHoura1.setHeight(50);

                    TextView twMotoHoura2 = new TextView(this.getActivity());
                    twMotoHoura2.setText(Html.fromHtml("<font color='" + ansColor + "'> " + MotoHours + "</font>"), TextView.BufferType.SPANNABLE);
                    twMotoHoura2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twMotoHoura2.setTextSize(18);

                    rowMotoHoura.addView(twMotoHoura1);
                    rowMotoHoura.addView(twMotoHoura2);

                    table.addView(rowMotoHoura);

                    TextView twSpecialHoura1 = new TextView(this.getActivity());
                    twSpecialHoura1.setText(Html.fromHtml("<font color='#979797'>Спецчасы</font>"), TextView.BufferType.SPANNABLE);
                    twSpecialHoura1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twSpecialHoura1.setTextSize(18);
                    twSpecialHoura1.setHeight(50);


                    TextView twSpecialHoura2 = new TextView(this.getActivity());
                    twSpecialHoura2.setText(Html.fromHtml("<font color='" + ansColor + "'> " + SpecialHours + "</font>"), TextView.BufferType.SPANNABLE);
                    twSpecialHoura2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twSpecialHoura2.setTextSize(18);

                    rowSpecialHoura.addView(twSpecialHoura1);
                    rowSpecialHoura.addView(twSpecialHoura2);

                    table.addView(rowSpecialHoura);

                    TextView twPlace1 = new TextView(this.getActivity());
                    twPlace1.setText(Html.fromHtml("<font color='#979797'>Место замены</font>"), TextView.BufferType.SPANNABLE);
                    twPlace1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twPlace1.setTextSize(18);
                    twPlace1.setHeight(50);


                    TextView twPlace2 = new TextView(this.getActivity());
                    twPlace2.setText(Html.fromHtml("<font color='" + ansColor + "'> " + Place + "</font>"), TextView.BufferType.SPANNABLE);
                    twPlace2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twPlace2.setTextSize(18);

                    rowPlace.addView(twPlace1);
                    rowPlace.addView(twPlace2);

                    table.addView(rowPlace);

                    TextView twReason1 = new TextView(this.getActivity());
                    twReason1.setText(Html.fromHtml("<font color='#979797'>Причина замены</font>"), TextView.BufferType.SPANNABLE);
                    twReason1.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twReason1.setTextSize(18);
                    twReason1.setHeight(50);

                    TextView twReason2 = new TextView(this.getActivity());
                    twReason2.setText(Html.fromHtml("<font color='" + ansColor + "'> " + Reason + "</font>"), TextView.BufferType.SPANNABLE);
                    twReason2.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.cuprum));
                    twReason2.setTextSize(18);

                    rowReason.addView(twReason1);
                    rowReason.addView(twReason2);
                    table.addView(rowReason);

                    vl.addView(table);

                    LinearLayout.LayoutParams layoutLine = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutLine.setMargins(20, 20, 20, 0);

                    TextView line = new TextView(this.getContext());
                    line.setHeight(2);
                    line.setGravity(Gravity.CENTER_HORIZONTAL);
                    line.setBackgroundColor(Color.parseColor("#909497"));
                    vl.addView(line, layoutLine);

                    LinearLayout.LayoutParams lpDel = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                    LinearLayout hlDel = new LinearLayout(this.getActivity());

                    hlDel.setOrientation(LinearLayout.HORIZONTAL);
                    hlDel.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
                    hlDel.setPadding(0,-10,20,-10);

                    Button btnDelete = new Button(this.getActivity());

                    btnDelete.setLayoutParams(lpDel);
                    btnDelete.setMinHeight(0);
                    btnDelete.setMinWidth(0);
                    btnDelete.setText("Удалить");
                    btnDelete.setTextSize(16);
                    btnDelete.setAllCaps(false);

                    btnDelete.setClickable(true);
                    btnDelete.setFocusable(true);
                    btnDelete.setHapticFeedbackEnabled(true);
                    btnDelete.setTextColor(Color.parseColor("#FC7E7E"));
                    btnDelete.setTypeface(ResourcesCompat.getFont(this.getActivity(), R.font.exo_2_light));//setBackgroundColor(Color.TRANSPARENT);//getBackground().setColorFilter(ContextCompat.getColor(this.getActivity(), R.color.colorDarkLight1), PorterDuff.Mode.MULTIPLY);
                    TypedValue outValue = new TypedValue();
                    getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    btnDelete.setBackgroundResource(outValue.resourceId);

                    btnDelete.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Vibrator vibro = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 500 milliseconds
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibro.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                vibro.vibrate(150);
                            }

                            showDeleteItemDialog(getContext(), date);
                        }

                    });

                    hlDel.addView(btnDelete);

                    vl.addView(hlDel);

                    cw.addView(vl);

                    rContainer.addView(cw, layoutParams);

                    id++;

                } while (cursor.moveToNext());
            }
            sqlLiteDatabase.close();
        }
        catch (Exception e){
            Log.d("Alexey", "GetData() Error: " + e.getMessage());
        }
    }

    private void showDeleteItemDialog(Context c, final String date) {
        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.MyAlertDialogTheme))
                .setTitle("Подтвердите")
                .setMessage("Удалить запись?")

                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqlLiteDatabase.open(getContext());
                        String updateQuery = "UPDATE RVD SET Deleted=1, SendToServer=0 WHERE DateEvent='" + date + "'";
                        sqlLiteDatabase.database.execSQL(updateQuery);
                        sqlLiteDatabase.close();
                        GetData();
                        saveNeedLoadParam(9);
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        dialog.show();
    }

    private void saveNeedLoadParam(int needLoad)
    {
        SharedPreferences preferences = this.getActivity().getSharedPreferences("CheckList", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString("NeedLoad", String.valueOf(needLoad));
        ed.commit();
    }

}