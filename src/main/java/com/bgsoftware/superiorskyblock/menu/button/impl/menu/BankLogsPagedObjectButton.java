package com.bgsoftware.superiorskyblock.menu.button.impl.menu;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.enums.BankAction;
import com.bgsoftware.superiorskyblock.api.island.bank.BankTransaction;
import com.bgsoftware.superiorskyblock.menu.SuperiorMenu;
import com.bgsoftware.superiorskyblock.menu.button.PagedObjectButton;
import com.bgsoftware.superiorskyblock.menu.impl.MenuBankLogs;
import com.bgsoftware.superiorskyblock.utils.StringUtils;
import com.bgsoftware.superiorskyblock.utils.items.ItemBuilder;
import com.bgsoftware.superiorskyblock.wrappers.SoundWrapper;
import com.google.common.base.Preconditions;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.UUID;

public final class BankLogsPagedObjectButton extends PagedObjectButton<BankTransaction> {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();
    private static final UUID CONSOLE_UUID = new UUID(0, 0);

    private BankLogsPagedObjectButton(ItemBuilder buttonItem, SoundWrapper clickSound, List<String> commands,
                                      String requiredPermission, SoundWrapper lackPermissionSound,
                                      ItemBuilder nullItem) {
        super(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound, nullItem);
    }

    @Override
    public void onButtonClick(SuperiorMenu superiorMenu, InventoryClickEvent clickEvent) {
        Preconditions.checkArgument(superiorMenu instanceof MenuBankLogs, "superiorMenu must be MenuBankLogs");
        ((MenuBankLogs) superiorMenu).setFilteredPlayer(pagedObject.getPlayer());
        superiorMenu.refreshPage();
    }

    @Override
    public ItemBuilder modifyButtonItem(ItemBuilder buttonItem, BankTransaction transaction) {
        return buttonItem
                .replaceAll("{0}", transaction.getPosition() + "")
                .replaceAll("{1}", getFilteredPlayerName(transaction.getPlayer() == null ? CONSOLE_UUID : transaction.getPlayer()))
                .replaceAll("{2}", (transaction.getAction() == BankAction.WITHDRAW_COMPLETED ?
                        Locale.BANK_WITHDRAW_COMPLETED : Locale.BANK_DEPOSIT_COMPLETED).getMessage(targetPlayer.getUserLocale()))
                .replaceAll("{3}", transaction.getDate())
                .replaceAll("{4}", transaction.getAmount() + "")
                .replaceAll("{5}", StringUtils.format(transaction.getAmount()))
                .replaceAll("{6}", StringUtils.fancyFormat(transaction.getAmount(), targetPlayer.getUserLocale()))
                .asSkullOf(targetPlayer);
    }

    private static String getFilteredPlayerName(UUID filteredPlayer) {
        if (filteredPlayer == null) {
            return "";
        } else if (filteredPlayer.equals(CONSOLE_UUID)) {
            return "Console";
        } else {
            return plugin.getPlayers().getSuperiorPlayer(filteredPlayer).getName();
        }
    }

    public static class Builder extends PagedObjectBuilder<Builder, BankLogsPagedObjectButton> {

        @Override
        public BankLogsPagedObjectButton build() {
            return new BankLogsPagedObjectButton(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, nullItem);
        }

    }

}