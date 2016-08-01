package mattparks.mods.space.mercury.blocks;

import mattparks.mods.space.core.blocks.BlockBasics;
import mattparks.mods.space.core.blocks.BlockBuilder;
import mattparks.mods.space.core.builder.ICoreBlocks;
import mattparks.mods.space.mercury.MercuryCore;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class MercuryBlocks implements ICoreBlocks {
	public static BlockBasics mercuryBlock;
	public static BlockBasics decorBlock;
	public static BlockCaravanModule caravanModule;
	public static BlockCaravanDummy caravanDummy;

	@Override
	public void initBlocks() {
		mercuryBlock = new BlockBasics("mercuryBlock", MercuryCore.instance.prefixTexture, new BlockBuilder[] {
			new BlockBuilder("mercurySurfaceRock").setHardness(1.25f).setPlantable(true).setTerraformable(true),
			new BlockBuilder("mercurySubRock").setHardness(1.0f),
			new BlockBuilder("mercuryRock").setHardness(1.5f),
			new BlockBuilder("mercuryCobblestone").setHardness(2.5f).setSealable(true),
			new BlockBuilder("mercuryBrickDungeon").setHardness(25.0f).setResistance(40.0f).setSealable(true),
			new BlockBuilder("mercuryOreIridium").setHardness(2.5f).setValueable(true),
			new BlockBuilder("mercuryOreMeteor").setHardness(2.5f).setValueable(true),
			new BlockBuilder("mercuryOreTin").setHardness(2.5f).setValueable(true),
			new BlockBuilder("mercuryOreCopper").setHardness(2.5f).setValueable(true),
			new BlockBuilder("mercuryOreGold").setHardness(2.5f).setValueable(true),
		});
		decorBlock = new BlockBasics("mercuryDecorBlock", MercuryCore.instance.prefixTexture, new BlockBuilder[] {
			new BlockBuilder("mercuryBlockIridium").setHardness(4.0f),
			new BlockBuilder("mercuryBlockMeteor").setHardness(4.0f),
		});
		caravanModule = new BlockCaravanModule("mercuryCaravanModule");
		caravanDummy = new BlockCaravanDummy("mercuryCaravanDummy");
	}

	@Override
	public void setHarvestLevels() {
	}

	@Override
	public void registerBlocks() {
		mercuryBlock.registerBlock();
		decorBlock.registerBlock();
		caravanModule.registerBlock();
		caravanDummy.registerBlock();
	}

	@Override
	public void oreDictRegistration() {
		OreDictionary.registerOre("oreIridium", new ItemStack(MercuryBlocks.mercuryBlock, 1, mercuryBlock.getIndex("mercuryOreIridium")));
		OreDictionary.registerOre("oreMeteor", new ItemStack(MercuryBlocks.mercuryBlock, 1, mercuryBlock.getIndex("mercuryOreMeteor")));
		OreDictionary.registerOre("oreTin", new ItemStack(MercuryBlocks.mercuryBlock, 1, mercuryBlock.getIndex("mercuryOreTin")));
		OreDictionary.registerOre("oreCopper", new ItemStack(MercuryBlocks.mercuryBlock, 1, mercuryBlock.getIndex("mercuryOreCopper")));
		OreDictionary.registerOre("oreGold", new ItemStack(MercuryBlocks.mercuryBlock, 1, mercuryBlock.getIndex("mercuryOreGold")));
	}
}
