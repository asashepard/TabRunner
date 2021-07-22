package com.gmail.creepycucumber1.tabrunner;

import com.earth2me.essentials.Essentials;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ChatColor;
import java.awt.Color;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class TabRunner extends JavaPlugin {

    private static TabRunner instance;

    public void onEnable() {
        LuckPerms api = LuckPermsProvider.get();
        instance = this;
        onTab();
        getLogger().info("TabRunner has been enabled!");
    }

    public static TabRunner getInstance() {
        return instance;
    }

    public static boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }

    public void onTab() {

        final Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        HashMap<UUID, Double> map = new HashMap<>(); // Player UUID, Health

        Bukkit.getScheduler().scheduleSyncRepeatingTask(TabRunner.getInstance(), new Runnable() {

            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()) {

                    String name = ChatColor.GRAY + "(" + p.getName() + ") ";
                    String nick = p.getDisplayName() + " ";
                    String h = String.valueOf(Math.round(p.getHealth()));
                    double health = p.getHealth();
                    String afk = "";

                    /* obscures health value if player is invisible,
                    makes adjustments for no nickname */
                    if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)) { h = "20"; }
                    if(p.getName().equalsIgnoreCase(ess.getUser(p).getNick())) { name = ""; }
                    if(ess.getUser(p).isAfk()) {
                        nick = ChatColor.GRAY + nick;
                        afk = ChatColor.GRAY + "[AFK] ";
                        name = "";
                    }

                    if(isPlayerInGroup(p, "owner")) {
                        nick = ChatColor.of(new Color(252, 153, 145)) + p.getDisplayName() + " ";
                    } else if (isPlayerInGroup(p, "staff")) {
                        nick = ChatColor.of(new Color(252, 183, 92)) + p.getDisplayName() + " ";
                    } else if (isPlayerInGroup(p, "sponsor")) {
                        nick = ChatColor.of(new Color(144, 231, 252)) + p.getDisplayName() + " ";
                    }

                    String healthvalue = ChatColor.RED + h;
                    String healthheart = ChatColor.DARK_RED + "♥";

                    // changes health display colors if player has effects
                    if (p.hasPotionEffect(PotionEffectType.POISON)) {
                        healthvalue = ChatColor.of(new Color(187, 183, 66)) + h;
                        healthheart = ChatColor.of(new Color(139, 135, 18)) + "♥";
                    }
                    if (p.hasPotionEffect(PotionEffectType.WITHER)) {
                        healthvalue = ChatColor.DARK_GRAY + h;
                        healthheart = ChatColor.BLACK + "♥";
                    }
                    if (p.isFrozen()) {
                        healthvalue = ChatColor.AQUA + h;
                        healthheart = ChatColor.DARK_AQUA + "♥";
                    }

                    // makes health display flash if damage taken
                    if (map.containsKey(p.getUniqueId()) && !(p.hasPotionEffect(PotionEffectType.INVISIBILITY))) {
                        double oldhealth = map.get(p.getUniqueId());
                        if (oldhealth > health) {
                            healthvalue = ChatColor.of(new Color(180, 180, 180)) + h;
                            healthheart = ChatColor.of(new Color(210, 210, 210)) + "♥";
                        }
                    }
                    map.put(p.getUniqueId(), health);

                    // changes tab list display
                    p.setPlayerListName(afk + nick + name + healthvalue + healthheart);
                }
            }
        }, 0, 5);
    }
}
