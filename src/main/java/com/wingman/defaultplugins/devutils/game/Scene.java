package com.wingman.defaultplugins.devutils.game;

import com.wingman.client.api.generated.Landscape;
import com.wingman.client.api.generated.LandscapeTile;
import com.wingman.client.api.generated.Static;

import java.awt.*;

public class Scene {

    public static Landscape region = Static.getLandscape();
    public static LandscapeTile[][][] tiles = region.getLandscapeTiles();
    public static byte[][][] tileSettings = Static.getTileSettings();
    public static int[][][] tileHeights = Static.getTileHeights();

    /**
     * Calculates the above ground height of a tile point
     *
     * @param x ground coordinate on the x axis
     * @param y ground coordinate on the y axis
     * @param plane the client plane, ground level
     * @return the offset from the ground of the tile
     */
    public static int getTileHeight(int x, int y, int plane) {
        int x2 = x >> 7;
        int y2 = y >> 7;
        if (x2 > 0 && y2 > 0 && x2 < 103 && y2 < 103) {
            if (plane < 3 && (tileSettings[1][x2][y2] & 0x2) == 2) {
                plane++;
            }

            int aa = tileHeights[plane][x2][y2] * (128 - (x & 0x7F)) + tileHeights[plane][x2 + 1][y2] * (x & 0x7F) >> 7;
            int ab = tileHeights[plane][x2][y2 + 1] * (128 - (x & 0x7F)) + tileHeights[plane][x2 + 1][y2 + 1] * (x & 0x7F) >> 7;
            return aa * (128 - (y & 0x7F)) + ab * (y & 0x7F) >> 7;
        }
        return 0;
    }

    /**
     * Gets the coordinates of the tile relative to the local player
     *
     * @param tile an instance of tile
     * @return the point in the 3D world the tile is at
     */
    public static Point getTilePosition(LandscapeTile tile) {
        return new Point(tile.getX() * 128, tile.getY() * 128);
    }

    /**
     * Calculates the distance in coordinates between two two-dimensional points
     *
     * @param x first x coordinate
     * @param y first y coordinate
     * @param x2 second x coordinate
     * @param y2 second y coordinate
     * @return distance between the two points
     */
    public static int distanceBetween(int x, int y, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2));
    }
}

