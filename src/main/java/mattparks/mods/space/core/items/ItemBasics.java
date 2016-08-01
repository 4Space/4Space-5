package mattparks.mods.space.core.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mattparks.mods.space.core.SpaceCore;
import mattparks.mods.space.core.proxy.ClientProxy;
import mattparks.mods.space.core.utils.SpaceLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * A class used to create many basic items.
 */
public class ItemBasics extends Item {
	private String unlocalizedName;
	private String texturePrefix;
	private String[] names;
	protected IIcon[] icons;

	public ItemBasics(String unlocalizedName, String texturePrefix, String[] names) {
		super();
		this.unlocalizedName = unlocalizedName;
		this.texturePrefix = texturePrefix;
		this.names = names;
		this.icons = new IIcon[names.length];
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	public void registerItem() {
		SpaceCore.registerItem(this, true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public CreativeTabs getCreativeTab() {
		return SpaceCore.spaceItemsTab;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		return ClientProxy.RARITY_SPACE_ITEM;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		int i = 0;

		for (String name : names) {
			this.icons[i++] = iconRegister.registerIcon(texturePrefix + name);
		}
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		if (this.icons.length > damage) {
			return this.icons[damage];
		}

		return super.getIconFromDamage(damage);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < names.length; i++) {
			par3List.add(new ItemStack(par1, 1, i));
		}
	}
	
	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		if (this.icons.length > par1ItemStack.getItemDamage()) {
			return "item." + names[par1ItemStack.getItemDamage()];
		}

		return "unnamed";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		// if (par1ItemStack != null && par1ItemStack.getItemDamage() == 8) {
		// par3List.add(SpaceUtil.translate("item.tier4.desc"));
		// }
	}

	@Override
	public int getMetadata(int par1) {
		return par1;
	}

	public int getIndex(String itemName) {
		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(itemName)) {
				return i;
			}
		}

		return -1;
	}
}
