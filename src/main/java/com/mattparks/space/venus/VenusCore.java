package com.mattparks.space.venus;

import com.mattparks.space.core.builder.ICorePlanet;
import com.mattparks.space.core.teleport.TeleportTypeBallons;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;

/**
 * A implementation of a 4Space planet, for the planet Venus.
 */
public class VenusCore extends ICorePlanet {
	public VenusCore() {
		super(-41, "spacevenus", "textures/gui/venusRocketGui.png", new VenusWorldProvider(), new TeleportTypeBallons());
	}

	@Override
	public void loadRecipes() {
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
	public boolean hasClouds() {
		return false;
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
}
