package mattparks.mods.space.mercury.items;

import mattparks.mods.space.core.builder.ICoreItems;
import mattparks.mods.space.core.items.ItemBasics;
import mattparks.mods.space.core.items.ItemSetArmour;
import mattparks.mods.space.core.items.ItemSetTools;
import mattparks.mods.space.mercury.MercuryCore;
import net.minecraftforge.common.util.EnumHelper;

public class MercuryItems implements ICoreItems {
	public static ItemBasics mercuryItem;
	public static ItemSetArmour armourIridium;
	public static ItemSetTools toolsIridium;
	
	@Override
	public void initItems() {
		mercuryItem = new ItemBasics("mercuryItem", MercuryCore.instance.prefixTexture, new String[] {
				"iridiumCrystals",
				"iridiumPlate",
		});
		armourIridium = new ItemSetArmour(MercuryCore.instance.prefixTexture, EnumHelper.addArmorMaterial("iridium", 15, new int[] { 1, 4, 2, 1 }, 7));
		toolsIridium = new ItemSetTools(MercuryCore.instance.prefixTexture, EnumHelper.addToolMaterial("iridium", 3, 416, 6.0f, 1, 2));
	}

	@Override
	public void registerItems() {
		mercuryItem.registerItem();
		armourIridium.registerItem();
		toolsIridium.registerItem();
	}

	@Override
	public void registerHarvestLevels() {
	}

}
