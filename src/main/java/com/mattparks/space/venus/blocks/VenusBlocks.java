package com.mattparks.space.venus.blocks;

import com.mattparks.space.core.SpaceCore;
import com.mattparks.space.core.blocks.BlockBasics;
import com.mattparks.space.core.blocks.BlockBasics.BasicBlock;
import com.mattparks.space.core.builder.ICoreBlocks;
import com.mattparks.space.venus.VenusCore;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class VenusBlocks implements ICoreBlocks {
	public static BlockBasics venusBasicBlock;
	
	@Override
	public void initBlocks() {
		BasicBlock[] basicBlocks = new BasicBlock[] {
			new BasicBlock() {
				@Override public String getType() { return "venusSurfaceRock"; }
				@Override public float getHardness() { return 1.25f; }
				@Override public float getResistance() { return -1; }
				@Override public String getDamagedDrop() { return null; }
			}, new BasicBlock() {
				@Override public String getType() { return "venusSubRock"; }
				@Override public float getHardness() { return 1.0f; }
				@Override public float getResistance() { return -1; }
				@Override public String getDamagedDrop() { return null; }
			}, new BasicBlock() {
				@Override public String getType() { return "venusRock"; }
				@Override public float getHardness() { return 1.5f; }
				@Override public float getResistance() { return -1; }
				@Override public String getDamagedDrop() { return "venusCobblestone"; }
			}, new BasicBlock() {
				@Override public String getType() { return "venusCobblestone"; }
				@Override public float getHardness() { return 2.5f; }
				@Override public float getResistance() { return -1; }
				@Override public String getDamagedDrop() { return null; }
			}, new BasicBlock() {
				@Override public String getType() { return "venusBrickDungeon"; }
				@Override public float getHardness() { return 25.0f; }
				@Override public float getResistance() { return 40.0f; }
				@Override public String getDamagedDrop() { return null; }
			}, new BasicBlock() {
				@Override public String getType() { return "venusOreSulfur"; }
				@Override public float getHardness() { return 2.5f; }
				@Override public float getResistance() { return -1.0f; }
				@Override public String getDamagedDrop() { return null; }
			}, new BasicBlock() {
				@Override public String getType() { return "venusOreUranium"; }
				@Override public float getHardness() { return 2.5f; }
				@Override public float getResistance() { return -1.0f; }
				@Override public String getDamagedDrop() { return null; }
			}, new BasicBlock() {
				@Override public String getType() { return "venusOreRuby"; }
				@Override public float getHardness() { return 2.5f; }
				@Override public float getResistance() { return -1.0f; }
				@Override public String getDamagedDrop() { return null; }
			}, new BasicBlock() {
				@Override public String getType() { return "venusOreCrystal"; }
				@Override public float getHardness() { return 2.5f; }
				@Override public float getResistance() { return -1.0f; }
				@Override public String getDamagedDrop() { return null; }
			}, new BasicBlock() {
				@Override public String getType() { return "venusOreTin"; }
				@Override public float getHardness() { return 2.5f; }
				@Override public float getResistance() { return -1.0f; }
				@Override public String getDamagedDrop() { return null; }
			}, new BasicBlock() {
				@Override public String getType() { return "venusOreCopper"; }
				@Override public float getHardness() { return 2.5f; }
				@Override public float getResistance() { return -1.0f; }
				@Override public String getDamagedDrop() { return null; }
			}, new BasicBlock() {
				@Override public String getType() { return "venusOreIron"; }
				@Override public float getHardness() { return 2.5f; }
				@Override public float getResistance() { return -1.0f; }
				@Override public String getDamagedDrop() { return null; }
			}, new BasicBlock() {
				@Override public String getType() { return "venusOreCoal"; }
				@Override public float getHardness() { return 2.5f; }
				@Override public float getResistance() { return -1.0f; }
				@Override public String getDamagedDrop() { return null; }
			}
		};
		venusBasicBlock = new BlockBasics("venus_basic", VenusCore.instance.prefixTexture, basicBlocks);
	}
	
	@Override
	public void setHarvestLevels() {
	}

	@Override
	public void registerBlocks() {
		venusBasicBlock.registerBasicBlocks();
	}

	@Override
	public void oreDictRegistration() {
		OreDictionary.registerOre("oreSulfur", new ItemStack(VenusBlocks.venusBasicBlock, 1, venusBasicBlock.getIndex("venusOreSulfur")));
		OreDictionary.registerOre("oreUranium", new ItemStack(VenusBlocks.venusBasicBlock, 1, venusBasicBlock.getIndex("venusOreUranium")));
		OreDictionary.registerOre("oreRuby", new ItemStack(VenusBlocks.venusBasicBlock, 1, venusBasicBlock.getIndex("venusOreRuby")));
		OreDictionary.registerOre("oreCrystal", new ItemStack(VenusBlocks.venusBasicBlock, 1, venusBasicBlock.getIndex("venusOreCrystal")));
		OreDictionary.registerOre("oreTin", new ItemStack(VenusBlocks.venusBasicBlock, 1, venusBasicBlock.getIndex("venusOreTin")));
		OreDictionary.registerOre("oreCopper", new ItemStack(VenusBlocks.venusBasicBlock, 1, venusBasicBlock.getIndex("venusOreCopper")));
		OreDictionary.registerOre("oreIron", new ItemStack(VenusBlocks.venusBasicBlock, 1, venusBasicBlock.getIndex("venusOreIron")));
		OreDictionary.registerOre("oreCoal", new ItemStack(VenusBlocks.venusBasicBlock, 1, venusBasicBlock.getIndex("venusOreCoal")));
	}
}
