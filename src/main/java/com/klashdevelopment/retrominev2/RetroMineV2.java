package com.klashdevelopment.retrominev2;

import org.bukkit.plugin.java.JavaPlugin;

public final class RetroMineV2 extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        RemboBot.main(new String[]{getConfig().getString("bot.token")});
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
