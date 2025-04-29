package com.azerite.cryptoairdropdiscoverysample.controller;

import com.azerite.cryptoairdropdiscovery.model.AirdropTweet;
import com.azerite.cryptoairdropdiscovery.service.AirdropDiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Sample controller that demonstrates how to use the AirdropDiscoveryService.
 */
@RestController
@RequestMapping("/sample")
public class SampleController {

    private final AirdropDiscoveryService airdropService;

    @Autowired
    public SampleController(AirdropDiscoveryService airdropService) {
        this.airdropService = airdropService;
    }

    /**
     * Endpoint to process a tweet manually.
     *
     * @param tweetData The tweet data containing text, source, and link
     * @return The processed airdrop tweet or an error message
     */
    @PostMapping("/process")
    public ResponseEntity<?> processTweet(@RequestBody Map<String, String> tweetData) {
        String text = tweetData.get("text");
        String source = tweetData.get("source");
        String link = tweetData.get("link");

        if (text == null || source == null || link == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        AirdropTweet result = airdropService.processTweet(text, source, link);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body("Not recognized as an airdrop");
        }
    }

    /**
     * Endpoint to get all filtered airdrops.
     *
     * @return List of filtered airdrops
     */
    @GetMapping("/airdrops")
    public ResponseEntity<List<AirdropTweet>> getAirdrops() {
        return ResponseEntity.ok(airdropService.getFilteredAirdrops());
    }

    /**
     * Endpoint to search for airdrops by keyword.
     *
     * @param keyword The keyword to search for
     * @return List of matching airdrops
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchAirdrops(@RequestParam String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return ResponseEntity.badRequest().body("Keyword is required");
        }
        return ResponseEntity.ok(airdropService.searchByKeyword(keyword));
    }
} 