package com.bgsoftware.superiorskyblock.menu;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.utils.FileUtils;
import com.bgsoftware.superiorskyblock.utils.LocaleUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.util.List;
import java.util.Map;

public final class MenuPlayerLanguage extends SuperiorMenu {

    private MenuPlayerLanguage(SuperiorPlayer superiorPlayer){
        super("menuPlayerLanguage", superiorPlayer);
    }

    @Override
    public void onPlayerClick(InventoryClickEvent e) {
        if(!containsData(e.getRawSlot() + ""))
            return;

        java.util.Locale locale = (java.util.Locale) getData(e.getRawSlot() + "");
        superiorPlayer.setUserLocale(locale);
        Locale.CHANGED_LANGUAGE.send(superiorPlayer);

        superiorPlayer.asPlayer().closeInventory();
    }

    public static void init(){
        MenuPlayerLanguage menuPlayerLanguage = new MenuPlayerLanguage(null);

        File file = new File(plugin.getDataFolder(), "menus/player-language.yml");

        if(!file.exists())
            FileUtils.saveResource("menus/player-language.yml");

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        Map<Character, List<Integer>> charSlots = FileUtils.loadGUI(menuPlayerLanguage, "player-language.yml", cfg);

        for(char ch : charSlots.keySet()){
            if(cfg.contains("items." + ch + ".language")) {
                String language = cfg.getString("items." + ch + ".language");
                for(int slot : charSlots.get(ch)) {
                    try {
                        java.util.Locale locale = LocaleUtils.getLocale(language);
                        if(!Locale.isValidLocale(locale))
                        menuPlayerLanguage.addData(slot + "", locale);
                    }catch(IllegalArgumentException ex){
                        SuperiorSkyblockPlugin.log("&c[player-language.yml] The language " + language + " is not valid.");
                    }
                }
            }
        }
    }

    public static void openInventory(SuperiorPlayer superiorPlayer, SuperiorMenu previousMenu){
        new MenuPlayerLanguage(superiorPlayer).open(previousMenu);
    }

    public static void refreshMenus(){
        SuperiorMenu.refreshMenus(MenuPlayerLanguage.class);
    }

}