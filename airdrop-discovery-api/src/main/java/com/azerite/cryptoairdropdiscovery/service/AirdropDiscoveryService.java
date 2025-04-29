package com.azerite.cryptoairdropdiscovery.service;

import com.azerite.cryptoairdropdiscovery.config.AirdropDiscoveryProperties;
import com.azerite.cryptoairdropdiscovery.model.AirdropTweet;
import com.azerite.cryptoairdropdiscovery.repository.AirdropTweetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for discovering and processing airdrop tweets.
 */
@Service
@Slf4j
public class AirdropDiscoveryService {

    private final AirdropTweetRepository repository;
    private final AirdropDiscoveryProperties properties;

    /**
     * Constructor for AirdropDiscoveryService.
     *
     * @param repository Repository for accessing tweet data
     * @param properties Configuration properties
     */
    public AirdropDiscoveryService(AirdropTweetRepository repository, AirdropDiscoveryProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    /**
     * Processes a tweet and saves it if it appears to be about an airdrop.
     *
     * @param tweetText The raw text of the tweet
     * @param source    The source of the tweet (e.g., Twitter)
     * @param link      The link to the original tweet
     * @return The saved AirdropTweet if the tweet is about an airdrop, null otherwise
     */
    @Transactional
    public AirdropTweet processTweet(String tweetText, String source, String link) {
        if (tweetText == null || tweetText.isEmpty()) {
            log.warn("Empty tweet text received, skipping processing");
            return null;
        }

        // Get keywords from properties
        List<String> airdropKeywords = properties.getKeywords().getPositive();
        List<String> exclusionKeywords = properties.getKeywords().getNegative();

        // Check if the tweet contains any airdrop keywords
        boolean containsAirdropKeyword = airdropKeywords.stream()
                .anyMatch(keyword -> tweetText.toLowerCase().contains(keyword.toLowerCase()));

        // Check if the tweet contains any exclusion keywords
        boolean containsExclusionKeyword = exclusionKeywords.stream()
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

            // Send webhook notification if enabled
            if (properties.getIntegration().isWebhookEnabled() &&
                    properties.getIntegration().getWebhookUrl() != null) {
                sendWebhookNotification(airdropTweet);
            }

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
        List<String> positiveKeywords = properties.getKeywords().getPositive();
        List<String> negativeKeywords = properties.getKeywords().getNegative();

        if (positiveKeywords.isEmpty()) {
            return repository.findFilteredAirdrops();
        }

        // Collect results for each positive keyword
        List<AirdropTweet> results = new ArrayList<>();

        for (String positiveKeyword : positiveKeywords) {
            // Prepare negative keywords (up to 5)
            String neg1 = negativeKeywords.size() > 0 ? negativeKeywords.get(0) : "";
            String neg2 = negativeKeywords.size() > 1 ? negativeKeywords.get(1) : "";
            String neg3 = negativeKeywords.size() > 2 ? negativeKeywords.get(2) : "";
            String neg4 = negativeKeywords.size() > 3 ? negativeKeywords.get(3) : "";
            String neg5 = negativeKeywords.size() > 4 ? negativeKeywords.get(4) : "";

            List<AirdropTweet> keywordResults = repository.findTweetsByKeywordFiltered(
                    positiveKeyword,
                    negativeKeywords.isEmpty() ? null : negativeKeywords,
                    neg1, neg2, neg3, neg4, neg5);

            // Add unique results
            for (AirdropTweet tweet : keywordResults) {
                if (!results.contains(tweet)) {
                    results.add(tweet);
                }
            }
        }

        return results;
    }

    /**
     * Searches for airdrop tweets containing a specific keyword.
     *
     * @param keyword The keyword to search for
     * @return A list of matching tweets
     */
    @Transactional(readOnly = true)
    public List<AirdropTweet> searchByKeyword(String keyword) {
        return repository.findByKeyword(keyword);
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
            for (String keyword : properties.getKeywords().getPositive()) {
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

    /**
     * Sends a webhook notification when a new airdrop is detected.
     * This is a simple implementation that can be expanded in the future.
     *
     * @param airdropTweet The detected airdrop tweet
     */
    private void sendWebhookNotification(AirdropTweet airdropTweet) {
        log.info("Would send webhook notification for airdrop: {} to URL: {}",
                airdropTweet.getTitle(),
                properties.getIntegration().getWebhookUrl());

        // TODO: Implement actual webhook notification in future versions
        // This would typically use WebClient, RestTemplate, or a dedicated webhook library
    }
} 