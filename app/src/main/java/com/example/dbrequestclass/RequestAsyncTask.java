package com.example.dbrequestclass;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class RequestAsyncTask extends AsyncTask<Void, Void, ResultSet> {

    final static String MYSQL_STR_CONN = "jdbc:mysql://db4free.net:3306/pspudb2?useSSL=false&serverTimezone=UTC&autoReconnect=true&failOverReadOnly=false";
    final static String USERNAME = "accel999";
    final static String PASS = "Foo5701478";

    private String query = "";

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    @Override
    protected ResultSet doInBackground(Void... voids) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            connection = DriverManager.getConnection(MYSQL_STR_CONN, USERNAME, PASS);
            if (connection != null) {
                statement = connection.createStatement();
                String operation = query.split(" ")[0];
                if (operation.equals("SELECT")) {
                    resultSet = statement.executeQuery(query);
                } else if (operation.equals("INSERT")) {
                    statement.executeUpdate(query);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultSet;
    }
}
