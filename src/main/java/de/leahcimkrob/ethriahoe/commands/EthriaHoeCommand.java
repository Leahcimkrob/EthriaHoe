package de.leahcimkrob.ethriahoe.commands;

import de.leahcimkrob.ethriahoe.EthriaHoe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class EthriaHoeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        EthriaHoe plugin = EthriaHoe.getInstance();
        FileConfiguration config = plugin.getConfig();
        String prefix = config.getString("prefix", "");

        // Neuer Subbefehl: /ethriahoe view <distanz>
        if (args.length > 0 && args[0].equalsIgnoreCase("view")) {
            if (!(sender instanceof Player)) {
                String msg = config.getString("messages.view_player_only", "&cNur Spieler können diesen Befehl nutzen.");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + msg));
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("ethriahoe.view")) {
                String msg = config.getString("messages.view_no_perm", "&cDu hast keine Berechtigung, diesen Befehl auszuführen.");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + msg));
                return true;
            }
            int distance = 10;
            if (args.length > 1) {
                try {
                    distance = Math.max(1, Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    String msg = config.getString("messages.view_invalid_distance", "&cUngültige Distanz. Bitte gib eine Zahl an.");
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + msg));
                    return true;
                }
            }

            // Werte aus der Config holen
            int duration = config.getInt("view_particle.duration", 3);
            String particleName = config.getString("view_particle.type", "HAPPY_VILLAGER");
            int maxDistance = config.getInt("view_particle.max_distance", 30);
            int particleAmount = config.getInt("view_particle.amount", 4);
            boolean force = config.getBoolean("view_particle.force", true);
            if (distance > maxDistance) {
                distance = maxDistance;
                String msg = config.getString("messages.view_distance_too_high", "&cMaximale Distanz ist {max}. Dein Wert wurde angepasst.")
                    .replace("{max}", String.valueOf(maxDistance));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + msg));
            }
            Particle particle;
            try {
                particle = Particle.valueOf(particleName);
            } catch (Exception e) {
                particle = Particle.HAPPY_VILLAGER;
            }
            final Particle finalParticle = particle;
            final int finalParticleAmount = particleAmount;
            final boolean finalForce = force;

            Collection<Entity> entities = player.getNearbyEntities(distance, distance, distance);
            List<ItemFrame> invisFrames = entities.stream()
                    .filter(e -> e instanceof ItemFrame)
                    .map(e -> (ItemFrame) e)
                    .filter(f -> !f.isVisible())
                    .collect(Collectors.toList());

            if (invisFrames.isEmpty()) {
                String msg = config.getString("messages.view_no_frames_found", "&eKeine unsichtbaren ItemFrames im Umkreis von {distance} Blöcken gefunden.")
                        .replace("{distance}", String.valueOf(distance));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + msg));
                return true;
            }

            String msg = config.getString("messages.view_frames_highlighted", "&aHebe {count} unsichtbare ItemFrames mit Partikeln hervor ({duration}s).")
                    .replace("{count}", String.valueOf(invisFrames.size()))
                    .replace("{duration}", String.valueOf(duration));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + msg));

            new BukkitRunnable() {
                int count = 0;
                final int maxTicks = duration * 20;

                @Override
                public void run() {
                    if (count >= maxTicks) {
                        cancel();
                        return;
                    }
                    for (ItemFrame frame : invisFrames) {
                        if (frame.isValid() && !frame.isVisible() && frame.getLocation().getWorld() == player.getWorld()) {
                            Location loc = frame.getLocation().toCenterLocation();
                            switch (frame.getFacing()) {
                                case UP:
                                    loc.add(0, 0.5, 0); // über dem Frame
                                    break;
                                case DOWN:
                                    loc.add(0, -0.5, 0); // unter dem Frame
                                    break;
                                default:
                                    loc.add(frame.getFacing().getDirection().multiply(0.4375)); // vor dem Frame
                                    loc.add(0, 0.01, 0); // minimal über dem Frame
                                    break;
                            }
                            frame.getWorld().spawnParticle(
                                    finalParticle,
                                    loc,
                                    finalParticleAmount, 0.2, 0.2, 0.2, 0.01,
                                    null, finalForce
                            );
                        }
                    }
                    count++;
                }
            }.runTaskTimer(plugin, 0, 1);

            return true;
        }

        // Reload wie gehabt
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("ethriahoe.admin")) {
                String noPermMsg = config.getString("messages.reload_no_perm", "Du hast keine Berechtigung, diesen Befehl auszuführen.");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + noPermMsg));
                return true;
            }
            plugin.reloadConfig();
            plugin.registerAliases(); // Aliasse auch beim Reload neu setzen!
            String reloadMsg = config.getString("messages.reload", "&4Die EthriaHoe-Konfiguration wurde neu geladen!");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + reloadMsg));
            return true;
        }

        String usageMsg = config.getString("messages.usage", "&eVerwendung: /ethriahoe reload oder /ethriahoe view <distanz>");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + usageMsg));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if ("reload".startsWith(args[0].toLowerCase()) && sender.hasPermission("ethriahoe.admin")) {
                completions.add("reload");
            }
            if ("view".startsWith(args[0].toLowerCase()) && sender.hasPermission("ethriahoe.view")) {
                completions.add("view");
            }
            return completions;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("view")) {
            completions.addAll(Arrays.asList("5", "10", "20", "30"));
            return completions;
        }
        return Collections.emptyList();
    }
}
