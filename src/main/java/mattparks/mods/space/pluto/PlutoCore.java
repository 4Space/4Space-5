package mattparks.mods.space.pluto;

import java.util.ArrayList;
import java.util.List;

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
 * A implementation of a 4Space planet, for the planet Pluto.
 */
public class PlutoCore extends ICorePlanet {
	public static PlutoCore instance;
	
	private int dimensionID;
	private int biomeID;
	private boolean planetEnabled;
	
	public PlutoCore(FMLPreInitializationEvent event) {
		super(event,  "spacepluto", "textures/gui/plutoRocketGui.png", new PlutoWorldProvider(), new TeleportTypeLander());
		instance = this;
	}

	@Override
	public Planet getNewPlanet() {
		if (!planetEnabled) {
			return null;
		}
		
		Planet pluto = new Planet("pluto");
		pluto.setParentSolarSystem(GalacticraftCore.solarSystemSol);
		pluto.setRingColorRGB(0.1F, 0.9F, 0.6F);
		pluto.setPhaseShift(2.0F);
		pluto.setRelativeSize(0.5319F);
		pluto.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(2.5F, 2.5F));
		pluto.setRelativeOrbitTime(194.84118291347207009857612267251F);
		pluto.setBodyIcon(new ResourceLocation(this.prefixAsset, "textures/gui/celestialbodies/pluto.png"));
		pluto.setTierRequired(2);
		pluto.setDimensionInfo(dimensionID, worldProvider.getClass());
		pluto.atmosphereComponent(IAtmosphericGas.CO2).atmosphereComponent(IAtmosphericGas.HELIUM).atmosphereComponent(IAtmosphericGas.ARGON);
		return pluto;
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
		return new PlutoSkyProvider(world);
	}

	@Override
	public boolean instanceOfProvider(WorldProvider provider) {
		return provider instanceof PlutoWorldProvider;
	}

	@Override
	public GenerationSettings getGenerationSettings() {
		String planetName = "Pluto";
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
		dimensionID = configuration.get(Configuration.CATEGORY_GENERAL, "Pluto Dimension", -42).getInt(-42);
		biomeID = configuration.get(Configuration.CATEGORY_GENERAL, "Pluto Biome", 212).getInt(212);
		planetEnabled = configuration.get(Configuration.CATEGORY_GENERAL, "Pluto Enabled", true).getBoolean(true);
		
	}

	@Override
	public ICoreBlocks getBlocks() {
		return null;
	}

	@Override
	public ICoreItems getItems() {
		return null;
	}

	@Override
	public void hideNEI() {
		
	}

	@Override
	public void addShapelessRecipes() {
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
