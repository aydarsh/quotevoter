package com.rostertwo.quotevoter.services;

import com.rostertwo.quotevoter.domain.Quote;
import com.rostertwo.quotevoter.domain.User;
import com.rostertwo.quotevoter.domain.Vote;
import com.rostertwo.quotevoter.dto.GraphData;
import com.rostertwo.quotevoter.enums.VoteType;
import com.rostertwo.quotevoter.repositories.VoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;
    @InjectMocks
    private VoteService voteService;

    @Test
    void getDailyVotesByType() {
        User user = User.builder().id(11L).username("User One").build();
        Quote quote = Quote.builder().id(1L).text("Quote Text 1").author(user).votes(1000).build();

        List<Vote> votes = Arrays.asList(
                Vote.builder().id(1L).quote(quote).user(user).createdAt(OffsetDateTime.now().minusDays(1)).type(VoteType.UPVOTE).build(),
                Vote.builder().id(2L).quote(quote).user(user).createdAt(OffsetDateTime.now().minusDays(1)).type(VoteType.UPVOTE).build(),
                Vote.builder().id(3L).quote(quote).user(user).createdAt(OffsetDateTime.now().minusDays(1)).type(VoteType.DOWNVOTE).build(),

                Vote.builder().id(4L).quote(quote).user(user).createdAt(OffsetDateTime.now()).type(VoteType.UPVOTE).build(),
                Vote.builder().id(5L).quote(quote).user(user).createdAt(OffsetDateTime.now()).type(VoteType.UPVOTE).build(),
                Vote.builder().id(6L).quote(quote).user(user).createdAt(OffsetDateTime.now()).type(VoteType.UPVOTE).build(),
                Vote.builder().id(7L).quote(quote).user(user).createdAt(OffsetDateTime.now()).type(VoteType.UPVOTE).build(),
                Vote.builder().id(8L).quote(quote).user(user).createdAt(OffsetDateTime.now()).type(VoteType.DOWNVOTE).build(),
                Vote.builder().id(9L).quote(quote).user(user).createdAt(OffsetDateTime.now()).type(VoteType.DOWNVOTE).build(),
                Vote.builder().id(10L).quote(quote).user(user).createdAt(OffsetDateTime.now()).type(VoteType.DOWNVOTE).build()
        );
        when(voteRepository.findByQuoteId(anyLong())).thenReturn(votes);

        Comparator<GraphData> byDate = Comparator.comparing(GraphData::getDate);
        List<GraphData> result = voteService.getDailyVotesByType(1L);
        result.sort(byDate);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(LocalDate.now().minusDays(1), result.get(0).getDate());
        assertEquals(2, result.get(0).getVoteCount().get(VoteType.UPVOTE));
        assertEquals(1, result.get(0).getVoteCount().get(VoteType.DOWNVOTE));

        assertEquals(LocalDate.now(), result.get(1).getDate());
        assertEquals(4, result.get(1).getVoteCount().get(VoteType.UPVOTE));
        assertEquals(3, result.get(1).getVoteCount().get(VoteType.DOWNVOTE));
    }
}