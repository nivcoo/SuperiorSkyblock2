package com.bgsoftware.superiorskyblock.menu.button.impl.menu;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.enums.BorderColor;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.menu.SuperiorMenu;
import com.bgsoftware.superiorskyblock.menu.button.SuperiorMenuButton;
import com.bgsoftware.superiorskyblock.menu.impl.MenuBorderColor;
import com.bgsoftware.superiorskyblock.utils.StringUtils;
import com.bgsoftware.superiorskyblock.utils.items.ItemBuilder;
import com.bgsoftware.superiorskyblock.utils.threads.Executor;
import com.bgsoftware.superiorskyblock.wrappers.SoundWrapper;
import com.google.common.base.Preconditions;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public final class BorderColorButton extends SuperiorMenuButton {

    private final BorderColor borderColor;

    private BorderColorButton(ItemBuilder buttonItem, SoundWrapper clickSound, List<String> commands,
                              String requiredPermission, SoundWrapper lackPermissionSound, BorderColor borderColor) {
        super(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound);
        this.borderColor = borderColor;
    }

    @Override
    public void onButtonClick(SuperiorSkyblockPlugin plugin, SuperiorMenu superiorMenu, InventoryClickEvent clickEvent) {
        Preconditions.checkArgument(superiorMenu instanceof MenuBorderColor, "superiorMenu must be MenuBorderColor");

        SuperiorPlayer clickedPlayer = plugin.getPlayers().getSuperiorPlayer(clickEvent.getWhoClicked());

        if (!clickedPlayer.hasWorldBorderEnabled())
            clickedPlayer.toggleWorldBorder();

        clickedPlayer.setBorderColor(borderColor);
        plugin.getNMSWorld().setWorldBorder(clickedPlayer,
                plugin.getGrid().getIslandAt(clickedPlayer.getLocation()));

        Locale.BORDER_PLAYER_COLOR_UPDATED.send(clickedPlayer,
                StringUtils.format(clickedPlayer.getUserLocale(), borderColor));

        Executor.sync(superiorMenu::closePage, 1L);
    }

    public static class Builder extends AbstractBuilder<Builder, BorderColorButton> {

        private BorderColor borderColor;

        public Builder setBorderColor(BorderColor borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        @Override
        public BorderColorButton build() {
            return new BorderColorButton(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, borderColor);
        }

    }

}