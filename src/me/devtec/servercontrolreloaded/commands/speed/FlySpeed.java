package me.devtec.servercontrolreloaded.commands.speed;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.devtec.servercontrolreloaded.commands.CommandsManager;
import me.devtec.servercontrolreloaded.scr.API;
import me.devtec.servercontrolreloaded.scr.Loader;
import me.devtec.servercontrolreloaded.scr.Loader.Placeholder;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StringUtils;

public class FlySpeed implements CommandExecutor, TabCompleter {
	public void speed(CommandSender s) {
		if (s instanceof Player) {
			Loader.Help(s, "FlySpeed", "Speed");
		}
		if (!(s instanceof Player)) {
			Loader.Help(s, "FlySpeed", "Speed");
		}
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (Loader.has(s, "FlySpeed", "Speed")) {
		if(!CommandsManager.canUse("Speed.FlySpeed", s)) {
			Loader.sendMessages(s, "Cooldowns.Commands", Placeholder.c().add("%time%", StringUtils.timeToString(CommandsManager.expire("Speed.FlySpeed", s))));
			return true;
		}
		if (args.length == 0) {
			speed(s);
			return true;
		}
		if (args.length == 1) {
			if (!(s instanceof Player)) {
				speed(s);
			} else {
				if (Loader.has(s, "FlySpeed", "Speed")) {
					double flightmodifier = StringUtils.getDouble(args[0]);
					if (flightmodifier > 10.0)
						flightmodifier = 10.0;
					if (flightmodifier < -10.0)
						flightmodifier = -10.0;
					((Player) s).setFlySpeed((float) flightmodifier / 10);
					TheAPI.getUser(s.getName()).setAndSave("FlySpeed", flightmodifier / 10);
					Loader.sendMessages(s, "Speed.Fly.You", Placeholder.c().add("%speed%", String.valueOf(flightmodifier)));
					return true;
				}
				Loader.noPerms(s, "FlySpeed", "Speed");
			}
			return true;
		}
			if (Loader.has(s, "FlySpeed", "Speed", "Other")) {
				Player target = TheAPI.getPlayer(args[0]);
				if (target != null) {
					double flightmodifier = StringUtils.getDouble(args[1]);
					if (flightmodifier > 10.0)
						flightmodifier = 10.0;
					if (flightmodifier < -10.0)
						flightmodifier = -10.0;
					target.setFlySpeed((float) flightmodifier / 10);
					TheAPI.getUser(target).setAndSave("FlySpeed", flightmodifier / 10);
					Loader.sendMessages(s, "Speed.Fly.Other.Sender", Placeholder.c().add("%speed%", String.valueOf(flightmodifier))
							.add("%player%", target.getName()).add("%playername%", target.getDisplayName()));

					Loader.sendMessages(target, "Speed.Fly.Other.Receiver", Placeholder.c().add("%speed%", String.valueOf(flightmodifier)));
					
					return true;
				}
				Loader.notOnline(s, args[0]);
				return true;
			}
			Loader.noPerms(s, "FlySpeed", "Speed", "Other");
			return true;
		}
		Loader.noPerms(s, "FlySpeed", "Speed");
		return true;
	}
	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1,
			String arg2, String[] args) {
		if(Loader.has(s, "FlySpeed", "Speed")) {
			if (args.length == 1)
				return StringUtils.copyPartialMatches(args[0], API.getPlayerNames(s));
			if (args.length == 2)
				return StringUtils.copyPartialMatches(args[1], Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
		}
		return Collections.emptyList();
	}
}