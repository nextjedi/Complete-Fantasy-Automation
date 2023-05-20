package com.ap.fantasy.dao;

import com.ap.fantasy.model.MatchDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface MatchRepository extends JpaRepository<MatchDetails,Long> {
    public List<MatchDetails> findByTimeAfter(Date time);
}
