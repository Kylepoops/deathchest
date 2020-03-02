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
	
	private int size;
	private int wait;
	private String staffPerm;
	private Material cMat;
	private Boolean holo, msgPlayer, broadcastMsg, msgStaff;
	public DChest(PlayerDeathEvent e, int size, int wait, Material cMat, Boolean holo, Boolean msgPlayer, Boolean broadcastMsg, Boolean msgStaff, String staffPerm) {
		this.size = size;
		this.wait = wait;
		this.cMat = cMat;
		this.holo = holo;
		this.msgPlayer = msgPlayer;
		this.broadcastMsg = broadcastMsg;
		this.msgStaff = msgStaff;
		this.staffPerm = staffPerm;
		chest(e);
	}
	
	private void chest(PlayerDeathEvent e) {
		p = e.getEntity();
		world = p.getWorld().getName();
		inv = Bukkit.createInventory(null, size, ChatColor.GREEN + p.getName() + "`s DeathChest");
		Inventory pInv = p.getInventory();
		chest = p.getLocation().getBlock();
		mat = chest.getType();
		chest.setType(cMat);
		if(holo) {
			stand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().add(0, 0, 0), EntityType.ARMOR_STAND);
			stand.setCustomName(ChatColor.GREEN + p.getName() + "`s DeathChest");
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
			p.sendMessage(ChatColor.GREEN + "[DeathChest]" + ChatColor.YELLOW +" Your DeathChest has been placed. Hurry up, or it will be removed!");
		}
		if(broadcastMsg) {
			Bukkit.broadcastMessage(ChatColor.GREEN + "[DeathChest]" + ChatColor.YELLOW + p.getName() + "`s DeathChest was placed. Hurry up, or it will be removed!");
		}
		if(msgStaff) {
			Bukkit.broadcast(ChatColor.GREEN + "[DeathChest] - STAFF MESSAGE " + ChatColor.YELLOW + p.getName()+"`s DeathChest was placed at X:" + chest.getLocation().getBlockX() + " Y:" + chest.getLocation().getBlockY() + chest.getLocation().getBlockX() + " Z:" + chest.getLocation().getBlockZ(), staffPerm);
			
		}
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("DeathChest"), new Runnable() {
			public void run() 
			{
				if(wait <= 0) 
				{
					Bukkit.getScheduler().cancelTask(id);
					restore();
				}else {
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
	
	
}
