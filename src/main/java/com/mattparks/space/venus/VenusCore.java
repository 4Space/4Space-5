package com.mattparks.space.venus;

import java.util.ArrayList;
import java.util.List;

import com.mattparks.space.core.builder.ICoreBlocks;
import com.mattparks.space.core.builder.ICoreItems;
import com.mattparks.space.core.builder.celestials.ICorePlanet;
import com.mattparks.space.core.teleport.TeleportTypeBallons;
import com.mattparks.space.core.utils.SpaceEntityUtil;
import com.mattparks.space.core.utils.SpacePair;
import com.mattparks.space.core.world.gen.GenBiomeDecorator;
import com.mattparks.space.core.world.gen.GenBiomeDecorator.GenerateOre;
import com.mattparks.space.core.world.gen.GenBiomeDecorator.GenerateStructure;
import com.mattparks.space.core.world.gen.GenChunkProvider.GenerationSettings;
import com.mattparks.space.venus.blocks.VenusBlocks;
import com.mattparks.space.venus.entities.EntityVenusianTNT;
import com.mattparks.space.venus.entities.render.RenderVenusianTNT;
import com.mattparks.space.venus.items.VenusItems;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.config.Configuration;

/**
 * A implementation of a 4Space planet, for the planet Venus.
 */
public class VenusCore extends ICorePlanet {
	public static VenusCore instance;
	
	private int dimensionID;
	private int biomeID;
	
	public VenusCore(FMLPreInitializationEvent event) {
		super(event, "spacevenus", "textures/gui/venusRocketGui.png", new VenusWorldProvider(), new TeleportTypeBallons());
		instance = this;
	}

	@Override
	public void loadFromConfig() {
		dimensionID = configuration.get(Configuration.CATEGORY_GENERAL, "Venus Dimension", -41).getInt(-41);
		biomeID = configuration.get(Configuration.CATEGORY_GENERAL, "Venus Biome", 211).getInt(211);
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
	public Planet getNewPlanet() {
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
	public IRenderHandler createSkyProvider(IGalacticraftWorldProvider world) {
		return new VenusSkyProvider(world);
	}

	@Override
	public void addShapelessRecipes() {
	}

	@Override
	public void registerTileEntities() {
	}

	@Override
	public void registerCreatures() {
	}

	@Override
	public void registerOtherEntities() {
		SpaceEntityUtil.registerSpaceNonMobEntity(EntityVenusianTNT.class, "VenusianTNT", 150, 1, true);
		RenderingRegistry.registerEntityRenderingHandler(EntityVenusianTNT.class, new RenderVenusianTNT());
	}

	@Override
	public void loadRecipes() {
	}

	@Override
	public boolean instanceOfProvider(WorldProvider provider) {
		return provider instanceof VenusWorldProvider;
	}
	
	@Override
	public GenerationSettings getGenerationSettings() {
		double terrainHeightMod = 11.0;
		double smallFeatureHeightMod = 44.0;
		double mountainHeightMod = 111.0;
		double valleyHeightMod = 55.0;
		int craterProbibility = 333;
		
		SpacePair<Block, Integer> blockTop = new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusSurfaceRock"));
		SpacePair<Block, Integer> blockFiller = new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusSubRock"));
		SpacePair<Block, Integer> blockLower = new SpacePair<Block, Integer>(VenusBlocks.venusBlock, VenusBlocks.venusBlock.getIndex("venusRock"));
		
		return new GenerationSettings(terrainHeightMod, smallFeatureHeightMod, mountainHeightMod, valleyHeightMod, craterProbibility, blockTop, blockFiller, blockLower, getSpawnableMonsters());
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
	//	spawnableMonsterList.add(new SpawnListEntry(EntityEvolvedBlaze.class, 4, 1, 1));
		return spawnableMonsters;
	}
}
