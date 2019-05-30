package com.example.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.PublicClientApplication;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private boolean permissionStatus = false;

    private String studentName = "Анпилогов А. А.";
    private long studentId = 1;
    private String direction = "Фундаментальные информационные технологии";
    private String faculty = "Математический";
    private String group = "111";

    PublicClientApplication sampleApp;

    public String getFaculty() { return faculty; }

    public String getGroup() {
        return group;
    }

    public long getStudentId() { return studentId; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Проверка разрешений
        checkPermission();

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
        ((TextView)header.findViewById(R.id.tvStudentName)).setText(studentName);
        ((TextView)header.findViewById(R.id.tvGroup)).setText("Группа: " + group);
        ((TextView)header.findViewById(R.id.tvFaculty)).setText("Факультет: " + faculty);
        ((TextView)header.findViewById(R.id.tvDirection)).setText(direction);

        //Показ фрагмента главной страницы
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flMain, new HomeFragment());
        ft.commit();
        navigationView.setCheckedItem(R.id.amdHome);

        sampleApp = AuthActivity.getSampleApp();
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
                permissionStatus = true;
            }
        } else {
            permissionStatus = true;
        }
    }

    //Переопределенный метод для проверки разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionStatus = true;
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    permissionStatus = false;
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
}
