package com.wingman.defaultplugins.devutils.game;

import com.wingman.client.api.generated.Static;

import java.awt.*;

public class Perspective {

    public static int[] SIN_TABLE = new int[0x4000];
    public static int[] COS_TABLE = new int[0x4000];

    /**
     * Translates two-dimensional ground coordinates within the 3D world to their corresponding coordinates on the game screen.
     *
     * @param x ground coordinate on the x axis
     * @param y ground coordinate on the y axis
     * @param clientPlane the client plane, ground level
     * @param zOffset the offset from the ground
     * @return a {@link Point} on screen corresponding to the position in 3D-space
     */
    public static Point worldToScreen(int x, int y, int clientPlane, int zOffset) {
        if (x >= 128 && y >= 128 && x <= 13056 && y <= 13056) {
            int z = Scene.getTileHeight(x, y, clientPlane) - clientPlane;
            x -= Static.getCameraX();
            y -= Static.getCameraY();
            z -= Static.getCameraZ();
            z -= zOffset;

            int cameraYaw = Static.getCameraYaw();
            int cameraPitch = Static.getCameraPitch();

            int pitchSin = SIN_TABLE[cameraPitch];
            int pitchCos = COS_TABLE[cameraPitch];
            int yawSin = SIN_TABLE[cameraYaw];
            int yawCos = COS_TABLE[cameraYaw];

            int angle = yawSin * y + yawCos * x >> 16;
            y = yawCos * y - yawSin * x >> 16;
            x = angle;
            angle = pitchCos * z - pitchSin * y >> 16;
            y = pitchSin * z + pitchCos * y >> 16;

            if (y >= 50) {
                int zoom = Static.getViewPortScale();
                x = Static.getViewPortWidth() / 2 + x * zoom / y;
                y = Static.getViewPortHeight() / 2 + angle * zoom / y;
                return new Point(x, y);
            }
        }
        return new Point(-1, -1);
    }

    /**
     * Translates two-dimensional ground coordinates within the 3D world to their corresponding coordinates on the minimap.
     *
     * @param x ground coordinate on the x axis
     * @param y ground coordinate on the y axis
     * @return a {@link Point} on the minimap corresponding to the position in 3D-space
     */
    public static Point worldToMiniMap(int x, int y) {
        int angle = Static.getMapScale() + Static.getMapAngle() & 0x7FF;
        x = x / 32 - Static.getLocalPlayer().getX() / 32;
        y = y / 32 - Static.getLocalPlayer().getY() / 32;

        int dist = x * x + y * y;
        if (dist < 6400) {
            int sin = SIN_TABLE[angle];
            int cos = COS_TABLE[angle];

            sin = sin * 256 / (Static.getMapOffset() + 256);
            cos = cos * 256 / (Static.getMapOffset() + 256);

            int xx = y * sin + cos * x >> 16;
            int yy = sin * x - y * cos >> 16;

            int miniMapX = Static.getAppletWidth() - (!Static.getResizableMode() ? 208 : 167);

            x = (miniMapX + 167 / 2) + xx;
            y = (167 / 2 - 1) + yy;
            return new Point(x, y);
        }
        return new Point(-1, -1);
    }

    /**
     * Initialises the sine and cosine lookup tables required for speedy coordinate calculations
     */
    static {
        for (int i = 0; i < SIN_TABLE.length; i++) {
            SIN_TABLE[i] = (int) (65536.0D * Math.sin((double) i * 0.0030679615D));
            COS_TABLE[i] = (int) (65536.0D * Math.cos((double) i * 0.0030679615D));
        }
    }
}
