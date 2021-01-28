package com.jns.rsmsutility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WebHandler
{
    public static String user,pass,subjectsTable;
    public static int hrs,lhrs,aphrs,dhrs,dahrs;
    public static Map<String, String> coky;
    public static ArrayList<String> listsem,hournumber;
    public static Bitmap image;

    //Function to be called so as to obtain cookies
    public static Map<String,String> getCookie()
    {
        try {
            Connection.Response loginForm = Jsoup.connect("http://rajagiritech.ac.in/stud/parent/varify.asp")
                    .timeout(0)
                    .data("user", user,"pass",pass,"I1.x", "9","I1.y","9")
                    .method(Connection.Method.POST)
                    .execute();

            return loginForm.cookies();

        } catch (Exception e)
        {
            //TODO: handle exception
        }
        return null;
    }

    public static String getAuth(String us,String pa)
    {
        user=us;
        pass=pa;
        coky=getCookie();
        try {

            Document home = Jsoup.connect("https://www.rajagiritech.ac.in/stud/ktu/Student/Home.asp")
                    .cookies(coky)
                    .get();

            coky=getCookie();
            Document attendance = Jsoup.connect("https://www.rajagiritech.ac.in/stud/ktu/Student/Leave.asp")
                    .cookies(coky)
                    .get();
            Element table = attendance.select("table").get(1).select("table").get(1);
            String[] list=table.text().split(" ");
            listsem = new ArrayList<String>(Arrays.asList(list));
            listsem.remove("Class");
            listsem.remove("Code:");
            InputStream inputStream=new java.net.URL("https://www.rajagiritech.ac.in/stud/ktu/stud/Photo/"+user+".jpg").openStream();
            image= BitmapFactory.decodeStream(inputStream);
            if (!home.title().equals("RSET - RSMS Login")) {
                 return home.getElementsByClass("scroller").text().split(":")[1];
            }
            else {
                return " ";
            }

        }
        catch (Exception e)
        {
            //handle
        }
        return "x";
    }

    public static String setAttendanceTable(String url)
    {
        String attendanceTable=new String("<table width=\"96%\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"3\">\n");
        coky=getCookie();
        hrs=dahrs=dhrs=lhrs=aphrs=0;
        hournumber=new ArrayList<String>();
        try {
            Document attendance = Jsoup.connect(url)
                    .cookies(coky)
                    .get();
            Element table = attendance.select("table").get(1).select("table").get(2);
            attendanceTable=attendanceTable+table.html()+"      </table>";

            ArrayList<String> hourlist = new ArrayList<>();
            Element table2 = attendance.select("table").get(1); //select the first table.
            Elements rows = table2.select("tr");
            Elements cols;
            Element row;
            Element cell;
            for (int i = 5; i < (rows.size()-2); i++) {
                row = rows.select("tr").get(i);
                cols = row.select("td");
                for(int j=0;j<cols.size();j++)
                {
                    cell=cols.get(j);
                    switch (cell.attr("bgcolor")) {
                        case "#9f0000":
                            hourlist.add(cell.text());
                            hrs++;
                            lhrs++;
                            break;
                        case "#006600":
                            hourlist.add(cell.text());
                            hrs++;
                            aphrs++;
                            break;
                        case "#ff9900":
                            hourlist.add(cell.text());
                            hrs++;
                            dhrs++;
                            break;
                        case "#cccc00":
                            hourlist.add(cell.text());
                            hrs++;
                            dahrs++;
                            break;
                    }
                }
            }
            Set<String> newList= new HashSet<>(hourlist);
            for(String s:newList)
            {
                hournumber.add(s+"  :  "+ Collections.frequency(hourlist, s));
            }
            return attendanceTable;

        }
        catch (Exception e)
        {
            //
        }
        return " ";
    }

    public static String setSessionalMarkTable(String url)
    {
        coky=getCookie();
        try {
            Document attendance = Jsoup.connect(url)
                    .cookies(coky)
                    .get();
            Element table1 = attendance.select("table").get(2).select("table").get(2);
            subjectsTable="<table width=\"96%\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"3\">\n"+table1.html().replace("class=\"ibox3\"", "bgcolor=\"#007cc3\"")+"      </table>";
            Element table = attendance.select("table").get(1).select("table").get(2);
            return "<table width=\"96%\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"3\">\n"+table.html().replace("class=\"ibox3\"", "bgcolor=\"#007cc3\"")+"      </table>";

        }
        catch (Exception e)
        {
            //handle
        }
        return " ";
    }


}
