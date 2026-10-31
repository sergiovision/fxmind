package com.fxmind.repository;

import com.fxmind.domain.Currency;
import com.fxmind.domain.Importance;
import com.fxmind.domain.NewsEvent;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsEventRepository extends JpaRepository<NewsEvent, Long> {

    boolean existsByCurrencyAndHappenTimeAndImportance(Currency currency, Instant happenTime, Importance importance);

    /** Events for any of the given currencies inside {@code [from, to]}, earliest and most important first. */
    @Query("""
            select e from NewsEvent e join fetch e.currency c
            where c.name in :currencies
              and e.happenTime between :from and :to
              and e.importance in :importances
            order by e.happenTime asc, e.importance desc
            """)
    List<NewsEvent> findUpcoming(
            @Param("currencies") Collection<String> currencies,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("importances") Collection<Importance> importances,
            Limit limit);

    @EntityGraph(attributePaths = "currency")
    Slice<NewsEvent> findByHappenTimeGreaterThanEqual(Instant from, Pageable pageable);

    long countByHappenTimeGreaterThanEqual(Instant from);
}
