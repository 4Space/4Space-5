package mattparks.mods.space.core.nei;

import codechicken.nei.api.IConfigureNEI;
import mattparks.mods.space.core.Constants;
import mattparks.mods.space.core.SpaceCore;

/**
 * A NEI config loader for 4Space.
 */
public class SpaceNEIConfig implements IConfigureNEI {
	@Override
	public void loadConfig() {
		SpaceCore.registerHideNEI();
	}

	@Override
	public String getName() {
		return Constants.MOD_NAME + " NEI-Plugin";
	}

	@Override
	public String getVersion() {
		return Constants.VERSION;
	}
}
