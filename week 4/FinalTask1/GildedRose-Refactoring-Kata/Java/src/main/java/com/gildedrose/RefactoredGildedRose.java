package com.gildedrose;

/**
 * The GildedRose class manages a collection of inventory items.
 * Its primary responsibility is to update the 'quality' and 'sellIn' properties
 * of each item based on a set of predefined business rules.
 * <p>
 * This refactored version uses the Strategy and Factory design patterns to
 * delegate the update logic for each item type to a specific handler class.
 * This approach greatly improves maintainability, testability, and adherence
 * to the Open/Closed Principle.
 */
public class RefactoredGildedRose {
    Item[] items;

    /**
     * Constructs a new RefactoredGildedRose instance.
     *
     * @param items An array of {@link Item} objects to be managed.
     * @throws IllegalArgumentException if the provided items array is null.
     */
    public RefactoredGildedRose(Item[] items) {
        // FIX: Poor Error Handling. Added a null check to prevent NullPointerException.
        if (items == null) {
            throw new IllegalArgumentException("Items array cannot be null.");
        }
        this.items = items;
    }

    /**
     * Updates the quality and sell-in value for every item in the inventory.
     * <p>
     * This method iterates through each item and uses a factory to obtain the
     * appropriate update strategy for the item's type. It then applies that strategy
     * to update the item's properties. This design delegates complex conditional
     * logic to individual strategy classes, making the system cleaner and easier to maintain.
     *
     * @see ItemUpdaterFactory
     * @see ItemUpdater
     */
    public void updateQuality() {
        // FIX: Systematic Refactoring & Performance Optimization.
        // The single loop with a factory call is highly efficient. It avoids all the
        // nested conditional logic and redundant string comparisons of the original implementation.
        for (Item item : items) {
            ItemUpdater updater = ItemUpdaterFactory.getUpdater(item);
            updater.update(item);
        }
    }
} 