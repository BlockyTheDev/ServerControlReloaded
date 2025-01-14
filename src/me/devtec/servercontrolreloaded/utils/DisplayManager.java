package me.devtec.servercontrolreloaded.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import me.devtec.servercontrolreloaded.scr.Loader;
import me.devtec.servercontrolreloaded.utils.displaymanager.BossBarManager;
import me.devtec.servercontrolreloaded.utils.displaymanager.SBossBar;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.placeholderapi.PlaceholderAPI;
import me.devtec.theapi.scheduler.Scheduler;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.scoreboardapi.ScoreboardAPI;
import me.devtec.theapi.scoreboardapi.SimpleScore;
import me.devtec.theapi.utils.StringUtils;

public class DisplayManager {
	private static final Map<String, ScoreboardAPI> map = SimpleScore.scores;
	
	public enum DisplayType {
		ACTIONBAR,
		BOSSBAR,
		SCOREBOARD
	}
	
	static List<Player> init = new ArrayList<>();

	public static void initializePlayer(Player p) {
		for(DisplayType t : DisplayType.values()) {
			if(TheAPI.getUser(p).getBoolean("SCR."+t.name()) && !ignore.get(t).contains(p.getName()))
				ignore.get(t).add(p.getName());
			if(!ignore.get(t).contains(p.getName()) && (t==DisplayType.ACTIONBAR?Loader.ac.getBoolean("Enabled"):(t==DisplayType.BOSSBAR?Loader.bb.getBoolean("Enabled"):setting.sb))) {
				init.add(p);
			}
		}
	}

	public static void removeCache(Player p) {
		for(DisplayType t : DisplayType.values()) {
			ignore.get(t).remove(p.getName());
			hide.get(t).remove(p.getName());
		}
		init.remove(p);
		TheAPI.sendActionBar(p, "");
	}
	
