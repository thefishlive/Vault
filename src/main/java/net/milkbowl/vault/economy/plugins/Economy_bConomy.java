package net.milkbowl.vault.economy.plugins;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import uk.badger.bConomy.Global;
import uk.badger.bConomy.bConomy;
import uk.badger.bConomy.account.Account;
import uk.badger.bConomy.config.Config;
import uk.badger.bConomy.config.DatabaseManager;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_bConomy implements Economy{

	private static final Logger log = Logger.getLogger("Vault");

    private final String name = "bConomy";
    private Plugin plugin = null;
    protected bConomy economy = null;

	public Economy_bConomy(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
   
        // Load Plugin in case it was loaded before
        if (economy == null) {
            Plugin ec = plugin.getServer().getPluginManager().getPlugin("iConomy");
            if (ec != null && ec.isEnabled() && ec.getClass().getName().equals("uk.badger.bConomy.bConomy")) {
                economy = (bConomy) ec;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }
	
	public class EconomyServerListener implements Listener{	
		Economy_bConomy economy = null;

        public EconomyServerListener(Economy_bConomy economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
        	if (economy.economy == null) {
                Plugin ec = plugin.getServer().getPluginManager().getPlugin("bConomy");
                
                if (ec != null && ec.isEnabled()) {
                    economy.economy = (bConomy) ec;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
        	 if (economy.economy != null) {
                 if (event.getPlugin().getDescription().getName().equals("bConomy")) {
                     economy.economy = null;
                     log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                 }
             }
        }
	}
	
	@Override
	public boolean isEnabled() {
		if (economy == null) {
            return false;
        } else {
            return economy.isEnabled();
        }
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public String format(double amount) {
		return Global.format(amount);
	}

	@Override
	public String currencyNamePlural() {
		return Config.currency;
	}

	@Override
	public String currencyNameSingular() {
		return Config.currency;
	}

	@Override
	public boolean hasAccount(String playerName) {
		return Global.getAccounts().get(playerName) != null;
	}

	@Override
	public double getBalance(String playerName) {
		Account account = Global.getAccounts().get(playerName);
		
		if (account == null)
			return 0;
		
		return account.getBalance();		
	}

	@Override
	public boolean has(String playerName, double amount) {
		Account account = Global.getAccounts().get(playerName);
		
		if (account == null)
			return false;
		
		return account.has(amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		Account account = Global.getAccounts().get(playerName);
		
		if (account == null)
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Account doesn't exist");
		
		if (account.has(amount)) {
			account.withdraw(amount);
			return new EconomyResponse(amount, account.getBalance(), ResponseType.SUCCESS, null);
		} else {
			return new EconomyResponse(0, account.getBalance(), ResponseType.FAILURE, "Insufficient funds");
		}
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		Account account = Global.getAccounts().get(playerName);
		
		if (account == null)
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Account doesn't exist");
		
		account.deposit(amount);
		return new EconomyResponse(amount, account.getBalance(), ResponseType.SUCCESS, null);
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, getName() + " does not support Banks.");
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, getName() + " does not support Banks.");
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, getName() + " does not support Banks.");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, getName() + " does not support Banks.");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, getName() + " does not support Banks.");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, getName() + " does not support Banks.");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, getName() + " does not support Banks.");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, getName() + " does not support Banks.");
	}

	@Override
	public List<String> getBanks() {
        throw new UnsupportedOperationException(getName() + " does not support listing of Banks");
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		if (hasAccount(playerName))
			return false;
		
		Account account = new Account(Global.getAccounts().size(),
									  	Global.getPlugin().getServer().getOfflinePlayer(playerName));
		
		Global.addAccout(account);
		DatabaseManager.addAccount(account);
		return true;
	}

	@Override
	public int fractionalDigits() {
		return -1;
	}

}
