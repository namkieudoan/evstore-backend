package com.project.shopapp.repositories;

import com.project.shopapp.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository đã mac dinh hieu rồi, ko can thiet
public interface CategoryRepository extends JpaRepository<Category,Long> {
}
