package com.rostertwo.quotevoter.repositories;

import com.rostertwo.quotevoter.domain.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    List<Quote> findTop10ByOrderByVotesDesc();
}
