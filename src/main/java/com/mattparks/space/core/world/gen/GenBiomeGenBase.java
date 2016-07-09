package com.mattparks.space.core.world.gen;

import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;

public class GenBiomeGenBase extends BiomeGenBase {
	public GenBiomeGenBase(int var1) {
		super(var1);
		
		spawnableMonsterList.clear();
		spawnableWaterCreatureList.clear();
		spawnableCreatureList.clear();

		rainfall = 0.0f;
	}
	
	public List<SpawnListEntry> getSpawnableMonsters() {
		return spawnableMonsterList;
	}

	@Override
	public GenBiomeGenBase setColor(int var1) {
		return (GenBiomeGenBase) super.setColor(var1);
	}

	@Override
	public float getSpawningChance() {
		return 0.01F;
	}
}
