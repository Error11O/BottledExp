package me.sacnoth.bottledexp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

public class EnchantingTableListener implements Listener {

    @EventHandler
    public void onEnchantingTableUse(PlayerInteractEvent event) {
        if (BottledExp.settingEnchantingTable) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND && event.getClickedBlock().getType() == Material.ENCHANTING_TABLE) {
                Player player = event.getPlayer();
                int amount = 1;
                int currentxp = BottledExp.getPlayerExperience(player);
                if (currentxp < amount * BottledExp.xpCost) {
                    player.sendMessage(ChatColor.RED + BottledExp.errXP);
                    return;
                } else if (amount <= 0) {
                    amount = 0;
                    player.sendMessage(ChatColor.GREEN + BottledExp.langOrder1 + " " + amount
                            + " " + BottledExp.langOrder2);
                    return;
                }

                boolean consumeItems = false;
                if (BottledExp.settingUseItems)
                {
                    consumeItems = BottledExp.checkInventory(player, Material.GLASS_BOTTLE, amount * BottledExp.amountConsumed);
                    if (!consumeItems) {
                        player.sendMessage(ChatColor.RED + BottledExp.langItemConsumer);
                        return;
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

                if (consumeItems) // Remove items from Player
                {
                    if (!BottledExp.consumeItem(player,
                            Material.GLASS_BOTTLE, amount
                                    * BottledExp.amountConsumed)) {
                        player.sendMessage(ChatColor.RED + BottledExp.langItemConsumer);
                    }
                }
            }
        }
    }
}
