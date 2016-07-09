package com.mattparks.space.core.world.gen;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldChunkManagerSpace;
import net.minecraft.world.biome.BiomeGenBase;

public class GenChunkManager extends WorldChunkManagerSpace {
	@Override
	public BiomeGenBase getBiome() {
		return GenBiomeGenBase.biome;
	}
}
