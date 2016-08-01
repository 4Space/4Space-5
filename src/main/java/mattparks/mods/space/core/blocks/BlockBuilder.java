package mattparks.mods.space.core.blocks;

import net.minecraft.item.Item;

/**
 * A class used to setup values for simple blocks.
 */
public class BlockBuilder {
	public String type = null;
	public float hardness = 1.0f;
	public float resistance = -1.0f;
	public String damagedDrop = null;
	public boolean isTerraformable = false;
	public boolean isPlantable = false;
	public boolean sealable = false;
	public boolean isValueable = false;
	public Item dropItem = null;
	public float smokeFactor = 0.0f;
	
	public BlockBuilder(String type) {
		this.type = type;
	}

	public BlockBuilder setHardness(float hardness) {
		this.hardness = hardness;
		return this;
	}

	public BlockBuilder setResistance(float resistance) {
		this.resistance = resistance;
		return this;
	}

	public BlockBuilder setDamagedDrop(String damagedDrop) {
		this.damagedDrop = damagedDrop;
		return this;
	}

	public BlockBuilder setTerraformable(boolean isTerraformable) {
		this.isTerraformable = isTerraformable;
		return this;
	}

	public BlockBuilder setPlantable(boolean isPlantable) {
		this.isPlantable = isPlantable;
		return this;
	}

	public BlockBuilder setSealable(boolean sealable) {
		this.sealable = sealable;
		return this;
	}

	public BlockBuilder setValueable(boolean isValueable) {
		this.isValueable = isValueable;
		return this;
	}

	public BlockBuilder setDropItem(Item dropItem) {
		this.dropItem = dropItem;
		return this;
	}

	public BlockBuilder setSmokes(float smokeFactor) {
		this.smokeFactor = smokeFactor;
		return this;
	}
}
