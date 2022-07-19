package me.yailya.diamondeconomy;

import me.yailya.diamondeconomy.commands.Deposit;
import me.yailya.diamondeconomy.commands.Withdraw;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiamondEconomy extends JavaPlugin {
    public static DiamondEconomy INSTANCE;

    private Economy economy;

    @Override
    public void onEnable() {
        INSTANCE = this;

        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault" +
                    " dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }

        new Deposit();
        new Withdraw();
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

        economy = rsp.getProvider();
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }
}
