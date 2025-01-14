package me.devtec.servercontrolreloaded.commands.other;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.devtec.servercontrolreloaded.commands.CommandsManager;
import me.devtec.servercontrolreloaded.scr.Loader;
import me.devtec.servercontrolreloaded.scr.Loader.Placeholder;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.blocksapi.BlocksAPI;
import me.devtec.theapi.utils.StringUtils;

public class Spawner implements CommandExecutor, TabCompleter {

	public static List<String> list() {
		ArrayList<String> w = new ArrayList<>();
		String[] d = { "FISHING_HOOK", "DROPPED_ITEM", "LEASH_HITCH", "LIGHTNING", "PLAYER", "MINECART_MOB_SPAWNER",
				"UKNOWN", "FIREWORK", "PRIMED_TNT", "AREA_EFFECT_CLOUD", "ENDER_SIGNAL", "UNKNOWN" };
		for (EntityType t : EntityType.values())
			w.add(t.name());
		for (String s : d)
			w.remove(s);
		return w;
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (Loader.has(s, "Spawner", "Other")) {
			if(!CommandsManager.canUse("Other.Spawner", s)) {
				Loader.sendMessages(s, "Cooldowns.Commands", Placeholder.c().add("%time%", StringUtils.timeToString(CommandsManager.expire("Other.Spawner", s))));
				return true;
			}
			if (s instanceof Player) {
				Player p = (Player) s;
				if (args.length == 0) {
					Loader.Help(s, "Spawner", "Other");
					return true;
				}
				if (TheAPI.isNewVersion()) {
					if (args[0].equalsIgnoreCase("setAmount")) {
						if (args.length == 1) {
							Loader.Help(s, "Spawner", "Other");
							return true;
						}
						Block b = BlocksAPI.getLookingBlock(p, 10);
						if (b.getType().name().contains("SPAWNER")) {
							CreatureSpawner ss = (CreatureSpawner) b.getState();
							ss.setSpawnCount(StringUtils.getInt(args[1]));
							ss.update();
							Loader.sendMessages(s, "Spawner.Set.Amount", Placeholder.c().replace("%amount%", "" + StringUtils.getInt(args[1])));
							return true;
						}
						Loader.sendMessages(s, "Spawner.BlockNotSpawner");
						return true;
					}

					if (args[0].equalsIgnoreCase("setRangePlayer")) {
						if (args.length == 1) {
							Loader.Help(s, "Spawner", "Other");
							return true;
						}
						Block b = BlocksAPI.getLookingBlock(p, 10);
						if (b.getType().name().contains("SPAWNER")) {
							CreatureSpawner ss = (CreatureSpawner) b.getState();
							ss.setRequiredPlayerRange(StringUtils.getInt(args[1]));
							ss.update();
							Loader.sendMessages(s, "Spawner.Set.Range", Placeholder.c().replace("%range%", "" + StringUtils.getInt(args[1])));
							return true;
						}
						Loader.sendMessages(s, "Spawner.BlockNotSpawner");
						return true;
					}
				} else {
					return true;
				}
				if (args[0].equalsIgnoreCase("setTime")) {
					if (args.length == 1) {
						Loader.Help(s, "Spawner", "Other");
						return true;
					}
					Block b = BlocksAPI.getLookingBlock(p, 10);
					if (b.getType().name().contains("SPAWNER")) {
						CreatureSpawner ss = (CreatureSpawner) b.getState();
						ss.setDelay(StringUtils.getInt(args[1]));
						ss.update();
						Loader.sendMessages(s, "Spawner.Set.SpawnTime", Placeholder.c().replace("%time%", "" + StringUtils.getInt(args[1])));
						return true;
					}
					Loader.sendMessages(s, "Spawner.BlockNotSpawner");
					return true;
				}
				if (args[0].equalsIgnoreCase("setMob")) {
					if (args.length == 1) {
						Loader.Help(s, "Spawner", "Other");
						return true;
					}
					Block b = BlocksAPI.getLookingBlock(p, 10);
					if (b.getType().name().contains("SPAWNER")) {
						
						EntityType type = EntityType.fromName(args[1]);
						if (type != null) {
							CreatureSpawner ss = (CreatureSpawner) b.getState();
							ss.setSpawnedType(type);
							ss.update();
							Loader.sendMessages(s, "Spawner.Set.Entity", Placeholder.c().replace("%entity%", type.name()));
							return true;
						}
						Loader.sendMessages(s, "Missing.Entity", Placeholder.c().replace("%entity%", args[1]));
						return true;
					}
					Loader.sendMessages(s, "Spawner.BlockNotSpawner");
					return true;
				}
				Loader.Help(s, "Spawner", "Other");
				return true;
			}
		}
		Loader.noPerms(s, "Spawner", "Other");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1, String arg2, String[] args) {
		if (Loader.has(s, "Spawner", "Other")) {
			if (args.length == 1) {
				List<String> list;
				if (TheAPI.isNewVersion()) {
					list = Arrays.asList("setMob", "setRangePlayer", "setTime", "setAmount");
				} else
					list = Arrays.asList("setMob", "setTime");
				return StringUtils.copyPartialMatches(args[0], list);
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("setMob"))
					return StringUtils.copyPartialMatches(args[1], list());

				if (TheAPI.isNewVersion()) {
					if (args[0].equalsIgnoreCase("setRangePlayer") || args[0].equalsIgnoreCase("setAmount"))
						return StringUtils.copyPartialMatches(args[1], Collections.singletonList("?"));
				}
				if (args[0].equalsIgnoreCase("setTime"))
					return StringUtils.copyPartialMatches(args[1], Collections.singletonList("?"));
			}
		}
		return Collections.emptyList();
	}
}
