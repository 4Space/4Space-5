package com.mattparks.space.venus.world;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldChunkManagerSpace;
import net.minecraft.world.biome.BiomeGenBase;

public class VenusWorldChunkManager extends WorldChunkManagerSpace {
	@Override
	public BiomeGenBase getBiome() {
		return VenusBiomeGenBase.venus;
	}
}
