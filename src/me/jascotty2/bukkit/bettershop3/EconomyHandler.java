/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 *
 * Description: (TODO)
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.jascotty2.bukkit.bettershop3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import me.jascotty2.libv2.io.CheckInput;
import me.jascotty2.libv2.io.FileIO;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyHandler {

	final BetterShop3 plugin;
	Economy vaultEcon = null;

	protected EconomyHandler(BetterShop3 plugin) {
		this.plugin = plugin;
	}

	protected void enable() {
		// first attempt to load external economy plugins
		Plugin v = plugin.getServer().getPluginManager().getPlugin("Vault");
		if (v instanceof Vault) {
			RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
			if (rsp != null) {
				vaultEcon = rsp.getProvider();
			}
		}
		if (vaultEcon == null) {
			enableInternal();
		}
	}

	public String getMethodName() {
		if (vaultEcon != null) {
			return vaultEcon.getName();
		} else {
			return "Internal";
		}
	}

	public boolean hasAccount(Player pl) {
		return hasAccount(pl.getName());
	}

	public boolean hasAccount(String pl) {
		if (pl == null) {
			return false;
		} else if (vaultEcon != null) {
			return vaultEcon.hasAccount(pl);
		} else {
			return true;
		}
	}

	public boolean hasBank(String bank) {
		if (vaultEcon != null) {
			return vaultEcon.hasBankSupport() && vaultEcon.bankBalance(bank).transactionSuccess();
		}
		return false;
	}

	public double getBalance(Player pl) {
		return pl == null ? 0 : getBalance(pl.getName());
	}

	public double getBalance(String pl) {
		if (pl == null) {
			return 0;
		} else if (vaultEcon != null) {
			return vaultEcon.getBalance(pl);
		} else {
			if (!accounts.containsKey(pl)) {
				accounts.put(pl, plugin.config.econ_internal_startAmount);
			}
			return accounts.get(pl);
		}
	}

	public boolean canAfford(Player pl, double amt) {
		return pl != null ? getBalance(pl.getName()) >= amt : false;
	}

	public boolean canAfford(String pl, double amt) {
		return pl != null ? getBalance(pl) >= amt : false;
	}

	public boolean playerTransaction(String player, double amount) {
		if (player != null && player.length() > 0
				&& plugin.config.econ_bankName.equalsIgnoreCase(player)) {
			return bankTransaction(player, amount);
		}
		// starting amount, for reference
		double preAmt = getBalance(player);
		// don't allow account to go negative
		if (amount > 0 || preAmt >= -amount) {
			if (vaultEcon != null) {
				if (amount > 0) {
					EconomyResponse resp = vaultEcon.depositPlayer(player, amount);
					return resp.transactionSuccess();
				} else {
					EconomyResponse resp = vaultEcon.withdrawPlayer(player, -amount);
					return resp.transactionSuccess();
				}
			} else {
				accounts.put(player, accounts.get(player) + amount);
				delayedSave();
				return true;
			}
			//return getBalance(player) != preAmt;
		}
		return false;
	}

	public boolean bankTransaction(String bank, double amount) {
		if (bank != null || bank.length() == 0) {
			return false;
		}
		double preAmt = 0;
		if (vaultEcon != null) {
			if (vaultEcon.hasBankSupport()) {
				EconomyResponse balance = vaultEcon.bankBalance(bank);
				if (balance.transactionSuccess()) {
					preAmt = balance.balance;
				}
			}
		}
		if (preAmt <= 0) {
			return false;
		}
		if (vaultEcon != null) {
			if (amount > 0) {
				EconomyResponse resp = vaultEcon.bankDeposit(bank, amount);
				return resp.transactionSuccess();
			} else {
				EconomyResponse resp = vaultEcon.bankWithdraw(bank, -amount);
				return resp.transactionSuccess();
			}
		}
		return false;
	}
	
	public String getCurrencyName() {
		if (vaultEcon != null) {
			return vaultEcon.currencyNamePlural();
		}
		return plugin.config.econ_currency_m;
	}
	
	public String numFormat(double amt) {
		if (vaultEcon != null) {
			return String.format("%." + (vaultEcon.fractionalDigits() < 0 ? 2 : vaultEcon.fractionalDigits()) + "f", amt);
		}
		return String.format("%.2f", amt);
	}

	public String format(double amt) {
//		try {
		if (vaultEcon != null) {
			return vaultEcon.format(amt);
		}
		if (plugin.config.econ_currency_multi) {
			int c = (int) Math.abs(Math.round((amt - (int) amt) * 100));
			if (c > 0) {
				return String.format("%d %s, %d %s", (int) amt,
						(int) Math.abs(amt) > 1 ? plugin.config.econ_currency_m : plugin.config.econ_currency_s,
						c, c > 1 ? plugin.config.econ_currency_minor_m : plugin.config.econ_currency_minor_s);
			}
			return String.format("%d %s", (int) amt,
					(int) Math.abs(amt) > 1 ? plugin.config.econ_currency_m : plugin.config.econ_currency_s);
		}
		return String.format("%.2f %s", amt, plugin.config.econ_currency_m);//Math.abs(amt) > 1 ? plugin.config.econ_currency_m : plugin.config.econ_currency_s);
//		} catch (Exception ex) {
//			plugin.getLogger().log(Level.WARNING, "Error Formatting Currency", ex);
//		}
//		return String.format("%.2f", amt);
	}

	public double getPlayerDiscount(Player p) {
		if (p != null && !plugin.permissions.has(p, "BetterShop.discount.none")) {
			double discount = Double.NEGATIVE_INFINITY;
			for (Map.Entry<String, Double> g : plugin.config.econ_discountGroups.entrySet()) {
				if (plugin.permissions.has(p, "BetterShop.discount." + g.getKey())) {
					if (g.getValue() > discount) {
						discount = g.getValue();
					}
				}
			}
			return discount > Double.NEGATIVE_INFINITY ? discount : 0;
		}
		return 0;
	}
	//----------------------------//
	//  internal econ methods     //
	//----------------------------//
	private Saver saveTask = null;
	final File internalDB = new File(FileManager.pluginDir, "accounts.csv");
	final HashMap<String, Double> accounts = new HashMap<String, Double>();

	private void enableInternal() {
		if (internalDB.exists()) {
			try {
				List<String[]> file = FileIO.loadCSVFile(internalDB);
				for (String[] line : file) {
					if (line.length > 1 && CheckInput.IsDouble(line[1])) {
						accounts.put(line[0], CheckInput.GetDouble(line[1], 0));
					}
				}
			} catch (Exception ex) {
				plugin.getLogger().log(Level.SEVERE, "Error loading Accounts DB", ex);
			}
		}
	}

	public void flushSave() {
		if (saveTask != null) {
			saveTask.cancel();
			saveTask.run();
		}
	}

	protected void delayedSave() {
		if (saveTask != null) {
			saveTask.cancel();
		}
		saveTask = new Saver();
		saveTask.start(30000);
	}

	protected void saveAccounts() {
		if (saveTask != null) {
			saveTask.cancel();
		}
		saveTask = null;
		if (!internalDB.exists()) {
			// first check if directory exists, then create the file
			File dir = new File(internalDB.getAbsolutePath().substring(0, internalDB.getAbsolutePath().lastIndexOf(File.separatorChar)));
			dir.mkdirs();
		}

		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(internalDB);
			out = new BufferedWriter(fstream);
			for (Map.Entry<String, Double> a : accounts.entrySet()) {
				out.write(a.getKey() + ", " + a.getValue());
				out.newLine();
			}
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to save Accounts DB", e);
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (Exception e) {
				}
				try {
					fstream.close();
				} catch (Exception e) {
				}
			}
		}
	}

	class Saver extends TimerTask {

		public void start(long wait) {
			(new Timer()).schedule(this, wait);
		}

		@Override
		public void run() {
			saveAccounts();
		}
	}
}
