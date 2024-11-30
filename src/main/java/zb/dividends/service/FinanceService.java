package zb.dividends.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import zb.dividends.model.Company;
import zb.dividends.model.Dividend;
import zb.dividends.model.ScrapedResult;
import zb.dividends.persist.CompanyRepository;
import zb.dividends.persist.DividendRepository;
import zb.dividends.persist.entity.CompanyEntity;
import zb.dividends.persist.entity.DividendEntity;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName) {
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다: " + companyName));

        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        List<Dividend> dividends = dividendEntities.stream()
                .map(e -> Dividend.builder()
                        .date(e.getDate())
                        .dividend(e.getDividend())
                        .build())
                .collect(Collectors.toList());

        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build(),
                dividends);
    }
}
