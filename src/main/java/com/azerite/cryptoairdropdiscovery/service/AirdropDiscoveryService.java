package com.azerite.cryptoairdropdiscovery.service;

import com.azerite.cryptoairdropdiscovery.model.AirdropTweet;
import com.azerite.cryptoairdropdiscovery.repository.AirdropTweetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for discovering and processing airdrop tweets.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AirdropDiscoveryService {

    private final AirdropTweetRepository repository;
    
    // Keywords that indicate a tweet might be about an airdrop with tasks
    private static final List<String> AIRDROP_KEYWORDS = Arrays.asList("follow", "mint", "galxe", "zk", "claim");
    
    // Keywords that indicate a tweet should be excluded
    private static final List<String> EXCLUSION_KEYWORDS = Arrays.asList("scam", "ended");
    
    /**
     * Processes a tweet and saves it if it appears to be about an airdrop.
     *
     * @param tweetText The raw text of the tweet
     * @param source The source of the tweet (e.g., Twitter)
     * @param link The link to the original tweet
     * @return The saved AirdropTweet if the tweet is about an airdrop, null otherwise
     */
    @Transactional
    public AirdropTweet processTweet(String tweetText, String source, String link) {
        if (tweetText == null || tweetText.isEmpty()) {
            log.warn("Empty tweet text received, skipping processing");
            return null;
        }
        
        // Check if the tweet contains any airdrop keywords
        boolean containsAirdropKeyword = AIRDROP_KEYWORDS.stream()
                .anyMatch(keyword -> tweetText.toLowerCase().contains(keyword.toLowerCase()));
        
        // Check if the tweet contains any exclusion keywords
        boolean containsExclusionKeyword = EXCLUSION_KEYWORDS.stream()
                .anyMatch(keyword -> tweetText.toLowerCase().contains(keyword.toLowerCase()));
        
        if (containsAirdropKeyword && !containsExclusionKeyword) {
            // Extract a title from the tweet (first non-empty line or first 50 chars)
            String title = extractTitle(tweetText);
            
            // Extract tasks from the tweet
            String tasks = extractTasks(tweetText);
            
            // Create and save the airdrop tweet
            AirdropTweet airdropTweet = AirdropTweet.builder()
                    .title(title)
                    .source(source)
                    .link(link)
                    .tasks(tasks)
                    .rawText(tweetText)
                    .build();
            
            log.info("Saving airdrop tweet: {}", title);
            return repository.save(airdropTweet);
        }
        
        log.debug("Tweet does not appear to be about an airdrop with tasks, skipping");
        return null;
    }
    
    /**
     * Gets all airdrop tweets that match the filter criteria.
     *
     * @return A list of filtered airdrop tweets
     */
    @Transactional(readOnly = true)
    public List<AirdropTweet> getFilteredAirdrops() {
        return repository.findFilteredAirdrops();
    }
    
    /**
     * Extracts a title from the tweet text.
     *
     * @param tweetText The raw text of the tweet
     * @return The extracted title
     */
    private String extractTitle(String tweetText) {
        // Get the first non-empty line as the title
        String[] lines = tweetText.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                return trimmed.length() > 100 ? trimmed.substring(0, 100) + "..." : trimmed;
            }
        }
        
        // Fallback to the first 50 characters if no non-empty line is found
        return tweetText.length() > 100 ? 
                tweetText.substring(0, 100) + "..." : 
                tweetText;
    }
    
    /**
     * Extracts tasks from the tweet text.
     *
     * @param tweetText The raw text of the tweet
     * @return The extracted tasks
     */
    private String extractTasks(String tweetText) {
        StringBuilder tasks = new StringBuilder();
        
        // Look for task patterns like "1. Do this" or "• Do that"
        Pattern taskPattern = Pattern.compile("(?m)^\\s*(?:[0-9]+\\.|[•\\-*])\\s*(.+)$");
        Matcher matcher = taskPattern.matcher(tweetText);
        
        while (matcher.find()) {
            tasks.append("- ").append(matcher.group(1).trim()).append("\n");
        }
        
        // If no structured tasks are found, look for keywords
        if (tasks.length() == 0) {
            for (String keyword : AIRDROP_KEYWORDS) {
                Pattern keywordPattern = Pattern.compile("(?i)\\b" + keyword + "\\b.{0,50}");
                Matcher keywordMatcher = keywordPattern.matcher(tweetText);
                
                while (keywordMatcher.find()) {
                    String match = keywordMatcher.group(0).trim();
                    tasks.append("- ").append(match).append("...\n");
                }
            }
        }
        
        return tasks.length() > 0 ? tasks.toString().trim() : null;
    }
} 