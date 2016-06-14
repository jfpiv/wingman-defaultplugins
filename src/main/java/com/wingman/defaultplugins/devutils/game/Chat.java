package com.wingman.defaultplugins.devutils.game;

import com.wingman.client.api.generated.Static;

public class Chat {

    public static void repaintChatBox() {
        Static.Unsafe.setRepaintChatBox(Static.getRepaintFlag());
    }
}
