package zb.dividends.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import zb.dividends.model.Company;
import zb.dividends.model.Dividend;
import zb.dividends.model.ScrapedResult;
import zb.dividends.model.constants.Month;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {
    private static final String BASE_URL = "https://finance.yahoo.com/quote/O/history/?period1=1618204284&period2=1649740284&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400;

    @Override
    public ScrapedResult scrap(Company company) {
        ScrapedResult scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000;
            String url = String.format(BASE_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.select("table.table.yf-j5d1ld.noDl");
            Element tableEls = parsingDivs.get(0);
            Element tbody = tableEls.children().get(1);

            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value: " + splits[0]);
                }

                dividends.add(Dividend.builder()
                        .date(LocalDateTime.of(year, month, day, 0, 0))
                        .dividend(dividend)
                        .build());
            }
            scrapResult.setDividendEntities(dividends);
        } catch (IOException e) {
            throw new RuntimeException("데이터를 가져오는 중 오류가 발생했습니다.", e);
        }

        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);
        try {
            Document document = Jsoup.connect(url).get();

            Element titleEle = document.selectFirst("h1");

            if (titleEle == null) {
                throw new RuntimeException("회사 이름을 찾을 수 없습니다.");
            }

            String title = titleEle.text();
            String[] parts = title.split(" - ");
            title = parts.length > 1 ? parts[1].trim() : parts[0].trim();

            return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("회사 정보를 가져오는 중 오류가 발생했습니다.");
        }
    }
}
