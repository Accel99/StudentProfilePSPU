package com.example.profile;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.MyAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderingCertificatesFragment extends Fragment implements View.OnClickListener {

    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.activity_main_drawer_ordering_certificates));

        View v = inflater.inflate(R.layout.fragment_ordering_certificates, container, false);

        listView = v.findViewById(R.id.listView);

        Button btnPlace = v.findViewById(R.id.btnPlace);
        Button btnPension = v.findViewById(R.id.btnPension);
        Button btnCall = v.findViewById(R.id.btnCall);

        btnPlace.setOnClickListener(this);
        btnPension.setOnClickListener(this);
        btnCall.setOnClickListener(this);

        loadList();

        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        OrderingChildFragment fragment = new OrderingChildFragment();
        Bundle bundle = new Bundle();
        if (id == R.id.btnPlace) {
            bundle.putInt("type", 1);
            fragment.setArguments(bundle);
            ft.replace(R.id.flOrderingPlace, fragment);
            ft.commit();
        } else if (id == R.id.btnPension) {
            bundle.putInt("type", 0);
            fragment.setArguments(bundle);
            ft.replace(R.id.flOrderingPension, fragment);
            ft.commit();
        } else if (id == R.id.btnCall) {
            bundle.putInt("type", 2);
            fragment.setArguments(bundle);
            ft.replace(R.id.flOrderingCall, fragment);
            ft.commit();
        }
    }

    public void loadList() {
        List<String[]> list = new ArrayList<>();

        try {
            RequestSelectAsyncTask request = new RequestSelectAsyncTask();
            request.execute(((MainActivity) getActivity()).getStudentLogin());
            ResultSet resultSet = request.get();
            if (resultSet != null) {
                String type = null;
                String status = null;
                while (resultSet.next()) {
                    switch (resultSet.getInt("TypeCertificateId")) {
                        case 0:
                            type = "Справка в пенсионный фонд";
                            break;
                        case 1:
                            type = "Справка по месту требования";
                            break;
                        case 2:
                            type = "Справка-вызов";
                            break;
                    }
                    switch (resultSet.getInt("StatusId")) {
                        case 0:
                            status = "Отказано";
                            break;
                        case 1:
                            status = "В обработке";
                            break;
                        case 2:
                            status = "Готово";
                            break;
                    }

                    list.add(new String[] {type, resultSet.getString("Id"), status, resultSet.getString("DateOrdering")});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        MyAdapter adapter = new MyAdapter(getActivity(), list);
        listView.setAdapter(adapter);
    }

    private class RequestSelectAsyncTask extends AsyncTask<String, Void, ResultSet> {

        final static String MSSQL_STR_CONN = "jdbc:jtds:sqlserver://sql6007.site4now.net;database=DB_A48F50_Accel99;user=DB_A48F50_Accel99_admin;password=Foo5701478";

        @Override
        protected ResultSet doInBackground(String... studentId) {
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(MSSQL_STR_CONN);
                if (connection != null) {
                    String query = "SELECT Id, TypeCertificateId, StatusId, DateOrdering FROM Ordering WHERE StudentId=" + studentId[0] + " ORDER BY Id DESC";
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
