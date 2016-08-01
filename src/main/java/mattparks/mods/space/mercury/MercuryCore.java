package mattparks.mods.space.mercury;

import java.util.ArrayList;
import java.util.List;

import codechicken.nei.api.API;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mattparks.mods.space.core.builder.ICoreBlocks;
import mattparks.mods.space.core.builder.ICoreItems;
import mattparks.mods.space.core.builder.celestials.ICorePlanet;
import mattparks.mods.space.core.teleport.TeleportTypeLander;
import mattparks.mods.space.core.utils.SpacePair;
import mattparks.mods.space.core.world.gen.GenBiomeDecorator;
import mattparks.mods.space.core.world.gen.GenBiomeDecorator.GenerateOre;
import mattparks.mods.space.core.world.gen.GenBiomeDecorator.GenerateStructure;
import mattparks.mods.space.core.world.gen.GenerationSettings;
import mattparks.mods.space.mercury.blocks.MercuryBlocks;
import mattparks.mods.space.mercury.items.MercuryItems;
import mattparks.mods.space.venus.items.VenusItems;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.RecipeUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

/**
 * A implementation of a 4Space planet, for the planet Mercury.
 */
public class MercuryCore extends ICorePlanet {
	public static MercuryCore instance;
	
	private int dimensionID;
	private int biomeID;
	private boolean planetEnabled;
	
	public MercuryCore(FMLPreInitializationEvent event) {
		super(event,  "spacemercury", "textures/gui/mercuryRocketGui.png", new MercuryWorldProvider(), new TeleportTypeLander());
		instance = this;
	}

	@Override
	public Planet getNewPlanet() {
		if (!planetEnabled) {
			return null;
		}
		
		Planet mercury = new Planet("mercury");
		mercury.setParentSolarSystem(GalacticraftCore.solarSystemSol);
		mercury.setRingColorRGB(0.1F, 0.9F, 0.6F);
		mercury.setPhaseShift(2.0F);
		mercury.setRelativeSize(0.5319F);
		mercury.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(0.5F, 0.5F));
		mercury.setRelativeOrbitTime(0.24096385542168674698795180722892F);
		mercury.setBodyIcon(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/mercury.png"));
		mercury.setTierRequired(2);
		mercury.setDimensionInfo(dimensionID, worldProvider.getClass());
		mercury.atmosphereComponent(IAtmosphericGas.CO2).atmosphereComponent(IAtmosphericGas.HELIUM).atmosphereComponent(IAtmosphericGas.ARGON);
		return mercury;
	}

	@Override
	public int getDimensionID() {
		return dimensionID;
	}

	@Override
	public int getBiomeID() {
		return biomeID;
	}

	@Override
	public String getMusicJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRenderHandler createSkyProvider(IGalacticraftWorldProvider world) {
		return new MercurySkyProvider(world);
	}

	@Override
	public boolean instanceOfProvider(WorldProvider provider) {
		return provider instanceof MercuryWorldProvider;
	}

	@Override
	public GenerationSettings getGenerationSettings() {
		String planetName = "Mercury";
		double terrainHeightMod = 11.0;
		double smallFeatureHeightMod = 44.0;
		double mountainHeightMod = 111.0;
		double valleyHeightMod = 55.0;
		int craterProbibility = 333;
		int caveChance = 25;
		
		SpacePair<Block, Integer> blockTop = new SpacePair<Block, Integer>(MercuryBlocks.mercuryBlock, MercuryBlocks.mercuryBlock.getIndex("mercurySurfaceRock"));
		SpacePair<Block, Integer> blockFiller = new SpacePair<Block, Integer>(MercuryBlocks.mercuryBlock, MercuryBlocks.mercuryBlock.getIndex("mercurySubRock"));
		SpacePair<Block, Integer> blockLower = new SpacePair<Block, Integer>(MercuryBlocks.mercuryBlock, MercuryBlocks.mercuryBlock.getIndex("mercuryRock"));
		SpacePair<Block, Integer> blockBrick = new SpacePair<Block, Integer>(MercuryBlocks.mercuryBlock, MercuryBlocks.mercuryBlock.getIndex("mercuryBrickDungeon"));
		Block blockEgg = null;
		
		boolean dungeonEnabled = false;
		boolean pitEnabled = false;
		boolean villageEnabled = false;
		
		return new GenerationSettings(planetName, terrainHeightMod, smallFeatureHeightMod, mountainHeightMod, valleyHeightMod, craterProbibility, caveChance, blockTop, blockFiller, blockLower, blockBrick, blockEgg, dungeonEnabled, pitEnabled, villageEnabled, getSpawnableMonsters());
	}

