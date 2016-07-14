package com.mattparks.space.core.builder.celestials;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import net.minecraft.util.ResourceLocation;

/**
 * An abstract class used to create new moons for the mod.
 */
public abstract class ICoreMoon extends ICoreCelestial {
	public Moon moon;

	/**
	 * Creates a new moon.
	 * 
	 * @param event The event given on pre init, used to create the configs.
	 * @param prefixAsset The asset prefix to use (EX: "spacemoon").
	 * @param rocketGuiLocation The location for the rocket GUI (EX: "textures/gui/moonRocketGui.png").
	 * @param worldProvider The world provider, the class that contains info about the planet.
	 * @param teleportType The teleport type used when entering the planet (use one from com.mattparks.space.core.teleport).
	 */
	public ICoreMoon(FMLPreInitializationEvent event, String prefixAsset, String rocketGuiLocation, WorldProviderSpace worldProvider, ITeleportType teleportType) {
		super(event, prefixAsset, rocketGuiLocation, worldProvider, teleportType);
		this.moon = null; // Will be created in `registerMoon()`.
	}

	/**
	 * Registers the moon with Galacticraft.
	 */
	public void registerMoon() {
		moon = getNewMoon();
		moon.setParentPlanet(getParent());
		GalaxyRegistry.registerMoon(moon);
		
		if (worldProvider != null) {
			GalacticraftRegistry.registerTeleportType(worldProvider.getClass(), teleportType);
			GalacticraftRegistry.registerRocketGui(worldProvider.getClass(), new ResourceLocation(prefixTexture + rocketGuiLocation));
		}
	}
	
	/**
	 * Gets the parent moon.
	 * 
	 * @return The moon to orbit around.
	 */
	public abstract Planet getParent();

	/**
	 * Creates a new moon for this ICoreMoon, do not define setParentPlanet from here.
	 * 
	 * @return The new moon.
	 */
	public abstract Moon getNewMoon(); 
}