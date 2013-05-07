/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: for displaying the spout gui shop interface
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.jascotty2.bukkit.bettershop3.displays.spout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.jascotty2.bukkit.bettershop3.BetterShop3;
import me.jascotty2.bukkit.bettershop3.database.ItemPrice;
import me.jascotty2.bukkit.bettershop3.database.Shop;
import me.jascotty2.bukkit.bettershop3.displays.spout.gui.*;
import me.jascotty2.bukkit.bettershop3.enums.SpoutCategoryMethod;
import me.jascotty2.libv2.bukkit.util.MinecraftChatStr;
import org.getspout.spoutapi.event.screen.TextFieldChangeEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericSlider;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Slider;
import org.getspout.spoutapi.gui.TextField;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutPopupDisplay {

	static Map<SpoutPlayer, SpoutPopupDisplay> popupOpen = new HashMap<SpoutPlayer, SpoutPopupDisplay>();
	protected static int xPad = 15, yPad = 8;
	int height = 160, width = 420, maxRows, maxCols;
	protected static final int MAX_WIDTH = 427, MAX_HEIGHT = 240;
	final BetterShop3 plugin;
	final SpoutPlayer player;
	final Shop shop;
	// gui objects
	GenericButton btnExit = new GenericButton(),
			btnAbout = new GenericButton();
	//GenericContainer items = new GenericContainer();
	GenericSlider itemScroll = new GenericSlider();
	GenericButton btnScrollLeft = new GenericButton(),
			btnScrollRight = new GenericButton();
	GenericLabel lblPageNum = new GenericLabel();
	GenericPopup popup = new GenericPopup();
	List<List<ItemButtonContainer>> menuPages = new ArrayList<List<ItemButtonContainer>>(); //new ArrayList<ItemButtonContainer>();
	List<ItemButtonContainer> menuItems = new ArrayList<ItemButtonContainer>();
	int currentPage = 0;
	int xpos[];
	MarketItemDetail itemDetail;
	boolean aboutActive = false;
	boolean isPaged = true;
	boolean useScroll = false;
	int numPages;
	// gradient :)
	GenericGradient gradient = new GenericGradient();
	// categories
	int catNum = 0;
	String[] categories;
	// cycle
	GenericButton btnCatCycle = null;
	// tabbed
	int tabPage = 0, tabSize = 60, tabXpad = 5, tabPageSize = (MAX_WIDTH - 20) / (tabSize + tabXpad);
	GenericButton btnTabLeft, btnTabRight;
	List<GenericButton> tabButtons = new ArrayList<GenericButton>();

	public SpoutPopupDisplay(BetterShop3 plugin, SpoutPlayer p, Shop shop) {
		this.plugin = plugin;
		this.shop = shop;
		this.player = p;
		useScroll = plugin.config.spout_useScroll;
		isPaged = plugin.config.spout_usePages;
		
		itemDetail = new MarketItemDetail(plugin, shop, p);
	} // end default constructor

	public static void popup(BetterShop3 plugin, SpoutPlayer p, Shop shop) {
		if (popupOpen.containsKey(p)) {
			closePopup(p);
			return;
		}
		shop.checkRestock();
		SpoutPopupDisplay d = new SpoutPopupDisplay(plugin, p, shop);
		//System.out.println(p.getMainScreen().getHeight() + " x " + p.getMainScreen().getWidth());
		popupOpen.put(p, d);
		d.show();
	}

	public static void closePopup(SpoutPlayer p) {
		if (popupOpen.containsKey(p)) {
			SpoutPopupDisplay d = popupOpen.get(p);
			if (d.aboutActive) {
				d.popup.removeWidgets(d.plugin);
				d.show();
				d.aboutActive = false;
			} else {
				p.getMainScreen().closePopup();
				d.close();
				popupOpen.remove(p);
			}
		}
	}

	public static SpoutPopupDisplay getPopup(SpoutPlayer p) {
		return popupOpen.get(p);
	}

	public void close() {
		btnExit = null;
	}

	public void show() {

		InGameHUD hudscreen = player.getMainScreen();

		popup.setVisible(true);

		//Exit Button
		btnExit.setText("EXIT").setWidth(45).setHeight(15).setX(378).setY(222);
		popup.attachWidget(plugin, btnExit);

		//"About" :)
		btnAbout.setText("?").setWidth(12).setHeight(12).setX(MAX_WIDTH - 4).setY(MAX_HEIGHT - 4);
		popup.attachWidget(plugin, btnAbout);

		Set<String> cats = plugin.config.categories.keySet();
		categories = new String[cats.size() + 1];
		categories[0] = "All";
		int i = 1;
		for(String c : cats) {
			categories[i++] = c;
		}

		if (plugin.config.spout_categories == SpoutCategoryMethod.CYCLE) {
			btnCatCycle = new GenericButton();
			btnCatCycle.setTooltip("Category");
			btnCatCycle.setWidth(50).setHeight(15).setX(374).setY(205);
			popup.attachWidget(plugin, btnCatCycle);
		} else if (plugin.config.spout_categories == SpoutCategoryMethod.TABBED) {
			btnTabLeft = new GenericButton();
			btnTabRight = new GenericButton();
			btnTabLeft.setText("<").setHeight(10).setWidth(10).setY(4).setX(2);
			btnTabLeft.setEnabled(false).setVisible(false);
			btnTabRight.setText(">").setHeight(10).setWidth(10).setY(4).setX(MAX_WIDTH - 12);
			btnTabRight.setEnabled(false).setVisible(false);

			popup.attachWidget(plugin, btnTabLeft);
			popup.attachWidget(plugin, btnTabRight);

			int x = 20;
			boolean vis = true;
			for (String c : categories) {
				GenericButton tab = new GenericButton(c);
				tab.setHeight(10).setWidth(tabSize).setY(4).setX(x);
				tab.setEnabled(vis).setVisible(vis);
				x += tabSize + 5;
				if (x + tabSize >= MAX_WIDTH - 15) {
					x = 20;
					vis = false;
					btnTabRight.setEnabled(true).setVisible(true);
				}
				tabButtons.add(tab);
				popup.attachWidget(plugin, tab);
			}
			height -= 15;
		}

		showCategory(0);

		btnScrollLeft.setText("<").setHeight(10).setWidth(10).setY(height).
				setX(useScroll ? 2 : MAX_WIDTH - 90);
		btnScrollRight.setText(">").setHeight(10).setWidth(10).setY(height).setX(MAX_WIDTH - 12);

		lblPageNum.setHeight(7).setWidth(40).setY(height + 11).setX(MAX_WIDTH - 75);
		lblPageNum.setTextColor(new Color(.65F, .65F, .65F));

		itemDetail.setX(2).setY(MAX_HEIGHT - itemDetail.getHeight()); //height + 15);

		gradient.setPriority(RenderPriority.Highest);
		gradient.setTopColor(new Color(0.5F, 0.5F, 0.5F, 0.4F)).
				setBottomColor(new Color(0.2F, 0.2F, 0.2F, 0.4F));
		gradient.setX(xPad / 2).setY(yPad / 2).setWidth(MAX_WIDTH - xPad).setHeight(height - yPad);

		if (useScroll) {
			// GenericSlider is not vertical :(
			//itemScroll.setHeight(350).setWidth(5).setX(100);
			itemScroll.setHeight(8).setWidth(width - 20);
			itemScroll.setY(height).setX((MAX_WIDTH - (width - 20)) / 2);
			itemScroll.setSliderPosition(0);
			popup.attachWidget(plugin, itemScroll);
		}
		popup.attachWidget(plugin, btnScrollLeft);
		popup.attachWidget(plugin, btnScrollRight);
		popup.attachWidget(plugin, lblPageNum);
		popup.attachWidget(plugin, itemDetail);
		popup.attachWidget(plugin, gradient);

		hudscreen.attachPopupScreen(popup);
		hudscreen.updateWidget(popup);
		hudscreen.setDirty(true);
	}

	protected void showAbout() {
		aboutActive = true;
		//player.getMainScreen().closePopup();

		popup.removeWidgets(plugin);

		popup.attachWidget(plugin, btnExit);

		GenericLabel lblTitle = new GenericLabel();
		lblTitle.setText(MinecraftChatStr.padCenter("About", ' ', 45)).setX((MAX_WIDTH - 45) / 2).
				setY(12).setWidth(45).setHeight(10);
		popup.attachWidget(plugin, lblTitle);

		GenericLabel lblAbout = new GenericLabel();
		lblAbout.setX((MAX_WIDTH - 200) / 2).setY((MAX_HEIGHT - 100) / 2).setWidth(200).setHeight(100);
		String about = "BetterShop " + plugin.getDescription().getVersion() + "\n"
				+ "Coding by Jacob Scott (jascotty2) \n"
				+ "https://github.com/jascotty2/BetterShop \n\n\n\n"
				+ "Help Support Development! \n"
				+ "send a small PayPal donation to \n"
				+ "    jascottytechie@gmail.com";
		String lines[] = about.split("\n");
		StringBuilder txt = new StringBuilder();
		for (int i = 0; i < lines.length;) {
			txt.append(MinecraftChatStr.padCenter(lines[i], ' ', 200));
			if (++i < lines.length) {
				txt.append("\n");
			}
		}
		lblAbout.setText(txt.toString());

		popup.attachWidget(plugin, lblAbout);


		GenericGradient gradTitle = new GenericGradient();

		gradTitle.setPriority(RenderPriority.Highest);
		gradTitle.setTopColor(new Color(0.4F, 0.4F, 0.4F, 0.6F)).
				setBottomColor(new Color(0.2F, 0.2F, 0.2F, 0.4F));
		gradTitle.setX(lblTitle.getX() - xPad).
				setY(lblTitle.getY() - yPad).
				setWidth(lblTitle.getWidth() + xPad * 2).
				setHeight(lblTitle.getHeight() + yPad * 2);

		popup.attachWidget(plugin, gradTitle);


		GenericGradient gradText = new GenericGradient();

		gradText.setPriority(RenderPriority.Highest);
		gradText.setTopColor(new Color(0.4F, 0.4F, 0.4F, 0.6F)).
				setBottomColor(new Color(0.2F, 0.2F, 0.2F, 0.4F));
		gradText.setX(lblAbout.getX() - xPad).
				setY(lblAbout.getY() - yPad).
				setWidth(lblAbout.getWidth() + xPad * 2).
				setHeight(lblAbout.getHeight() + yPad * 2);

		popup.attachWidget(plugin, gradText);


		InGameHUD hudscreen = player.getMainScreen();
		hudscreen.attachPopupScreen(popup);
		hudscreen.updateWidget(popup);
		hudscreen.setDirty(true);
	}

	protected void showCategory(int catNum) {
		while (catNum >= categories.length) {
			catNum -= categories.length;
		}
		if (catNum < 0) {
			catNum = 0;
		}

		this.catNum = catNum;
		String cat = categories[catNum];
		if (btnCatCycle != null) {
			btnCatCycle.setText(cat).setDirty(true);
		} else if (plugin.config.spout_categories == SpoutCategoryMethod.TABBED) {
			for (int c = 0; c < tabButtons.size(); ++c) {
				tabButtons.get(c).setTextColor(
						c == catNum ? new Color(.95F, .95F, .95F)//.95F, .35F, .35F)//(float) 33 / 255, (float) 25 / 255, (float) 210 / 255)
						: new Color(.7F, .7F, .7F)).setDirty(true);
			}
		}

		clearDisplay();
		
//		ArrayList<ItemPrice> items = plugin.shopManager.getRegionShop(player.getLocation())
//
//		try {
//			PriceListItem allitems[] = BetterShop.getPricelist(player.getLocation()).getPricelistItems(
//					BSPermissions.hasPermission(player, BetterShopPermission.ADMIN_ILLEGAL));
//			if (catNum == 0) {
//				items.addAll(Arrays.asList(allitems));
//			} else {
//				for (PriceListItem p : allitems) {
//					if (p.HasCategory(cat)) {
//						items.add(p);
//					}
//				}
//				if (catNum > 0 && BetterShop.getSettings().spoutCatCustomSort) {
//					final List<JItem> catItems = JItemDB.getCategory(cat);
//					if (catItems != null) {
//						Collections.sort(items, new Comparator<PriceListItem>() {
//
//							public int compare(PriceListItem o1, PriceListItem o2) {
//								return catItems.indexOf(o1) - catItems.indexOf(o2);
//							}
//						});
//					}
//				}
//			}
//		} catch (Exception e) {
//			popup.attachWidget(plugin,
//					new GenericLabel().setText("Error Loading Pricelist! \n  "
//					+ e.getMessage()).setTextColor(
//					new Color(240 / (float) 255,
//					45 / (float) 255,
//					45 / (float) 255)).setX(150).setY(100)).setPriority(RenderPriority.Lowest);
//			return;
//		}

		try {

			int x, y, ix, iy, dx = xPad, dy = yPad;
			boolean vis = true,
					lg = plugin.config.spout_largeMenu;
			if (lg) {
				dx += LargeMarketMenuItem.DEF_WIDTH;
				dy += LargeMarketMenuItem.DEF_HEIGHT;
			} else {
				dx += SmallMarketMenuItem.DEF_WIDTH;
				dy += SmallMarketMenuItem.DEF_HEIGHT;
			}
			maxCols = MAX_WIDTH / dx;
			x = (MAX_WIDTH - (maxCols * dx) + xPad) / 2;
			ix = x;

			maxRows = height / dy;
			y = (height - (maxRows * dy) + yPad) / 2;
			if (plugin.config.spout_categories == SpoutCategoryMethod.TABBED) {
				y += 15;
			}
			iy = y;

			xpos = new int[maxCols];
			int col = 0;

			List<ItemButtonContainer> page = new ArrayList<ItemButtonContainer>();
			
//			for (PriceListItem p : items) {
//				ItemButtonContainer m;
//				if (lg) {
//					m = new LargeMarketMenuItem(p.ID(), (byte) p.Data());
//				} else {
//					m = new SmallMarketMenuItem(p.ID(), (byte) p.Data());
//				}
//				m.setEnabled(/*vis*/false).setY(y).setX(x);//.setWidth(wid).setHeight(hgt);
//				if (vis) {
//					delayShowList.add(m);
//				}
//				menuItems.add(m);
//				page.add(m);
//				//items.addChild(m);
//				popup.attachWidget(plugin, (GenericContainer) m);
//
//				y += dy;
//				if (y + dy >= height) {
//					if (vis) {
//						xpos[col] = x;
//					}
//					y = iy;
//					if (x + dx >= width) {
//						x = ix;
//						vis = false;
//						menuPages.add(page);
//						page = new ArrayList<ItemButtonContainer>();
//					} else {
//						x += dx;
//						++col;
//					}
//				}
//			}
			if (page.size() > 0) {
				menuPages.add(page);
			}

			numPages = isPaged ? menuPages.size()
					: (int) Math.ceil(((float) menuItems.size() / maxRows) - (maxCols - 1));

			lblPageNum.setText("Page 1 of " + numPages).setDirty(true);

			btnScrollLeft.setEnabled(false).setVisible(false).setDirty(true);
			btnScrollRight.setEnabled(numPages > 1).setVisible(numPages > 1).setDirty(true);

			startShow();
		} catch (Exception e) {
			popup.attachWidget(plugin,
					new GenericLabel().setText("Error Displaying Itemlist! \n  "
					+ e.getMessage()).setTextColor(
					new Color(240 / (float) 255,
					45 / (float) 255,
					45 / (float) 255)).setX(150).setY(100)).setPriority(RenderPriority.Lowest);
		}

	}
	/*   temp fix for a new spout problem .. */
	ArrayList<ItemButtonContainer> delayShowList = new ArrayList<ItemButtonContainer>();
	java.util.Timer showDelay;

	private void startShow() {
		showDelay = new java.util.Timer();
		showDelay.schedule(new java.util.TimerTask() {

			@Override
			public void run() {
				for (ItemButtonContainer m : delayShowList) {
					m.setEnabled(true);
				}
				delayShowList.clear();
			}
		}, 100);
	}

	protected void clearDisplay() {
		for (ItemButtonContainer m : menuItems) {
			popup.removeWidget(m);
		}
		menuItems.clear();
		for (List<ItemButtonContainer> mp : menuPages) {
			mp.clear();
		}
		menuPages.clear();
		numPages = 0;

//		itemScroll.setSliderPosition(0);

		itemDetail.setVisible(false);
	}

	protected synchronized void showPage(int page) {
		if (page < 0 || page > numPages) {
			throw new IllegalArgumentException("Illegal page number: " + page);
		}
		if (isPaged) {
			if (currentPage < menuPages.size()) {
				for (ItemButtonContainer p : menuPages.get(currentPage)) {
					p.setEnabled(false);
				}
			}
			if (page < menuPages.size()) {
				for (ItemButtonContainer p : menuPages.get(page)) {
					p.setEnabled(true);
				}
			}
		} else {
			for (int i = 0; i < menuItems.size(); ++i) {
				ItemButtonContainer m = menuItems.get(i);
				int p = i / maxRows;
				if (p >= page && p < page + maxCols) {
					m.setX(xpos[p - page]).setDirty(true);
					m.setEnabled(true);
				} else if (m.isVisible()) {
					m.setEnabled(false);
				}
			}
		}

		currentPage = page;
		lblPageNum.setText("Page " + (currentPage + 1) + " of " + numPages);
		lblPageNum.setDirty(true);

		if (currentPage == 0) {
			if (btnScrollLeft.isVisible()) {
				btnScrollLeft.setEnabled(false).setVisible(false).setDirty(true);
			}
		} else if (!btnScrollLeft.isVisible()) {
			btnScrollLeft.setEnabled(true).setVisible(true).setDirty(true);
		}
		if (currentPage + 1 >= numPages) {//((menuItems.size() / maxRows) - (maxCols - 1))) {
			if (btnScrollRight.isVisible()) {
				btnScrollRight.setEnabled(false).setVisible(false).setDirty(true);
			}
		} else if (!btnScrollRight.isVisible()) {
			btnScrollRight.setEnabled(true).setVisible(true).setDirty(true);
		}
	}

	protected synchronized void showCatPage(int page) {
		int pageStart = page * tabPageSize;
		if (pageStart > tabButtons.size()) {
			throw new IllegalArgumentException("Illegal tab page number: " + page);
		}
		int p = 0;
		for (GenericButton b : tabButtons) {
			if (p >= pageStart && p < pageStart + tabPageSize) {
				b.setEnabled(true).setVisible(true).setDirty(true);
			} else if (b.isVisible()) {
				b.setEnabled(false).setVisible(false).setDirty(true);
			}
			++p;
		}
		if (page * tabPageSize >= tabButtons.size()) {
			btnTabRight.setEnabled(false).setVisible(false).setDirty(true);
		} else if (!btnTabRight.isVisible()) {
			btnTabRight.setEnabled(true).setVisible(true).setDirty(true);
		}

		if (page <= 0) {
			btnTabLeft.setEnabled(false).setVisible(false).setDirty(true);
		} else {
			btnTabLeft.setEnabled(true).setVisible(true).setDirty(true);
		}
		tabPage = page;
	}

	public void buttonPress(Button btn) {
		if (btn == btnExit) {
			if (aboutActive) {
				popup.removeWidgets(plugin);
				show();
				aboutActive = false;
			} else {
				closePopup(player);
			}
		} else if (btn == btnAbout) {
			showAbout();
		} else if (btn == btnScrollLeft) {
			if (useScroll) {
				itemScroll.setSliderPosition((float) (currentPage - 1) / numPages).setDirty(true);
				sliderChanged(itemScroll);
			} else {
				showPage(currentPage - 1);
			}
		} else if (btn == btnScrollRight) {
			if (useScroll) {
				itemScroll.setSliderPosition((float) (currentPage + 1.2) / numPages).setDirty(true);
				sliderChanged(itemScroll);
			} else {
				showPage(currentPage + 1);
			}
		} else if (btn == itemDetail.btnUp) {
			itemDetail.buttonUpPressed(1);
		} else if (btn == itemDetail.btnDown) {
			itemDetail.buttonDownPressed(1);
		} else if (btn == itemDetail.btnUp5) {
			itemDetail.buttonUpPressed(5);
		} else if (btn == itemDetail.btnDown5) {
			itemDetail.buttonDownPressed(5);
		} else if (btn == itemDetail.btnUp20) {
			itemDetail.buttonUpPressed(20);
		} else if (btn == itemDetail.btnDown20) {
			itemDetail.buttonDownPressed(20);
		} else if (btn == itemDetail.btnBuy) {
			if (itemDetail.buyAmt() > 0) {
				player.performCommand("shopbuy " + itemDetail.itemIDD() + " " + itemDetail.buyAmt());
//				plugin.onCommand(player,
//						new PluginCommand("shopbuy", plugin), "shopbuy", new String[]{itemDetail.itemIDD(), String.valueOf(itemDetail.buyAmt())});
				closePopup(player);
			}
		} else if (btn == itemDetail.btnSell) {
			if (itemDetail.sellAmt() > 0) {
				player.performCommand("shopsell " + itemDetail.itemIDD() + " " + itemDetail.sellAmt());
				closePopup(player);
			}
		} else if (btn == btnCatCycle) {
			showCategory(catNum + 1);
		} else if (btn == btnTabLeft) {
			showCatPage(tabPage - 1);
		} else if (btn == btnTabRight) {
			showCatPage(tabPage + 1);
		} else {
			int cat = 0;
			for (GenericButton b : tabButtons) {
				if (btn == b) {
					showCategory(cat);
					return;
				}
				++cat;
			}
			for (ItemButtonContainer m : menuItems) {
				if (btn == m.getButton()) {
					itemDetail.updateItem(m.getID(), m.getData());
					return;
				}
			}
		}
	}

	public void sliderChanged(Slider scrollbar) {
		if (scrollbar == itemScroll) {
			itemScroll.savePos();
			//int page = (int) Math.ceil((((float) menuItems.size() / maxRows) - (maxCols - 1)) * scrollbar.getSliderPosition()) - 1;
			int page = (int) Math.ceil(numPages * scrollbar.getSliderPosition()) - 1;
			if (page < 0) {
				page = 0;
			}
			if (currentPage != page) {
				showPage(page);
			}
		}
	}

	public void textChanged(TextFieldChangeEvent event) {
		TextField t = event.getTextField();
		if (t == itemDetail.txtAmt) {
			itemDetail.amtChanged(event.getOldText(), event.getNewText());
		}
	}

	public SpoutPlayer getPlayer() {
		return player;
	}
} // end class SpoutPopupDisplay

