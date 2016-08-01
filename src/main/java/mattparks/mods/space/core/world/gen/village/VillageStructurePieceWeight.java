package mattparks.mods.space.core.world.gen.village;

public class VillageStructurePieceWeight {
	public Class<? extends VillageStructureComponent> villagePieceClass;
	public final int villagePieceWeight;
	public int villagePiecesSpawned;
	public int villagePiecesLimit;

	public VillageStructurePieceWeight(Class<? extends VillageStructureComponent> par1Class, int par2, int par3) {
		this.villagePieceClass = par1Class;
		this.villagePieceWeight = par2;
		this.villagePiecesLimit = (int) (par3 / 1.5D);
	}

	public boolean canSpawnMoreVillagePiecesOfType(int par1) {
		return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
	}

	public boolean canSpawnMoreVillagePieces() {
		return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
	}
}
