package com.example.ut.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Random;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.ut.SecurityConfig;
import com.example.ut.service.RewardService;

@Import(value = SecurityConfig.class)
@WebMvcTest(value = RewardRestController.class)
class RewardRestControllerRoleTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RewardService rewardService;

    @BeforeEach
    void setup() {
        when(rewardService.getPersonalTotalReward())
                .thenReturn(25_000 + new Random().nextInt(100_000));
    }

    @WithMockUser
    @Test
    void givenRoleUser_whenAskForTotalReward_shouldSucceedWith200() throws Exception {
        mvc.perform(get("/reward/total"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.total")
                        .value(Matchers.greaterThanOrEqualTo(25_000))
                )
                .andDo(print());
    }

    @WithMockUser(roles = {"MANAGER"})
    @Test
    void givenRoleManager_whenAskForTotalReward_shouldForbidden() throws Exception {
        mvc.perform(get("/reward/total"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    void givenRoleAdmin_whenAskForTotalReward_shouldForbidden() throws Exception {
        mvc.perform(get("/reward/total"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}
