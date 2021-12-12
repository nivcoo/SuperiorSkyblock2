package com.bgsoftware.superiorskyblock.menu.button.impl;

import com.bgsoftware.superiorskyblock.menu.SuperiorMenu;
import com.bgsoftware.superiorskyblock.menu.button.SuperiorMenuButton;
import com.bgsoftware.superiorskyblock.utils.items.ItemBuilder;
import com.bgsoftware.superiorskyblock.wrappers.SoundWrapper;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public final class CurrentPageButton extends SuperiorMenuButton {

    private CurrentPageButton(ItemBuilder buttonItem, SoundWrapper clickSound, List<String> commands,
                              String requiredPermission, SoundWrapper lackPermissionSound) {
        super(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound);
    }

    @Override
    public void onButtonClick(SuperiorMenu superiorMenu, InventoryClickEvent clickEvent) {
        // Dummy button, doesn't do anything when clicked.
    }

    public static class Builder extends AbstractBuilder<Builder, CurrentPageButton> {

        @Override
        public CurrentPageButton build() {
            return new CurrentPageButton(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound);
        }

    }

}