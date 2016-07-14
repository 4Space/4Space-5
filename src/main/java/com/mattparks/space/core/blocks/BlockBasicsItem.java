package com.mattparks.space.core.blocks;

import java.util.HashMap;
import java.util.Map;

import com.mattparks.space.core.proxy.ClientProxy;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * A class used to give simple blocks names.
 */
public class BlockBasicsItem extends ItemBlock {
	public static Map<Block, String[]> BLOCK_TYPES = new HashMap<Block, String[]>();
	
	public BlockBasicsItem(Block block) {
		super(block);
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
		return super.getUnlocalizedName() + "." + getMetaName(meta);
	}
	
	private String getMetaName(int meta) {
		String[] types = BLOCK_TYPES.get(field_150939_a);
		
		if (types == null || meta < 0 || meta >= types.length) {
			return "unknown";
		}
		
		return types[meta];
	}
}
