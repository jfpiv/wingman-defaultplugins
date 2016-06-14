package com.wingman.defaultplugins.devutils.game;

import com.wingman.client.api.generated.Static;
import com.wingman.client.api.generated.Widget;

public class Inventory {

    public static Widget inventoryWidget = Static.getWidgets()[149][0];

    /**
     * Gets the item IDs of the items present in the inventory
     *
     * @return an array of items IDs in the inventory
     */
    public static int[] getItemIds() {
        try {
            return inventoryWidget.getItemIds();
        } catch (NullPointerException e) {
            return new int[] {};
        }
    }

    /**
     * Gets the item quantities of the items present in the inventory
     *
     * @return an array of items quantities in the inventory
     */
    public static int[] getItemQuantities() {
        // TODO: Implement this method
        return null;
    }

    /**
     * Gets whether an item is in the inventory
     *
     * @param itemId the ID of the item
     * @return {@code true} if the item was successfully found in the inventory;
     *         {@code false} if it was not found
     */
    public static boolean contains(int itemId) {
        for(int id : getItemIds()){
            if(id == itemId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the amount of a specific item in the inventory
     *
     * @param itemId an item ID
     * @return amount of the item in the inventory
     */
    public static int countItem(int itemId) {
        int count = 0;
        int[] ids = getItemIds();
        int[] quantities = getItemQuantities();
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == itemId) {
                if (quantities[i] > 1) {
                    count += quantities[i];
                } else {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Counts the amount of inventory slots currently filled with items
     *
     * @return the amount of items in the inventory
     */
    public static int size() {
        int count = 0;
        int[] items = getItemIds();
        for (int item : items) {
            if (item != 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the amount of free spaces in the inventory
     *
     * @return amount of free spaces in inventory
     */
    public static int freeSpace() {
        return getCapacity() - size();
    }

    /**
     * Gets whether the inventory is full
     *
     * @return {@code true} if the inventory is full;
     *         {@code false} if it is not full
     */
    public static boolean isFull() {
        return size() >= 28;
    }

    /**
     * @return the maximum capacity of the inventory
     */
    public static int getCapacity() {
        return 28;
    }
}
