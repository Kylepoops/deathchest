package net.p24dev.deathchest;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DChest implements Listener{

	public Inventory inv;
	public Block chest;
	public Player p;
	public ArmorStand stand;
	private int id = 1;
	public String world;
	public ArrayList<Player> opened = new ArrayList<>();
	private Material mat;
	private Boolean firstUpdate = true;
	
	private int size;
	private int wait;
	private String staffPerm;
	private Material cMat;
	private Boolean holo, msgPlayer, broadcastMsg, msgStaff, prefixUsed, keepUnknownPlaceholders;
	private String prefix, staffPrefix, tpmessageText, crateName, holoName, playerMessage, staffMessage, bcMessage;
	public DChest(PlayerDeathEvent e, int size, int wait, Material cMat, Boolean holo, Boolean msgPlayer, Boolean broadcastMsg, Boolean msgStaff, String staffPerm, String prefix, String staffPrefix, String tpmessageText, String crateName, String holoName, String playerMessage, String staffMessage, String bcMessage, Boolean keepUnknownPlaceholders, Boolean prefixUsed) {
		this.size = size;
		this.wait = wait;
		this.cMat = cMat;
		this.holo = holo;
		this.msgPlayer = msgPlayer;
		this.broadcastMsg = broadcastMsg;
		this.msgStaff = msgStaff;
		this.staffPerm = staffPerm;
		this.prefix = prefix;
		this.staffPrefix = staffPrefix;
		this.tpmessageText = tpmessageText;
		this.crateName = crateName;
		this.holoName = holoName;
		this.playerMessage = playerMessage;
		this.staffMessage = staffMessage;
		this.bcMessage = bcMessage;
		this.keepUnknownPlaceholders = keepUnknownPlaceholders;
		this.prefixUsed = prefixUsed;
		chest(e);
	}
	
	private void chest(PlayerDeathEvent e) {
		p = e.getEntity();
		world = p.getWorld().getName();
		chest = p.getLocation().getBlock();
		inv = Bukkit.createInventory(null, size, text(crateName));
		Inventory pInv = p.getInventory();
		mat = chest.getType();
		
		if(holo) {
			stand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().add(0, 0, 0), EntityType.ARMOR_STAND);
			stand.setCustomName(text(holoName));
			stand.setVisible(false);
			stand.setGravity(false);
			stand.setInvulnerable(true);
			stand.setCustomNameVisible(true);
		}
		for(ItemStack item : pInv) {
			if(item != null) {
				inv.addItem(item);
			}
						
		}
		opened.add(p);
		if(msgPlayer) {
			msg(p, playerMessage);
		}
		if(broadcastMsg) {
			bc(null, bcMessage);
		}
		if(msgStaff) {
			bc(staffPerm, staffMessage);
//			sendTPMessage(p, tpmessageText);
		}
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("DeathChest"), new Runnable() {
			public void run() 
			{
				if(wait <= 0) 
				{
					restore();
					cancelTask();
				}else {
					if(firstUpdate) {
					chest.setType(cMat);
					firstUpdate = false;
					}
					Bukkit.getWorld(world).spawnParticle(Particle.VILLAGER_HAPPY, chest.getLocation(), 50, 2, 2, 2);
					wait = wait - 10;
				}
				
			}
			
		}, 10L, 10L);
	}
	public void restore() {
		chest.setType(mat);
		if(holo) {
			stand.remove();	
		}
	}
	public void cancelTask() {
		Bukkit.getScheduler().cancelTask(id);
	}
	private String text(String msg) {
		char[] chars = msg.toCharArray();
		ArrayList<String> labels = new ArrayList<>();
		ArrayList<Integer> pos = new ArrayList<>();
		labels.add("");
		pos.add(0);
		boolean flag = false;
		int p = 0;
		int n = 1;
		for(char c : chars) {
			if(c == '%') {
				flag = !flag;
				if(flag) {
					pos.add(p);
					labels.add("");
				}else {
					n++;
				}
			}else if(flag) {
				labels.set(n, labels.get(n) + c);
			}
			p++;

		}
		pos.add(msg.length());
		String finalMsg = msg.substring(0, pos.get(1));
		for (int i = 1; i < labels.size(); i++) {
			switch (labels.get(i)) {
			case "plrnm":
				finalMsg += this.p.getName();
				break;
			case "x":
				finalMsg += chest.getX();
				break;
			case "y":
				finalMsg += chest.getY();
				break;
			case "z":
				finalMsg += chest.getZ();
				break;
			case "loc":
				finalMsg += "X: " + chest.getX() + ", Y: " + chest.getY() + ", Z: " + chest.getZ();
				break;
			case "time":
				finalMsg += wait / 20;
				break;
			default:
				if(keepUnknownPlaceholders) {
					finalMsg += "%" + labels.get(i) + "%";
				}
				break;
			}
			finalMsg += msg.substring(pos.get(i) + labels.get(i).length() + 2, pos.get(i+1));
		}
		return ChatColor.translateAlternateColorCodes('&', finalMsg);
	}
	private void msg(Player p, String msg) {
		if(prefixUsed) {	msg = prefix + " " + msg;	}
		p.sendMessage(text(msg));
	}
	private void bc(String perm, String msg) {
		
		if(perm !=null) {
			if(prefixUsed) {	msg = prefix + staffPrefix + " " + msg;	}
			Bukkit.broadcast(text(msg), perm);
			return;
		}
		if(prefixUsed) {	msg = prefix + " " + msg;	}
		Bukkit.broadcastMessage(text(msg));
	}
}