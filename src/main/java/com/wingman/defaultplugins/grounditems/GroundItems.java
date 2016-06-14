package com.wingman.defaultplugins.grounditems;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.wingman.client.api.enums.GameState;
import com.wingman.client.api.events.rendering.AbstractRenderHook;
import com.wingman.client.api.events.rendering.RenderHook;
import com.wingman.client.api.generated.*;
import com.wingman.client.api.plugin.Plugin;
import com.wingman.client.api.plugin.PluginDependency;
import com.wingman.client.api.plugin.PluginHelper;
import com.wingman.client.api.settings.PropertiesSettings;
import com.wingman.client.api.ui.SettingsSection;
import com.wingman.client.api.ui.SettingsSectionDesigner;
import com.wingman.defaultplugins.devutils.game.Perspective;
import com.wingman.defaultplugins.devutils.game.Scene;
import com.wingman.defaultplugins.devutils.util.ItemPriceCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

@PluginDependency(
        id = "DevUtils",
        minVersion = "1.0.0"
)
@Plugin(
        id = "GroundItems",
        name = "Ground Items",
        description = "Diplays the item name, quantity and price of an item on the ground.",
        version = "1.0.0"
)
public class GroundItems {

    @Plugin.Helper
    public PluginHelper helper;

    private PropertiesSettings settings;
    private boolean miniMapDotEnabled = true;

    private int MINIMUM_VERY_EXPENSIVE = 1_000_000;
    private int MINIMUM_EXPENSIVE = 50_000;

    private Color COLOR_VERY_EXPENSIVE = new Color(212, 175, 55);
    private Color COLOR_EXPENSIVE = new Color(50, 205, 50);
    private Color COLOR_UNTRADABLE = new Color(255, 20, 147);
    private Color COLOR_LOW_PRICE = new Color(255, 255, 255);

    private Font textFont = new Font("Verdana", Font.PLAIN, 11);
    private FontMetrics fontMetrics = null;
    private final StringBuilder itemStringBuilder = new StringBuilder();
    private AbstractRenderHook renderHook;

