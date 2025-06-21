package com.gildedrose;

/**
 * Implements the update strategy for "Sulfuras".
 * <p>
 * "Sulfuras" is a legendary item and its Quality and sellIn date never change.
 * Therefore, the update method for this class is intentionally left empty.
 */
public class SulfurasUpdater implements ItemUpdater {
    @Override
    public void update(Item item) {
        // Legendary items do not change. This method is a no-op.
    }
} 