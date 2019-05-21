package com.example.profile;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class GradesFragment extends Fragment implements View.OnClickListener {

    Button btnSession;
    Button btnSemesters;

    public GradesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setActionBarTitle(getResources().getString(R.string.activity_main_drawer_grades));

        View v = inflater.inflate(R.layout.fragment_grades, container, false);

        btnSession = (Button) v.findViewById(R.id.btnSession);
        btnSemesters = (Button) v.findViewById(R.id.btnSemesters);

        btnSession.setOnClickListener(this);
        btnSemesters.setOnClickListener(this);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.chaildclGrades, new SessionChildFragment());
        ft.commit();
        btnSession.setBackgroundResource(R.color.colorPrimary);
        btnSemesters.setBackgroundResource(R.color.colorGray);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnSession) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.chaildclGrades, new SessionChildFragment());
            ft.commit();
            btnSession.setBackgroundResource(R.color.colorPrimary);
            btnSemesters.setBackgroundResource(R.color.colorGray);
        } else if (id == R.id.btnSemesters) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.chaildclGrades, new SemestersChildFragment());
            ft.commit();
            btnSemesters.setBackgroundResource(R.color.colorPrimary);
            btnSession.setBackgroundResource(R.color.colorGray);
        }
    }
}
