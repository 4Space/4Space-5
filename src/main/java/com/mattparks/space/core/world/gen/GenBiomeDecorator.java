package com.mattparks.space.core.world.gen;

import java.util.List;
import java.util.Random;

import com.mattparks.space.core.utils.SpacePair;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.core.world.gen.WorldGenMinableMeta;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * A class capable of generating ores and other simple structures.
 */
public class GenBiomeDecorator extends BiomeDecoratorSpace {
	private List<GenerateOre> oreList;
	private List<GenerateStructure> structureList;
	private World currentWorld;

	/**
	 * Creates a new biome decorator.
	 * 
	 * @param oreList Ores the generator is capable of generating.
	 * @param structureList An optional list of simple generators.
	 */
	public GenBiomeDecorator(List<GenerateOre> oreList, List<GenerateStructure> structureList) {
		this.oreList = oreList;
		this.structureList = structureList;
	}

	@Override
	protected void decorate() {
		for (GenerateOre ore : oreList) {
			generateOre(ore.amountPerChunk, ore.generator, ore.minY, ore.maxY);
		}
		
		if (structureList != null) {
			for (GenerateStructure structure : structureList) {
				structure.generate(this);
			}
		}
	}

	@Override
	public void setCurrentWorld(World world) {
		this.currentWorld = world;
	}

	@Override
	public World getCurrentWorld() {
		return this.currentWorld;
	}
	
	public int getChunkX() {
		return chunkX;
	}

	public int getChunkZ() {
		return chunkZ;
	}
	
	public Random getRandom() {
		return rand;
	}

	/**
	 * A class representing a ore that can be generated.
	 */
	public static class GenerateOre {
		protected SpacePair<Block, Byte> bGen;
		protected SpacePair<Block, Byte> bGenOn;
		protected WorldGenerator generator;
		protected int amountPerChunk;
		protected int minY;
		protected int maxY;
		
		/**
		 * Creates a new generatable ore.
		 * 
		 * @param bGen The block to generate.
		 * @param bGenOn The type of block to generate in.
		 * @param commonness How common the ore will be.
		 * @param amountPerChunk The amount of the ore per chunk.
		 * @param minY The minimum elevation.
		 * @param maxY The maximum elevation.
		 */
		public GenerateOre(SpacePair<Block, Integer> bGen, SpacePair<Block, Integer> bGenOn, int commonness, int amountPerChunk, int minY, int maxY) {
			this.bGen = new SpacePair<Block, Byte>(bGen.getFirst(), new Byte("" + bGen.getSecond()));
			this.bGenOn = new SpacePair<Block, Byte>(bGenOn.getFirst(), new Byte("" + bGenOn.getSecond()));
			this.generator = new WorldGenMinableMeta(bGen.getFirst(), commonness, bGen.getSecond(), true, bGenOn.getFirst(), bGenOn.getSecond());
			this.amountPerChunk = amountPerChunk;
			this.minY = minY;
			this.maxY = maxY;
		}
	}
	
	/**
	 * A simple interface used to generate simple structure from the decorator.
	 */
	public static interface GenerateStructure {
		/**
		 * Generates the structure.
		 * 
		 * @param decorator The decorator to generate in.
		 */
		void generate(GenBiomeDecorator decorator);
	}
}
