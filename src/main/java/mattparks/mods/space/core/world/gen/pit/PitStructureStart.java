package mattparks.mods.space.core.world.gen.pit;

import java.util.Random;

import mattparks.mods.space.core.world.gen.GenerationSettings;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

public class PitStructureStart extends StructureStart {
	public PitStructureStart(GenerationSettings settings, World world, Random random, int x, int z) {
		final int var5 = (x << 4) + 8;
		final int var6 = (z << 4) + 8;
		final PitStructureComponentRoom room = new PitStructureComponentRoom(settings, 0, world, random, var5, 90, var6, 40, 7, 0);

		if (room != null) {
			this.components.add(room);
			room.buildComponent(room, this.components, random);
		}

		this.updateBoundingBox();
	}
}
