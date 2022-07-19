package me.yailya.diamondeconomy.commands;

import me.yailya.diamondeconomy.DiamondEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Withdraw implements CommandExecutor {
    public Withdraw() {
        DiamondEconomy.INSTANCE.getCommand("withdraw").setExecutor(this);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command");

            return true;
        }

        if (args == null || args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments!" +
                    " Correct usage - /" + label + " <Count/all>");

            return true;
        }

        Economy economy = DiamondEconomy.INSTANCE.getEconomy();
        Player player = (Player) sender;
        int count;

        try {
            count = Integer.parseInt(args[0]);

            if (count <= 0) {
                sender.sendMessage(ChatColor.RED
                        + "Invalid arguments! Argument <Count> cannot be equal to 0" +
                        " or a smaller number"
                        + ChatColor.RESET);

                return true;
            }
        } catch (NumberFormatException exception) {
            if (Objects.equals(args[0], "all")) {
                if (economy.getBalance(player) < 1) {
                    sender.sendMessage(ChatColor.RED + "You don't have enough money!"
                            + ChatColor.RESET);

                    return true;
                } else {
                    count = (int) economy.getBalance(player);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid arguments!" +
                        " Correct usage - /" + label + " <Count/all>");
                return true;
            }
        }

        int mayAdd = getMayAdd(player);

        if (mayAdd <= 0) {
            sender.sendMessage(ChatColor.RED + "There are not enough slots in " +
                    "your inventory! Clear it and use the command again."
                    + ChatColor.RESET);

            return true;
        }

        if (economy.getBalance(player) >= count && count <= mayAdd) {
            EconomyResponse response = economy.withdrawPlayer(player, count);

            if (response.transactionSuccess()) {
                sender.sendMessage(ChatColor.GREEN + "You received "
                        + ChatColor.BLUE + count + ChatColor.GREEN
                        + " diamonds in exchange for " + ChatColor.RED
                        + count + "$" + ChatColor.RESET);
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, count));
            } else {
                sender.sendMessage(ChatColor.RED + "A transaction error occurred!"
                        + ChatColor.RESET);
                DiamondEconomy.INSTANCE.getLogger().warning("Error message: "
                        + response.errorMessage + "\n Player: " + sender.getName());
            }
        } else if (economy.getBalance(player) >= mayAdd && count >= mayAdd) {
            EconomyResponse response = economy.withdrawPlayer(player, mayAdd);

            if (response.transactionSuccess()) {
                sender.sendMessage(ChatColor.GREEN + "You were given an incomplete" +
                        " amount due to insufficient slots in your inventory"
                        + ChatColor.RESET);
                sender.sendMessage(ChatColor.GREEN + "You received "
                        + ChatColor.BLUE + mayAdd + ChatColor.GREEN
                        + " diamonds in exchange for " + ChatColor.RED
                        + mayAdd + "$" + ChatColor.RESET);
                player.getInventory().addItem(new ItemStack(Material.DIAMOND, mayAdd));
            } else {
                sender.sendMessage(ChatColor.RED + "A transaction error occurred!"
                        + ChatColor.RESET);
                DiamondEconomy.INSTANCE.getLogger().warning("Error message: "
                        + response.errorMessage + "\n Player: " + sender.getName());
            }
        } else if (economy.getBalance(player) < 1) {
            sender.sendMessage(ChatColor.RED + "You don't have enough money!"
                    + ChatColor.RESET);
        }

        return true;
    }

    private int getMayAdd(Player Player) {
        int mayAdd = 0;

        for (ItemStack stack : Player.getInventory().getStorageContents()) {
            if (stack == null) {
                mayAdd += 64;
            } else if (stack.getType() == Material.DIAMOND) {
                mayAdd += 64 - stack.getAmount();
            }
        }

        return mayAdd;
    }
}
