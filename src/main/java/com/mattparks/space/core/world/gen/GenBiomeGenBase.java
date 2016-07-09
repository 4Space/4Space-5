package com.mattparks.space.core.world.gen;

import java.util.List;

import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie;
import net.minecraft.world.biome.BiomeGenBase;

public class GenBiomeGenBase extends BiomeGenBase {
	public static GenBiomeGenBase biome = (GenBiomeGenBase) new GenBiomeGenBase(211).setBiomeName("Venus");

	public GenBiomeGenBase(int var1) {
		super(var1);
		
		spawnableMonsterList.clear();
		spawnableWaterCreatureList.clear();
		spawnableCreatureList.clear();

		spawnableMonsterList.add(new SpawnListEntry(EntityEvolvedZombie.class, 5, 1, 1));
		spawnableMonsterList.add(new SpawnListEntry(EntityEvolvedSpider.class, 3, 1, 1));
		spawnableMonsterList.add(new SpawnListEntry(EntityEvolvedCreeper.class, 2, 1, 1));
	//	spawnableMonsterList.add(new SpawnListEntry(EntityEvolvedBlaze.class, 4, 1, 1));

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
