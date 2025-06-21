package com.gildedrose;

/**
 * Defines the contract for an item update strategy. Each concrete implementation
 * of this interface will contain the specific logic for updating a particular
 * type of item. This is the core of the Strategy design pattern.
 */
public interface ItemUpdater {
    /**
     * Updates the quality and sellIn properties of the given item according to
     * its specific business rules.
     *
     * @param item The item to be updated. It cannot be null.
     */
    void update(Item item);
} 