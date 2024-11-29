package zb.dividends;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

//@SpringBootApplication
public class DividendsApplication {

    public static void main(String[] args) {
        //SpringApplication.run(DemoApplication.class, args);

        Connection connection = Jsoup.connect("https://finance.yahoo.com/quote/O/history/?period1=1618204284&period2=1649740284&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true");

        try {
            // HTML 문서 가져오기
            Document document = connection.get();

            Elements tables = document.select("table.table.yf-j5d1ld.noDl");
            Element ele = tables.get(0);

            Element tbody = ele.children().get(1);
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }
                String[] splits = txt.split(" ");
                String month = splits[0];
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];
                System.out.println(year + "/" + month + "/" + day + " -> " + dividend);
            }

        } catch (IOException e) {
            throw new RuntimeException("데이터를 가져오는 중 오류가 발생했습니다.", e);
        }
    }
}
