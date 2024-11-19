package com.ProHURT.services;

import com.ProHURT.entities.Item;
import com.ProHURT.entities.ItemCategory;
import com.ProHURT.exceptions.ResourceNotFoundException;
import com.ProHURT.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private CategoryService(CategoryRepository categoryRepository) {this.categoryRepository = categoryRepository;}

    //Create new ItemCategory
    public ItemCategory createCategory(ItemCategory itemCategory) {return categoryRepository.save(itemCategory);}

    //Update an existing ItemCategory
    public ItemCategory updateCategory(Long categoryId, ItemCategory updatedItemCategory) throws ResourceNotFoundException {
        if (categoryRepository.existsById(categoryId)) {
            updatedItemCategory.setId(categoryId);
            return categoryRepository.save(updatedItemCategory);
        } else {
            throw new ResourceNotFoundException("ItemCategory not found!");
        }
    }

    //Delete existing category
    public void deleteCategory(Long categoryId) throws ResourceNotFoundException {
        if(categoryRepository.existsById(categoryId)) {
            categoryRepository.deleteById(categoryId);
        } else {
            throw new ResourceNotFoundException("Item not found!");
        }
    }

    public ItemCategory getCategoryById(Long categoryId) throws ResourceNotFoundException {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("ItemCategory not found for this id :: " + categoryId));
    }

    //Get all categories
    public List<ItemCategory> getAllCategories() {return categoryRepository.findAll();}

}
