package me.devtec.servercontrolreloaded.commands.other;


import me.devtec.servercontrolreloaded.commands.CommandsManager;
import me.devtec.servercontrolreloaded.scr.API;
import me.devtec.servercontrolreloaded.scr.Loader;
import me.devtec.servercontrolreloaded.scr.Loader.Placeholder;
import me.devtec.servercontrolreloaded.utils.SPlayer;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.scheduler.Scheduler;
import me.devtec.theapi.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class Fly implements CommandExecutor, TabCompleter {
	public static final HashMap<SPlayer, Integer> task = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (Loader.has(s, "Fly", "Other")) {
			if(!CommandsManager.canUse("Other.Fly", s)) {
				Loader.sendMessages(s, "Cooldowns.Commands", Placeholder.c().add("%time%", StringUtils.timeToString(CommandsManager.expire("Other.Fly", s))));
				return true;
			}
			if (args.length == 0) {
				if (s instanceof Player) {
					SPlayer p = API.getSPlayer((Player) s);
					if (task.get(p) != null)
						Scheduler.cancelTask(task.remove(p));
					p.toggleFly(null);
					return true;
				}
				Loader.Help(s, "Fly", "Other");
				return true;
			}
			SPlayer target;
			if (args.length == 1) {
				if(s instanceof Player) {
				if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("false")) {
					if (Loader.has(s, "Fly", "Other")) {
					target = API.getSPlayer((Player)s);
					if (task.get(target) != null)
						Scheduler.cancelTask(task.remove(target));
					target.disableFly();
					Loader.sendMessages(s, "Fly.Disabled.You");
					return true;
					}
					Loader.noPerms(s, "Fly", "Other");
					return true;
				}
				if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("true")) {
					if (Loader.has(s, "Fly", "Other")) {
					target = API.getSPlayer((Player)s);
					if (task.get(target) != null)
						Scheduler.cancelTask(task.remove(target));
					target.enableFly();
					Loader.sendMessages(s, "Fly.Enabled.You");
					return true;
					}
					Loader.noPerms(s, "Fly", "Other");
					return true;
				}}
				if (TheAPI.getPlayer(args[0]) == null) {
					Loader.notOnline(s, args[0]);
					return true;
				}
				target = API.getSPlayer(args[0]);
				if (target.getPlayer() == s) {
					if (Loader.has(s, "Fly", "Other")) {
						if (task.get(target) != null)
							Scheduler.cancelTask(task.remove(target));
						target.toggleFly(null);
						return true;
					}
					Loader.noPerms(s, "Fly", "Other");
					return true;
				} else {
					if (Loader.has(s, "Fly", "Other", "Other")) {
						if (task.get(target) != null)
							Scheduler.cancelTask(task.remove(target));
						target.toggleFly(s);
						return true;
					}
					Loader.noPerms(s, "Fly", "Other", "Other");
					return true;
				}
			}
			if (TheAPI.getPlayer(args[0]) == null) {
				Loader.notOnline(s, args[0]);
				return true;
			}
			target = API.getSPlayer(args[0]);
			if (target.getPlayer() != s) {
				if (Loader.has(s, "Fly", "Other", "Other")) {
					if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false")) {
						if (task.get(target) != null)
							Scheduler.cancelTask(task.remove(target));
						target.disableFly();
						Loader.sendMessages(s, "Fly.Disabled.Other.Sender", Placeholder.c().replace("%player%", target.getName())
								.replace("%playername%", target.getDisplayName()));
						Loader.sendMessages(target.getPlayer(), "Fly.Disabled.Other.Receiver", Placeholder.c().replace("%player%", s.getName())
								.replace("%playername%", s.getName()));
						return true;
					}
					if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true")) {
						if (task.get(target) != null)
							Scheduler.cancelTask(task.remove(target));
						target.enableFly();
						Loader.sendMessages(s, "Fly.Enabled.Other.Sender", Placeholder.c().replace("%player%", target.getName())
								.replace("%playername%", target.getDisplayName()));
						Loader.sendMessages(target.getPlayer(), "Fly.Enabled.Other.Receiver", Placeholder.c().replace("%player%", s.getName())
								.replace("%playername%", s.getName()));
						return true;
					}
					Loader.Help(s, "Fly", "Other");
					return true;
				}
				Loader.noPerms(s, "Fly", "Other", "Other");
				return true;
			} else {
				if (Loader.has(s, "Fly", "Other")) {
					if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false")) {
						if (task.get(target) != null)
							Scheduler.cancelTask(task.remove(target));
						target.disableFly();
						Loader.sendMessages(s, "Fly.Disabled.You");
						return true;
					}
					if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true")) {
						if (task.get(target) != null)
							Scheduler.cancelTask(task.remove(target));
						target.enableFly();
						Loader.sendMessages(s, "Fly.Enabled.You");
						return true;
					}
					Loader.Help(s, "Fly", "Other");
					return true;
				}
				Loader.noPerms(s, "Fly", "Other");
				return true;
			}
		}
		Loader.noPerms(s, "Fly", "Other");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1, String a, String[] args) {
		if(Loader.has(s, "Fly", "Other")) {
			if(args.length==1) {
				List<String> list = new ArrayList<>(Arrays.asList("On", "Off"));
				if(Loader.has(s, "Fly", "Other", "Other"))
					list.addAll(API.getPlayerNames(s));
				return StringUtils.copyPartialMatches(args[0], list);
			}
			if(args.length==2 && Loader.has(s, "Fly", "Other", "Other"))
				return StringUtils.copyPartialMatches(args[1], Arrays.asList("On","Off"));
		}
		return Collections.emptyList();
	}
}