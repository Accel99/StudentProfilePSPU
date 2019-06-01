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
import android.widget.Toast;

import com.example.adapter.CertificatesAdapter;
import com.example.dbrequestclass.Ordering;
import com.example.dbrequestclass.RequestAsyncTask;
import com.example.dbrequestclass.StudentInfo;

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

    private ListView listView;
    private List<Ordering> list = new ArrayList<>();
    private StudentInfo studentInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.activity_main_drawer_ordering_certificates));

        View v = inflater.inflate(R.layout.fragment_ordering_certificates, container, false);

        listView = v.findViewById(R.id.listView);

        Button btnPlace = v.findViewById(R.id.btnPlace);
        Button btnPension = v.findViewById(R.id.btnPension);
        Button btnCall = v.findViewById(R.id.btnCall);

        studentInfo = ((MainActivity)getActivity()).getStudentInfo();

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
            bundle.putInt("type", 2);
            fragment.setArguments(bundle);
            ft.replace(R.id.flOrderingPlace, fragment);
            ft.commit();
        } else if (id == R.id.btnPension) {
            bundle.putInt("type", 1);
            fragment.setArguments(bundle);
            ft.replace(R.id.flOrderingPension, fragment);
            ft.commit();
        } else if (id == R.id.btnCall) {
            bundle.putInt("type", 3);
            fragment.setArguments(bundle);
            ft.replace(R.id.flOrderingCall, fragment);
            ft.commit();
        }
    }

    public void loadList() {
        list.clear();

        try {
            String query = "SELECT ЗаказыСправок.Код, НазваниеТипа, Количество, Дата, Статус FROM ЗаказыСправок, ТипСправок, Статусы WHERE КодТипаСправки=ТипСправок.Код AND КодСтатуса=Статусы.Код AND КодСтудента=" + studentInfo.studentId + " ORDER BY ЗаказыСправок.Код DESC";
            RequestAsyncTask request = new RequestAsyncTask();
            request.setQuery(query);
            request.execute();
            ResultSet resultSet = request.get();
            if (resultSet != null) {
                while (resultSet.next()) {
                    Ordering ordering = new Ordering();
                    ordering.id = resultSet.getLong("Код");
                    ordering.type = resultSet.getString("НазваниеТипа");
                    ordering.count = resultSet.getInt("Количество");
                    ordering.date = resultSet.getString("Дата");
                    ordering.status = resultSet.getString("Статус");
                    list.add(ordering);
                }
            } else {
                Toast.makeText(getActivity(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        CertificatesAdapter adapter = new CertificatesAdapter(getActivity(), list);
        listView.setAdapter(adapter);
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
//                Class.forName("net.sourceforge.jtds.jdbc.Driver");
//
//                connection = DriverManager.getConnection(MYSQL_STR_CONN, USERNAME, PASS);
//                if (connection != null) {
//                    String query = "SELECT ЗаказыСправок.Код, НазваниеТипа, Количество, Дата, Статус FROM ЗаказыСправок, ТипСправок, Статусы WHERE КодТипаСправки=ТипСправок.Код AND КодСтатуса=Статусы.Код AND КодСтудента=" + studentInfo.studentId + " ORDER BY ЗаказыСправок.Код DESC";
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
