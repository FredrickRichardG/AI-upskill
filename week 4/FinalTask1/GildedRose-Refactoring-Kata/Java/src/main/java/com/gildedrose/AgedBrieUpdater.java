package com.gildedrose;

/**
 * Implements the update strategy for "Aged Brie".
 * <p>
 * Rules for Aged Brie:
 * <ul>
 *   <li>Quality increases by 1 each day.</li>
 *   <li>Quality increases by 2 each day after the sell-by date.</li>
 *   <li>The Quality of an item is never more than 50.</li>
 * </ul>
 */
public class AgedBrieUpdater implements ItemUpdater {
    @Override
    public void update(Item item) {
        // Increase quality, but not over 50
        if (item.quality < 50) {
            item.quality = item.quality + 1;
        }

        // Decrease sellIn date
        item.sellIn = item.sellIn - 1;

        // If past sell-by date, increase quality again
        if (item.sellIn < 0) {
            if (item.quality < 50) {
                item.quality = item.quality + 1;
            }
        }
    }
} 