package com.rostertwo.quotevoter.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rostertwo.quotevoter.domain.Quote;
import com.rostertwo.quotevoter.domain.User;
import com.rostertwo.quotevoter.exceptions.QuoteNotFoundException;
import com.rostertwo.quotevoter.repositories.QuoteRepository;
import com.rostertwo.quotevoter.services.QuoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private QuoteRepository quoteRepository;
    @MockBean
    private QuoteService quoteService;

    @Test
    void getAllQuotes() throws Exception {
        List<Quote> quotes = Arrays.asList(
                Quote.builder().id(1L).text("Quote Text 1").author(User.builder().id(11L).build()).build(),
                Quote.builder().id(2L).text("Quote Text 2").author(User.builder().id(21L).build()).build()
        );
        when(quoteRepository.findAll()).thenReturn(quotes);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/quotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("Quote Text 1"))
                .andExpect(jsonPath("$[0].author.id").value(11))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].text").value("Quote Text 2"))
                .andExpect(jsonPath("$[1].author.id").value(21));
    }

    @Test
    void getTop10Quotes() throws Exception {
        List<Quote> quotes = new ArrayList<>();
        for (int i = 10; i > 0 ; i--) {
            quotes.add(Quote.builder().id((long) i).votes(i).build());
        }
        when(quoteRepository.findTop10ByOrderByVotesDesc()).thenReturn(quotes);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/quotes/top"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].votes").value(10))
                .andExpect(jsonPath("$[9].id").value(1))
                .andExpect(jsonPath("$[9].votes").value(1));
    }

    @Test
    void getQuoteById() throws Exception {
        Quote quote = Quote.builder().id(1L).text("Quote Text 1").author(User.builder().id(11L).build()).build();
        when(quoteRepository.findById(anyLong())).thenReturn(Optional.ofNullable(quote));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/quotes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Quote Text 1"))
                .andExpect(jsonPath("$.author.id").value(11));
    }

    @Test
    void getQuoteByIdNonExisting() throws Exception {
        when(quoteRepository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/quotes/1"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof QuoteNotFoundException))
                .andExpect(result -> assertEquals("Quote with id 1 could not be found", result.getResolvedException().getMessage()));
    }

    @Test
    void createQuote() throws Exception {
        Quote quote = Quote.builder().id(1L).text("Quote Text 1").author(User.builder().id(11L).build()).build();
        when(quoteRepository.save(any())).thenReturn(quote);
        String json = objectMapper.writeValueAsString(quote);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Quote Text 1"))
                .andExpect(jsonPath("$.author.id").value(11));
    }

    @Test
    void updateQuote() throws Exception {
        Quote quoteUpdated = Quote.builder().id(1L).text("New Quote Text").author(User.builder().id(12L).build()).build();
        when(quoteRepository.existsById(1L)).thenReturn(true);
        when(quoteRepository.save(any())).thenReturn(quoteUpdated);
        String json = objectMapper.writeValueAsString(quoteUpdated);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/quotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("New Quote Text"))
                .andExpect(jsonPath("$.author.id").value(12));
    }

    @Test
    void updateQuoteNonExisting() throws Exception {
        String json = objectMapper.writeValueAsString(Quote.builder().build());
        when(quoteRepository.existsById(1L)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/quotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof QuoteNotFoundException))
                .andExpect(result -> assertEquals("Quote with id 1 could not be found", result.getResolvedException().getMessage()));
    }

    @Test
    void deleteQuote() throws Exception {
        when(quoteRepository.existsById(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/quotes/1"))
                .andExpect(status().isNoContent());
        verify(quoteRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserNonExisting() throws Exception {
        when(quoteRepository.existsById(1L)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/quotes/1"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof QuoteNotFoundException))
                .andExpect(result -> assertEquals("Quote with id 1 could not be found", result.getResolvedException().getMessage()));
    }

    @Test
    void upvoteQuote() throws Exception {
        Quote quote = Quote.builder()
                .id(1L)
                .text("Quote Text 1")
                .author(User.builder().id(11L).build())
                .votes(1234)
                .build();
        when(quoteService.upvoteQuote(1L, 11L)).thenReturn(quote);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/quotes/1/upvote?userId=11")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Quote Text 1"))
                .andExpect(jsonPath("$.author.id").value(11))
                .andExpect(jsonPath("$.votes").value(1234));
    }

    @Test
    void downvoteQuote() throws Exception {
        Quote quote = Quote.builder()
                .id(1L)
                .text("Quote Text 1")
                .author(User.builder().id(11L).build())
                .votes(1234)
                .build();
        when(quoteService.downvoteQuote(1L, 11L)).thenReturn(quote);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/quotes/1/downvote?userId=11")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Quote Text 1"))
                .andExpect(jsonPath("$.author.id").value(11))
                .andExpect(jsonPath("$.votes").value(1234));
    }
}