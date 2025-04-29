package com.azerite.cryptoairdropdiscovery.service;

import com.azerite.cryptoairdropdiscovery.config.AirdropDiscoveryProperties;
import com.azerite.cryptoairdropdiscovery.model.AirdropTweet;
import com.azerite.cryptoairdropdiscovery.repository.AirdropTweetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirdropDiscoveryServiceTest {

    @Mock
    private AirdropTweetRepository repository;

    @Mock
    private AirdropDiscoveryProperties properties;

    @Mock
    private AirdropDiscoveryProperties.Keywords keywords;

    private AirdropDiscoveryService service;

    @BeforeEach
    void setUp() {
        when(properties.getKeywords()).thenReturn(keywords);
        when(keywords.getPositive()).thenReturn(Arrays.asList("follow", "mint", "galxe", "zk", "claim"));
        when(keywords.getNegative()).thenReturn(Arrays.asList("scam", "ended"));

        service = new AirdropDiscoveryService(repository, properties);
    }

    @Test
    void processTweet_WithAirdropKeyword_ShouldSaveTweet() {
        // Given
        String tweetText = "New airdrop! Follow us and claim your tokens.";
        String source = "Twitter";
        String link = "https://twitter.com/test/status/123";

        AirdropTweet savedTweet = AirdropTweet.builder()
                .id(1L)
                .title("New airdrop! Follow us and claim your tokens.")
                .source(source)
                .link(link)
                .tasks("- Follow us and claim...")
                .rawText(tweetText)
                .build();

        when(repository.save(any(AirdropTweet.class))).thenReturn(savedTweet);

        // When
        AirdropTweet result = service.processTweet(tweetText, source, link);

        // Then
        assertNotNull(result);
        assertEquals("New airdrop! Follow us and claim your tokens.", result.getTitle());

        ArgumentCaptor<AirdropTweet> tweetCaptor = ArgumentCaptor.forClass(AirdropTweet.class);
        verify(repository).save(tweetCaptor.capture());

        AirdropTweet capturedTweet = tweetCaptor.getValue();
        assertEquals(tweetText, capturedTweet.getRawText());
        assertEquals(source, capturedTweet.getSource());
        assertEquals(link, capturedTweet.getLink());
    }

    @Test
    void processTweet_WithExclusionKeyword_ShouldNotSaveTweet() {
        // Given
        String tweetText = "This airdrop has ended. No more claims accepted.";
        String source = "Twitter";
        String link = "https://twitter.com/test/status/123";

        // When
        AirdropTweet result = service.processTweet(tweetText, source, link);

        // Then
        assertNull(result);
        verify(repository, never()).save(any());
    }

    @Test
    void processTweet_WithNoAirdropKeyword_ShouldNotSaveTweet() {
        // Given
        String tweetText = "Just a regular tweet with no airdrop related keywords.";
        String source = "Twitter";
        String link = "https://twitter.com/test/status/123";

        // When
        AirdropTweet result = service.processTweet(tweetText, source, link);

        // Then
        assertNull(result);
        verify(repository, never()).save(any());
    }

    @Test
    void processTweet_WithEmptyText_ShouldReturnNull() {
        // Given
        String tweetText = "";
        String source = "Twitter";
        String link = "https://twitter.com/test/status/123";

        // When
        AirdropTweet result = service.processTweet(tweetText, source, link);

        // Then
        assertNull(result);
        verify(repository, never()).save(any());
    }
} 