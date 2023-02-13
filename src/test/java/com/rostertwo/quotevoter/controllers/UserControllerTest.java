package com.rostertwo.quotevoter.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rostertwo.quotevoter.domain.User;
import com.rostertwo.quotevoter.exceptions.UserNotFoundException;
import com.rostertwo.quotevoter.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllUsers() throws Exception {
        List<User> users = Arrays.asList(
                User.builder().id(1L).username("User One").build(),
                User.builder().id(2L).username("User Two").build()
        );
        when(userRepository.findAll()).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("User One"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("User Two"));
    }

    @Test
    void getUserById() throws Exception {
        User user = User.builder().id(1L).username("User One").build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("User One"));
    }

    @Test
    void getUserByIdNonExisting() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User with id 1 could not be found", result.getResolvedException().getMessage()));
    }

    @Test
    void createUser() throws Exception {
        User user = User.builder().id(1L).username("User One").build();
        when(userRepository.save(any())).thenReturn(user);
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("User One"));
    }

    @Test
    void updateUser() throws Exception {
        User userUpdated = User.builder().id(1L).username("New Name").build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.save(any())).thenReturn(userUpdated);
        String json = objectMapper.writeValueAsString(userUpdated);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("New Name"));
    }

    @Test
    void updateUserNonExisting() throws Exception {
        String json = objectMapper.writeValueAsString(User.builder().build());
        when(userRepository.existsById(1L)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User with id 1 could not be found", result.getResolvedException().getMessage()));
    }

    @Test
    void deleteUser() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/1"))
                .andExpect(status().isNoContent());
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserNonExisting() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User with id 1 could not be found", result.getResolvedException().getMessage()));
    }
}