package com.example.profile;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurriculumFragment extends Fragment {

    private int planNum = 1;
    private List<PlanString> list = new ArrayList<>();
    private TableLayout tlCurriculum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setActionBarTitle(getResources().getString(R.string.activity_main_drawer_curriculum));

        View v = inflater.inflate(R.layout.fragment_curriculum, container, false);

        TableRow.LayoutParams paramsTr;

        tlCurriculum = v.findViewById(R.id.tlCurriculum);

        fillList();

        int semester = 0;
        for (PlanString ps : list) {
            if (ps.semester != semester) {
                semester = ps.semester;

                TableRow row = new TableRow(getActivity());
                row.setPadding(10, 20, 5, 10);
                row.setBackgroundColor(Color.parseColor("#FFFFFF"));

                TextView tv = new TextView(getActivity());

                tv.setTextSize(20);
                tv.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tv.setTextColor(Color.parseColor("#000000"));
                tv.setText(semester + " cеместр ");

                row.addView(tv);

                tlCurriculum.addView(row);
            }

            TableRow row = new TableRow(getActivity());

            TextView tv1 = new TextView(getActivity());
            paramsTr = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.8f);
            paramsTr.bottomMargin = 1;
            paramsTr.leftMargin = 1;
            paramsTr.rightMargin = 1;
            paramsTr.topMargin = 1;
            tv1.setLayoutParams(paramsTr);
            tv1.setBackgroundColor(Color.parseColor("#FFFFFF"));
            tv1.setGravity(Gravity.CENTER_VERTICAL);
            tv1.setPadding(3, 10, 3, 10);
            tv1.setTextSize(16);
            tv1.setText(ps.name);

            TextView tv2 = new TextView(getActivity());
            paramsTr = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.2f);
            paramsTr.bottomMargin = 1;
            paramsTr.leftMargin = 1;
            paramsTr.rightMargin = 1;
            paramsTr.topMargin = 1;
            tv2.setLayoutParams(paramsTr);
            tv2.setGravity(Gravity.CENTER_VERTICAL);
            tv2.setPadding(3, 3, 3, 3);
            tv2.setTextSize(16);
            tv2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            tv2.setText(ps.type);

            row.addView(tv1);
            row.addView(tv2);
            row.setTag(ps);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int rowIndex = tlCurriculum.indexOfChild(v);
                    PlanString ps = (PlanString)tlCurriculum.getChildAt(rowIndex).getTag();
                    TableRow rowNext = (TableRow)tlCurriculum.getChildAt(rowIndex + 1);
                    if (rowNext == null || rowNext.getTag() == null || rowNext.getTag() instanceof PlanString) {
                        addRowsHours(rowIndex, ps);
                    } else if (rowNext.getTag() instanceof Integer){
                        tlCurriculum.removeView(rowNext);
                        tlCurriculum.removeView(tlCurriculum.getChildAt(rowIndex + 1));
                        tlCurriculum.removeView(tlCurriculum.getChildAt(rowIndex + 1));
                    }
                }
            });

            tlCurriculum.addView(row);
        }

        return v;
    }

    private void addRowsHours(int rowIndex, PlanString ps) {

        TableRow row1 = new TableRow(getActivity());
        row1.setTag(0);
        row1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        TextView tv1 = new TextView(getActivity());
        tv1.setGravity(Gravity.CENTER_VERTICAL);
        tv1.setPadding(20, 3, 3, 3);
        tv1.setTextSize(14);
        tv1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tv1.setText("Аудиторная работа: " + (ps.hCount - ps.hIndCount) + " ч.");
        row1.addView(tv1);
        tlCurriculum.addView(row1, rowIndex + 1);

        TableRow row2 = new TableRow(getActivity());
        row2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        TextView tv2 = new TextView(getActivity());
        tv2.setGravity(Gravity.CENTER_VERTICAL);
        tv2.setPadding(20, 3, 3, 3);
        tv2.setTextSize(14);
        tv2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tv2.setText("Самостоятельная работа: " + ps.hIndCount + " ч.");
        row2.addView(tv2);
        tlCurriculum.addView(row2, rowIndex + 2);

        TableRow row3 = new TableRow(getActivity());
        row3.setBackgroundColor(Color.parseColor("#FFFFFF"));
        TextView tv3 = new TextView(getActivity());
        tv3.setGravity(Gravity.CENTER_VERTICAL);
        tv3.setPadding(20, 3, 3, 3);
        tv3.setTextSize(14);
        tv3.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tv3.setText("Всего: " + ps.hCount + " ч.");
        row3.addView(tv3);
        tlCurriculum.addView(row3, rowIndex + 3);
    }

    private void fillList() {
        try {
            RequestSelectAsyncTask request = new RequestSelectAsyncTask();
            request.execute();
            ResultSet result = request.get();
            if (result != null) {
                list.clear();
                while (result.next()) {
                    PlanString ps = new PlanString();
                    ps.name = result.getString("НазваниеДисциплины");
                    ps.optionaly = result.getInt("ПоВыбору");
                    ps.semester = result.getInt("Семестр");
                    ps.type = result.getString("ТипОтчетности");
                    ps.hCount = result.getInt("КолЧасов");
                    ps.hIndCount = result.getInt("КолСамЧасов");
                    list.add(ps);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class RequestSelectAsyncTask extends AsyncTask<Void, Void, ResultSet> {

        final static String MYSQL_STR_CONN = "jdbc:mysql://db4free.net:3306/pspudb2?useSSL=false&serverTimezone=UTC&autoReconnect=true&failOverReadOnly=false";

        final static String USERNAME = "accel999";
        final static String PASS = "Foo5701478";

        @Override
        protected ResultSet doInBackground(Void... voids) {
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();

                connection = DriverManager.getConnection(MYSQL_STR_CONN, USERNAME, PASS);
                if (connection != null) {
                    String query = "SELECT НазваниеДисциплины, ПоВыбору, Семестр, ТипОтчетности, КолЧасов, КолСамЧасов FROM СтрокиПланов, Дисциплины WHERE КодДисциплины=Дисциплины.Код AND НомерПлана=" + planNum + " ORDER BY Семестр";
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(query);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultSet;
        }
    }

    private class PlanString {
        String name;
        int optionaly;
        int semester;
        String type;
        int hCount;
        int hIndCount;
    }

}
