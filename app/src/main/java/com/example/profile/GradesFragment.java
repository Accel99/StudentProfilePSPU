package com.example.profile;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dbrequestclass.Grade;
import com.example.dbrequestclass.RequestAsyncTask;

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


        tlGrades = v.findViewById(R.id.tlGrades);

        fillList();

        int semester = 0;
        for (Grade grade : list) {
            if (grade.semester != semester) {
                semester = grade.semester;

                TableRow row = new TableRow(getActivity());
                row.setPadding(10, 20, 5, 10);

                TextView tv = new TextView(getActivity());

                tv.setTextSize(20);
                tv.setTextColor(Color.parseColor("#000000"));
                tv.setText(semester + " СЕМЕСТР ");
                tv.setTypeface(null, Typeface.BOLD);

                row.addView(tv);

                tlGrades.addView(row);
            }

            TableRow row = new TableRow(getActivity());
            row.setBackground(getResources().getDrawable(R.drawable.row_rounded));
            TableLayout.LayoutParams paramsTl = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            paramsTl.setMargins(2,2,2,2);
            row.setLayoutParams(paramsTl);

            TableRow.LayoutParams paramsTr;

            TextView tv1 = new TextView(getActivity());
            paramsTr = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.75f);
            tv1.setLayoutParams(paramsTr);
            tv1.setGravity(Gravity.CENTER_VERTICAL);
            tv1.setPadding(7, 10, 7, 10);
            tv1.setTextColor(Color.parseColor("#000000"));
            tv1.setTextSize(16);
            tv1.setText(grade.name);

            View view = new View(getActivity());
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            TableRow.LayoutParams paramsV = new TableRow.LayoutParams(3, TableRow.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(paramsV);

            TextView tv2 = new TextView(getActivity());
            paramsTr = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.25f);
            tv2.setLayoutParams(paramsTr);
            tv2.setGravity(Gravity.CENTER);
            tv2.setPadding(5, 3, 5, 3);
            tv2.setTextColor(Color.parseColor("#000000"));
            tv2.setTextSize(16);
            tv2.setText(grade.grade);

            row.addView(tv1);
            row.addView(view);
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

        TextView tv1 = new TextView(getActivity());
        tv1.setGravity(Gravity.CENTER_VERTICAL);
        tv1.setPadding(20, 3, 3, 3);
        tv1.setTextSize(14);
        tv1.setText("Дата выставления: " + grade.data);

        row1.addView(tv1);

        tlGrades.addView(row1, rowIndex + 1);

        TableRow row2 = new TableRow(getActivity());

        TextView tv2 = new TextView(getActivity());
        tv2.setGravity(Gravity.CENTER_VERTICAL);
        tv2.setPadding(20, 3, 3, 3);
        tv2.setTextSize(14);
        tv2.setText("Преподаватель: " + grade.teacher);

        row2.addView(tv2);

        tlGrades.addView(row2, rowIndex + 2);
    }

    private void fillList() {
        try {
            String query = "SELECT НазваниеДисциплины, Семестр, Оценка, Дата, Преподаватель FROM СтрокиПланов, Дисциплины, Оценки WHERE КодСтудента=" + studentId + " AND КодСтрокиПлана=СтрокиПланов.Код AND КодДисциплины=Дисциплины.Код ORDER BY Семестр";
            RequestAsyncTask request = new RequestAsyncTask();
            request.setQuery(query);
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
            } else {
                Toast.makeText(getActivity(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private class RequestSelectAsyncTask extends AsyncTask<Void, Void, ResultSet> {
//
//        final static String MYSQL_STR_CONN = "jdbc:mysql://db4free.net:3306/pspudb2?useSSL=false&serverTimezone=UTC&autoReconnect=true&failOverReadOnly=false";
//        final static String USERNAME = "accel999";
//        final static String PASS = "Foo5701478";
//
//        @Override
//        protected ResultSet doInBackground(Void... voids) {
//            Connection connection = null;
//            Statement statement = null;
//            ResultSet resultSet = null;
//
//            try {
//                Class.forName("com.mysql.jdbc.Driver").newInstance();
//
//                connection = DriverManager.getConnection(MYSQL_STR_CONN, USERNAME, PASS);
//                if (connection != null) {
//                    String query = "SELECT НазваниеДисциплины, Семестр, Оценка, Дата, Преподаватель FROM СтрокиПланов, Дисциплины, Оценки WHERE КодСтудента=" + studentId + " AND КодСтрокиПлана=СтрокиПланов.Код AND КодДисциплины=Дисциплины.Код ORDER BY Семестр";
//                    statement = connection.createStatement();
//                    resultSet = statement.executeQuery(query);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return resultSet;
//        }
//    }
}
