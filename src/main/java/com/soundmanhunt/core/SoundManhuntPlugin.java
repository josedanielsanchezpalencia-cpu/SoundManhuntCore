package com.soundmanhunt.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SoundManhuntPlugin extends JavaPlugin {

    private PartyManager partyManager;

    @Override
    public void onEnable() {
        this.partyManager = new PartyManager(this);

        CommandHandler commandHandler = new CommandHandler(this);
        if (getCommand("party") != null) {
            getCommand("party").setExecutor(commandHandler);
            getCommand("party").setTabCompleter(commandHandler);
        }
        if (getCommand("manhunt") != null) {
            getCommand("manhunt").setExecutor(commandHandler);
            getCommand("manhunt").setTabCompleter(commandHandler);
        }

        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
        getLogger().info("SoundManhuntCore iniciado correctamente.");
    }

    @Override
    public void onDisable() {
        if (partyManager != null) {
            partyManager.endAllMatches("Plugin deshabilitado.");
        }
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }
}