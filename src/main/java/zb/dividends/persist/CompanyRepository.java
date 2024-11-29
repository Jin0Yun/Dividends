package zb.dividends.persist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zb.dividends.persist.entity.DividendEntity;

@Repository
public interface CompanyRepository extends JpaRepository<DividendEntity, Long> { }
