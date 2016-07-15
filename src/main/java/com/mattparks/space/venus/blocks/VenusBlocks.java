package com.mattparks.space.venus.blocks;

import com.mattparks.space.core.blocks.BlockBasics;
import com.mattparks.space.core.blocks.BlockBuilder;
import com.mattparks.space.core.builder.ICoreBlocks;
import com.mattparks.space.venus.VenusCore;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class VenusBlocks implements ICoreBlocks {
	public static BlockBasics venusBlock;
	public static BlockSulfurTorch sulfurTorch;
	public static BlockVenusianTNT venusianTNT;
	public static BlockEvolvedBlazeEgg evolvedBlazeEgg;
	
	@Override
	public void initBlocks() {
		venusBlock = new BlockBasics("venusBlock", VenusCore.instance.prefixTexture, new BlockBuilder[] {
				new BlockBuilder("venusSurfaceRock").setHardness(1.25f).setPlantable(true).setTerraformable(true),
				new BlockBuilder("venusSubRock"),
				new BlockBuilder("venusRock").setHardness(1.5f),
				new BlockBuilder("venusCobblestone").setHardness(2.5f).setSealable(true),
				new BlockBuilder("venusBrickDungeon").setHardness(25.0f).setResistance(40.0f).setSealable(true),
				new BlockBuilder("venusOreSulfur").setHardness(2.5f).setValueable(true),
				new BlockBuilder("venusOreUranium").setHardness(2.5f).setValueable(true),
				new BlockBuilder("venusOreRuby").setHardness(2.5f).setValueable(true),
				new BlockBuilder("venusOrePellucidum").setHardness(2.5f).setValueable(true),
				new BlockBuilder("venusOreTin").setHardness(2.5f).setValueable(true),
				new BlockBuilder("venusOreCopper").setHardness(2.5f).setValueable(true),
				new BlockBuilder("venusOreIron").setHardness(2.5f).setValueable(true),
				new BlockBuilder("venusOreCoal").setHardness(2.5f).setValueable(true),
				new BlockBuilder("venusBlockSulfur").setHardness(4.0f),
				new BlockBuilder("venusBlockUranium").setHardness(4.0f),
				new BlockBuilder("venusBlockRuby").setHardness(4.0f),
				new BlockBuilder("venusBlockPellucidum").setHardness(4.0f),
		});
		sulfurTorch = new BlockSulfurTorch("sulfurTorch");
		venusianTNT = new BlockVenusianTNT("venusianTNT");
		evolvedBlazeEgg = new BlockEvolvedBlazeEgg("evolvedBlazeEgg");
	}
	
	@Override
	public void setHarvestLevels() {
	}

	@Override
	public void registerBlocks() {
		venusBlock.registerBlock();
		sulfurTorch.registerBlock();
		venusianTNT.registerBlock();
		evolvedBlazeEgg.registerBlock();
	}

	@Override
	public void oreDictRegistration() {
		OreDictionary.registerOre("oreSulfur", new ItemStack(VenusBlocks.venusBlock, 1, venusBlock.getIndex("venusOreSulfur")));
		OreDictionary.registerOre("oreUranium", new ItemStack(VenusBlocks.venusBlock, 1, venusBlock.getIndex("venusOreUranium")));
		OreDictionary.registerOre("oreRuby", new ItemStack(VenusBlocks.venusBlock, 1, venusBlock.getIndex("venusOreRuby")));
		OreDictionary.registerOre("oreCrystal", new ItemStack(VenusBlocks.venusBlock, 1, venusBlock.getIndex("venusOreCrystal")));
		OreDictionary.registerOre("oreTin", new ItemStack(VenusBlocks.venusBlock, 1, venusBlock.getIndex("venusOreTin")));
		OreDictionary.registerOre("oreCopper", new ItemStack(VenusBlocks.venusBlock, 1, venusBlock.getIndex("venusOreCopper")));
		OreDictionary.registerOre("oreIron", new ItemStack(VenusBlocks.venusBlock, 1, venusBlock.getIndex("venusOreIron")));
		OreDictionary.registerOre("oreCoal", new ItemStack(VenusBlocks.venusBlock, 1, venusBlock.getIndex("venusOreCoal")));
	}
}
