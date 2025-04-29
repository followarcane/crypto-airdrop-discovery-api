package com.azerite.cryptoairdropdiscovery.repository;

import com.azerite.cryptoairdropdiscovery.model.AirdropTweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for AirdropTweet entities.
 */
@Repository
public interface AirdropTweetRepository extends JpaRepository<AirdropTweet, Long> {

    /**
     * Finds all airdrop tweets with tasks matching the specified keywords.
     *
     * @return A list of airdrop tweets that contain tasks
     */
    @Query("SELECT a FROM AirdropTweet a WHERE " +
            "(LOWER(a.rawText) LIKE '%follow%' OR " +
            "LOWER(a.rawText) LIKE '%mint%' OR " +
            "LOWER(a.rawText) LIKE '%galxe%' OR " +
            "LOWER(a.rawText) LIKE '%zk%' OR " +
            "LOWER(a.rawText) LIKE '%claim%') AND " +
            "LOWER(a.rawText) NOT LIKE '%scam%' AND " +
            "LOWER(a.rawText) NOT LIKE '%ended%'")
    List<AirdropTweet> findFilteredAirdrops();
} 