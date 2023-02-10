package com.rostertwo.quotevoter.services;

import com.rostertwo.quotevoter.domain.Quote;
import com.rostertwo.quotevoter.domain.User;
import com.rostertwo.quotevoter.domain.Vote;
import com.rostertwo.quotevoter.enums.VoteType;
import com.rostertwo.quotevoter.exceptions.QuoteNotFoundException;
import com.rostertwo.quotevoter.exceptions.UserNotFoundException;
import com.rostertwo.quotevoter.repositories.QuoteRepository;
import com.rostertwo.quotevoter.repositories.UserRepository;
import com.rostertwo.quotevoter.repositories.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    public Quote upvoteQuote(Long quoteId, Long userId) {
        return voteOnQuote(quoteId, userId, VoteType.UPVOTE);
    }

    public Quote downvoteQuote(Long quoteId, Long userId) {
        return voteOnQuote(quoteId, userId, VoteType.DOWNVOTE);
    }
    private Quote voteOnQuote(Long quoteId, Long userId, VoteType voteType) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new QuoteNotFoundException(quoteId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Vote vote = Vote.builder()
                .quote(quote)
                .user(user)
                .type(voteType)
                .createdAt(OffsetDateTime.now())
                .build();
        voteRepository.save(vote);
        int voteDelta = voteType == VoteType.UPVOTE ? 1 : -1;
        quote.setVotes(quote.getVotes() + voteDelta);
        return quoteRepository.save(quote);
    }
}
