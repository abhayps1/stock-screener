package com.aps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aps.entity.Watchlist;
import com.aps.repository.WatchlistRepository;

import java.util.List;

@Service
public class WatchlistService {

    @Autowired
    private WatchlistRepository watchlistRepository;

    public List<String> getAllWatchlistNames() {
        return watchlistRepository.getAllWatchlistNames();
    }


    public Watchlist createWatchlistByName(String newWatchlistName) {
        if(watchlistRepository.existsById(newWatchlistName)) {
            throw new RuntimeException("Watchlist already exists with name: " + newWatchlistName);
        }
        Watchlist watchlist = new Watchlist();
        watchlist.setName(newWatchlistName);
        return watchlistRepository.save(watchlist);
    }

    // public Watchlist updateWatchlist(Long id, Watchlist updatedWatchlist) {
    //     return watchlistRepository.findById(id)
    //             .map(existing -> {
    //                 existing.setName(updatedWatchlist.getName());
    //                 return watchlistRepository.save(existing);
    //             })
    //             .orElseThrow(() -> new RuntimeException("Watchlist not found with id: " + id));
    // }

    public void deleteWatchlistByName(String watchlistName) {
        if (!watchlistRepository.existsById(watchlistName)) {
            throw new RuntimeException("Watchlist not found with name: " + watchlistName);
        }
        watchlistRepository.deleteById(watchlistName);
    }
}
