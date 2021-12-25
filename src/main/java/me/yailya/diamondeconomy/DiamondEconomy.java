package me.yailya.diamondeconomy;

import me.yailya.diamondeconomy.commands.Deposit;
import me.yailya.diamondeconomy.commands.Withdraw;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiamondEconomy extends JavaPlugin {
    public Economy Economy;

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault" +
                    " dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }

        new Deposit(this);
        new Withdraw(this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().
                getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        Economy = rsp.getProvider();
        return Economy != null;
    }
}
