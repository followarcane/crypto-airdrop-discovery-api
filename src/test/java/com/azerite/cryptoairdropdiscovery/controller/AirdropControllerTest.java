package com.azerite.cryptoairdropdiscovery.controller;

import com.azerite.cryptoairdropdiscovery.model.AirdropTweet;
import com.azerite.cryptoairdropdiscovery.service.AirdropDiscoveryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AirdropController.class)
class AirdropControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AirdropDiscoveryService airdropDiscoveryService;

    @Test
    void ingestTweet_ValidTweet_ReturnsOk() throws Exception {
        // Given
        AirdropTweet airdropTweet = AirdropTweet.builder()
                .id(1L)
                .title("New airdrop! Follow us and claim your tokens.")
                .source("Twitter")
                .link("https://twitter.com/test/status/123")
                .tasks("- Follow us and claim...")
                .rawText("New airdrop! Follow us and claim your tokens.")
                .createdAt(LocalDateTime.now())
                .build();

        when(airdropDiscoveryService.processTweet(anyString(), anyString(), anyString()))
                .thenReturn(airdropTweet);

        // When & Then
        mockMvc.perform(post("/airdrops/ingest")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"New airdrop! Follow us and claim your tokens.\",\"source\":\"Twitter\",\"link\":\"https://twitter.com/test/status/123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("New airdrop! Follow us and claim your tokens.")))
                .andExpect(jsonPath("$.source", is("Twitter")))
                .andExpect(jsonPath("$.link", is("https://twitter.com/test/status/123")));
    }

    @Test
    void ingestTweet_InvalidTweet_ReturnsBadRequest() throws Exception {
        // Given
        when(airdropDiscoveryService.processTweet(anyString(), anyString(), anyString()))
                .thenReturn(null);

        // When & Then
        mockMvc.perform(post("/airdrops/ingest")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"Just a regular tweet.\",\"source\":\"Twitter\",\"link\":\"https://twitter.com/test/status/123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ingestTweet_MissingFields_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/airdrops/ingest")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"New airdrop! Follow us and claim your tokens.\",\"source\":\"Twitter\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFilteredAirdrops_ReturnsAirdropList() throws Exception {
        // Given
        List<AirdropTweet> airdrops = Arrays.asList(
                AirdropTweet.builder()
                        .id(1L)
                        .title("Airdrop 1")
                        .source("Twitter")
                        .link("https://twitter.com/test/status/123")
                        .tasks("- Follow\n- Claim")
                        .rawText("Airdrop 1: Follow and Claim")
                        .createdAt(LocalDateTime.now())
                        .build(),
                AirdropTweet.builder()
                        .id(2L)
                        .title("Airdrop 2")
                        .source("Twitter")
                        .link("https://twitter.com/test/status/456")
                        .tasks("- Mint\n- Galxe task")
                        .rawText("Airdrop 2: Mint and complete Galxe task")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        when(airdropDiscoveryService.getFilteredAirdrops()).thenReturn(airdrops);

        // When & Then
        mockMvc.perform(get("/airdrops/filtered"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Airdrop 1")))
                .andExpect(jsonPath("$[1].title", is("Airdrop 2")));
    }
} 