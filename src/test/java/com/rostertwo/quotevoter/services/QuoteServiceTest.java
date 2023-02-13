package com.rostertwo.quotevoter.services;

import com.rostertwo.quotevoter.domain.Quote;
import com.rostertwo.quotevoter.domain.User;
import com.rostertwo.quotevoter.domain.Vote;
import com.rostertwo.quotevoter.repositories.QuoteRepository;
import com.rostertwo.quotevoter.repositories.UserRepository;
import com.rostertwo.quotevoter.repositories.VoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private QuoteRepository quoteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VoteRepository voteRepository;
    @InjectMocks
    private QuoteService quoteService;

    @Test
    void upvoteQuote() {
        User user = User.builder().id(11L).username("User One").build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        Quote quote = Quote.builder().id(1L).text("Quote Text 1").author(user).votes(1000).build();
        when(quoteRepository.findById(anyLong())).thenReturn(Optional.ofNullable(quote));

        Quote quoteUpvoted = Quote.builder().id(1L).text("Quote Text 1").author(user).votes(1001).build();
        when(quoteRepository.save(any())).thenReturn(quoteUpvoted);

        Quote result = quoteService.upvoteQuote(1L, 11L);
        verify(voteRepository, times(1)).save(any(Vote.class));
        assertNotNull(result);
        assertEquals(1001, result.getVotes());
    }

    @Test
    void downvoteQuote() {
        User user = User.builder().id(11L).username("User One").build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        Quote quote = Quote.builder().id(1L).text("Quote Text 1").author(user).votes(1000).build();
        when(quoteRepository.findById(anyLong())).thenReturn(Optional.ofNullable(quote));

        Quote quoteDownvoted = Quote.builder().id(1L).text("Quote Text 1").author(user).votes(999).build();
        when(quoteRepository.save(any())).thenReturn(quoteDownvoted);

        Quote result = quoteService.downvoteQuote(1L, 11L);
        verify(voteRepository, times(1)).save(any(Vote.class));
        assertNotNull(result);
        assertEquals(999, result.getVotes());
    }
}