	@Override
	public GenBiomeDecorator getGenBiomeDecorator() {
		List<GenerateOre> oreList = new ArrayList<GenerateOre>();
		SpacePair<Block, Integer> stoneBlock = new SpacePair<Block, Integer>(MercuryBlocks.mercuryBlock, MercuryBlocks.mercuryBlock.getIndex("mercuryRock"));

		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(MercuryBlocks.mercuryBlock, MercuryBlocks.mercuryBlock.getIndex("mercurySubRock")), stoneBlock, 32, 32, 0, 256));
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(MercuryBlocks.mercuryBlock, MercuryBlocks.mercuryBlock.getIndex("mercuryOreIridium")), stoneBlock, 12, 12, 0, 64));
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(MercuryBlocks.mercuryBlock, MercuryBlocks.mercuryBlock.getIndex("mercuryOreMeteor")), stoneBlock, 2, 8, 0, 16));
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(MercuryBlocks.mercuryBlock, MercuryBlocks.mercuryBlock.getIndex("mercuryOreTin")), stoneBlock, 4, 16, 0, 48));
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(MercuryBlocks.mercuryBlock, MercuryBlocks.mercuryBlock.getIndex("mercuryOreCopper")), stoneBlock, 5, 16, 0, 48));
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(MercuryBlocks.mercuryBlock, MercuryBlocks.mercuryBlock.getIndex("mercuryOreGold")), stoneBlock, 7, 16, 0, 64));
		
		List<GenerateStructure> structureList = new ArrayList<GenerateStructure>();
		
		return new GenBiomeDecorator(oreList, structureList);
	}

	@Override
	public List<SpawnListEntry> getSpawnableMonsters() {
		List<SpawnListEntry> spawnableMonsters = new ArrayList<SpawnListEntry>();
		spawnableMonsters.add(new SpawnListEntry(EntityEvolvedZombie.class, 5, 1, 1));
		spawnableMonsters.add(new SpawnListEntry(EntityEvolvedSpider.class, 3, 1, 1));
		spawnableMonsters.add(new SpawnListEntry(EntityEvolvedCreeper.class, 2, 1, 1));
		return spawnableMonsters;
	}

	@Override
	public void loadFromConfig() {
		dimensionID = configuration.get(Configuration.CATEGORY_GENERAL, "Mercury Dimension", -40).getInt(-40);
		biomeID = configuration.get(Configuration.CATEGORY_GENERAL, "Mercury Biome", 210).getInt(210);
		planetEnabled = configuration.get(Configuration.CATEGORY_GENERAL, "Mercury Enabled", true).getBoolean(true);
		
	}

	@Override
	public ICoreBlocks getBlocks() {
		return new MercuryBlocks();
	}

	@Override
	public ICoreItems getItems() {
		return new MercuryItems();
	}

	@Override
	public void hideNEI() {
		API.hideItem(new ItemStack(MercuryBlocks.caravanDummy, 1, 0));
	}

	@Override
	public void addShapelessRecipes() {
		// ORE DICTIONARY
		OreDictionary.registerOre("iridiumCrystals", new ItemStack(MercuryItems.mercuryItem, 1, MercuryItems.mercuryItem.getIndex("iridiumCrystals")));
		OreDictionary.registerOre("iridiumPlate", new ItemStack(MercuryItems.mercuryItem, 1, MercuryItems.mercuryItem.getIndex("iridiumPlate")));
		OreDictionary.registerOre("mercuryStone", new ItemStack(MercuryBlocks.mercuryBlock, 1, MercuryBlocks.mercuryBlock.getIndex("mercuryRock")));
		OreDictionary.registerOre("iridiumDecor", new ItemStack(MercuryBlocks.decorBlock, MercuryBlocks.decorBlock.getIndex("mercuryBlockIridium")));
		OreDictionary.registerOre("meteorDecor", new ItemStack(MercuryBlocks.decorBlock, MercuryBlocks.decorBlock.getIndex("mercuryBlockMeteor")));
		OreDictionary.registerOre("meteoricIronIngot", new ItemStack(GCItems.meteoricIronIngot, 1, 0));

		// ARMOR AND TOOLS CRAFTING
		RecipeUtil.addRecipe(new ItemStack(MercuryItems.armourIridium.boots), new Object[] { "X X", "X X", 'X', "iridiumCrystals" });
		RecipeUtil.addRecipe(new ItemStack(MercuryItems.armourIridium.helmet), new Object[] { "XXX", "X X", 'X', "iridiumCrystals" });
		RecipeUtil.addRecipe(new ItemStack(MercuryItems.armourIridium.chestplate), new Object[] { "X X", "XXX", "XXX", 'X', "iridiumCrystals" });
		RecipeUtil.addRecipe(new ItemStack(MercuryItems.armourIridium.leggings), new Object[] { "XXX", "X X", "X X", 'X', "iridiumCrystals" });

		RecipeUtil.addRecipe(new ItemStack(MercuryItems.toolsIridium.sword), new Object[] { "X", "X", "Y", 'X', "iridiumCrystals", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(MercuryItems.toolsIridium.pickaxe), new Object[] { "XXX", " Y ", " Y ", 'X', "iridiumCrystals", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(MercuryItems.toolsIridium.spade), new Object[] { "X", "Y", "Y", 'X', "iridiumCrystals", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(MercuryItems.toolsIridium.hoe), new Object[] { "XX", " Y", " Y", 'X', "iridiumCrystals", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(MercuryItems.toolsIridium.axe), new Object[] { "XX", "XY", " Y", 'X', "iridiumCrystals", 'Y', Items.stick });

		//for (int var2 = 0; var2 < 16; ++var2) {
		//	CraftingManager.getInstance().addShapelessRecipe(new ItemStack(MercuryBlocks.mercuryGlowstone, 1, 15 - var2), new Object[] { new ItemStack(Items.dye, 1, var2), Blocks.glowstone });
		//}

		// ITEM TO BLOCK
		RecipeUtil.addRecipe(new ItemStack(MercuryBlocks.caravanModule), new Object[] { "XYZ", "GJG", "GGG", 'X', Blocks.furnace, 'Y', Blocks.crafting_table, 'Z', Blocks.chest, 'G', GCItems.canvas, 'J', new ItemStack(GCItems.canister, 1, 0) });
		RecipeUtil.addRecipe(new ItemStack(MercuryBlocks.decorBlock, 1, MercuryBlocks.decorBlock.getIndex("mercuryBlockIridium")), new Object[] { "XXX", "XXX", "XXX", 'X', "iridiumCrystals" });
		RecipeUtil.addRecipe(new ItemStack(MercuryBlocks.decorBlock, 1, MercuryBlocks.decorBlock.getIndex("mercuryBlockMeteor")), new Object[] { "XXX", "XXX", "XXX", 'X', "meteoricIronIngot" });

		// BLOCK TO ITEM
		RecipeUtil.addRecipe(new ItemStack(MercuryItems.mercuryItem, 9, MercuryItems.mercuryItem.getIndex("iridiumCrystals")), new Object[] { "X", 'X', "iridiumDecor" });
		RecipeUtil.addRecipe(new ItemStack(GCItems.meteoricIronIngot, 9, 0), new Object[] { "X", 'X', "meteorDecor" });
		
		// Smelting
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(MercuryBlocks.mercuryBlock, 1, MercuryItems.mercuryItem.getIndex("mercuryCobblestone")), OreDictionary.getOres("mercuryStone").get(0), 0.2F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(MercuryBlocks.mercuryBlock, 1, MercuryItems.mercuryItem.getIndex("mercuryOreCopper")), OreDictionary.getOres("ingotCopper").get(0), 0.2F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(MercuryBlocks.mercuryBlock, 1, MercuryItems.mercuryItem.getIndex("mercuryOreTin")), OreDictionary.getOres("ingotTin").get(0), 0.2F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(MercuryBlocks.mercuryBlock, 1, MercuryItems.mercuryItem.getIndex("mercuryOreIridium")), OreDictionary.getOres("iridiumCrystals").get(0), 0.2F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(MercuryBlocks.mercuryBlock, 1, MercuryItems.mercuryItem.getIndex("mercuryOreMeteor")), new ItemStack(GCItems.meteoricIronRaw), 0.2F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(MercuryBlocks.mercuryBlock, 1, MercuryItems.mercuryItem.getIndex("mercuryOreGold")), new ItemStack(Items.gold_ingot), 0.2F);
	}

	@Override
	public void registerTileEntities() {
		
	}

	@Override
	public void registerCreatures() {
		try {
			loadCreaturesRenderers();
		} catch(NoSuchMethodError e) {
		}
	}

	@SideOnly(Side.CLIENT)
	private void loadCreaturesRenderers() {
	}


	@Override
	public void registerOtherEntities() {
		try {
			loadOtherEntitiesRenderers();
		} catch(NoSuchMethodError e) {
		}
	}

	@SideOnly(Side.CLIENT)
	private void loadOtherEntitiesRenderers() {
	}
	
	@Override
	public void loadRecipes() {
		
	}
}