	public static void show(Player s, DisplayType type, boolean msg) {
		TheAPI.getUser(s).setAndSave("SCR."+type.name(), false);
		ignore.get(type).remove(s.getName());
		hide.get(type).remove(s.getName());
		switch(type) {
			case ACTIONBAR:{
				try {
				if(!s.hasPermission(Loader.ac.getString("Permission"))) {
					if(!hide.get(DisplayType.ACTIONBAR).contains(s.getName())) {
						hide.get(DisplayType.ACTIONBAR).add(s.getName());
						TheAPI.sendActionBar(s, "");
					}
					return;
				}
				if(Loader.ac.getStringList("ForbiddenWorlds").contains(s.getWorld().getName())) {
					if(!hide.get(DisplayType.ACTIONBAR).contains(s.getName())) {
						hide.get(DisplayType.ACTIONBAR).add(s.getName());
						TheAPI.sendActionBar(s, "");
						return;
					}
					return;
				}
				if(ignore.get(DisplayType.ACTIONBAR).contains(s.getName())) {
					if(!hide.get(DisplayType.ACTIONBAR).contains(s.getName())) {
						if(!isToggleable(s, DisplayType.ACTIONBAR)) {
							String text = "Text";
							if(Loader.ac.exists("PerPlayer."+s.getName())) {
								text="PerPlayer."+s.getName()+".Text";
							}else {
								if(Loader.ac.exists("PerWorld."+s.getWorld().getName())) {
									text="PerWorld."+s.getWorld().getName()+".Text";
								}
							}
							TheAPI.sendActionBar(s, ac.replace(s, Loader.ac.getString(text)));
							if(msg)
							Loader.sendMessages(s, "DisplayManager.ActionBar.Show");
							return;
						}
						hide.get(DisplayType.ACTIONBAR).add(s.getName());
						TheAPI.sendActionBar(s, ""); //remove
						return;
					}else {
						if(!isToggleable(s, DisplayType.ACTIONBAR)) {
							hide.get(DisplayType.ACTIONBAR).remove(s.getName());
							String text = "Text";
							if(Loader.ac.exists("PerPlayer."+s.getName())) {
								text="PerPlayer."+s.getName()+".Text";
							}else {
								if(Loader.ac.exists("PerWorld."+s.getWorld().getName())) {
									text="PerWorld."+s.getWorld().getName()+".Text";
								}
							}
							TheAPI.sendActionBar(s, ac.replace(s, Loader.ac.getString(text)));
							if(msg)
							Loader.sendMessages(s, "DisplayManager.ActionBar.Show");
							return;
						}
						//already gone
						return;
					}
				}else {
					hide.get(DisplayType.ACTIONBAR).remove(s.getName());
					String text = "Text";
					if(Loader.ac.exists("PerPlayer."+s.getName())) {
						text="PerPlayer."+s.getName()+".Text";
					}else {
						if(Loader.ac.exists("PerWorld."+s.getWorld().getName())) {
							text="PerWorld."+s.getWorld().getName()+".Text";
						}
					}
					TheAPI.sendActionBar(s, ac.replace(s, Loader.ac.getString(text)));
					if(msg)
					Loader.sendMessages(s, "DisplayManager.ActionBar.Show");
					return;
				}
				}catch(Exception er) {}
			}
			break;
			case BOSSBAR:{
				try {
				if(!s.hasPermission(Loader.bb.getString("Permission"))) {
					if(!hide.get(DisplayType.BOSSBAR).contains(s.getName())) {
						hide.get(DisplayType.BOSSBAR).add(s.getName());
						BossBarManager.remove(s);
					}
					return;
				}	
				if(Loader.bb.getStringList("ForbiddenWorlds").contains(s.getWorld().getName())) {
					if(!hide.get(DisplayType.BOSSBAR).contains(s.getName())) {
						hide.get(DisplayType.BOSSBAR).add(s.getName());
						BossBarManager.remove(s);
						return;
					}
					return;
				}
				if(ignore.get(DisplayType.BOSSBAR).contains(s.getName())) {
					if(!hide.get(DisplayType.BOSSBAR).contains(s.getName())) {
						if(!isToggleable(s, DisplayType.BOSSBAR)) {
							String text = "Text";
							String stage = "Stage";
							String style = "Style";
							String color = "Color";
							if(Loader.bb.exists("PerPlayer."+s.getName())) {
								text="PerPlayer."+s.getName()+".Text";
								stage="PerPlayer."+s.getName()+".Stage";
								style="PerPlayer."+s.getName()+".Style";
								color="PerPlayer."+s.getName()+".Color";
							}else {
								if(Loader.bb.exists("PerWorld."+s.getWorld().getName())) {
									text="PerWorld."+s.getWorld().getName()+".Text";
									stage="PerWorld."+s.getWorld().getName()+".Stage";
									style="PerWorld."+s.getWorld().getName()+".Style";
									color="PerWorld."+s.getWorld().getName()+".Color";
								}
							}
							SBossBar b = sendBossBar(s, bb.replace(s, Loader.bb.getString(text)), StringUtils.calculate(PlaceholderAPI.setPlaceholders(s, Loader.bb.getString(stage))) /100);
							if(Loader.bb.getString(color)!=null)
								try {
									if(Loader.bb.getString(color).toUpperCase().equals("RANDOM")) {
										b.setRandomColor();
									}else
									b.setColor(Loader.bb.getString(color).toUpperCase());
								}catch(Exception | NoSuchFieldError e) {}
							if(Loader.bb.getString(style)!=null)
								try {
									b.setStyle(Loader.bb.getString(style).toUpperCase());
								}catch(Exception | NoSuchFieldError e) {}
							if(msg)
							Loader.sendMessages(s, "DisplayManager.BossBar.Show");
							return;
						}
						hide.get(DisplayType.BOSSBAR).add(s.getName());
						BossBarManager.remove(s); //remove
						return;
					}else {
						if(!isToggleable(s, DisplayType.BOSSBAR)) {
							hide.get(DisplayType.BOSSBAR).remove(s.getName());
							String text = "Text";
							String stage = "Stage";
							String style = "Style";
							String color = "Color";
							if(Loader.bb.exists("PerPlayer."+s.getName())) {
								text="PerPlayer."+s.getName()+".Text";
								stage="PerPlayer."+s.getName()+".Stage";
								style="PerPlayer."+s.getName()+".Style";
								color="PerPlayer."+s.getName()+".Color";
							}else {
								if(Loader.bb.exists("PerWorld."+s.getWorld().getName())) {
									text="PerWorld."+s.getWorld().getName()+".Text";
									stage="PerWorld."+s.getWorld().getName()+".Stage";
									style="PerWorld."+s.getWorld().getName()+".Style";
									color="PerWorld."+s.getWorld().getName()+".Color";
								}
							}
							SBossBar b = sendBossBar(s, bb.replace(s, Loader.bb.getString(text)), StringUtils.calculate(PlaceholderAPI.setPlaceholders(s, Loader.bb.getString(stage))) /100);
							if(Loader.bb.getString(color)!=null)
								try {
									if(Loader.bb.getString(color).toUpperCase().equals("RANDOM")) {
										b.setRandomColor();
									}else
									b.setColor(Loader.bb.getString(color).toUpperCase());
								}catch(Exception | NoSuchFieldError e) {}
							if(Loader.bb.getString(style)!=null)
								try {
									b.setStyle(Loader.bb.getString(style).toUpperCase());
								}catch(Exception | NoSuchFieldError e) {}
							if(msg)
							Loader.sendMessages(s, "DisplayManager.BossBar.Show");
							return;
						}
						//already gone
						return;
					}
				}else {
					hide.get(DisplayType.BOSSBAR).remove(s.getName());
					String text = "Text";
					String stage = "Stage";
					String style = "Style";
					String color = "Color";
					if(Loader.bb.exists("PerPlayer."+s.getName())) {
						text="PerPlayer."+s.getName()+".Text";
						stage="PerPlayer."+s.getName()+".Stage";
						style="PerPlayer."+s.getName()+".Style";
						color="PerPlayer."+s.getName()+".Color";
					}else {
						if(Loader.bb.exists("PerWorld."+s.getWorld().getName())) {
							text="PerWorld."+s.getWorld().getName()+".Text";
							stage="PerWorld."+s.getWorld().getName()+".Stage";
							style="PerWorld."+s.getWorld().getName()+".Style";
							color="PerWorld."+s.getWorld().getName()+".Color";
						}
					}
					SBossBar b = sendBossBar(s, bb.replace(s, Loader.bb.getString(text)), StringUtils.calculate(PlaceholderAPI.setPlaceholders(s, Loader.bb.getString(stage))) /100);
					if(Loader.bb.getString(color)!=null)
						try {
							if(Loader.bb.getString(color).toUpperCase().equals("RANDOM")) {
								b.setRandomColor();
							}else
							b.setColor(Loader.bb.getString(color).toUpperCase());
						}catch(Exception | NoSuchFieldError e) {}
					if(Loader.bb.getString(style)!=null)
						try {
							b.setStyle(Loader.bb.getString(style).toUpperCase());
						}catch(Exception | NoSuchFieldError e) {}
					if(msg)
					Loader.sendMessages(s, "DisplayManager.BossBar.Show");
					return;
				}
				}catch(Exception er) {}
			}
			break;
			case SCOREBOARD:{
				try {
				if(!s.hasPermission(Loader.sb.getString("Options.Permission"))) {
					if(!hide.get(DisplayType.SCOREBOARD).contains(s.getName())) {
						hide.get(DisplayType.SCOREBOARD).add(s.getName());
						if(map.containsKey(s.getName())) {
							map.remove(s.getName()).destroy();
						}
					}
					return;
				}
				if(Loader.sb.getStringList("Options.ForbiddenWorlds").contains(s.getWorld().getName())) {
					if(!hide.get(DisplayType.SCOREBOARD).contains(s.getName())) {
						hide.get(DisplayType.SCOREBOARD).add(s.getName());
						if(map.containsKey(s.getName())) {
							map.remove(s.getName()).destroy();
						}
						return;
					}
					return;
				}
				if(ignore.get(DisplayType.SCOREBOARD).contains(s.getName())) {
					if(!hide.get(DisplayType.SCOREBOARD).contains(s.getName())) {
						if(!isToggleable(s, DisplayType.SCOREBOARD)) {
							String name = "Name";
							String lines = "Lines";
							if(Loader.sb.exists("PerPlayer."+s.getName())) {
								name="PerPlayer."+s.getName()+".Name";
								lines="PerPlayer."+s.getName()+".Lines";
							}else if(Loader.sb.exists("PerWorld."+s.getWorld().getName())) {
								name="PerWorld."+s.getWorld().getName()+".Name";
								lines="PerWorld."+s.getWorld().getName()+".Lines";
							}
							score.setTitle(sb.replace(s, Loader.sb.getString(name)));
							for(String line : Loader.sb.getStringList(lines)) {
								score.addLine(sb.replace(s, line));
							}
							score.send(s);
							if(msg)
							Loader.sendMessages(s, "DisplayManager.Scoreboard.Show");
							return;
						}
						hide.get(DisplayType.SCOREBOARD).add(s.getName());
						if(map.containsKey(s.getName())) {
							map.remove(s.getName()).destroy();
						}
						return;
					}else {
						if(!isToggleable(s, DisplayType.SCOREBOARD)) {
							hide.get(DisplayType.SCOREBOARD).remove(s.getName());
							String name = "Name";
							String lines = "Lines";
							if(Loader.sb.exists("PerPlayer."+s.getName())) {
								name="PerPlayer."+s.getName()+".Name";
								lines="PerPlayer."+s.getName()+".Lines";
							}else if(Loader.sb.exists("PerWorld."+s.getWorld().getName())) {
								name="PerWorld."+s.getWorld().getName()+".Name";
								lines="PerWorld."+s.getWorld().getName()+".Lines";
							}
							score.setTitle(sb.replace(s, Loader.sb.getString(name)));
							for(String line : Loader.sb.getStringList(lines)) {
								score.addLine(sb.replace(s, line));
							}
							score.send(s);
							if(msg)
							Loader.sendMessages(s, "DisplayManager.Scoreboard.Show");
							return;
						}
						//already gone
						return;
					}
				}else {
					hide.get(DisplayType.SCOREBOARD).remove(s.getName());
					String name = "Name";
					String lines = "Lines";
					if(Loader.sb.exists("PerPlayer."+s.getName())) {
						name="PerPlayer."+s.getName()+".Name";
						lines="PerPlayer."+s.getName()+".Lines";
					}else if(Loader.sb.exists("PerWorld."+s.getWorld().getName())) {
						name="PerWorld."+s.getWorld().getName()+".Name";
						lines="PerWorld."+s.getWorld().getName()+".Lines";
					}
					score.setTitle(sb.replace(s, Loader.sb.getString(name)));
					for(String line : Loader.sb.getStringList(lines)) {
						score.addLine(sb.replace(s, line));
					}
					score.send(s);
					if(msg)
					Loader.sendMessages(s, "DisplayManager.Scoreboard.Show");
					return;
				}
				}catch(Exception er) {}
			}
			break;
		}
	}
	
