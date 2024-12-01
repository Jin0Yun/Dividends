package zb.dividends.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zb.dividends.model.Company;
import zb.dividends.model.ScrapedResult;
import zb.dividends.persist.CompanyRepository;
import zb.dividends.persist.DividendRepository;
import zb.dividends.persist.entity.CompanyEntity;
import zb.dividends.persist.entity.DividendEntity;
import zb.dividends.scraper.Scraper;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is starting...  : ");

        List<CompanyEntity> companies = this.companyRepository.findAll();
        for (var company : companies) {
            log.info("scraping scheduler is starting...  : " + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(Company.builder()
                    .name(company.getName())
                    .ticker(company.getTicker())
                    .build());

            scrapedResult.getDividends().stream()
                    .map(e -> new DividendEntity(company.getId(), e))
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());

                        if (!exists) {
                            this.dividendRepository.save(e);
                            log.info("insert new dividend: " + e.toString());
                        }
                    });

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }
}
