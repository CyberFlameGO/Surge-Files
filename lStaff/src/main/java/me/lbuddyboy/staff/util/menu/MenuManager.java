package me.lbuddyboy.staff.util.menu;

import me.lbuddyboy.staff.lStaff;
import me.lbuddyboy.staff.util.menu.listener.MenuListener;
import org.bukkit.Bukkit;


public class MenuManager {

	public MenuManager() {
		Bukkit.getPluginManager().registerEvents(new MenuListener(), lStaff.getInstance());
	}

}
