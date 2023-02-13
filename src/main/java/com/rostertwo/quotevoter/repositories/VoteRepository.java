package com.rostertwo.quotevoter.repositories;

import com.rostertwo.quotevoter.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findByQuoteId(Long quoteId);
}

