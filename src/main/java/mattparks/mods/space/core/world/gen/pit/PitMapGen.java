package mattparks.mods.space.core.world.gen.pit;

import mattparks.mods.space.core.world.gen.GenerationSettings;
import net.minecraft.world.gen.structure.StructureStart;

public class PitMapGen extends PitMapGenStructure {
	private GenerationSettings settings;
	
	public PitMapGen(GenerationSettings settings) {
		this.settings = settings;
	}
	
	@Override
	protected boolean canSpawnStructureAtCoords(int par1, int par2) {
		if (this.rand.nextInt(100) != 0) {
			return false;
		}
		return true;
	}

	@Override
	protected StructureStart getStructureStart(int par1, int par2) {
		return new PitStructureStart(settings, this.worldObj, this.rand, par1, par2);
	}
}
