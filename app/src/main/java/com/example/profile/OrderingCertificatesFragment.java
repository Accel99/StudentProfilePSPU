package com.example.profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderingCertificatesFragment extends Fragment {


    public OrderingCertificatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setActionBarTitle(getResources().getString(R.string.activity_main_drawer_ordering_certificates));
        return inflater.inflate(R.layout.fragment_ordering_certificates, container, false);
    }

}
