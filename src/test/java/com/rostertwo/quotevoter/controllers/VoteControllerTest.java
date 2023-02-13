package com.rostertwo.quotevoter.controllers;

import com.rostertwo.quotevoter.dto.GraphData;
import com.rostertwo.quotevoter.enums.VoteType;
import com.rostertwo.quotevoter.services.VoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private VoteService voteService;

    @Test
    void getDailyVotesByType() throws Exception {
        Map<VoteType, Long> votes1 = new HashMap<>();
        votes1.put(VoteType.UPVOTE, 3L);
        votes1.put(VoteType.DOWNVOTE, 2L);
        Map<VoteType, Long> votes2 = new HashMap<>();
        votes2.put(VoteType.UPVOTE, 1000L);
        votes2.put(VoteType.DOWNVOTE, 100L);

        List<GraphData> votesData = Arrays.asList(
                GraphData.builder().date(LocalDate.now().minusDays(1)).voteCount(votes1).build(),
                GraphData.builder().date(LocalDate.now()).voteCount(votes2).build()
        );
        when(voteService.getDailyVotesByType(1L)).thenReturn(votesData);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/votes/1/daily"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].date").value(LocalDate.now().minusDays(1).toString()))
                .andExpect(jsonPath("$[0].voteCount.UPVOTE").value(3))
                .andExpect(jsonPath("$[0].voteCount.DOWNVOTE").value(2))
                .andExpect(jsonPath("$[1].date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[1].voteCount.UPVOTE").value(1000))
                .andExpect(jsonPath("$[1].voteCount.DOWNVOTE").value(100));
    }
}