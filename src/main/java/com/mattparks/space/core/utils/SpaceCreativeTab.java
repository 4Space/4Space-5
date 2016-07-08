package com.mattparks.space.core.utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * A class for making simple creative tabs.
 */
public class SpaceCreativeTab extends CreativeTabs {
	private final Item itemForTab;
	private final int metaForTab;

	/**
	 * Creates a new simple creative tab.
	 * 
	 * @param id The ID for the tab.
	 * @param name The name for the tab.
	 * @param itemForTab The item to use for the tab.
	 * @param metaForTab The items meta.
	 */
	public SpaceCreativeTab(int id, String name, Item itemForTab, int metaForTab) {
		super(id, name);
		this.itemForTab = itemForTab;
		this.metaForTab = metaForTab;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return this.itemForTab;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_151243_f() {
		return this.metaForTab;
	}
}
