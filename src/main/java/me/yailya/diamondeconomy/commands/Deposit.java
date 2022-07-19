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

public class Deposit implements CommandExecutor {
    public Deposit() {
        DiamondEconomy.INSTANCE.getCommand("deposit").setExecutor(this);
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
                        + "Invalid arguments! Argument <Count> cannot be equal to 0 or a smaller number"
                        + ChatColor.RESET);

                return true;
            }
        } catch (NumberFormatException exception) {
            if (Objects.equals(args[0], "all")) {
                count = getDiamondsCount(player);

                if (count == 0) {
                    sender.sendMessage(ChatColor.RED + "You don't have enough diamonds!"
                            + ChatColor.RESET);

                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid arguments!" +
                        " Correct usage - /" + label + " <Count/all>");

                return true;
            }
        }

        if (getDiamondsCount(player) >= count) {
            int money = diamondsToMoney(player, count);
            EconomyResponse response = economy.depositPlayer(player, money);

            if (response.transactionSuccess()) {
                sender.sendMessage(ChatColor.GREEN + "You received " + ChatColor.RED
                        + money + "$" + ChatColor.GREEN + " in exchange for " + ChatColor.BLUE
                        + money + ChatColor.GREEN + " diamonds" + ChatColor.RESET);
            } else {
                sender.sendMessage(ChatColor.RED + "A transaction error has occurred! The diamonds were returned to your inventory"
                        + ChatColor.RESET);

                player.getInventory().addItem(new ItemStack(Material.DIAMOND, money));

                DiamondEconomy.INSTANCE.getLogger().warning("Error message: "
                        + response.errorMessage + "\n Player: " + sender.getName());
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have "
                    + count + " diamonds!" + ChatColor.RESET);
        }

        return true;
    }

    private int getDiamondsCount(Player player) {
        int count = 0;

        for (ItemStack stack : player.getInventory()) {
            if (stack != null && stack.getType() == Material.DIAMOND) {
                count += stack.getAmount();
            }
        }

        return count;
    }

    private int diamondsToMoney(Player player, int count) {
        int money = 0;

        if (count == 0) return money;

        for (ItemStack stack : player.getInventory()) {
            if (stack != null && stack.getType() == Material.DIAMOND) {
                int stackAmount = stack.getAmount();

                if (stackAmount > count) {
                    stackAmount = count;
                }

                money += stackAmount;
                count -= stackAmount;
                stack.setAmount(0);

                if (count == 0) {
                    break;
                }
            }
        }

        return money;
    }
}
