package br.AtomGamers.CSTransaction;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestShopTransaction extends JavaPlugin implements Listener {

    protected List<Player> EnabledTransaction = new ArrayList<Player>();
    protected ConsoleCommandSender ccs = null;
    protected PluginManager plugins = null;
    protected boolean using = false;
    protected Plugin plugin = null;
    protected String version = "";

    @Override
    public void onEnable() {
        ccs = Bukkit.getConsoleSender();
        plugins = Bukkit.getPluginManager();
        plugin = Bukkit.getPluginManager().getPlugin("ChestShop");
        if (plugin != null) {
            version = plugin.getDescription().getVersion();
            ccs.sendMessage("§b[CSTransaction] §fHooked to ChestShop [v" + version + "]");
        } else {
            ccs.sendMessage("§b[CSTransaction] §cChestShop não encontrado!");
            ccs.sendMessage("§b[CSTransaction] §fPlugin finalizado (Autor=AtomGamers)");
            plugins.disablePlugin(this);
        }
        if (!(new File(getDataFolder(), "config.yml").exists())) {
            try {
                saveResource("config_template.yml", false);
                new File(getDataFolder(), "config_template.yml").renameTo(new File(getDataFolder(), "config.yml"));
            } catch (Exception ex) {
                ccs.sendMessage("§b[CSTransaction] §c" + ex.getMessage());
            }
        }
        getServer().getPluginManager().registerEvents(this, this);
        ccs.sendMessage("§b[CSTransaction] §fPlugin inicializado. (Autor=AtomGamers)");
    }

    @Override
    public void onDisable() {
        ccs = Bukkit.getConsoleSender();
        ccs.sendMessage("§b[CSTransaction] §fPlugin finalizado. (Autor=AtomGamers)");
    }

    protected String getMensagem(String config) {
        String msg = getConfig().getString(config);
        msg = msg.replaceAll("&", "§");
        return msg;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onTransactionEvent(PreTransactionEvent e) {
        Player sender = (Player) e.getClient();
        if (sender instanceof Player) {
            if (!(EnabledTransaction.contains(sender))) {
                e.setCancelled(PreTransactionEvent.TransactionOutcome.OTHER);
                sender.sendMessage(getMensagem("Error1"));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onLoginSet(PlayerJoinEvent e) {
        if (EnabledTransaction.contains(e.getPlayer())) {
            EnabledTransaction.remove(e.getPlayer());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onLoginSet(PlayerQuitEvent e) {
       if (EnabledTransaction.contains(e.getPlayer())) {
            EnabledTransaction.remove(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onCommandStatus(PlayerCommandPreprocessEvent e) {
        Player sender = (Player) e.getPlayer();
        String command = e.getMessage().toLowerCase();
        String selected = getConfig().getString("Command").toLowerCase();
        if (command.equalsIgnoreCase(selected)) {
            if (EnabledTransaction.contains(sender)) {
                EnabledTransaction.remove(sender);
                sender.sendMessage(getMensagem("Success1"));
                using = true;
            } else {
                if (!(using)) {
                    EnabledTransaction.add(sender);
                    sender.sendMessage(getMensagem("Success2"));
                }
            }
            using = false;
        } else if (command.startsWith(selected) && !(command.equals(selected))) {
            sender.sendMessage(getMensagem("Error2"));
        }
    }
}
