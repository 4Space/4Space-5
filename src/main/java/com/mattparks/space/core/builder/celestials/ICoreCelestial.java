package com.mattparks.space.core.builder.celestials;

import java.util.List;

import com.mattparks.space.core.builder.ICoreModule;
import com.mattparks.space.core.music.MusicHandlerClient;
import com.mattparks.space.core.world.gen.GenBiomeDecorator;
import com.mattparks.space.core.world.gen.GenChunkProvider.GenerationSettings;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraftforge.client.IRenderHandler;

/**
 * An abstract class used to create new celestial bodys in the mod.
 */
public abstract class ICoreCelestial extends ICoreModule {
	public String rocketGuiLocation;
	
	public WorldProviderSpace worldProvider;
	public ITeleportType teleportType;

	/**
	 * Creates a new celestial body.
	 * 
	 * @param event The event given on pre init, used to create the configs.
	 * @param prefixAsset The asset prefix to use (EX: "spacecelestial").
	 * @param rocketGuiLocation The location for the rocket GUI (EX: "textures/gui/celestialRocketGui.png").
	 * @param worldProvider The world provider, the class that contains info about the celestial body.
	 * @param teleportType The teleport type used when entering the planet (use one from the com.mattparks.space.core.teleport package).
	 */
	public ICoreCelestial(FMLPreInitializationEvent event, String prefixAsset, String rocketGuiLocation, WorldProviderSpace worldProvider, ITeleportType teleportType) {
		super(event, prefixAsset);
		this.rocketGuiLocation = rocketGuiLocation;
		
		this.worldProvider = worldProvider;
		this.teleportType = teleportType;
	}
	
	public abstract int getDimensionID();
	
	public abstract int getBiomeID();

	/**
	 * Gets the optional music player json file.
	 * 
	 * @return The optional music json file.
	 */
	public abstract String getMusicJSON();

	/**
	 * Creates the sky provider for the celestial body.
	 * 
	 * @param world The world to create a sky provider for.
	 * 
	 * @return A new skyprovider for the celestial body.
	 */
	public abstract IRenderHandler createSkyProvider(IGalacticraftWorldProvider world);
	
	/**
	 * Gets if a provider is an instance of the world provider used for the celestial body.
	 * 
	 * @param provider The provider to check.
	 * 
	 * @return If the provider is an instance of the world provider used in the celestial body.
	 */
	public abstract boolean instanceOfProvider(WorldProvider provider);
	
	/**
	 * Gets a object that contains generation settings.
	 * 
	 * @return The object with generation settings.
	 */
	public abstract GenerationSettings getGenerationSettings();
	
	/**
	 * Gets the main biome decorator for the celestial body.
	 * 
	 * @return The celestial bodys biome decorator.
	 */
	public abstract GenBiomeDecorator getGenBiomeDecorator();
	
	/**
	 * Gets a list of mobs to spawn on the celestial body.
	 * 
	 * @return Spawnable mobs.
	 */
	public abstract List<SpawnListEntry> getSpawnableMonsters();
}