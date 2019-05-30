package com.example.profile;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderingChildFragment extends Fragment implements View.OnClickListener {

    private int typeId;
    private long studentId;
    private int count;

    RadioButton rb1;
    RadioButton rb2;
    RadioButton rb3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ordering_child, container, false);

        rb1 = v.findViewById(R.id.rb1);
        rb2 = v.findViewById(R.id.rb2);
        rb3 = v.findViewById(R.id.rb3);

        studentId = ((MainActivity)getActivity()).getStudentId();
        typeId = getArguments().getInt("type");

        Button btnOrder = v.findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnOrder) {

            if (rb1.isChecked()) count = 1;
            if (rb2.isChecked()) count = 2;
            if (rb3.isChecked()) count = 3;

            RequestInsertAsyncTask request = new RequestInsertAsyncTask();
            request.execute();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.hide(this);
            ft.commit();

            ((OrderingCertificatesFragment) getParentFragment()).loadList();
        }
    }

    private class RequestInsertAsyncTask extends AsyncTask<Void, Void, Void> {

        final static String MYSQL_STR_CONN = "jdbc:mysql://db4free.net:3306/pspudb2?useSSL=false&serverTimezone=UTC&autoReconnect=true&failOverReadOnly=false";
        final static String USERNAME = "accel999";
        final static String PASS = "Foo5701478";

        @Override
        protected Void doInBackground(Void... voids) {
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");

                connection = DriverManager.getConnection(MYSQL_STR_CONN, USERNAME, PASS);
                if (connection != null) {
                    String query = "INSERT INTO ЗаказыСправок (КодСтудента, КодТипаСправки, Количество, КодСтатуса) VALUES (" + studentId + ", " + typeId + ", " + count + ", 2)";
                    statement = connection.createStatement();
                    statement.execute(query);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
