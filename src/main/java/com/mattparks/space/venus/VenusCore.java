package com.mattparks.space.venus;

import java.util.ArrayList;
import java.util.List;

import com.mattparks.space.core.builder.ICorePlanet;
import com.mattparks.space.core.teleport.TeleportTypeBallons;
import com.mattparks.space.core.utils.SpacePair;
import com.mattparks.space.core.world.gen.GenBiomeDecorator.GenerateOre;
import com.mattparks.space.core.world.gen.GenChunkProvider.GenerationSettings;
import com.mattparks.space.core.world.gen.GenChunkProvider.WorldGenBlocks;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;

/**
 * A implementation of a 4Space planet, for the planet Venus.
 */
public class VenusCore extends ICorePlanet {
	public static VenusCore instance;
	
	public VenusCore() {
		super(-41, "spacevenus", "textures/gui/venusRocketGui.png", new VenusWorldProvider(), new TeleportTypeBallons());
		instance = this;
	}

	@Override
	public void loadBlocks() {
	}

	@Override
	public void loadItems() {
	}

	@Override
	public void hideNEI() {
		//	API.hideItem(new ItemStack(MercuryBlocks.caravanModuleDummy, 1, 0));
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
		venus.atmosphereComponent(IAtmosphericGas.CO2).atmosphereComponent(IAtmosphericGas.HELIUM).atmosphereComponent(IAtmosphericGas.ARGON);
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
	}

	@Override
	public void loadRecipes() {
	}

	@Override
	public GenerationSettings getGenerationSettings() {
		double terrainHeightMod = 11.0;
		double smallFeatureHeightMod = 44.0;
		double mountainHeightMod = 111.0;
		double valleyHeightMod = 55.0;
		int craterProbibility = 333;
		return new GenerationSettings(terrainHeightMod, smallFeatureHeightMod, mountainHeightMod, valleyHeightMod, craterProbibility);
	}
	
	@Override
	public WorldGenBlocks getWorldGenBlocks() {
		return new WorldGenBlocks(
			new SpacePair<Block, Byte>(Blocks.grass, new Byte("0")),
			new SpacePair<Block, Byte>(Blocks.dirt, new Byte("0")),
			new SpacePair<Block, Byte>(Blocks.stone, new Byte("0"))
		);
	}
	
	@Override
	public List<GenerateOre> getGeneratableOres() {
		List<GenerateOre> oreList = new ArrayList<GenerateOre>();
		oreList.add(new GenerateOre(new SpacePair<Block, Byte>(Blocks.dirt, new Byte("0")), new SpacePair<Block, Byte>(Blocks.stone, new Byte("0")), 32, 32, 0, 256));
		oreList.add(new GenerateOre(new SpacePair<Block, Byte>(Blocks.coal_ore, new Byte("0")), new SpacePair<Block, Byte>(Blocks.stone, new Byte("0")), 15, 32, 0, 185));
		oreList.add(new GenerateOre(new SpacePair<Block, Byte>(Blocks.iron_ore, new Byte("0")), new SpacePair<Block, Byte>(Blocks.stone, new Byte("0")), 9, 32, 0, 100));
		return oreList;
	}
}
