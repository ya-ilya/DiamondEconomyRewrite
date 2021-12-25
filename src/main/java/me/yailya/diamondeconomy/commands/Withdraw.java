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

public class Withdraw implements CommandExecutor {
    private final DiamondEconomy Main;

    public Withdraw(DiamondEconomy Main) {
        this.Main = Main;
        Main.getCommand("withdraw").setExecutor(this);
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
                                + "Invalid arguments! Argument <Count> cannot be equal to 0" +
                                " or a smaller number"
                                + ChatColor.RESET);
                        return true;
                    }
                } catch (Exception exception) {

                    if (Objects.equals(args[0], "all")) {
                        if (Main.Economy.getBalance(Player) < 1) {
                            sender.sendMessage(ChatColor.RED + "You don't have enough money!"
                                    + ChatColor.RESET);
                            return true;
                        }else {
                            Count = (int) Main.Economy.getBalance(Player);
                        }
                    }else {
                        sender.sendMessage(ChatColor.RED + "Invalid arguments!" +
                                " Correct usage - /" + label + " <Count/all>");
                        return true;
                    }
                }

                int MayAdd = MayAdd(Player);

                if (MayAdd <= 0) {
                    sender.sendMessage(ChatColor.RED + "There are not enough slots in " +
                            "your inventory! Clear it and use the command again."
                            + ChatColor.RESET);
                    return true;
                }

                if (Main.Economy.getBalance(Player) >= Count && Count <= MayAdd) {
                    var response = Main.Economy.withdrawPlayer(Player, Count);

                    if (response.transactionSuccess()) {
                        sender.sendMessage(ChatColor.GREEN + "You received "
                                + ChatColor.BLUE + Count + ChatColor.GREEN
                                + " diamonds in exchange for " + ChatColor.RED
                                + Count + "$" + ChatColor.RESET);
                        Player.getInventory().addItem(new ItemStack(Material.DIAMOND, Count));
                    } else {
                        sender.sendMessage(ChatColor.RED + "A transaction error occurred!"
                                + ChatColor.RESET);

                        Main.getLogger().warning("Error message: "
                                + response.errorMessage + "\n Player: " + sender.getName());
                    }
                } else if (Main.Economy.getBalance(Player) >= MayAdd && Count >= MayAdd) {
                    var response = Main.Economy.withdrawPlayer(Player, MayAdd);

                    if (response.transactionSuccess()) {
                        sender.sendMessage(ChatColor.GREEN + "You were given an incomplete" +
                                " amount due to insufficient slots in your inventory"
                                + ChatColor.RESET);
                        sender.sendMessage(ChatColor.GREEN + "You received "
                                + ChatColor.BLUE + MayAdd + ChatColor.GREEN
                                + " diamonds in exchange for " + ChatColor.RED
                                + MayAdd + "$" + ChatColor.RESET);
                        Player.getInventory().addItem(new ItemStack(Material.DIAMOND, MayAdd));
                    } else {
                        sender.sendMessage(ChatColor.RED + "A transaction error occurred!"
                                + ChatColor.RESET);

                        Main.getLogger().warning("Error message: "
                                + response.errorMessage + "\n Player: " + sender.getName());
                    }
                } else if (Main.Economy.getBalance(Player) < 1) {
                    sender.sendMessage(ChatColor.RED + "You don't have enough money!"
                            + ChatColor.RESET);
                }
            }
        }else {
            sender.sendMessage(ChatColor.RED + "Invalid arguments!" +
                    " Correct usage - /" + label + " <Count/all>");
        }

        return true;
    }

    public int MayAdd(Player Player) {
        int MayAdd = 0;
        for (var Item : Player.getInventory().getStorageContents()) {
            if (Item == null) {
                MayAdd += 64;
            }else {
                if (Item.getType() == Material.DIAMOND) {
                    MayAdd += 64 - Item.getAmount();
                }
            }
        }
        return MayAdd;
    }
}
