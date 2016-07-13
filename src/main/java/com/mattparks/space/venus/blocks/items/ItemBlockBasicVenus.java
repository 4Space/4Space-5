package com.mattparks.space.venus.blocks.items;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import com.mattparks.space.core.proxy.ClientProxy;

public class ItemBlockBasicVenus extends ItemBlock {
	private static final String[] types = new String[] { 
		"surfaceRock", 
		"subSurface", 
		"rock", 
		"cobblestone", 
		"sulfurOre", 
		"uraniumOre", 
		"rubyOre", 
		"crystalOre", 
		"tinOre", 
		"copperOre", 
		"ironOre", 
		"coalOre", 
		"dungeonBrick" 
	};

	public ItemBlockBasicVenus(Block par1) {
		super(par1);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return ClientProxy.RARITY_SPACE_ITEM;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		int meta = itemstack.getItemDamage();

		if (meta < 0 || meta >= ItemBlockBasicVenus.types.length) {
			meta = 0;
		}

		return super.getUnlocalizedName() + "." + ItemBlockBasicVenus.types[meta];
	}
}
