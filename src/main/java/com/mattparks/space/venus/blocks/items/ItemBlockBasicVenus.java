package com.mattparks.space.venus.blocks.items;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import com.mattparks.space.core.proxy.ClientProxy;

public class ItemBlockBasicVenus extends ItemBlock {
	public static enum BlocksBasic {
		SURFACE_ROCK("venusSurfaceRock", 1.25f), 
		SUB_ROCK("venusSubRock", 1.0f), 
		COBBLESTONE("venusCobblestone", 2.5f), 
		ROCK("venusRock", 1.5f, BlocksBasic.COBBLESTONE), 
		ORE_SULFUR("venusOreSulfur", 2.5f), 
		ORE_URANIUM("venusOreUranium", 2.5f), 
		ORE_RUBY("venusOreRuby", 2.5f), 
		ORE_CRYSTAL("venusOreCrystal", 2.5f), 
		ORE_TIN("venusOreTin", 2.5f), 
		ORE_COPPER("venusOreCopper", 2.5f), 
		ORE_IRON("venusOreIron", 2.5f), 
		ORE_COAL("venusOreCoal", 2.5f), 
		BRICK_DUNGEON("venusBrickDungeon", 25.0f, 40.0f);
		
		public String type;
		public float hardness;
		public float resistance;
		public BlocksBasic damagedDrop;
		
		BlocksBasic(String type, float hardness, float resistance) {
			this.type = type;
			this.hardness = hardness;
			this.resistance = resistance;
			this.damagedDrop = null;
		}

		BlocksBasic(String type, float hardness, BlocksBasic damagedDrop) {
			this.type = type;
			this.hardness = hardness;
			this.resistance = -1;
			this.damagedDrop = damagedDrop;
		}
		
		BlocksBasic(String type, float hardness) {
			this.type = type;
			this.hardness = hardness;
			this.resistance = -1;
			this.damagedDrop = null;
		}
	}
	
	public ItemBlockBasicVenus(Block par1) {
		super(par1);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return ClientProxy.RARITY_SPACE_ITEM;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		int meta = itemstack.getItemDamage();

		if (meta < 0 || meta >= BlocksBasic.values().length) {
			meta = 0;
		}

		return super.getUnlocalizedName() + "." + BlocksBasic.values()[meta].type;
	}
}
