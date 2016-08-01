package mattparks.mods.space.core.builder.celestials;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import net.minecraft.util.ResourceLocation;

/**
 * An abstract class used to create new planets for the mod.
 */
public abstract class ICorePlanet extends ICoreCelestial {
	public Planet planet;

	/**
	 * Creates a new planet.
	 * 
	 * @param event The event given on pre init, used to create the configs.
	 * @param prefixAsset The asset prefix to use (EX: "spaceplanet").
	 * @param rocketGuiLocation The location for the rocket GUI (EX: "textures/gui/planetRocketGui.png").
	 * @param worldProvider The world provider, the class that contains info about the planet.
	 * @param teleportType The teleport type used when entering the planet (use one from com.mattparks.space.core.teleport).
	 */
	public ICorePlanet(FMLPreInitializationEvent event, String prefixAsset, String rocketGuiLocation, WorldProviderSpace worldProvider, ITeleportType teleportType) {
		super(event, prefixAsset, rocketGuiLocation, worldProvider, teleportType);
		this.planet = null; // Will be created in `registerPlanet()`.
	}

	/**
	 * Registers the planet with Galacticraft.
	 */
	public void registerPlanet() {
		planet = getNewPlanet();
		
		if (planet != null) {
			GalaxyRegistry.registerPlanet(planet);
			
			if (worldProvider != null) {
				GalacticraftRegistry.registerTeleportType(worldProvider.getClass(), teleportType);
				GalacticraftRegistry.registerRocketGui(worldProvider.getClass(), new ResourceLocation(prefixTexture + rocketGuiLocation));
			}
		}
	}

	/**
	 * Creates a new planet for this ICorePlanet.
	 * 
	 * @return The new planet.
	 */
	public abstract Planet getNewPlanet(); 
}