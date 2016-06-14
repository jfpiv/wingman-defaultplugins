package com.wingman.defaultplugins.devutils.game;

import com.wingman.client.api.generated.Static;

public class GameSettings {

    public static boolean isChatTransparent() {
        return (Static.getGameSettings()[1055] & 512) == 0;
    }

    public static boolean isChatClickable() {
        return (Static.getGameSettings()[1055] & 2048) == 0;
    }

    public static boolean isSidePanelTransparent() {
        return (Static.getGameSettings()[1055] & 1024) == 0;
    }

    public static boolean isSidePanelClosableByHotKeys() {
        return (Static.getGameSettings()[1055] & 4096) == 0;
    }

    public static boolean areSideButtonsInTheBottom() {
        return (Static.getGameSettings()[1055] & 256) != 0;
    }

    public static boolean areDataOrbsEnabled() {
        return (Static.getGameSettings()[1055] & 8) != 0;
    }

    public static boolean isRunOn() {
        return Static.getGameSettings()[173] == 1;
    }

    public static boolean areChatEffectsOn() {
        return Static.getGameSettings()[171] == 0;
    }

    public static boolean isAcceptAidOn() {
        return Static.getGameSettings()[427] == 1;
    }

    public static boolean isProfanityFilterOn() {
        return Static.getGameSettings()[1074] == 0;
    }

    public static boolean isPrivateChatSplit() {
        return Static.getGameSettings()[287] == 1;
    }

    public static boolean isPrivateChatHiddenWhenChatBoxIsHidden() {
        return (Static.getGameSettings()[1055] & 16384) != 0;
    }

    public static boolean isLoginLogoutNotificationTimeoutOn() {
        return (Static.getGameSettings()[1055] & 128) == 0;
    }

    public static boolean isMouseCameraOn() {
        return (Static.getGameSettings()[1055] & 32) == 0;
    }

    public static boolean isNumberOfMouseButtonsTwo() {
        return Static.getGameSettings()[170] == 0;
    }

    public static boolean isMusicLooping() {
        return Static.getGameSettings()[19] == 1;
    }

    public static boolean isMusicPlayingAutomatically() {
        return Static.getGameSettings()[18] == 1;
    }

    public static boolean isAutoRetaliateOn() {
        return Static.getGameSettings()[172] == 0;
    }

    public static AttackOption getPlayerAttackOption() {
        return AttackOption.values()[Static.getGameSettings()[1107]];
    }

    public static AttackOption getNpcAttackOption() {
        return AttackOption.values()[Static.getGameSettings()[1306]];
    }

    public static int getBrightnessLevel() {
        return Static.getGameSettings()[166];
    }

    public static int getMusicLevel() {
        return Static.getGameSettings()[168];
    }

    public static int getSoundEffectLevel() {
        return Static.getGameSettings()[169];
    }

    public static int getAreaSoundEffectLevel() {
        return Static.getGameSettings()[872];
    }

    public static int getAttackStyle() {
        return Static.getGameSettings()[43];
    }

    public static int getOpenSkillManual() {
        return Static.getGameSettings()[965];
    }
}
