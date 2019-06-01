package com.example.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dbrequestclass.RequestAsyncTask;
import com.example.dbrequestclass.StudentInfo;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.PublicClientApplication;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_STORAGE_CODE = 1000;
//    private boolean permissionStatus = false;

    private String studentIdAAD;
    private StudentInfo studentInfo;

    PublicClientApplication sampleApp;


    public StudentInfo getStudentInfo() { return studentInfo; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Проверка разрешений
        checkPermission();

        fillStudentInfoFromDB(getIntent().getStringExtra("studentInfoStr"));

        //Тулбар
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Меню
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Установка данных студента в меню
        View header = navigationView.getHeaderView(0);
        ((TextView)header.findViewById(R.id.tvStudentName)).setText(studentInfo.getFullName());
        ((TextView)header.findViewById(R.id.tvGroup)).setText("Группа: " + studentInfo.group);
        ((TextView)header.findViewById(R.id.tvFaculty)).setText("Факультет: " + studentInfo.faculty);
        ((TextView)header.findViewById(R.id.tvDirection)).setText(studentInfo.getDirection());

        //Показ фрагмента главной страницы
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flMain, new HomeFragment());
        ft.commit();
        navigationView.setCheckedItem(R.id.amdHome);

        sampleApp = AuthActivity.getSampleApp();
    }

    private void fillStudentInfoFromDB(String studentJsonStr) {
        try {
            JSONObject studentJson = new JSONObject(studentJsonStr);
            studentIdAAD = studentJson.getString("id");

//            RequestSelectAsyncTask request = new RequestSelectAsyncTask();
//            request.execute();
//            ResultSet result = request.get();

            String query = "SELECT Студенты.Код, Фамилия, Имя, Отчество, НазваниеГруппы, НазваниеСпециальности, НазваниеПрофиля, НазваниеФакультета, Группы.НомерПлана " +
                    "FROM Студенты, Группы, Специальности, Факультеты " +
                    "WHERE Студенты.КодГруппы=Группы.Код AND Группы.КодСпециальности=Специальности.Код AND Специальности.КодФакультета=Факультеты.Код AND Идентификатор='" + studentIdAAD + "'";
            RequestAsyncTask request = new RequestAsyncTask();
            request.setQuery(query);
            request.execute();
            ResultSet result = request.get();

            studentInfo = new StudentInfo();
            if (result != null) {
                result.next();
                studentInfo.studentId = result.getLong("Студенты.Код");
                studentInfo.firstname = result.getString("Имя");
                studentInfo.lastname = result.getString("Фамилия");
                studentInfo.middlename = result.getString("Отчество");
                studentInfo.group = result.getString("НазваниеГруппы");
                studentInfo.faculty = result.getString("НазваниеФакультета");
                studentInfo.specialty = result.getString("НазваниеСпециальности");
                studentInfo.profileName = result.getString("НазваниеПрофиля");
                studentInfo.planNum = result.getLong("Группы.НомерПлана");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Установка заголовка фрагментов
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Обработка нажатий пунктов меню
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.amdHome) {
            //Главная
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain, new HomeFragment());
            ft.commit();
        } else if (id == R.id.amdCurriculum) {
            //Учебный план
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain, new CurriculumFragment());
            ft.commit();
        } else if (id == R.id.amdSchedule) {
            //Расписание занятий
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain, new ScheduleFragment());
            ft.commit();
        } else if (id == R.id.amdGrades) {
            //Оценки
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain, new GradesFragment());
            ft.commit();
        } else if (id == R.id.amdOrderingCertificates) {
            //Заказ справок
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMain, new OrderingCertificatesFragment());
            ft.commit();
        } else if (id == R.id.amdExit) {
            onExitClicked();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Проверка разрешений
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_STORAGE_CODE);
            } else {
//                permissionStatus = true;
            }
        } else {
//            permissionStatus = true;
        }
    }

    //Переопределенный метод для проверки разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    permissionStatus = true;
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//                    permissionStatus = false;
                }
        }
    }

    /* Clears an account's tokens from the cache.
     * Logically similar to "sign out" but only signs out of this app.
     * User will get interactive SSO if trying to sign back-in.
     */
    private void onExitClicked() {
        /* Attempt to get a user and acquireTokenSilent
         * If this fails we do an interactive request
         */
        sampleApp.getAccounts(new PublicClientApplication.AccountsLoadedCallback() {
            @Override
            public void onAccountsLoaded(final List<IAccount> accounts) {
                if (accounts.isEmpty()) {
                    /* No accounts to remove */
                } else {
                    for (final IAccount account : accounts) {
                        sampleApp.removeAccount(
                                account,
                                new PublicClientApplication.AccountsRemovedCallback() {
                                    @Override
                                    public void onAccountsRemoved(Boolean isSuccess) {
                                        if (isSuccess) {
                                            /* successfully removed account */
                                        } else {
                                            /* failed to remove account */
                                        }
                                    }
                                });
                    }
                }

                updateExitUI();
            }
        });
    }

    private void updateExitUI() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
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
//                    String query = "SELECT Студенты.Код, Фамилия, Имя, Отчество, НазваниеГруппы, НазваниеСпециальности, НазваниеПрофиля, НазваниеФакультета, Группы.НомерПлана " +
//                            "FROM Студенты, Группы, Специальности, Факультеты " +
//                            "WHERE Студенты.КодГруппы=Группы.Код AND Группы.КодСпециальности=Специальности.Код AND Специальности.КодФакультета=Факультеты.Код AND Идентификатор='" + studentIdAAD + "'";
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
