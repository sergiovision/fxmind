package com.fxmind.repository;

import com.fxmind.domain.OpenPositionRatio;
import com.fxmind.dto.SentimentAverage;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OpenPositionRatioRepository extends JpaRepository<OpenPositionRatio, Long> {

    /**
     * Averages every sample for the symbol inside {@code [from, to]}. A {@code null} site aggregates all
     * sources (the "global" sentiment).
     */
    @Query("""
            select new com.fxmind.dto.SentimentAverage(avg(r.longRatio), avg(r.shortRatio), count(r))
            from OpenPositionRatio r
            where r.symbol.name = :symbol
              and r.parsedAt between :from and :to
              and (:siteId is null or r.site.id = :siteId)
            """)
    SentimentAverage average(
            @Param("symbol") String symbol,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("siteId") Long siteId);
}
