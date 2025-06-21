package com.gildedrose;

/**
 * Implements the update strategy for a normal, standard inventory item.
 * <p>
 * Rules for normal items:
 * <ul>
 *   <li>Quality decreases by 1 each day.</li>
 *   <li>Once the sell-by date has passed, Quality degrades twice as fast (decreases by 2).</li>
 *   <li>The Quality of an item is never negative.</li>
 * </ul>
 */
public class NormalItemUpdater implements ItemUpdater {
    @Override
    public void update(Item item) {
        // Decrease quality, but not below 0
        if (item.quality > 0) {
            item.quality = item.quality - 1;
        }

        // Decrease sellIn date
        item.sellIn = item.sellIn - 1;

        // If past sell-by date, decrease quality again
        if (item.sellIn < 0) {
            if (item.quality > 0) {
                item.quality = item.quality - 1;
            }
        }
    }
} 