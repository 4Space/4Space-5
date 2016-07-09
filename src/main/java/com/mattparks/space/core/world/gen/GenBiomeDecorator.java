package com.mattparks.space.core.world.gen;

import java.util.List;

import com.mattparks.space.core.utils.SpacePair;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class GenBiomeDecorator extends BiomeDecoratorSpace {
	private List<GenerateOre> oreList;
	private World currentWorld;

	public GenBiomeDecorator(List<GenerateOre> oreList) {
		this.oreList = oreList;
	}

	@Override
	protected void decorate() {
		for (GenerateOre ore : oreList) {
			generateOre(ore.amountPerChunk, ore.generator, ore.minY, ore.maxY);
		}
	}

	protected void setCurrentWorld(World world) {
		this.currentWorld = world;
	}

	protected World getCurrentWorld() {
		return this.currentWorld;
	}
	
	public static class GenerateOre {
		protected SpacePair<Block, Byte> bGen;
		protected SpacePair<Block, Byte> bGenOn;
		protected WorldGenerator generator;
		protected int amountPerChunk;
		protected int minY;
		protected int maxY;
		
		public GenerateOre(SpacePair<Block, Byte> bGen, SpacePair<Block, Byte> bGenOn, int commonness, int amountPerChunk, int minY, int maxY) {
			this.bGen = bGen;
			this.bGenOn = bGenOn;
			this.generator = new WorldGenMinableMeta(bGen.getFirst(), commonness, bGen.getSecond(), true, bGenOn.getFirst(), bGenOn.getSecond());
			this.amountPerChunk = amountPerChunk;
			this.minY = minY;
			this.maxY = maxY;
		}
	}
}
