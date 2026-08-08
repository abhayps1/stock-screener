package com.aps.repository;

import com.aps.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository  extends JpaRepository<Result, String> {
}
