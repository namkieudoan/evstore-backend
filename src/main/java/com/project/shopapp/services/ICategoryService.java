package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import org.springframework.stereotype.Service;

import java.util.List;
public interface ICategoryService {
    Category createCategory(CategoryDTO categoryDTO);
    Category getCategoryById(long categoryId);
    List<Category> getAllCategories();
    Category updateCategory(long categoryId, CategoryDTO categoryDTO);
    void deleteCategory(long categoryId);

}
