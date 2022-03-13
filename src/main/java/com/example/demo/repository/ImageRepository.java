package com.example.demo.repository;

import com.example.demo.Image;
import com.example.demo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByUserID(String userID);
}
