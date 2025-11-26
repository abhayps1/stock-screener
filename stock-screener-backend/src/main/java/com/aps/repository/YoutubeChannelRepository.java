
package com.aps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aps.entity.YoutubeChannel;

@Repository
public interface YoutubeChannelRepository extends JpaRepository<YoutubeChannel, String> {
    
}