package mattparks.mods.space.core.world.gen;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldChunkManagerSpace;
import net.minecraft.world.biome.BiomeGenBase;

public class GenChunkManager extends WorldChunkManagerSpace {
	public GenBiomeGenBase biome;
	
	public GenChunkManager(String biomeName, int biomeID) {
		this.biome = (GenBiomeGenBase) new GenBiomeGenBase(biomeID).setBiomeName(biomeName);
	}
	
	@Override
	public BiomeGenBase getBiome() {
		return biome;
	}
}
