package com.gildedrose;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class responsible for providing the correct item update strategy
 * based on the item's name. This decouples the main GildedRose class from
 * the concrete updater implementations, adhering to the Factory design pattern.
 */
public class ItemUpdaterFactory {

    private static final Map<String, ItemUpdater> UPDATERS = new HashMap<>();
    private static final ItemUpdater DEFAULT_UPDATER = new NormalItemUpdater();

    // Statically initialize the map of specific updaters.
    // This is more efficient than a long if/else or switch statement.
    static {
        UPDATERS.put("Aged Brie", new AgedBrieUpdater());
        UPDATERS.put("Backstage passes to a TAFKAL80ETC concert", new BackstagePassUpdater());
        UPDATERS.put("Sulfuras, Hand of Ragnaros", new SulfurasUpdater());
    }

    /**
     * Retrieves the appropriate {@link ItemUpdater} for the given item.
     * <p>
     * It uses the item's name as a key to look up the correct strategy. If no specific
     * strategy is found for the item name, it returns a default updater for normal items.
     *
     * @param item The item for which to find an update strategy. Must not be null.
     * @return The corresponding {@link ItemUpdater} instance. Never null.
     */
    public static ItemUpdater getUpdater(Item item) {
        return UPDATERS.getOrDefault(item.name, DEFAULT_UPDATER);
    }
} 