package com.rostertwo.quotevoter.services;

import com.rostertwo.quotevoter.dto.GraphData;
import com.rostertwo.quotevoter.domain.Vote;
import com.rostertwo.quotevoter.repositories.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    public List<GraphData> getDailyVotesByType(Long quoteId) {
        return voteRepository.findByQuoteId(quoteId)
                .stream()
                .collect(Collectors.groupingBy(vote -> vote.getCreatedAt().toLocalDate(),
                        Collectors.groupingBy(Vote::getType, Collectors.counting())))
                .entrySet().stream()
                .map(entry -> GraphData.builder()
                        .date(entry.getKey())
                        .voteCount(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }
}
