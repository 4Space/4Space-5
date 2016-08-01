package mattparks.mods.space.core.builder;

/**
 * A interface for creating and registering blocks.
 */
public interface ICoreBlocks {
	/**
	 * Used to create the block objects.
	 */
	public void initBlocks();
	
	/**
	 * Used to set block harvest levels.
	 */
	public void setHarvestLevels();
	
	/**
	 * Used to register the blocks to minecraft, using {@link com.mattparls.space.core.registerBlock(Block, Class)}.
	 */
	public void registerBlocks();
	
	/**
	 * Used to register blocks into an ore dictionary, using OreDictionary.registerOre.
	 */
	public void oreDictRegistration();
}
