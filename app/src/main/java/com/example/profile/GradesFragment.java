package com.example.profile;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GradesFragment extends Fragment {

    private int studentId=1;
    private List<Grade> list = new ArrayList<>();
    private TableLayout tlGrades;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setActionBarTitle(getResources().getString(R.string.activity_main_drawer_grades));

        View v = inflater.inflate(R.layout.fragment_grades, container, false);

        TableRow.LayoutParams paramsTr;

        tlGrades = v.findViewById(R.id.tlGrades);

        fillList();

        int semester = 0;
        for (Grade grade : list) {
            if (grade.semester != semester) {
                semester = grade.semester;

                TableRow row = new TableRow(getActivity());
                row.setPadding(10, 20, 5, 10);
                row.setBackgroundColor(Color.parseColor("#FFFFFF"));

                TextView tv = new TextView(getActivity());

                tv.setTextSize(20);
                tv.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tv.setTextColor(Color.parseColor("#000000"));
                tv.setText(semester + " cеместр ");

                row.addView(tv);

                tlGrades.addView(row);
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
            tv1.setText(grade.name);

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
            tv2.setText(grade.grade);

            row.addView(tv1);
            row.addView(tv2);
            row.setTag(grade);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int rowIndex = tlGrades.indexOfChild(v);
                    Grade grade = (Grade) tlGrades.getChildAt(rowIndex).getTag();
                    TableRow rowNext = (TableRow)tlGrades.getChildAt(rowIndex + 1);
                    if (rowNext == null || rowNext.getTag() == null || rowNext.getTag() instanceof Grade) {
                        addRowsHours(rowIndex, grade);
                    } else if (rowNext.getTag() instanceof Integer){
                        tlGrades.removeView(rowNext);
                        tlGrades.removeView(tlGrades.getChildAt(rowIndex + 1));
                    }
                }
            });

            tlGrades.addView(row);
        }

        return v;
    }

    private void addRowsHours(int rowIndex, Grade grade) {

        TableRow row1 = new TableRow(getActivity());
        row1.setTag(0);
        row1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        TextView tv1 = new TextView(getActivity());
        tv1.setGravity(Gravity.CENTER_VERTICAL);
        tv1.setPadding(20, 3, 3, 3);
        tv1.setTextSize(14);
        tv1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tv1.setText("Дата выставления: " + grade.data);
        row1.addView(tv1);
        tlGrades.addView(row1, rowIndex + 1);

        TableRow row2 = new TableRow(getActivity());
        row2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        TextView tv2 = new TextView(getActivity());
        tv2.setGravity(Gravity.CENTER_VERTICAL);
        tv2.setPadding(20, 3, 3, 3);
        tv2.setTextSize(14);
        tv2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tv2.setText("Преподаватель: " + grade.teacher);
        row2.addView(tv2);
        tlGrades.addView(row2, rowIndex + 2);
    }

    private void fillList() {
        try {
            RequestSelectAsyncTask request = new RequestSelectAsyncTask();
            request.execute();
            ResultSet result = request.get();
            if (result != null) {
                list.clear();
                while (result.next()) {
                    Grade grade = new Grade();
                    grade.name = result.getString("НазваниеДисциплины");
                    grade.semester = result.getInt("Семестр");
                    grade.grade = result.getString("Оценка");
                    grade.data = result.getString("Дата");
                    grade.teacher = result.getString("Преподаватель");
                    list.add(grade);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Grade {
        String name;
        int semester;
        String grade;
        String data;
        String teacher;
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
                    String query = "SELECT НазваниеДисциплины, Семестр, Оценка, Дата, Преподаватель FROM СтрокиПланов, Дисциплины, Оценки WHERE КодСтудента=" + studentId + " AND КодСтрокиПлана=СтрокиПланов.Код AND КодДисциплины=Дисциплины.Код ORDER BY Семестр";
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(query);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultSet;
        }
    }
}