    @Plugin.Activate
    public void activate() {
        addSettings();

        renderHook = new AbstractRenderHook() {
            @Override
            public boolean display(Graphics graphics) {
                if (Static.getGameState() != GameState.PLAYING) {
                    return false;
                }

                ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

                graphics.setFont(textFont);
                if (fontMetrics == null) {
                    fontMetrics = graphics.getFontMetrics();
                }

                int plane = Static.getClientPlane();

                for (LandscapeTile[] planeTiles : Scene.tiles[plane]) {
                    for (LandscapeTile tile : planeTiles) {
                        if (tile != null) {
                            ItemLayer itemLayer = tile.getItemLayer();
                            if (itemLayer != null) {
                                int x = itemLayer.getX();
                                int y = itemLayer.getY();

                                Point point = Perspective.worldToScreen(x, y, plane, itemLayer.getHeight());
                                if (point.x != -1 && point.y != -1) {
                                    ArrayList<Integer> itemIds = new ArrayList<>();
                                    Map<Integer, Integer> itemQuantities = new HashMap<>();

                                    Node bottomLayer = itemLayer.getBottom();
                                    addItemToMap((GroundItem) bottomLayer, itemIds, itemQuantities);
                                    Node next = bottomLayer.getNext();
                                    while (next != bottomLayer && next instanceof GroundItem) {
                                        addItemToMap((GroundItem) next, itemIds, itemQuantities);
                                        next = next.getNext();
                                    }

                                    Collections.reverse(itemIds);

                                    for (int i = 0; i < itemIds.size(); i++) {
                                        Integer id = itemIds.get(i);
                                        Integer qty = itemQuantities.get(id);
                                        String itemName;
                                        long price;

                                        if (id == 995) {
                                            price = qty;
                                            itemName = "Coins";
                                            itemStringBuilder.append(MessageFormat.format("{0} ({1})", itemName, formatValue(price)));
                                        } else {
                                            if (qty > 1) {
                                                if (qty == 65535) {
                                                    itemStringBuilder.append("Lots of ");
                                                } else {
                                                    itemStringBuilder.append(qty).append("x ");
                                                }
                                            }

                                            itemName = Static.getItemDefinition(id).getName();
                                            itemStringBuilder.append(itemName);

                                            price = ItemPriceCache.getItemPrice(id) * qty;
                                            if (price > 0) {
                                                itemStringBuilder.append(MessageFormat.format(" ({0})", formatValue(price)));
                                            }
                                        }

                                        String itemString = itemStringBuilder.toString();
                                        itemStringBuilder.setLength(0);
                                        int screenX = point.x + 2 - (fontMetrics.stringWidth(itemString) / 2);

                                        graphics.setColor(Color.BLACK);
                                        graphics.drawString(itemString, screenX + 1, point.y - (15 * i) + 1);
                                        graphics.setColor(getValueColor(price));
                                        graphics.drawString(itemString, screenX, point.y - (15 * i));

                                        if (miniMapDotEnabled) {
                                            if (price >= MINIMUM_EXPENSIVE) {
                                                if (itemName != null) {
                                                    Point miniMapPoint = Perspective.worldToMiniMap(x, y);
                                                    graphics.setColor(Color.BLACK);
                                                    graphics.drawString(itemName, miniMapPoint.x + 1, miniMapPoint.y + 1);
                                                    graphics.setColor(getValueColor(price));
                                                    graphics.drawString(itemName, miniMapPoint.x, miniMapPoint.y);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return false;
            }
        };
        RenderHook.renderHooks.add(renderHook);
    }

    private void addSettings() {
        final String EXPENSIVE_MINIMAP_MARKING = "expensive_minimap_marking";

        try {
            settings = new PropertiesSettings(helper.getContainer().pluginData.id() + ".properties",
                    helper.getContainer().pluginData.name() + " - " + helper.getContainer().pluginData.description()) {
                @Override
                public void checkKeys() {
                    Map<String, Object> defaultProperties = ImmutableMap.<String, Object>builder()
                            .put(EXPENSIVE_MINIMAP_MARKING, "true")
                            .build();

                    if (properties.isEmpty()) {
                        for (Map.Entry<String, Object> e : defaultProperties.entrySet()) {
                            properties.put(e.getKey(), e.getValue());
                        }
                    } else {
                        for (Map.Entry<String, Object> e : defaultProperties.entrySet()) {
                            if (!properties.containsKey(e.getKey())) {
                                properties.put(e.getKey(), e.getValue());
                            }
                        }

                        Set<String> keysToRemove = new HashSet<>();
                        for (Map.Entry<Object, Object> e : properties.entrySet()) {
                            String key = (String) e.getKey();
                            if (!defaultProperties.containsKey(key)) {
                                keysToRemove.add(key);
                            }
                        }
                        for (String k : keysToRemove) {
                            properties.remove(k);
                        }
                    }
                }
            };
        } catch (IOException e) {
            Throwables.propagate(e);
        }

        SettingsSection settingsSection = new SettingsSection();
        settingsSection.sideText = helper.getContainer().pluginData.name();

        ArrayList<JPanel> settingsList = new ArrayList<>();

        JCheckBox expensiveMiniMapDot = new JCheckBox();
        expensiveMiniMapDot.setSelected(settings.getBoolean(EXPENSIVE_MINIMAP_MARKING));
        expensiveMiniMapDot.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                miniMapDotEnabled = e.getStateChange() == ItemEvent.SELECTED;
                settings.update(EXPENSIVE_MINIMAP_MARKING, miniMapDotEnabled ? "true" : false);
            }
        });
        settingsList.add(SettingsSectionDesigner.createSettingsRow("Mark expensive items on the minimap", expensiveMiniMapDot));

        SettingsSectionDesigner.design(settingsSection, settingsList, true);
        settingsSection.register();
    }

    private void addItemToMap(GroundItem item, ArrayList<Integer> itemIds, Map<Integer, Integer> itemQuantities) {
        int id = item.getId();
        if (itemIds.contains(id)) {
            itemQuantities.put(id, itemQuantities.get(id) + item.getQuantity());
        } else {
            itemIds.add(id);
            itemQuantities.put(id, item.getQuantity());
        }
    }

    private Color getValueColor(long amount) {
        if (amount >= MINIMUM_VERY_EXPENSIVE) {
            return COLOR_VERY_EXPENSIVE;
        } else if (amount >= MINIMUM_EXPENSIVE) {
            return COLOR_EXPENSIVE;
        } else if (amount == 0) {
            return COLOR_UNTRADABLE;
        }
        return COLOR_LOW_PRICE;
    }

    private String formatValue(long n) {
        if (n == 65535) {
            return "65K+";
        } else if (n >= 1000) {
            return formatNumber(n);
        } else {
            return formatNumber(n) + "gp";
        }
    }

    private String formatNumber(double n) {
        return formatNumber(n, 0);
    }

    private String[] suffixes = {"", "K", "M", "B", "T", "Q", "W", "T", "F"};

    private String formatNumber(double n, int iteration) {
        if (n >= 1000) {
            return formatNumber(((int) (n / 100D)) / 10D, iteration + 1);
        } else {
            return ((int) n) + suffixes[iteration];
        }
    }

    @Plugin.Deactivate
    public void deactivate() {
        if (renderHook != null) {
            renderHook.remove();
            renderHook = null;
        }
        if (settings != null) {
            settings.save();
        }
    }
}
