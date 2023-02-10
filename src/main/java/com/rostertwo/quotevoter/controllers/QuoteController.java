package com.rostertwo.quotevoter.controllers;

import com.rostertwo.quotevoter.domain.Quote;
import com.rostertwo.quotevoter.exceptions.QuoteNotFoundException;
import com.rostertwo.quotevoter.repositories.QuoteRepository;
import com.rostertwo.quotevoter.services.QuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quotes")
public class QuoteController {

    private final QuoteRepository quoteRepository;
    private final QuoteService quoteService;

    @GetMapping
    public List<Quote> getAllQuotes() {
        return quoteRepository.findAll();
    }

    @GetMapping("/top")
    public List<Quote> getTop10Quotes() {
        return quoteRepository.findTop10ByOrderByVotesDesc();
    }

    @GetMapping("/{id}")
    public Quote getQuoteById(@PathVariable Long id) {
        return quoteRepository.findById(id)
                .orElseThrow(() -> new QuoteNotFoundException(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Quote createQuote(@RequestBody Quote quote) {
        return quoteRepository.save(quote);
    }

    @PutMapping("/{id}")
    public Quote updateQuote(@PathVariable Long id, @RequestBody Quote quote) {
        if (!quoteRepository.existsById(id)) {
            throw new QuoteNotFoundException(id);
        }
        quote.setId(id);
        return quoteRepository.save(quote);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuote(@PathVariable Long id) {
        if (!quoteRepository.existsById(id)) {
            throw new QuoteNotFoundException(id);
        }

        quoteRepository.deleteById(id);
    }

    @PutMapping("/{id}/upvote")
    public Quote upvoteQuote(@PathVariable Long id, @RequestParam Long userId) {
        return quoteService.upvoteQuote(id, userId);
    }

    @PutMapping("/{id}/downvote")
    public Quote downvoteQuote(@PathVariable Long id, @RequestParam Long userId) {
        return quoteService.downvoteQuote(id, userId);
    }
}