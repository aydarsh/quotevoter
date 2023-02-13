package com.rostertwo.quotevoter.controllers;

import com.rostertwo.quotevoter.dto.GraphData;
import com.rostertwo.quotevoter.services.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;

    @GetMapping("/{quoteId}/daily")
    public List<GraphData> getDailyVotesByType(@PathVariable Long quoteId) {
        return voteService.getDailyVotesByType(quoteId);
    }
}