	private static SBossBar sendBossBar(Player s, String replace, double d) {
		SBossBar get = BossBarManager.getOrCreate(s);
		get.setTitle(replace);
		get.setProgress(d);
		return get;
	}

	private static final SimpleScore score = new SimpleScore();
	protected static final AnimationManager ac = new AnimationManager();
	protected static final AnimationManager bb= new AnimationManager();
	protected static final AnimationManager sb=new AnimationManager();
	
	public static void hide(Player s, DisplayType type) {
		TheAPI.getUser(s).setAndSave("SCR."+type.name(), true);
		if(!ignore.get(type).contains(s.getName()))
			ignore.get(type).add(s.getName());
		switch(type) {
			case ACTIONBAR:{
				Loader.sendMessages(s, "DisplayManager.ActionBar.Hide");
				if(!isToggleable(s, DisplayType.ACTIONBAR)) {
					return;
				}
				hide.get(DisplayType.ACTIONBAR).add(s.getName());
				TheAPI.sendActionBar(s, ""); //remove
			}
			break;
			case BOSSBAR:{
				Loader.sendMessages(s, "DisplayManager.BossBar.Hide");
				if(!isToggleable(s, DisplayType.BOSSBAR)) {
					return;
				}
				hide.get(DisplayType.BOSSBAR).add(s.getName());
				BossBarManager.remove(s); //remove
			}
			break;
			case SCOREBOARD:{
				Loader.sendMessages(s, "DisplayManager.Scoreboard.Hide");
				if(!isToggleable(s, DisplayType.SCOREBOARD)) {
					return;
				}
				hide.get(DisplayType.SCOREBOARD).add(s.getName());
				if(map.containsKey(s.getName()))
					map.remove(s.getName()).destroy();
			}
			break;
		}
	}
	
