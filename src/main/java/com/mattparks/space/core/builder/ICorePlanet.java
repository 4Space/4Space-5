package com.mattparks.space.core.builder;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;

/**
 * An abstract class used to create new planets for the mod.
 */
public abstract class ICorePlanet {
	public static int dimensionID;
	public String prefixAsset;
	public String prefixTexture;
	
	public String rocketGuiLocation;
	
	public static Planet planet;
	public static WorldProviderSpace worldProvider;
	public static ITeleportType teleportType;
	
	/**
	 * Creates a new planet.
	 * 
	 * @param dimensionID The planets dimension (EX: -41).
	 * @param prefixAsset The asset prefix to use (EX: "spacevenus").
	 * @param rocketGuiLocation The location for the rocket GUI (EX: "textures/gui/venusRocketGui.png").
	 * @param worldProvider The world provider, the class that contains info about the planet.
	 * @param teleportType The teleport type used when entering the planet (use one from com.mattparks.space.core.teleport).
	 */
	public ICorePlanet(int dimensionID, String prefixAsset, String rocketGuiLocation, WorldProviderSpace worldProvider, ITeleportType teleportType) {
		this.dimensionID = dimensionID;
		this.prefixAsset = prefixAsset;
		this.prefixTexture = prefixAsset + ":";
		
		this.rocketGuiLocation = rocketGuiLocation;
		
		this.planet = null; // Will be created in `registerPlanet()`.
		this.worldProvider = worldProvider;
		this.teleportType = teleportType;
	}
	
	/**
	 * Registers the planet with Galacticraft.
	 */
	public void registerPlanet() {
		planet = getNewPlanet();
		GalaxyRegistry.registerPlanet(planet);
		
		if (worldProvider != null) {
			GalacticraftRegistry.registerTeleportType(worldProvider.getClass(), teleportType);
			GalacticraftRegistry.registerRocketGui(worldProvider.getClass(), new ResourceLocation(prefixTexture + rocketGuiLocation));
		}
	}
	
	/**
	 * Loads crafting and smelting recipes to minecraft.
	 */
	public abstract void loadRecipes();
	
	/**
	 * Loads blocks to minecraft.
	 */
	public abstract void loadBlocks();
	
	/**
	 * Loads items to minecraft.
	 */
	public abstract void loadItems();
	
	/**
	 * Hides items from NEI.
	 */
	public abstract void hideNEI();
	
	/**
	 * Creates a new planet for this ICorePlanet.
	 * 
	 * @return The new planet.
	 */
	public abstract Planet getNewPlanet(); 
	
	/**
	 * Creates the sky provider for the planet.
	 * 
	 * @param world The world to create a sky provider for.
	 * 
	 * @return A new skyprovider for the planet.
	 */
	public abstract IRenderHandler createSkyProvider(IGalacticraftWorldProvider world);
	
	/**
	 * Will the planet have clouds?
	 * 
	 * @return Will clouds be rendered.
	 */
	public abstract boolean hasClouds();

	/**
	 * Adds shapeless recipes, used crafters like galacticrafts smeltery.
	 */
	public abstract void addShapelessRecipes();
	
	/**
	 * Registers tile entities.
	 */
	public abstract void registerTileEntities();
	
	/**
	 * Registers creatures (mobs).
	 */
	public abstract void registerCreatures();
	
	/**
	 * Registers any other type of entity.
	 */
	public abstract void registerOtherEntities();
}
