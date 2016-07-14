package com.mattparks.space.core.utils;

import com.mattparks.space.core.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A class that can be used to create one off blocks.
 */
public class ItemBlockUtil extends ItemBlock {
	public ItemBlockUtil(Block block) {
		super(block);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return ClientProxy.RARITY_SPACE_ITEM;
	}
}
