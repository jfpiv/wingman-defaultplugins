package com.wingman.defaultplugins.devutils.game;

import com.wingman.client.api.generated.ItemContainer;
import com.wingman.client.api.generated.NodeTable;
import com.wingman.client.api.generated.Static;
import com.wingman.client.api.generated.Widget;

public class Bank {

    /**
     * Gets the currently open bank tab
     *
     * @return the currently open bank tab
     */
    public static int getOpenTab() {
        return Static.getGameSettings()[115] / 4;
    }

    /**
     * Gets the item IDs of the items present in the bank
     *
     * @return an array of items IDs in the bank
     */
    public static int[] getItemIds() {
        return getItemContainer().getIds();
    }

    /**
     * Gets the item quantities of the items present in the bank
     *
     * @return an array of items quantities in the bank
     */
    public static int[] getItemQuantities() {
        return getItemContainer().getQuantities();
    }

    /**
     * Gets whether the bank widget is open
     *
     * @return {@code true} if the bank is open;
     *         {@code false} if it isn't
     */
    public static boolean isOpen() {
        return getWidgets() != null;
    }

    public static Widget getWidget() {
        return getWidgets()[4];
    }

    public static Widget[] getWidgets() {
        return Static.getWidgets()[12];
    }

    public static ItemContainer getItemContainer() {
        NodeTable itemContainers = Static.getItemContainers();
        return (ItemContainer) itemContainers.get(95);
    }
}
