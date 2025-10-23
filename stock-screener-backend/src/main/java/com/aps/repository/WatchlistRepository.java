package com.aps.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aps.entity.Watchlist;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, String> {
    
    @Query(value = "SELECT name FROM watchlists", nativeQuery = true)
    List<String> getAllWatchlistNames();
}
