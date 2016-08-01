package mattparks.mods.space.venus;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mattparks.mods.space.core.builder.ICoreBlocks;
import mattparks.mods.space.core.builder.ICoreItems;
import mattparks.mods.space.core.builder.celestials.ICorePlanet;
import mattparks.mods.space.core.teleport.TeleportTypeBallons;
import mattparks.mods.space.core.utils.SpaceEntityUtil;
import mattparks.mods.space.core.utils.SpacePair;
import mattparks.mods.space.core.world.gen.GenBiomeDecorator;
import mattparks.mods.space.core.world.gen.GenBiomeDecorator.GenerateOre;
import mattparks.mods.space.core.world.gen.GenBiomeDecorator.GenerateStructure;
import mattparks.mods.space.core.world.gen.GenerationSettings;
import mattparks.mods.space.venus.blocks.VenusBlocks;
import mattparks.mods.space.venus.entities.EntityEvolvedBlaze;
import mattparks.mods.space.venus.entities.EntityVenusianTNT;
import mattparks.mods.space.venus.entities.render.RenderEvolvedBlaze;
import mattparks.mods.space.venus.entities.render.RenderVenusianTNT;
import mattparks.mods.space.venus.items.VenusItems;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie;
import micdoodle8.mods.galacticraft.core.util.RecipeUtil;
import net.minecraft.block.Block;
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
 * A implementation of a 4Space planet, for the planet Venus.
 */
public class VenusCore extends ICorePlanet {
	public static VenusCore instance;
	
	private int dimensionID;
	private int biomeID;
	private boolean planetEnabled;
	
	public VenusCore(FMLPreInitializationEvent event) {
		super(event, "spacevenus", "textures/gui/venusRocketGui.png", new VenusWorldProvider(), new TeleportTypeBallons());
		instance = this;
	}

	@Override
	public Planet getNewPlanet() {
		if (!planetEnabled) {
			return null;
		}
		
		Planet venus = new Planet("venus");
		venus.setParentSolarSystem(GalacticraftCore.solarSystemSol);
		venus.setRingColorRGB(0.1F, 0.9F, 0.6F);
		venus.setPhaseShift(2.0F);
		venus.setRelativeSize(0.5319F);
		venus.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(0.75F, 0.75F));
		venus.setRelativeOrbitTime(0.61527929901423877327491785323111F);
		venus.setBodyIcon(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/venus.png"));
		venus.setTierRequired(2);
		venus.setDimensionInfo(dimensionID, worldProvider.getClass());
		venus.atmosphereComponent(IAtmosphericGas.CO2).atmosphereComponent(IAtmosphericGas.HELIUM).atmosphereComponent(IAtmosphericGas.NITROGEN).atmosphereComponent(IAtmosphericGas.ARGON);
		return venus;
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
		return "spaceCore.venusMusic";
	}

	@Override
	public IRenderHandler createSkyProvider(IGalacticraftWorldProvider world) {
		return new VenusSkyProvider(world);
	}

	@Override
	public boolean instanceOfProvider(WorldProvider provider) {
		return provider instanceof VenusWorldProvider;
	}
	
	@Override
	public GenerationSettings getGenerationSettings() {
		String planetName = "Venus";
		double terrainHeightMod = 11.0;
		double smallFeatureHeightMod = 44.0;
		double mountainHeightMod = 111.0;
		double valleyHeightMod = 55.0;
		int craterProbibility = 333;
		int caveChance = 25;
		
		SpacePair<Block, Integer> blockTop = new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusSurfaceRock"));
		SpacePair<Block, Integer> blockFiller = new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusSubRock"));
		SpacePair<Block, Integer> blockLower = new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusRock"));
		SpacePair<Block, Integer> blockBrick = new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusBrickDungeon"));
		Block blockEgg = VenusBlocks.evolvedBlazeEgg;
		
		boolean dungeonEnabled = false;
		boolean pitEnabled = true;
		boolean villageEnabled = false;
		
		return new GenerationSettings(planetName, terrainHeightMod, smallFeatureHeightMod, mountainHeightMod, valleyHeightMod, craterProbibility, caveChance, blockTop, blockFiller, blockLower, blockBrick, blockEgg, dungeonEnabled, pitEnabled, villageEnabled, getSpawnableMonsters());
	}
	
