package xyz.kaisershare.crouchjump;

import java.util.HashMap;
import java.util.UUID;

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
	
	HashMap<UUID, Integer> storeDurationData = new HashMap<UUID, Integer>();
	HashMap<UUID, Integer> storeAmplifierData = new HashMap<UUID, Integer>();
	HashMap<UUID, Boolean> hasJumpBoost = new HashMap<UUID, Boolean>();
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("crouchjump").setExecutor(this);
		FileConfiguration config = this.getConfig();
		config.addDefault("duration", 10);
		config.addDefault("amplifier", 2);
		config.addDefault("effect-stack-multiplier", "additive");
		config.addDefault("lock-crouch-jumping-behind-permissions", false);
		config.options().copyDefaults(true);
		this.saveDefaultConfig();
		ConsoleCommandSender console = getServer().getConsoleSender();
		console.sendMessage("§aCrouch-Jump by §6https://KaiserShare.xyz §ashould be running...");
		console.sendMessage("§aIf you like this plugin, give its GitHub repository a star! §6https://github.com/KaiserByte/Crouch-Jump");
	}
	public void onDisable() {
		ConsoleCommandSender console = getServer().getConsoleSender();
		console.sendMessage("§cCrouch-Jump by §6https://KaiserShare.xyz §chas been disabled.");
	}

	@EventHandler()
	public void onCrouch(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		UUID eventUUID = player.getUniqueId();
		FileConfiguration config = this.getConfig();
		if (config.getBoolean("lock-crouch-jumping-behind-permissions")) {
			if (!player.hasPermission("crouchjump.jump")) {
				return;
			}
		}
		if (!player.isSneaking()) {
			storeDurationData.put(eventUUID, 0);
	        storeAmplifierData.put(eventUUID, 0);
	        hasJumpBoost.put(eventUUID, false);
			for (PotionEffect potioneffect : player.getActivePotionEffects()) {
	            if (player.hasPotionEffect(PotionEffectType.JUMP)) {
	            	hasJumpBoost.put(eventUUID, true);
	                storeDurationData.put(eventUUID, potioneffect.getDuration());
	                storeAmplifierData.put(eventUUID, potioneffect.getAmplifier());
	                player.removePotionEffect(PotionEffectType.JUMP);
	            }
	            }
			int configAmplifier = (config.getInt("amplifier")-1);
			int eventAmplifier = (storeAmplifierData.get(eventUUID));
			
			if (config.getString("effect-stack-multiplier").equalsIgnoreCase("additive")) {
				if (hasJumpBoost.get(eventUUID)) {
					eventAmplifier = ((storeAmplifierData.get(eventUUID)+1) + (configAmplifier+1)+1);
					eventAmplifier = eventAmplifier - 2;
				} else {
					eventAmplifier = ((storeAmplifierData.get(eventUUID)+1) + (configAmplifier+1));
					eventAmplifier = eventAmplifier - 2;
				}
			} else if (config.getString("effect-stack-multiplier").equalsIgnoreCase("multiply")) {
				if (hasJumpBoost.get(eventUUID)) {
					eventAmplifier = ((storeAmplifierData.get(eventUUID)+1) * (configAmplifier+1)+1);
					eventAmplifier = eventAmplifier - 2;
				} else {
					eventAmplifier = ((storeAmplifierData.get(eventUUID)+1) * (configAmplifier+1));
					eventAmplifier = eventAmplifier - 2;
				}
			} else {
				eventAmplifier = configAmplifier;
			}
				if (config.getInt("duration") <= -1) {
					player.addPotionEffect((new PotionEffect(PotionEffectType.JUMP, 999999, eventAmplifier)));
				} else {
					player.addPotionEffect((new PotionEffect(PotionEffectType.JUMP, config.getInt("duration"), eventAmplifier)));
				}
		} else {
			player.removePotionEffect(PotionEffectType.JUMP);
			boolean durationCheckResult = storeDurationData.containsValue(storeDurationData.get(eventUUID));
			boolean amplifierCheckResult = storeAmplifierData.containsValue(storeAmplifierData.get(eventUUID));
			if (java.util.Objects.equals(durationCheckResult, amplifierCheckResult)) {
				player.addPotionEffect((new PotionEffect(PotionEffectType.JUMP, storeDurationData.get(eventUUID), storeAmplifierData.get(eventUUID))));
			}
        	storeDurationData.remove(eventUUID);
        	storeAmplifierData.remove(eventUUID);
		}
		}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("crouchjump")) {
			if (args.length == 0) {
				sender.sendMessage("§d[§aCrouch-Jump§d] §6Command list: §f/crouchjump reload");
			}
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("reload")) {
					this.reloadConfig();
					sender.sendMessage("§d[§aCrouch-Jump§d] §6Config has been reloaded.");
				}
			}
		}
		return false;
	}
}