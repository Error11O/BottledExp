package me.sacnoth.bottledexp;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BottledExpCommandExecutor implements CommandExecutor, TabCompleter {

	public BottledExpCommandExecutor(BottledExp plugin) {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
							 String commandLabel, String[] args) {
		if ((sender instanceof Player player)) {
            if (cmd.getName().equalsIgnoreCase("bottle")
					&& BottledExp.checkPermission("bottle.use", player)) {
				int currentxp = BottledExp.getPlayerExperience(player);

				if (args.length == 0) {
					sender.sendMessage(BottledExp.langCurrentXP + ": " + currentxp + " XP!");
					return true;
				}
				int amount = 0;
				if (args.length == 1) {
					switch (args[0]) {
						case "max":
							if (!BottledExp.checkPermission("bottle.max", player)) return true;
							amount = (int) Math.floor((double) currentxp / BottledExp.xpCost);
							amount = Math.min(BottledExp.countItems(player, Material.GLASS_BOTTLE) / BottledExp.amountConsumed, amount);
							break;
						case "reload":
							if (!BottledExp.checkPermission("bottle.reload", player) && !player.isOp()) return true;
							BottledExp.config.reload(sender);
							sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
							break;
						default:
							try {
								amount = Integer.valueOf(args[0]).intValue();
							} catch (NumberFormatException nfe) {
								sender.sendMessage(ChatColor.RED
										+ BottledExp.errAmount);
								return true;
							}
							break;
					}

					if (currentxp < amount * BottledExp.xpCost) {
						sender.sendMessage(ChatColor.RED + BottledExp.errXP);
						return true;
					} else if (amount <= 0) {
						amount = 0;
						sender.sendMessage(ChatColor.GREEN + BottledExp.langOrder1 + " " + amount
								+ " " + BottledExp.langOrder2);
						return true;
					}

					boolean money = false;
					if (BottledExp.useVaultEcon) // Check if the player has enough
												// money
					{
						if (BottledExp.getBalance(player) > BottledExp.moneyCost
								* amount) {
							money = true;
						} else {
							player.sendMessage(ChatColor.RED + BottledExp.errMoney);
							return true;
						}
					}

					boolean consumeItems = false;
					if (BottledExp.settingUseItems) // Check if the player has enough items
					{
						consumeItems = BottledExp.checkInventory(player, Material.GLASS_BOTTLE, amount * BottledExp.amountConsumed);
						if (!consumeItems) {
							sender.sendMessage(ChatColor.RED + BottledExp.langItemConsumer);
							return true;
						}
					}

					PlayerInventory inventory = player.getInventory();
					ItemStack items = new ItemStack(Material.EXPERIENCE_BOTTLE, amount);
					HashMap<Integer, ItemStack> leftoverItems = inventory.addItem(items);
					player.setTotalExperience(0);
					player.setLevel(0);
					player.setExp(0);
					player.giveExp(currentxp - (amount * BottledExp.xpCost));
					if (leftoverItems.containsKey(0)) {
						int refundAmount = leftoverItems.get(0).getAmount();
						player.giveExp(refundAmount * BottledExp.xpCost);
						player.sendMessage(ChatColor.GREEN + BottledExp.langRefund + ": " + refundAmount); amount -= refundAmount;
					}

					if (money) // Remove money from player
					{
						BottledExp.withdrawMoney(player, BottledExp.moneyCost
								* amount);
						player.sendMessage(BottledExp.langMoney + ": "
								+ BottledExp.moneyCost * amount + " "
								+ BottledExp.economy.currencyNamePlural());
					}

					if (consumeItems) // Remove items from Player
					{
						if (!BottledExp.consumeItem(player,
								Material.GLASS_BOTTLE, amount
										* BottledExp.amountConsumed)) {
							sender.sendMessage(ChatColor.RED + BottledExp.langItemConsumer);
							return true;
						}
					}

					sender.sendMessage(ChatColor.GREEN + BottledExp.langOrder1 + " " + amount + " " + BottledExp.langOrder2);
				}
				return true;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You have to be a player!");
			return false;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
		if (commandSender.isOp()){
			return List.of("reload", "max", "16", "64", "128");
		}
		return List.of("max", "16", "64", "128");
	}
}
