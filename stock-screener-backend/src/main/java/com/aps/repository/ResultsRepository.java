package com.aps.repository;

import com.aps.entity.Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResultsRepository extends JpaRepository<Results, String>{

    @Query("SELECT r FROM Results r ORDER BY r.resultDate DESC")
    List<Results> findAllOrderByResultDateDesc();
}
