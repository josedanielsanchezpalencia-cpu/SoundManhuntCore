package com.soundmanhunt.core;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final SoundManhuntPlugin plugin;

    public CommandHandler(SoundManhuntPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Solo jugadores pueden usar este comando.");
            return true;
        }

        PartyManager pm = plugin.getPartyManager();
        String cmd = command.getName().toLowerCase();

        if ("party".equals(cmd)) {
            if (args.length == 0) {
                player.sendMessage("§eUso: /party <crear|invitar|aceptar|salir|modo|iniciar>");
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "crear" -> pm.createParty(player);
                case "invitar" -> {
                    if (args.length < 2) {
                        player.sendMessage("§cUso: /party invitar <jugador>");
                        return true;
                    }
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target == null) {
                        player.sendMessage("§cJugador no encontrado.");
                        return true;
                    }
                    pm.invitePlayer(player, target);
                }
                case "aceptar" -> pm.acceptInvite(player);
                case "salir" -> pm.leaveParty(player);
                case "modo" -> {
                    if (args.length < 2) {
                        player.sendMessage("§cUso: /party modo <1-10>");
                        return true;
                    }
                    try {
                        pm.setMode(player, Integer.parseInt(args[1]));
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cNumero invalido.");
                    }
                }
                case "iniciar" -> pm.startMatch(player);
                default -> player.sendMessage("§eUso: /party <crear|invitar|aceptar|salir|modo|iniciar>");
            }
            return true;
        }

        if ("manhunt".equals(cmd) || "mh".equals(cmd)) {
            if (args.length == 0) {
                player.sendMessage("§eUso: /manhunt <brujula|terminar>");
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "brujula" -> {
                    MatchSession session = pm.getMatch(player.getUniqueId());
                    if (session == null || !session.isHunter(player.getUniqueId())) {
                        player.sendMessage("§cSolo los cazadores en partida pueden usar esto.");
                        return true;
                    }
                    session.giveCompass(player);
                }
                case "terminar" -> {
                    MatchSession session = pm.getMatch(player.getUniqueId());
                    if (session == null) {
                        player.sendMessage("§cNo estas en una partida activa.");
                        return true;
                    }
                    session.finish("Partida finalizada manualmente.");
                }
                default -> player.sendMessage("§eUso: /manhunt <brujula|terminar>");
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if ("party".equalsIgnoreCase(command.getName()) && args.length == 1) {
            return List.of("crear", "invitar", "aceptar", "salir", "modo", "iniciar");
        }
        if (("manhunt".equalsIgnoreCase(command.getName()) || "mh".equalsIgnoreCase(command.getName())) && args.length == 1) {
            return List.of("brujula", "terminar");
        }
        return Collections.emptyList();
    }
}