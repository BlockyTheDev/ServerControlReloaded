package Commands.Message;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ServerControl.Loader;
import ServerControl.Loader.Placeholder;
import Utils.Colors;
import me.DevTec.TheAPI.TheAPI;

public class PrivateMessage implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (Loader.has(s, "PrivateMessage", "Message")) {
			if (args.length == 0 || args.length == 1) {
				Loader.Help(s, "/Msg <player> <message>", "Message");
			}
			if (args.length >= 2) {

				String msg = Colors.colorize(TheAPI.buildString(args).replaceFirst(args[0] + " ", ""), false, s);
				String from = "";
				String to = "";
				if (args[0].equalsIgnoreCase("CONSOLE")) {
					if (s instanceof Player == false) {
						TheAPI.getUser(s.getName()).setAndSave("Server.Reply", "CONSOLE");
					} else {
						TheAPI.getUser(s.getName()).setAndSave("Server.Reply", s.getName());
						TheAPI.getUser(s.getName()).setAndSave("Players." + s.getName() + ".Reply", "CONSOLE");
					}
					from = TheAPI.colorize(Loader.config.getString("Format.PrivateMessageFrom")
							.replace("%from%", s.getName()).replace("%to%", "CONSOLE"));
					to = TheAPI.colorize(Loader.config.getString("Format.PrivateMessageTo")
							.replace("%from%", s.getName()).replace("%to%", "CONSOLE"));
					to = to.replace("%message%", msg);
					from = from.replace("%message%", msg);
					s.sendMessage(to);
					Bukkit.getConsoleSender().sendMessage(from);
					return true;
				} else {
					Player p = TheAPI.getPlayer(args[0]);
					if (p != null) {
						from = TheAPI.colorize(Loader.config.getString("Format.PrivateMessageFrom")
								.replace("%from%", s.getName()).replace("%to%", p.getName()));
						to = TheAPI.colorize(Loader.config.getString("Format.PrivateMessageTo")
								.replace("%from%", s.getName()).replace("%to%", p.getName()));
						to = to.replace("%message%", msg);
						from = from.replace("%message%", msg);
						if (s instanceof Player == false) {
							TheAPI.getUser(s.getName()).setAndSave("Reply", "CONSOLE");
							TheAPI.getUser("CONSOLE").setAndSave("Server", p.getName());
						} else {
							TheAPI.getUser(s.getName()).setAndSave("Reply", p.getName());
							TheAPI.getUser(p).setAndSave("Reply", s.getName());
						}
						s.sendMessage(to);
						p.sendMessage(from);
						return true;
					}
					Loader.sendMessages(s, "Missing.Player.Offline", Placeholder.c().add("%player%", args[0]));
					return true;
				}
			}
		}
		return true;
	}

}