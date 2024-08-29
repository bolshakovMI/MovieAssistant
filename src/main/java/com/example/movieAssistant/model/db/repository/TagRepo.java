package com.example.movieAssistant.model.db.repository;

import com.example.movieAssistant.model.db.entity.Tag;
import com.example.movieAssistant.model.db.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepo extends JpaRepository<Tag, Long> {
    void deleteAllByWish(Wish wish);
}
