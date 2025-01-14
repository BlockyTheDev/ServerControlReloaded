package me.devtec.servercontrolreloaded.commands.other;


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
import me.devtec.servercontrolreloaded.utils.skins.SkinManager;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StringUtils;

public class Skin implements CommandExecutor, TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1, String arg2, String[] args) {
		if(Loader.has(s,"Skin","Other")) {
			if(args.length==1) {
				List<String> w =  API.getPlayerNames(s);
				w.add("Reset");
				return StringUtils.copyPartialMatches(args[0], w);
			}
			if(args.length==2)
				return StringUtils.copyPartialMatches(args[1], API.getPlayerNames(s));
		}
		return Collections.emptyList();
	}

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(Loader.has(s,"Skin","Other")) {
		if(!CommandsManager.canUse("Other.Skin", s)) {
			Loader.sendMessages(s, "Cooldowns.Commands", Placeholder.c().add("%time%", StringUtils.timeToString(CommandsManager.expire("Other.Skin", s))));
			return true;
		}
		if(args.length==0) {
			Loader.Help(s, "Skin", "Other");
			return true;
		}
		if(args.length==1) {
			if(s instanceof Player) {
			if(args[0].equalsIgnoreCase("reset")) {
				SkinManager.generateSkin(s.getName(), data -> SkinManager.loadSkin((Player)s, data), false);
				Loader.sendMessages(s, "Skin.Reset.You");
				return true;
			}
			SkinManager.generateSkin(args[0], data -> SkinManager.loadSkin((Player)s, data), false);
			Loader.sendMessages(s, "Skin.Set.You", Placeholder.c().add("%skin%", args[0]));
			return true;
			}
			Loader.Help(s, "Skin", "Other");
			return true;
		}
		Player a = TheAPI.getPlayer(args[1]);
		if(a==null) {
			Loader.notOnline(s, args[1]);
			return true;
		}
		if(args[0].equalsIgnoreCase("reset")) {
			SkinManager.generateSkin(a.getName(), data -> SkinManager.loadSkin(a, data), false);
			Loader.sendMessages(s, "Skin.Reset.Other.Sender", Placeholder.c().add("%player%", a.getName()).add("%playername%", a.getDisplayName()).add("%skin%", args[0]));
			Loader.sendMessages(a, "Skin.Reset.Other.Receiver", Placeholder.c().add("%player%", s.getName()).add("%playername%", s.getName()).add("%skin%", args[0]));
			return true;
		}
		SkinManager.generateSkin(args[0], data -> SkinManager.loadSkin(a, data), false);
		Loader.sendMessages(s, "Skin.Set.Other.Sender", Placeholder.c().add("%skin%", args[0]).add("%player%", a.getName()).add("%playername%", a.getDisplayName()).add("%skin%", args[0]));
		Loader.sendMessages(a, "Skin.Set.Other.Receiver", Placeholder.c().add("%skin%", args[0]).add("%player%", s.getName()).add("%playername%", s.getName()).add("%skin%", args[0]));
		return true;
		}
		Loader.noPerms(s, "Skin", "Other");
		return true;
	}
}
