package zb.dividends.scraper;

import zb.dividends.model.Company;
import zb.dividends.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
