package com.aps.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "watchlists")
@Data
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // @Column(name = "created_at", nullable = false, updatable = false)
    // private LocalDateTime createdAt = LocalDateTime.now();

    // Bidirectional mapping (optional)
    @OneToMany(mappedBy = "watchlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stock> stocks;

    
}