	public static boolean has(Player p, DisplayType type) {
		return !TheAPI.getUser(p).exists("SCR." + type.name()) || TheAPI.getUser(p).getBoolean("SCR." + type.name());
	}

	public static boolean isToggleable(Player s, DisplayType type) {
		switch(type) {
			case ACTIONBAR:
				return Loader.ac.exists("PerPlayer."+s.getName())?Loader.ac.getBoolean("PerPlayer."+s.getName()+".Toggleable"):(Loader.ac.exists("PerWorld."+s.getWorld().getName())?Loader.ac.getBoolean("PerWorld."+s.getWorld().getName()+".Toggleable"):Loader.ac.getBoolean("Toggleable"));
			case BOSSBAR:
				return Loader.bb.exists("PerPlayer."+s.getName())?Loader.bb.getBoolean("PerPlayer."+s.getName()+".Toggleable"):(Loader.bb.exists("PerWorld."+s.getWorld().getName())?Loader.bb.getBoolean("PerWorld."+s.getWorld().getName()+".Toggleable"):Loader.bb.getBoolean("Toggleable"));
			case SCOREBOARD:
				return Loader.sb.exists("PerPlayer."+s.getName())?Loader.sb.getBoolean("PerPlayer."+s.getName()+".Toggleable"):(Loader.sb.exists("PerWorld."+s.getWorld().getName())?Loader.sb.getBoolean("PerWorld."+s.getWorld().getName()+".Toggleable"):Loader.sb.getBoolean("Options.Toggleable"));
		}
		return true;
	}

