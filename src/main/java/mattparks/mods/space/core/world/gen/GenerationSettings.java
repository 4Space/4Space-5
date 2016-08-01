package mattparks.mods.space.core.world.gen;

import java.util.List;

import mattparks.mods.space.core.utils.SpacePair;
import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

public class GenerationSettings {
	public String planetName;
	public double terrainHeightMod;
	public double smallFeatureHeightMod;
	public double mountainHeightMod;
	public double valleyHeightMod;
	public int craterProbibility;
	public int caveChance;
	
	public SpacePair<Block, Byte> blockTop;
	public SpacePair<Block, Byte> blockFiller;
	public SpacePair<Block, Byte> blockLower;
	public SpacePair<Block, Byte> blockBrick;
	public Block blockEgg;
	
	public boolean dungeonEnabled;
	public boolean pitEnabled;
	public boolean villageEnabled;
	
	public List<SpawnListEntry> spawnableMonsters;
	
	public GenerationSettings(String planetName, double terrainHeightMod, double smallFeatureHeightMod, double mountainHeightMod, double valleyHeightMod, int craterProbibility, int caveChance,
			SpacePair<Block, Integer> blockTop, SpacePair<Block, Integer> blockFiller, SpacePair<Block, Integer> blockLower, SpacePair<Block, Integer> blockBrick, Block blockEgg,
			boolean dungeonEnabled, boolean pitEnabled, boolean villageEnabled,
			List<SpawnListEntry> spawnableMonsters) {
		this.planetName = planetName;
		this.terrainHeightMod = terrainHeightMod;
		this.smallFeatureHeightMod = smallFeatureHeightMod;
		this.mountainHeightMod = mountainHeightMod;
		this.valleyHeightMod = valleyHeightMod;
		this.craterProbibility = craterProbibility;
		this.caveChance = caveChance;
		
		this.blockTop = new SpacePair<Block, Byte>(blockTop.getFirst(), new Byte("" + blockTop.getSecond()));
		this.blockFiller = new SpacePair<Block, Byte>(blockFiller.getFirst(), new Byte("" + blockFiller.getSecond()));
		this.blockLower = new SpacePair<Block, Byte>(blockLower.getFirst(), new Byte("" + blockLower.getSecond()));
		this.blockBrick = new SpacePair<Block, Byte>(blockBrick.getFirst(), new Byte("" + blockBrick.getSecond()));
		this.blockEgg = blockEgg;
		
		this.spawnableMonsters = spawnableMonsters;
	}
}
