package com.azerite.cryptoairdropdiscovery.repository;

import com.azerite.cryptoairdropdiscovery.model.AirdropTweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for AirdropTweet entities.
 */
@Repository
public interface AirdropTweetRepository extends JpaRepository<AirdropTweet, Long> {

    /**
     * Finds all airdrop tweets with a specific keyword in their text.
     *
     * @param keyword The keyword to search for
     * @return A list of matching airdrop tweets
     */
    @Query("SELECT a FROM AirdropTweet a WHERE LOWER(a.rawText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<AirdropTweet> findByKeyword(@Param("keyword") String keyword);

    /**
     * Finds all airdrop tweets that contain any positive keyword and no negative keywords.
     * This is a database-agnostic implementation that works with any JPA provider.
     *
     * @param positiveKeyword  A positive keyword to match
     * @param negativeKeywords List of negative keywords to exclude
     * @return A list of filtered airdrop tweets containing the positive keyword
     */
    @Query("SELECT a FROM AirdropTweet a WHERE " +
            "LOWER(a.rawText) LIKE LOWER(CONCAT('%', :positiveKeyword, '%')) AND " +
            "(:negativeKeywords IS NULL OR (" +
            "   NOT LOWER(a.rawText) LIKE LOWER(CONCAT('%', :negativeKeyword1, '%')) AND " +
            "   NOT LOWER(a.rawText) LIKE LOWER(CONCAT('%', :negativeKeyword2, '%')) AND " +
            "   NOT LOWER(a.rawText) LIKE LOWER(CONCAT('%', :negativeKeyword3, '%')) AND " +
            "   NOT LOWER(a.rawText) LIKE LOWER(CONCAT('%', :negativeKeyword4, '%')) AND " +
            "   NOT LOWER(a.rawText) LIKE LOWER(CONCAT('%', :negativeKeyword5, '%'))" +
            "))")
    List<AirdropTweet> findTweetsByKeywordFiltered(
            @Param("positiveKeyword") String positiveKeyword,
            @Param("negativeKeywords") List<String> negativeKeywords,
            @Param("negativeKeyword1") String negativeKeyword1,
            @Param("negativeKeyword2") String negativeKeyword2,
            @Param("negativeKeyword3") String negativeKeyword3,
            @Param("negativeKeyword4") String negativeKeyword4,
            @Param("negativeKeyword5") String negativeKeyword5);

    /**
     * Legacy method for backward compatibility.
     * This will be used if no configuration properties are provided.
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