package com.aps.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aps.entity.Watchlist;
import com.aps.service.WatchlistService;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public List<String> getAllWatchlists() {
        return watchlistService.getAllWatchlistNames();
    }

    @GetMapping("/names")
    public List<String> getAllWatchlistNames() {
        return watchlistService.getAllWatchlistNames();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Watchlist> getWatchlistById(@PathVariable Long id) {
        return watchlistService.getWatchlistById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Watchlist> createWatchlist(@RequestBody Watchlist watchlist) {
        Watchlist created = watchlistService.createWatchlist(watchlist);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/create")
    public ResponseEntity<Watchlist> createWatchlistByName(@RequestParam String newWatchlistName) {
        Watchlist created = watchlistService.createWatchlistByName(newWatchlistName);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Watchlist> updateWatchlist(@PathVariable Long id, @RequestBody Watchlist watchlist) {
        Watchlist updated = watchlistService.updateWatchlist(id, watchlist);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWatchlist(@PathVariable Long id) {
        watchlistService.deleteWatchlist(id);
        return ResponseEntity.noContent().build();
    }
}
