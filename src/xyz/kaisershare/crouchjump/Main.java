package xyz.kaisershare.crouchjump;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("crouchjump").setExecutor(this);
		FileConfiguration config = this.getConfig();
		config.addDefault("duration", 10);
		config.addDefault("amplifier", 1);
		config.options().copyDefaults(true);
		this.saveDefaultConfig();
		ConsoleCommandSender console = getServer().getConsoleSender();
		console.sendMessage("§aCrouch-jump by §6https://KaiserShare.xyz §ashould be running...");
	}
	public void onDisable() {
		ConsoleCommandSender console = getServer().getConsoleSender();
		console.sendMessage("§cCrouch-jump by §6https://KaiserShare.xyz §chas been disabled.");
	}

	@EventHandler()
	public void onCrouch(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		FileConfiguration config = this.getConfig();
		if (!player.isSneaking()) {
			player.addPotionEffect((new PotionEffect(PotionEffectType.JUMP, config.getInt("duration"), config.getInt("amplifier"))));
			//player.sendMessage("Duration:" + config.getInt("duration") + "Amplifier:" + config.getInt("amplifier"));
		}
		else {
			player.removePotionEffect(PotionEffectType.JUMP);
			//player.sendMessage("Duration:" + config.getInt("duration") + "Amplifier:" + config.getInt("amplifier"));
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("crouchjump")) {
			if (!sender.hasPermission("crouchjump.cmd")) {
				sender.sendMessage("§cAccess denied.");
			}
			if (args.length == 0) {
				sender.sendMessage("§d[§aCrouch-Jump§d] §6Command list: §f/crouchjump reload");
			}
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("reload")) {
					this.reloadConfig();
					sender.sendMessage("§d[§aCrouch-Jump§d] §6has been reloaded.");
				}
			}
		}
		return false;
	}
}