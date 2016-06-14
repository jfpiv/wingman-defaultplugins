package com.wingman.defaultplugins.devutils.game;

public enum AttackOption {

    DEPENDS_ON_COMBAT (0),
    RIGHT_CLICK (1),
    LEFT_CLICK (2),
    HIDDEN (3);

    public final int id;

    AttackOption(int id) {
        this.id = id;
    }
}