	public static boolean hasToggled(Player s, DisplayType type) {
		return TheAPI.getUser(s).getBoolean("SCR."+type.name());
	}
	
	private static final Set<Integer> tasks = new HashSet<>();
	private static final Map<DisplayType, Set<String>> ignore = new HashMap<>();
	private static final Map<DisplayType, Set<String>> hide = new HashMap<>();
	static {
		for(DisplayType t : DisplayType.values()) {
			ignore.put(t, new HashSet<>());
			hide.put(t, new HashSet<>());
		}
	}
	
	public static void load() {
		for(Player s : TheAPI.getOnlinePlayers())
			initializePlayer(s);
		if(Loader.ac.getBoolean("Enabled")) {
		tasks.add(new Tasker() {
			public void run() {
				for(Player s : TheAPI.getOnlinePlayers()) {
					try {
					if(!s.hasPermission(Loader.ac.getString("Permission"))) {
						if(!hide.get(DisplayType.ACTIONBAR).contains(s.getName())) {
							hide.get(DisplayType.ACTIONBAR).add(s.getName());
							TheAPI.sendActionBar(s, "");
						}
						continue;
					}
					if(Loader.ac.getStringList("ForbiddenWorlds").contains(s.getWorld().getName())) {
						if(!hide.get(DisplayType.ACTIONBAR).contains(s.getName())) {
							hide.get(DisplayType.ACTIONBAR).add(s.getName());
							TheAPI.sendActionBar(s, "");
							continue;
						}
						continue;
					}
					if(ignore.get(DisplayType.ACTIONBAR).contains(s.getName())) {
						if(!hide.get(DisplayType.ACTIONBAR).contains(s.getName())) {
							if(!isToggleable(s, DisplayType.ACTIONBAR)) {
								String text = "Text";
								if(Loader.ac.exists("PerPlayer."+s.getName())) {
									text="PerPlayer."+s.getName()+".Text";
								}else {
									if(Loader.ac.exists("PerWorld."+s.getWorld().getName())) {
										text="PerWorld."+s.getWorld().getName()+".Text";
									}
								}
								TheAPI.sendActionBar(s, ac.replace(s, Loader.ac.getString(text)));
								continue;
							}
							hide.get(DisplayType.ACTIONBAR).add(s.getName());
							TheAPI.sendActionBar(s, ""); //remove
							continue;
						}else {
							if(!isToggleable(s, DisplayType.ACTIONBAR)) {
								hide.get(DisplayType.ACTIONBAR).remove(s.getName());
								String text = "Text";
								if(Loader.ac.exists("PerPlayer."+s.getName())) {
									text="PerPlayer."+s.getName()+".Text";
								}else {
									if(Loader.ac.exists("PerWorld."+s.getWorld().getName())) {
										text="PerWorld."+s.getWorld().getName()+".Text";
									}
								}
								TheAPI.sendActionBar(s, ac.replace(s, Loader.ac.getString(text)));
								continue;
							}
							//already gone
						}
					}else {
						hide.get(DisplayType.ACTIONBAR).remove(s.getName());
						String text = "Text";
						if(Loader.ac.exists("PerPlayer."+s.getName())) {
							text="PerPlayer."+s.getName()+".Text";
						}else {
							if(Loader.ac.exists("PerWorld."+s.getWorld().getName())) {
								text="PerWorld."+s.getWorld().getName()+".Text";
							}
						}
						TheAPI.sendActionBar(s, ac.replace(s, Loader.ac.getString(text)));
					}
					}catch(Exception er) {}
				}
				ac.update();
			}
		}.runRepeating(0, (long) StringUtils.calculate(Loader.ac.getString("RefleshTick"))));
		}
		if(Loader.bb.getBoolean("Enabled"))
		tasks.add(new Tasker() {
			public void run() {
				for(Player s : TheAPI.getOnlinePlayers()) {
					try {
					if(!s.hasPermission(Loader.bb.getString("Permission"))) {
						if(!hide.get(DisplayType.BOSSBAR).contains(s.getName())) {
							hide.get(DisplayType.BOSSBAR).add(s.getName());
							BossBarManager.remove(s);
						}
						continue;
					}	
					if(Loader.bb.getStringList("ForbiddenWorlds").contains(s.getWorld().getName())) {
						if(!hide.get(DisplayType.BOSSBAR).contains(s.getName())) {
							hide.get(DisplayType.BOSSBAR).add(s.getName());
							BossBarManager.remove(s);
							continue;
						}
						continue;
					}
					if(ignore.get(DisplayType.BOSSBAR).contains(s.getName())) {
						if(!hide.get(DisplayType.BOSSBAR).contains(s.getName())) {
							if(!isToggleable(s, DisplayType.BOSSBAR)) {
								String text = "Text";
								String stage = "Stage";
								String style = "Style";
								String color = "Color";
								if(Loader.bb.exists("PerPlayer."+s.getName())) {
									text="PerPlayer."+s.getName()+".Text";
									stage="PerPlayer."+s.getName()+".Stage";
									style="PerPlayer."+s.getName()+".Style";
									color="PerPlayer."+s.getName()+".Color";
								}else {
									if(Loader.bb.exists("PerWorld."+s.getWorld().getName())) {
										text="PerWorld."+s.getWorld().getName()+".Text";
										stage="PerWorld."+s.getWorld().getName()+".Stage";
										style="PerWorld."+s.getWorld().getName()+".Style";
										color="PerWorld."+s.getWorld().getName()+".Color";
									}
								}
								SBossBar b = sendBossBar(s, bb.replace(s, Loader.bb.getString(text)), StringUtils.calculate(PlaceholderAPI.setPlaceholders(s, Loader.bb.getString(stage))) /100);
								if(Loader.bb.getString(color)!=null)
									try {
										if(Loader.bb.getString(color).toUpperCase().equals("RANDOM")) {
											b.setRandomColor();
										}else
										b.setColor(Loader.bb.getString(color).toUpperCase());
									}catch(Exception | NoSuchFieldError e) {}
								if(Loader.bb.getString(style)!=null)
									try {
										b.setStyle(Loader.bb.getString(style).toUpperCase());
									}catch(Exception | NoSuchFieldError e) {}
								continue;
							}
							hide.get(DisplayType.BOSSBAR).add(s.getName());
							BossBarManager.remove(s); //remove
							continue;
						}else {
							if(!isToggleable(s, DisplayType.BOSSBAR)) {
								hide.get(DisplayType.BOSSBAR).remove(s.getName());
								String text = "Text";
								String stage = "Stage";
								String style = "Style";
								String color = "Color";
								if(Loader.bb.exists("PerPlayer."+s.getName())) {
									text="PerPlayer."+s.getName()+".Text";
									stage="PerPlayer."+s.getName()+".Stage";
									style="PerPlayer."+s.getName()+".Style";
									color="PerPlayer."+s.getName()+".Color";
								}else {
									if(Loader.bb.exists("PerWorld."+s.getWorld().getName())) {
										text="PerWorld."+s.getWorld().getName()+".Text";
										stage="PerWorld."+s.getWorld().getName()+".Stage";
										style="PerWorld."+s.getWorld().getName()+".Style";
										color="PerWorld."+s.getWorld().getName()+".Color";
									}
								}
								SBossBar b = sendBossBar(s, bb.replace(s, Loader.bb.getString(text)), StringUtils.calculate(PlaceholderAPI.setPlaceholders(s, Loader.bb.getString(stage))) /100);
								if(Loader.bb.getString(color)!=null)
									try {
										if(Loader.bb.getString(color).toUpperCase().equals("RANDOM")) {
											b.setRandomColor();
										}else
										b.setColor(Loader.bb.getString(color).toUpperCase());
									}catch(Exception | NoSuchFieldError e) {}
								if(Loader.bb.getString(style)!=null)
									try {
										b.setStyle(Loader.bb.getString(style).toUpperCase());
									}catch(Exception | NoSuchFieldError e) {}
								continue;
							}
							//already gone
						}
					}else {
						hide.get(DisplayType.BOSSBAR).remove(s.getName());
						String text = "Text";
						String stage = "Stage";
						String style = "Style";
						String color = "Color";
						if(Loader.bb.exists("PerPlayer."+s.getName())) {
							text="PerPlayer."+s.getName()+".Text";
							stage="PerPlayer."+s.getName()+".Stage";
							style="PerPlayer."+s.getName()+".Style";
							color="PerPlayer."+s.getName()+".Color";
						}else {
							if(Loader.bb.exists("PerWorld."+s.getWorld().getName())) {
								text="PerWorld."+s.getWorld().getName()+".Text";
								stage="PerWorld."+s.getWorld().getName()+".Stage";
								style="PerWorld."+s.getWorld().getName()+".Style";
								color="PerWorld."+s.getWorld().getName()+".Color";
							}
						}
						SBossBar b = sendBossBar(s, bb.replace(s, Loader.bb.getString(text)), StringUtils.calculate(PlaceholderAPI.setPlaceholders(s, Loader.bb.getString(stage))) /100);
						if(Loader.bb.getString(color)!=null)
							try {
								if(Loader.bb.getString(color).toUpperCase().equals("RANDOM")) {
									b.setRandomColor();
								}else
								b.setColor(Loader.bb.getString(color).toUpperCase());
							}catch(Exception | NoSuchFieldError e) {}
						if(Loader.bb.getString(style)!=null)
							try {
								b.setStyle(Loader.bb.getString(style).toUpperCase());
							}catch(Exception | NoSuchFieldError e) {}
					}
					}catch(Exception er) {}
				}
				bb.update();
			}
		}.runRepeating(0, (long) StringUtils.calculate(Loader.bb.getString("RefleshTick"))));
		if (setting.sb)
		tasks.add(new Tasker() {
			public void run() {
				for(Player s : TheAPI.getOnlinePlayers()) {
					if(!init.contains(s))continue;
					try {
					if(!s.hasPermission(Loader.sb.getString("Options.Permission"))) {
						if(!hide.get(DisplayType.SCOREBOARD).contains(s.getName())) {
							hide.get(DisplayType.SCOREBOARD).add(s.getName());
							if(map.containsKey(s.getName())) {
								map.remove(s.getName()).destroy();
							}
						}
						continue;
					}
					if(Loader.sb.getStringList("Options.ForbiddenWorlds").contains(s.getWorld().getName())) {
						if(!hide.get(DisplayType.SCOREBOARD).contains(s.getName())) {
							hide.get(DisplayType.SCOREBOARD).add(s.getName());
							if(map.containsKey(s.getName())) {
								map.remove(s.getName()).destroy();
							}
						}
						continue;
					}
					if(ignore.get(DisplayType.SCOREBOARD).contains(s.getName())) {
						if(!hide.get(DisplayType.SCOREBOARD).contains(s.getName())) {
							if(!isToggleable(s, DisplayType.SCOREBOARD)) {
								String name = "Name";
								String lines = "Lines";
								if(Loader.sb.exists("PerPlayer."+s.getName())) {
									name="PerPlayer."+s.getName()+".Name";
									lines="PerPlayer."+s.getName()+".Lines";
								}else if(Loader.sb.exists("PerWorld."+s.getWorld().getName())) {
									name="PerWorld."+s.getWorld().getName()+".Name";
									lines="PerWorld."+s.getWorld().getName()+".Lines";
								}
								score.setTitle(sb.replace(s, Loader.sb.getString(name)));
								List<String> sdd = Loader.sb.getStringList(lines);
								sdd.replaceAll(a -> sb.replace(s, a));
								score.addLines(sdd);
								score.send(s);
								continue;
							}
							hide.get(DisplayType.SCOREBOARD).add(s.getName());
							if(map.containsKey(s.getName()))
								map.remove(s.getName()).destroy();
							continue;
						}else {
							if(!isToggleable(s, DisplayType.SCOREBOARD)) {
								hide.get(DisplayType.SCOREBOARD).remove(s.getName());
								String name = "Name";
								String lines = "Lines";
								if(Loader.sb.exists("PerPlayer."+s.getName())) {
									name="PerPlayer."+s.getName()+".Name";
									lines="PerPlayer."+s.getName()+".Lines";
								}else if(Loader.sb.exists("PerWorld."+s.getWorld().getName())) {
									name="PerWorld."+s.getWorld().getName()+".Name";
									lines="PerWorld."+s.getWorld().getName()+".Lines";
								}
								score.setTitle(sb.replace(s, Loader.sb.getString(name)));
								List<String> sdd = Loader.sb.getStringList(lines);
								sdd.replaceAll(a -> sb.replace(s, a));
								score.addLines(sdd);
								score.send(s);
								continue;
							}
							//already gone
						}
					}else {
						hide.get(DisplayType.SCOREBOARD).remove(s.getName());
						String name = "Name";
						String lines = "Lines";
						if(Loader.sb.exists("PerPlayer."+s.getName())) {
							name="PerPlayer."+s.getName()+".Name";
							lines="PerPlayer."+s.getName()+".Lines";
						}else if(Loader.sb.exists("PerWorld."+s.getWorld().getName())) {
							name="PerWorld."+s.getWorld().getName()+".Name";
							lines="PerWorld."+s.getWorld().getName()+".Lines";
						}
						score.setTitle(sb.replace(s, Loader.sb.getString(name)));
						List<String> sdd = Loader.sb.getStringList(lines);
						sdd.replaceAll(a -> sb.replace(s, a));
						score.addLines(sdd);
						score.send(s);
					}
					}catch(Exception er) {}
				}
				sb.update();
			}
		}.runRepeating(0, (long) StringUtils.calculate(Loader.sb.getString("Options.RefleshTick"))));
		
	}
	
	public static void unload() {
		for(int i : tasks)
			Scheduler.cancelTask(i);
		tasks.clear();
		for(DisplayType t : DisplayType.values()) {
			ignore.get(t).clear();
			hide.get(t).clear();
		}
		for(Player s : TheAPI.getOnlinePlayers()) {
			TheAPI.sendActionBar(s, "");
			if(BossBarManager.get(s)!=null)
			BossBarManager.remove(s);
			if(map.containsKey(s.getName()))
				map.remove(s.getName()).destroy();
		}
	}
}