	@Override
	public GenBiomeDecorator getGenBiomeDecorator() {
		List<GenerateOre> oreList = new ArrayList<GenerateOre>();
		SpacePair<Block, Integer> stoneBlock = new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusRock"));
		
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusSubRock")), stoneBlock, 32, 32, 0, 256));
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusOreSulfur")), stoneBlock, 12, 12, 0, 64));
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusOreUranium")), stoneBlock, 2, 8, 0, 16));
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusOreRuby")), stoneBlock, 8, 8, 0, 16));
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusOreCrystal")), stoneBlock, 3, 8, 0, 16));
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusOreTin")), stoneBlock, 4, 16, 0, 48));
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusOreCopper")), stoneBlock, 5, 16, 0, 48));
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusOreIron")), stoneBlock, 7, 16, 0, 64));
		oreList.add(new GenerateOre(new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusOreCoal")), stoneBlock, 10, 16, 0, 128));

		List<GenerateStructure> structureList = new ArrayList<GenerateStructure>();
		structureList.add(new GenerateStructure() {
			@Override
			public void generate(GenBiomeDecorator decorator) {
				int lavaLakesPerChunk = 5;
				
				for (int i = 0; i < lavaLakesPerChunk; i++) {
					int x = decorator.getChunkX() + decorator.getRandom().nextInt(16) + 8;
					int y = decorator.getRandom().nextInt(decorator.getRandom().nextInt(decorator.getRandom().nextInt(112) + 8) + 8);
					int z = decorator.getChunkX() + decorator.getRandom().nextInt(16) + 8;
					// TODO: Generate lava!
				}
			}
		});
		
		return new GenBiomeDecorator(oreList, structureList);
	}

	@Override
	public List<SpawnListEntry> getSpawnableMonsters() {
		List<SpawnListEntry> spawnableMonsters = new ArrayList<SpawnListEntry>();
		spawnableMonsters.add(new SpawnListEntry(EntityEvolvedZombie.class, 5, 1, 1));
		spawnableMonsters.add(new SpawnListEntry(EntityEvolvedSpider.class, 3, 1, 1));
		spawnableMonsters.add(new SpawnListEntry(EntityEvolvedCreeper.class, 2, 1, 1));
		spawnableMonsters.add(new SpawnListEntry(EntityEvolvedBlaze.class, 4, 1, 1));
		return spawnableMonsters;
	}

	@Override
	public void loadFromConfig() {
		dimensionID = configuration.get(Configuration.CATEGORY_GENERAL, "Venus Dimension", -41).getInt(-41);
		biomeID = configuration.get(Configuration.CATEGORY_GENERAL, "Venus Biome", 211).getInt(211);
		planetEnabled = configuration.get(Configuration.CATEGORY_GENERAL, "Venus Enabled", true).getBoolean(true);
	}

	@Override
	public ICoreBlocks getBlocks() {
		return new VenusBlocks();
	}

	@Override
	public ICoreItems getItems() {
		return new VenusItems();
	}

	@Override
	public void hideNEI() {
	}

	@Override
	public void addShapelessRecipes() {
		// ORE DICTIONARY
		OreDictionary.registerOre("dustSulfur", new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("sulfurDust")));
		OreDictionary.registerOre("itemSulfurDust", new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("sulfurDust"))); // FOR ICBM SPIKES
		OreDictionary.registerOre("ingotSulfur", new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("ingotSulfur")));
		OreDictionary.registerOre("ruby", new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("ruby")));
		OreDictionary.registerOre("ingotUranium", new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("ingotUranium")));
		OreDictionary.registerOre("pellucidum", new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("pellucidum")));
		OreDictionary.registerOre("sulfurPlate", new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("sulfurPlate")));
		OreDictionary.registerOre("rubyPlate", new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("rubyPlate")));
		OreDictionary.registerOre("uraniumPlate", new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("uraniumPlate")));

		OreDictionary.registerOre("venusRodDust", new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("venusRodDust")));
		OreDictionary.registerOre("powerPellucidum", new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("powerPellucidum")));

		OreDictionary.registerOre("venusStone", new ItemStack(VenusBlocks.decorBlock, 1, VenusBlocks.decorBlock.getIndex("venusRock")));
		OreDictionary.registerOre("sulfurDecor", new ItemStack(VenusBlocks.decorBlock, 1, VenusBlocks.decorBlock.getIndex("venusBlockSulfur")));
		OreDictionary.registerOre("uraniumDecor", new ItemStack(VenusBlocks.decorBlock, 1, VenusBlocks.decorBlock.getIndex("venusBlockUranium")));
		OreDictionary.registerOre("rubyDecor", new ItemStack(VenusBlocks.decorBlock, 1, VenusBlocks.decorBlock.getIndex("venusBlockRuby")));
		OreDictionary.registerOre("pellucidumDecor", new ItemStack(VenusBlocks.decorBlock, 1, VenusBlocks.decorBlock.getIndex("venusBlockPellucidum")));

		// ARMOR AND TOOLS CRAFTING
		RecipeUtil.addRecipe(new ItemStack(VenusItems.armourRuby.boots), new Object[] { "X X", "X X", 'X', "ruby" });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.armourRuby.helmet), new Object[] { "XXX", "X X", 'X', "ruby" });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.armourRuby.chestplate), new Object[] { "X X", "XXX", "XXX", 'X', "ruby" });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.armourRuby.leggings), new Object[] { "XXX", "X X", "X X", 'X', "ruby" });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.armourSulfur.helmet), new Object[] { "XXX", "X X", 'X', "ingotSulfur" });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.armourSulfur.chestplate), new Object[] { "X X", "XXX", "XXX", 'X', "ingotSulfur" });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.armourSulfur.leggings), new Object[] { "XXX", "X X", "X X", 'X', "ingotSulfur" });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.armourSulfur.boots), new Object[] { "X X", "X X", 'X', "ingotSulfur" });
		
		RecipeUtil.addRecipe(new ItemStack(VenusItems.toolsRuby.sword), new Object[] { "X", "X", "Y", 'X', "ruby", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.toolsRuby.pickaxe), new Object[] { "XXX", " Y ", " Y ", 'X', "ruby", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.toolsRuby.spade), new Object[] { "X", "Y", "Y", 'X', "ruby", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.toolsRuby.hoe), new Object[] { "XX", " Y", " Y", 'X', "ruby", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.toolsRuby.axe), new Object[] { "XX", "XY", " Y", 'X', "ruby", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.toolsSulfur.sword), new Object[] { "X", "X", "Y", 'X', "ingotSulfur", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.toolsSulfur.pickaxe), new Object[] { "XXX", " Y ", " Y ", 'X', "ingotSulfur", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.toolsSulfur.spade), new Object[] { "X", "Y", "Y", 'X', "ingotSulfur", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.toolsSulfur.hoe), new Object[] { "XX", " Y", " Y", 'X', "ingotSulfur", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.toolsSulfur.axe), new Object[] { "XX", "XY", " Y", 'X', "ingotSulfur", 'Y', Items.stick });

		// OTHERS CRAFTING
		RecipeUtil.addRecipe(new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("powerPellucidum")), new Object[] { "D D", " C ", "D D", 'D', "venusRodDust", 'C', "pellucidum" });

		RecipeUtil.addRecipe(new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("ingotSulfur")), new Object[] { "XX", 'X', "dustSulfur" });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.venusItem, 2, VenusItems.venusItem.getIndex("sulfurDust")), new Object[] { "X", 'X', "ingotSulfur" });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.venusItem, 4, VenusItems.venusItem.getIndex("venusRodDust")), new Object[] { "X", 'X', new ItemStack(VenusItems.venusItem, 1, VenusItems.venusItem.getIndex("venusRod")) });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.batteryUranium, 1, 100), new Object[] { "TTT", "SIS", "TTT", 'T', "rubyPlate", 'I', "uraniumDecor", 'S', VenusItems.batterySulfur });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.batterySulfur, 1, 100), new Object[] { "TRT", "RVR", "TRT", 'T', "sulfurPlate", 'V', "powerPellucidum", 'R', Items.redstone });

		// ITEM TO BLOCK
		RecipeUtil.addRecipe(new ItemStack(VenusBlocks.decorBlock, 1, VenusBlocks.decorBlock.getIndex("venusBlockPellucidum")), new Object[] { "XXX", "XXX", "XXX", 'X', "pellucidum" });
		RecipeUtil.addRecipe(new ItemStack(VenusBlocks.decorBlock, 1, VenusBlocks.decorBlock.getIndex("venusBlockUranium")), new Object[] { "XXX", "XXX", "XXX", 'X', "ingotUranium" });
		RecipeUtil.addRecipe(new ItemStack(VenusBlocks.decorBlock, 1, VenusBlocks.decorBlock.getIndex("venusBlockRuby")), new Object[] { "XXX", "XXX", "XXX", 'X', "ruby" });
		RecipeUtil.addRecipe(new ItemStack(VenusBlocks.decorBlock, 1, VenusBlocks.decorBlock.getIndex("venusBlockSulfur")), new Object[] { "XXX", "XXX", "XXX", 'X', "ingotSulfur" });

		RecipeUtil.addRecipe(new ItemStack(VenusBlocks.sulfurTorch, 4, 0), new Object[] { "X", "Y", 'X', "dustSulfur", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(VenusBlocks.sulfurTorch, 4, 0), new Object[] { "X", "Y", 'X', "itemSulfurDust", 'Y', Items.stick });
		RecipeUtil.addRecipe(new ItemStack(VenusBlocks.venusianTNT, 1, 0), new Object[] { "DXD", "XDX", "DXD", 'X', "dustSulfur", 'D', "venusRodDust" });

		// BLOCK TO ITEM
		RecipeUtil.addRecipe(new ItemStack(VenusItems.venusItem, 9, VenusItems.venusItem.getIndex("ingotSulfur")), new Object[] { "X", 'X', "sulfurDecor" });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.venusItem, 9, VenusItems.venusItem.getIndex("ruby")), new Object[] { "X", 'X', "rubyDecor" });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.venusItem, 9, VenusItems.venusItem.getIndex("ingotUranium")), new Object[] { "X", 'X', "uraniumDecor" });
		RecipeUtil.addRecipe(new ItemStack(VenusItems.venusItem, 9, VenusItems.venusItem.getIndex("pellucidum")), new Object[] { "X", 'X', "pellucidumDecor" });

		// Smelting
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(VenusBlocks.venusBlock, 1, VenusBlocks.venusBlock.getIndex("venusCobblestone")), OreDictionary.getOres("venusStone").get(0), 0.2f);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(VenusBlocks.venusBlock, 2, VenusBlocks.venusBlock.getIndex("venusOreSulfur")), OreDictionary.getOres("dustSulfur").get(0), 0.2f);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(VenusBlocks.venusBlock, 1, VenusBlocks.venusBlock.getIndex("venusOreUranium")), OreDictionary.getOres("ingotUranium").get(0), 0.2f);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(VenusBlocks.venusBlock, 1, VenusBlocks.venusBlock.getIndex("venusOreRuby")), OreDictionary.getOres("ruby").get(0), 0.2f);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(VenusBlocks.venusBlock, 1, VenusBlocks.venusBlock.getIndex("venusOrePellucidum")), OreDictionary.getOres("pellucidum").get(0), 0.2f);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(VenusBlocks.venusBlock, 1, VenusBlocks.venusBlock.getIndex("venusOreTin")), OreDictionary.getOres("ingotTin").get(0), 0.2f);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(VenusBlocks.venusBlock, 1, VenusBlocks.venusBlock.getIndex("venusOreCopper")), OreDictionary.getOres("ingotCopper").get(0), 0.2f);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(VenusBlocks.venusBlock, 1, VenusBlocks.venusBlock.getIndex("venusOreIron")), new ItemStack(Items.iron_ingot), 0.2f);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(VenusBlocks.venusBlock, 1, VenusBlocks.venusBlock.getIndex("venusOreCoal")), new ItemStack(Items.coal), 0.2f);
	}

	@Override
	public void registerTileEntities() {
	}

	@Override
	public void registerCreatures() {
		SpaceEntityUtil.registerSpaceCreature(EntityEvolvedBlaze.class, "EvolvedBlaze", -771829, -870131);
	//	SpaceUtil.registerSpaceCreature(EntityVenusianVillager.class, "VenusianVillager", SpaceUtil.to32BitColor(255, 103, 181, 145), 16167425);

		try {
			loadCreaturesRenderers();
		} catch(NoSuchMethodError e) {
		//	e.printStackTrace();
		}
	}

	@SideOnly(Side.CLIENT)
	private void loadCreaturesRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityEvolvedBlaze.class, new RenderEvolvedBlaze());
		//	RenderingRegistry.registerEntityRenderingHandler(EntityVenusianVillager.class, new RenderVenusianVillager());
	}

	@Override
	public void registerOtherEntities() {
		SpaceEntityUtil.registerSpaceNonMobEntity(EntityVenusianTNT.class, "VenusianTNT", 150, 1, true);

		try {
			loadOtherEntitiesRenderers();
		} catch(NoSuchMethodError e) {
		//	e.printStackTrace();
		}
	}

	@SideOnly(Side.CLIENT)
	private void loadOtherEntitiesRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityVenusianTNT.class, new RenderVenusianTNT());
	}
	
	@Override
	public void loadRecipes() {
	}
}
