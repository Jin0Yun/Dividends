package zb.dividends.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.apache.commons.collections4.Trie;
import org.springframework.util.ObjectUtils;
import zb.dividends.model.Company;
import zb.dividends.model.ScrapedResult;
import zb.dividends.persist.CompanyRepository;
import zb.dividends.persist.DividendRepository;
import zb.dividends.persist.entity.CompanyEntity;
import zb.dividends.persist.entity.DividendEntity;
import zb.dividends.scraper.Scraper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {
    private final Trie trie;
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new RuntimeException("company already exists: " + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // 회사 정보를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker: " + ticker);
        }

        // 배당금 정보 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 회사 정보 저장
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));

        // 배당금 정보 저장
        List<DividendEntity> dividendEntityList = scrapedResult.getDividendEntities().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());
        this.dividendRepository.saveAll(dividendEntityList);

        return company;
    }

    public List<String> getcompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());
    }
    public void addAutoCompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
    }

    public List<String> autoComplete(String keyword) {
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    public void deleteAutoCompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }
}
