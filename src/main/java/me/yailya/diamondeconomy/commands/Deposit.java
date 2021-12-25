package me.yailya.diamondeconomy.commands;

import me.yailya.diamondeconomy.DiamondEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Deposit implements CommandExecutor {
    private final DiamondEconomy Main;

    public Deposit(DiamondEconomy Main) {
        this.Main = Main;
        Main.getCommand("deposit").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args != null) {
            if (sender instanceof Player) {
                int Count;

                var Player = (org.bukkit.entity.Player) sender;

                try {
                    Count = Integer.parseInt(args[0]);

                    if (Count <= 0) {
                        sender.sendMessage(ChatColor.RED
                                + "Invalid arguments! Argument <Count> cannot be equal to 0 or a smaller number"
                                + ChatColor.RESET);
                        return true;
                    }
                } catch (Exception exception) {
                    if (Objects.equals(args[0], "all")) {
                        if (GetItemsCount(Player, Material.DIAMOND) == 0) {
                            sender.sendMessage(ChatColor.RED + "You don't have enough diamonds!"
                                    + ChatColor.RESET);
                            return true;
                        }else {
                            Count = GetItemsCount(Player, Material.DIAMOND);
                        }
                    }else {
                        sender.sendMessage(ChatColor.RED + "Invalid arguments!" +
                                " Correct usage - /" + label + " <Count/all>");
                        return true;
                    }
                }

                if (GetItemsCount(Player, Material.DIAMOND) >= Count) {
                    int Money = ItemToMoney(Player, Material.DIAMOND, Count);

                    var response = Main.Economy.depositPlayer(Player, Money);

                    if (response.transactionSuccess()) {
                        sender.sendMessage(ChatColor.GREEN + "You received " + ChatColor.RED
                                + Money + "$" + ChatColor.GREEN + " in exchange for " + ChatColor.BLUE
                                + Money + ChatColor.GREEN + " diamonds" + ChatColor.RESET);
                    } else {
                        sender.sendMessage(ChatColor.RED + "A transaction error has occurred! The diamonds were returned to your inventory"
                                + ChatColor.RESET);

                        Player.getInventory().addItem(new ItemStack(Material.DIAMOND, Money));

                        Main.getLogger().warning("Error message: "
                                + response.errorMessage + "\n Player: " + sender.getName());
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have "
                            + Count + " diamonds!" + ChatColor.RESET);
                }
            }
        }else {
            sender.sendMessage(ChatColor.RED + "Invalid arguments!" +
                    " Correct usage - /" + label + " <Count/all>");
        }

        return true;
    }

    public int GetItemsCount(Player Player, Material Material) {
        int Count = 0;
        for (var Item : Player.getInventory()) {
            if (Item != null) {
                if (Item.getType() == Material) {
                    Count += Item.getAmount();
                }
            }
        }
        return Count;
    }

    public int ItemToMoney(Player Player, Material Material, int Count) {
        int Money = 0;
        for (var Item : Player.getInventory()) {
            if (Count == 0) break;
            if (Item != null) {
                if (Item.getType() == Material) {
                    if (Item.getAmount() <= Count) {
                        Money += Item.getAmount();
                        Count -= Item.getAmount();
                        Item.setAmount(0);
                    } else if (Item.getAmount() > Count) {
                        Money += Count;
                        Item.setAmount(Item.getAmount() - Count);
                        Count = 0;
                    }
                }
            }
        }
        return Money;
    }
}
