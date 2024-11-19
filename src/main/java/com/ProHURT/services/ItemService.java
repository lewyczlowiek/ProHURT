package com.ProHURT.services;

import com.ProHURT.entities.Item;
import com.ProHURT.entities.ItemCategory;
import com.ProHURT.exceptions.ResourceNotFoundException;
import com.ProHURT.repositories.CategoryRepository;
import com.ProHURT.repositories.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    //Get Item
    public Item getItemById(Long id) throws ResourceNotFoundException {
        return itemRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("Item wasn't found!!!"));
    }


    //Create item
    public Item createItem(Item item) {return itemRepository.save(item);}

    //Add category to item
    public Item addCategoryToItem(Long itemId, Long categoryId) throws ResourceNotFoundException {
        Item item = itemRepository.findById(itemId).orElseThrow(()-> new ResourceNotFoundException("Item wasn't found!!"));
        ItemCategory category = categoryRepository.findById(categoryId).orElseThrow(()-> new ResourceNotFoundException("Category wasn't found!"));
       category.addItem(item);
       categoryRepository.save(category);
       return itemRepository.save(item);
    }

    //Update existing item
    public Item updateItem(Long id, Item item) throws ResourceNotFoundException {
        if(itemRepository.existsById(id)) {
            item.setId(id);
            return  itemRepository.save(item);
        } else {
            throw new ResourceNotFoundException("Item wasn't found!");
        }
    }

    //Delete existing item
    public void deleteItem(Long id) throws ResourceNotFoundException {
        if(itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Item wasn't found!");
        }
    }

    public void updateQuantity(Long id, int quantity) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        item.setQuantity(quantity);
        itemRepository.save(item);
    }

    //Add item to category
    public Item addItemToCategory(Long itemId, Long categoryId) throws ResourceNotFoundException {
        Item item = itemRepository.findById(itemId).orElseThrow(()-> new ResourceNotFoundException("Item not found!"));
        ItemCategory category = categoryRepository.findById(categoryId).orElseThrow(()-> new ResourceNotFoundException("Categroy wasn't found"));
        category.addItem(item);
        categoryRepository.save(category);
        return itemRepository.save(item);
    }

    //Get category by Item
    public ItemCategory getCategoryByItemId(Long itemId) throws ResourceNotFoundException {
        Item item =itemRepository.findById(itemId).orElseThrow(()-> new ResourceNotFoundException("Item not found!"));
        return item.getCategory();
    }

    //Get all items
    public List<Item> getAllItems() {return itemRepository.findAll();}

    public List<Item> searchByName(String name) {return itemRepository.findByNameContainingIgnoreCase(name);}

    public List<Item> getItemsSortedByQuantity(boolean ascending) {
        return ascending ? itemRepository.findAllByOrderByQuantityAsc() : itemRepository.findAllByOrderByQuantityDesc();
    }

    public List<Item> getItemsByCategory(String categoryName) {
        return itemRepository.findByCategoryName(categoryName);
    }

    public List<ItemCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
}
