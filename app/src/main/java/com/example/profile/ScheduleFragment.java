package com.example.profile;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.parser.PspuHtmlParser;
import com.example.dbrequestclass.StudentInfo;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
//Класс отображения расписания занятий
public class ScheduleFragment extends Fragment {

    private PDFView pdfView;
    private StudentInfo studentInfo;

    //Идентификатор загрузки
    long downloadID;
    //Создание слушателя загрузки
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Если id совпадает с идентификатором нашей загрузки
            if (downloadID == id) {
                //Открыть документ
                openPdfDoc();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Установка заголовка в тулбаре
        ((MainActivity)getActivity()).setActionBarTitle(getResources().getString(R.string.activity_main_drawer_schedule));

        View v = inflater.inflate(R.layout.fragment_schedule, container, false);

        pdfView = (PDFView) v.findViewById(R.id.pdfView);
        studentInfo = ((MainActivity)getActivity()).getStudentInfo();


        //Получение события об окончании загрузки
        getActivity().registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        //Кнопка Обновить
        Button btnUpdPdf = v.findViewById(R.id.btnUpdPdf);
        btnUpdPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(new File(Environment.getExternalStorageDirectory(), "pspudir").getAbsolutePath(), studentInfo.group);
                //Если файл существует
                if (file.exists()) {
                    //Удалить
                    file.delete();
                }
                //Скачать
                startDownloading();
            }
        });

        //Загрузка при открытии фрагмента
        File file = new File(new File(Environment.getExternalStorageDirectory(), "pspudir").getAbsolutePath(), studentInfo.group);
        //Если файла не существует
        if (!file.exists()) {
            //Скачать
            startDownloading();
        } else {
            //Открытие файла
            openPdfDoc();
        }

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Отмена регистрации слушетеля
        getActivity().unregisterReceiver(onDownloadComplete);
    }

    //Загрузка файла расписания группы в формате PDF
    private void startDownloading() {
        //Получение ссылки на файл
        String url = null;
        try {
            DownloadingAsyncTask downloadingAsyncTask = new DownloadingAsyncTask();
            downloadingAsyncTask.execute();
            url = downloadingAsyncTask.get();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
        }
        url = url.replace("edit#", "export?format=pdf&");

        //Настройка менеджера загрузок
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setTitle("Download PDF");
            request.setDescription("Downloading PDF file");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir("pspudir", studentInfo.group);

            //Запуск менеджера загрузок
            DownloadManager manager = (DownloadManager) ((MainActivity) getActivity()).getSystemService(Context.DOWNLOAD_SERVICE);
            downloadID = manager.enqueue(request);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
        }
    }

    //Открытие PDF файла с расписанием группы
    private void openPdfDoc() {
        File file = new File(new File(Environment.getExternalStorageDirectory(), "pspudir").getAbsolutePath(), studentInfo.group);
        pdfView.fromFile(file).load();
    }

    //Класс для фоновой обработки парсинга сайта PSPU
    class DownloadingAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String url = PspuHtmlParser.getDocScheduleGroupUrl(studentInfo.faculty, studentInfo.group);
            return url;
        }
    }

}
