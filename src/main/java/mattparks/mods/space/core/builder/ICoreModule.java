package mattparks.mods.space.core.builder;

import java.io.File;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mattparks.mods.space.core.Constants;
import net.minecraftforge.common.config.Configuration;

/**
 * An abstract class used to creating new mod modules.
 */
public abstract class ICoreModule {
	public String prefixAsset;
	public String prefixTexture;
	public static Configuration configuration;
	
	/**
	 * Creates a new 4Space mod module.
	 * 
	 * @param event The event given on pre init, used to create the configs.
	 * @param prefixAsset The asset prefix to use (EX: "spacevenus").
	 */
	public ICoreModule(FMLPreInitializationEvent event, String prefixAsset) {
		this.prefixAsset = prefixAsset;
		this.prefixTexture = prefixAsset + ":";
		this.configuration = new Configuration(new File(event.getModConfigurationDirectory(), Constants.MOD_NAME + "/" + prefixAsset + ".cfg"));
		
		this.configuration.load();
		loadFromConfig();
		this.configuration.save();
	}
	
	/**
	 * Used to load values from the config.
	 */
	public abstract void loadFromConfig();

	/**
	 * Loads blocks to minecraft.
	 */
	public void loadBlocks() {
		if (getBlocks() != null) {
			getBlocks().initBlocks();
			getBlocks().setHarvestLevels();
			getBlocks().registerBlocks();
			getBlocks().oreDictRegistration();
		}
	}

	/**
	 * Loads items to minecraft.
	 */
	public void loadItems() {
		if (getItems() != null) {
			getItems().initItems();
			getItems().registerItems();
			getItems().registerHarvestLevels();
		}
	}

	/**
	 * The implementation for the planets blocks.
	 * 
	 * @return The planets blocks.
	 */
	public abstract ICoreBlocks getBlocks();

	/**
	 * The implementation for the planets items.
	 * 
	 * @return The planets items.
	 */
	public abstract ICoreItems getItems();
	
	/**
	 * Hides items from NEI.
	 */
	public abstract void hideNEI();
	
	/**
	 * Adds shapeless recipes, used crafters like galacticrafts compressor.
	 */
	public abstract void addShapelessRecipes();
	
	/**
	 * Registers tile entities.
	 */
	public abstract void registerTileEntities();
	
	/**
	 * Registers creatures (mobs).
	 */
	public abstract void registerCreatures();
	
	/**
	 * Registers any other type of entity.
	 */
	public abstract void registerOtherEntities();
	
	/**
	 * Loads crafting and smelting recipes to minecraft.
	 */
	public abstract void loadRecipes();
}