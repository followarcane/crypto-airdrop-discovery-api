package com.azerite.cryptoairdropdiscovery.controller;

import com.azerite.cryptoairdropdiscovery.model.AirdropTweet;
import com.azerite.cryptoairdropdiscovery.service.AirdropDiscoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for airdrop discovery functionality.
 */
@RestController
@RequestMapping("/airdrops")
@RequiredArgsConstructor
@Slf4j
public class AirdropController {

    private final AirdropDiscoveryService airdropDiscoveryService;

    /**
     * Endpoint to ingest a tweet and process it for potential airdrop information.
     *
     * @param payload A map containing the tweet text, source, and link
     * @return The created AirdropTweet or a 400 Bad Request if the tweet is not saved
     */
    @PostMapping("/ingest")
    public ResponseEntity<?> ingestTweet(@RequestBody Map<String, String> payload) {
        String tweetText = payload.get("text");
        String source = payload.get("source");
        String link = payload.get("link");

        if (tweetText == null || source == null || link == null) {
            log.warn("Missing required field in ingest request");
            return ResponseEntity.badRequest().body("Missing required field: text, source, or link");
        }

        AirdropTweet airdropTweet = airdropDiscoveryService.processTweet(tweetText, source, link);

        if (airdropTweet != null) {
            log.info("Successfully ingested and processed tweet: {}", airdropTweet.getTitle());
            return ResponseEntity.ok(airdropTweet);
        } else {
            log.info("Tweet was not recognized as an airdrop or didn't meet criteria");
            return ResponseEntity.badRequest().body("Tweet was not recognized as an airdrop or didn't meet criteria");
        }
    }

    /**
     * Endpoint to get filtered airdrop tweets based on configured keywords.
     *
     * @return A list of AirdropTweet objects that match the filter criteria
     */
    @GetMapping("/filtered")
    public ResponseEntity<List<AirdropTweet>> getFilteredAirdrops() {
        List<AirdropTweet> airdrops = airdropDiscoveryService.getFilteredAirdrops();
        log.info("Returning {} filtered airdrop tweets", airdrops.size());
        return ResponseEntity.ok(airdrops);
    }

    /**
     * Endpoint to search for airdrop tweets by keyword.
     *
     * @param keyword The keyword to search for
     * @return A list of matching airdrop tweets
     */
    @GetMapping("/search")
    public ResponseEntity<List<AirdropTweet>> searchAirdrops(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<AirdropTweet> results = airdropDiscoveryService.searchByKeyword(keyword);
        log.info("Found {} airdrops matching keyword: {}", results.size(), keyword);
        return ResponseEntity.ok(results);
    }
} 