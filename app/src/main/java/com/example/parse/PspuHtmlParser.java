package com.example.parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class PspuHtmlParser {

//    public static void main(String[] args) {
//        String faculty = "Иностранных языков";
//        String group = "ZS711";
//        String res = getDocScheduleGroupUrl(faculty, group);
//        System.out.println(res);
//    }

    public static String getDocScheduleGroupUrl(String faculty, String group) {
        try {
            String homeUrl = "http://pspu.ru";
            Document docFaculty = Jsoup.connect(homeUrl + "/student").get();
            Element tFacultyElement = docFaculty.selectFirst("section#content table");
            Elements trFacultyElements = tFacultyElement.select("tr:contains(" + faculty + ")");
            String facultyUrl = "";
            for (Element trFacultyElement : trFacultyElements) {
                if (trFacultyElement.selectFirst("td").text().equals(faculty)) {
                    facultyUrl = trFacultyElement.selectFirst("a:contains(Перейти)").attr("href");
                }
            }

            Document docSchedule = Jsoup.connect(homeUrl + facultyUrl).get();
            Elements tScheduleElements = docSchedule.select("section#content table");
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
