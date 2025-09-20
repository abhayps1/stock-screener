package com.aps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aps.entity.Watchlist;
import com.aps.repository.WatchlistRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WatchlistService {

    @Autowired
    private WatchlistRepository watchlistRepository;

    public List<String> getAllWatchlistNames() {
        return watchlistRepository.getAllWatchlistNames();
    }

    public Optional<Watchlist> getWatchlistById(Long id) {
        return watchlistRepository.findById(id);
    }

    public Watchlist createWatchlist(Watchlist watchlist) {
        return watchlistRepository.save(watchlist);
    }

    public Watchlist createWatchlistByName(String newWatchlistName) {
        Watchlist watchlist = new Watchlist();
        watchlist.setName(newWatchlistName);
        return watchlistRepository.save(watchlist);
    }

    public Watchlist updateWatchlist(Long id, Watchlist updatedWatchlist) {
        return watchlistRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedWatchlist.getName());
                    return watchlistRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Watchlist not found with id: " + id));
    }

    public void deleteWatchlist(Long id) {
        if (!watchlistRepository.existsById(id)) {
            throw new RuntimeException("Watchlist not found with id: " + id);
        }
        watchlistRepository.deleteById(id);
    }
}