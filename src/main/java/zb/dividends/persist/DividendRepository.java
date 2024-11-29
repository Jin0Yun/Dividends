package zb.dividends.persist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zb.dividends.persist.entity.CompanyEntity;

@Repository
public interface DividendRepository extends JpaRepository<CompanyEntity, Long> { }
