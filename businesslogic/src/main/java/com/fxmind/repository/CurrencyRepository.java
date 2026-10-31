package com.fxmind.repository;

import com.fxmind.domain.Currency;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findByNameIgnoreCase(String name);
}
