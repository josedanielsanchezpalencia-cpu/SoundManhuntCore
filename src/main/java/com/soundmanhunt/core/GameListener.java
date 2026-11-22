package com.soundmanhunt.core;

import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

public class GameListener implements Listener {

    private final SoundManhuntPlugin plugin;

    public GameListener(SoundManhuntPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCompassClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null || event.getItem().getType() != Material.COMPASS) return;

        Player player = event.getPlayer();
        MatchSession session = plugin.getPartyManager().getMatch(player.getUniqueId());
        if (session != null && session.isHunter(player.getUniqueId())) {
            event.setCancelled(true);
            session.updateCompass(player);
        }
    }

    @EventHandler
    public void onRunnerMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        Player player = event.getPlayer();
        MatchSession session = plugin.getPartyManager().getMatch(player.getUniqueId());
        if (session != null && session.getRunnerId().equals(player.getUniqueId())) {
            session.updateRunnerLocation(event.getTo());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        MatchSession session = plugin.getPartyManager().getMatch(dead.getUniqueId());
        if (session != null && session.getRunnerId().equals(dead.getUniqueId())) {
            session.finish("¡Los Cazadores ganaron! El corredor murio.");
        }
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof EnderDragon)) return;
        for (Player p : event.getEntity().getWorld().getPlayers()) {
            MatchSession session = plugin.getPartyManager().getMatch(p.getUniqueId());
            if (session != null && session.getRunnerId().equals(p.getUniqueId())) {
                session.finish("¡El Corredor gano! El Ender Dragon fue derrotado.");
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player quit = event.getPlayer();
        MatchSession session = plugin.getPartyManager().getMatch(quit.getUniqueId());
        if (session != null && session.getRunnerId().equals(quit.getUniqueId())) {
            session.finish("Partida terminada porque el corredor se desconecto.");
        }
    }
}