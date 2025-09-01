package me.sacnoth.bottledexp;

import java.util.Objects;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class BottledExp extends JavaPlugin {
	static boolean settingEnchantingTable;
	static Logger log;
    static int xpCost;
	static int xpEarn;
	static String errAmount;
	static String errXP;
	static String langCurrentXP;
	static String langOrder1;
	static String langOrder2;
	static String langRefund;
	static String langItemConsumer;
	static boolean settingUseItems;
	static int amountConsumed;
	static Config config;

	public void onEnable() {
		log = this.getLogger();

        BottledExpCommandExecutor myExecutor = new BottledExpCommandExecutor(this);
		Objects.requireNonNull(getCommand("bottle")).setExecutor(myExecutor);

		this.getServer().getPluginManager().registerEvents(new EnchantingTableListener(), this);

		config = new Config(this);
		config.load();
		log.info("You are now able to fill XP into Bottles");
	}

	public void onDisable() {
		log.info("You are no longer able to fill XP into Bottles");
	}


	public static int getPlayerExperience(Player player) {
		int bukkitExp = player.getTotalExperience();
		return bukkitExp;
	}

	public static boolean checkInventory(Player player, Material item, int amount) {
		PlayerInventory inventory = player.getInventory();

		if (inventory.contains(Material.GLASS_BOTTLE, amount)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean consumeItem(Player player, Material item, int amount) {
		PlayerInventory inventory = player.getInventory();

		if (inventory.contains(Material.GLASS_BOTTLE, amount)) {
			ItemStack items = new ItemStack(item, amount);
			inventory.removeItem(items);
			return true;
		} else {
			return false;
		}
	}

	public static int countItems(Player player, Material item) {
		PlayerInventory inventory = player.getInventory();

		int amount = 0;
		ItemStack curItem;
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			curItem = inventory.getItem(slot);
			if (curItem != null && curItem.getType() == item)
				amount += curItem.getAmount();
		}
		return amount;
	}
}
