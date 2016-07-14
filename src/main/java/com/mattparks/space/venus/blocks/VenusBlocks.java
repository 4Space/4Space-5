package com.mattparks.space.venus.blocks;

import com.mattparks.space.core.SpaceCore;
import com.mattparks.space.core.builder.ICoreBlocks;
import com.mattparks.space.venus.blocks.items.ItemBlockBasicVenus;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class VenusBlocks implements ICoreBlocks {
	public static Block venusBasicBlock;
	
	@Override
	public void initBlocks() {
		venusBasicBlock = new BlockBasicVenus("venus_basic");
	}
	
	@Override
	public void setHarvestLevels() {
	}

	@Override
	public void registerBlocks() {
		SpaceCore.registerBlock(VenusBlocks.venusBasicBlock, ItemBlockBasicVenus.class, true);
	}

	@Override
	public void oreDictRegistration() {
		OreDictionary.registerOre("oreSulfur", new ItemStack(VenusBlocks.venusBasicBlock, 1, 4));
		OreDictionary.registerOre("oreUranium", new ItemStack(VenusBlocks.venusBasicBlock, 1, 5));
		OreDictionary.registerOre("oreRuby", new ItemStack(VenusBlocks.venusBasicBlock, 1, 6));
		OreDictionary.registerOre("oreCrystal", new ItemStack(VenusBlocks.venusBasicBlock, 1, 7));
		OreDictionary.registerOre("oreTin", new ItemStack(VenusBlocks.venusBasicBlock, 1, 8));
		OreDictionary.registerOre("oreCopper", new ItemStack(VenusBlocks.venusBasicBlock, 1, 9));
		OreDictionary.registerOre("oreIron", new ItemStack(VenusBlocks.venusBasicBlock, 1, 10));
		OreDictionary.registerOre("oreCoal", new ItemStack(VenusBlocks.venusBasicBlock, 1, 11));
	}
}
