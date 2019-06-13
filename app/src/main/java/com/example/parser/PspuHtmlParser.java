package com.example.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class PspuHtmlParser {

    public static String getDocScheduleGroupUrl(String faculty, String group) {
        try {
            String homeUrl = "http://pspu.ru";
            //Зарузка кода страницы http://pspu.ru/student
            Document docFaculty = Jsoup.connect(homeUrl + "/student").get();
            //Получение таблицы факультетов
            Element tFacultyElement = docFaculty.selectFirst("section#content table");
            //Получение строки конкретного факультета
            Elements trFacultyElements = tFacultyElement.select("tr:contains(" + faculty + ")");
            String facultyUrl = "";
            for (Element trFacultyElement : trFacultyElements) {
                if (trFacultyElement.selectFirst("td").text().equals(faculty)) {
                    //Получение ссылки на страницу с расписанием групп фаультета
                    facultyUrl = trFacultyElement.selectFirst("a:contains(Перейти)").attr("href");
                }
            }
            //Загрузка кода страницы с расписанием групп фаультета
            Document docSchedule = Jsoup.connect(homeUrl + facultyUrl).get();
            //Получение таблицы с расписаниями
            Elements tScheduleElements = docSchedule.select("section#content table");
            //Получение ссылки на расписание
            Elements resElements = tScheduleElements.select("td a:contains(" + group + ")");
            for (Element resElement : resElements) {
                if (resElement.text().equals(group)) {
                    return resElement.attr("href");
                }
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
