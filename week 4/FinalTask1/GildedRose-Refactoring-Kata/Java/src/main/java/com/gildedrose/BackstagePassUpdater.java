package com.gildedrose;

/**
 * Implements the update strategy for "Backstage passes".
 * <p>
 * Rules for Backstage passes:
 * <ul>
 *   <li>Quality increases by 1 each day.</li>
 *   <li>Quality increases by 2 when there are 10 days or less.</li>
 *   <li>Quality increases by 3 when there are 5 days or less.</li>
 *   <li>Quality drops to 0 after the concert (sell-by date is 0).</li>
 *   <li>The Quality of an item is never more than 50.</li>
 * </ul>
 */
public class BackstagePassUpdater implements ItemUpdater {
    @Override
    public void update(Item item) {
        // Quality increases as sellIn date approaches
        if (item.quality < 50) {
            item.quality = item.quality + 1;

            if (item.sellIn < 11) {
                if (item.quality < 50) {
                    item.quality = item.quality + 1;
                }
            }

            if (item.sellIn < 6) {
                if (item.quality < 50) {
                    item.quality = item.quality + 1;
                }
            }
        }

        // Decrease sellIn date
        item.sellIn = item.sellIn - 1;

        // After the concert, quality drops to 0
        if (item.sellIn < 0) {
            item.quality = 0;
        }
    }
} 