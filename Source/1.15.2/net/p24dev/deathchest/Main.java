package net.p24dev.deathchest;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	private ArrayList<DChest> chests = new ArrayList<>();
	private int size;
	private int wait;
	private String staffPerm;
	private Material cMat;
	private Boolean holo, msgPlayer, broadcastMsg, msgStaff, keepUnknownPlaceholders, prefixUsed;
	private String prefix, staffPrefix, tpmessageText, crateName, holoName, playerMessage, staffMessage, bcMessage;
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);

		loadConfig();
	}
	@Override
	public void onDisable() {
		for(DChest chest : chests){
			chest.restore();
		}
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		
		e.getDrops().clear();
		chests.add(new DChest(e, size, wait, cMat, holo, msgPlayer, broadcastMsg, msgStaff, staffPerm, prefix, staffPrefix, tpmessageText, crateName, holoName, playerMessage, staffMessage, bcMessage, keepUnknownPlaceholders, prefixUsed));
		
	}
	public void loadConfig() {
		if(this.getConfig().getString("config-version") != "1.0.3") {
			this.getConfig().options().copyDefaults();
			saveDefaultConfig();
			this.getConfig().set("config-version", "1.0.3");
		}
		wait = this.getConfig().getInt("lifespan-ticks");
		size = this.getConfig().getInt("crate-size");
		holo = this.getConfig().getBoolean("holo");
		msgPlayer = this.getConfig().getBoolean("msg-player");
		broadcastMsg = this.getConfig().getBoolean("bc-msg");
		msgStaff = this.getConfig().getBoolean("msg-staff");
		staffPerm = this.getConfig().getString("staff-perm");
		String matStr = this.getConfig().getString("crate-block");
		cMat = Material.getMaterial(matStr);
		prefix = this.getConfig().getString("prefix");
		staffPrefix = this.getConfig().getString("staff-prefix");
		tpmessageText = this.getConfig().getString("tpmessage-text");
		crateName = this.getConfig().getString("crate-name");
		holoName = this.getConfig().getString("holo-name");
		playerMessage = this.getConfig().getString("player-message");
		staffMessage = this.getConfig().getString("staff-message"); 
		bcMessage = this.getConfig().getString("bc-message");
		keepUnknownPlaceholders = this.getConfig().getBoolean("keep-unknown-placeholders");
		prefixUsed = this.getConfig().getBoolean("use-prefix");
		
	}
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		try {
			if(e.getClickedBlock().getType().equals(cMat)) {
				Block target = e.getClickedBlock();
				for (int i = 0; i < chests.size(); i++) {
					if(chests.get(i).chest.getLocation().equals(target.getLocation())) {
						if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
							e.setCancelled(true);
							e.getPlayer().openInventory(chests.get(i).inv);
						} else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
							Player p = e.getPlayer();
							Location loc = p.getLocation();
							
							for (ItemStack item : chests.get(i).inv.getContents()) {
							    if (item != null) {
							        loc.getWorld().dropItemNaturally(loc, item.clone());
							    }
							}
							chests.get(i).inv.clear();
							chests.get(i).cancelTask();
							chests.get(i).restore();
							chests.remove(i);
							
						} 
					}
				}
				
			}
		} catch (Exception err) {
		}
		
		
	}

	
}