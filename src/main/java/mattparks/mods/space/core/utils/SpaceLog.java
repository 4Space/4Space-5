package mattparks.mods.space.core.utils;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.FMLRelaunchLog;
import mattparks.mods.space.core.Constants;

/**
 * A simple logging function class for 4Space.
 */
public class SpaceLog {
	/**
	 * Logs an info message.
	 * 
	 * @param message The message.
	 */
	public static void info(String message) {
		FMLRelaunchLog.log(Constants.MOD_NAME, Level.INFO, message);
	}

	/**
	 * Logs an severe message.
	 * 
	 * @param message The message.
	 */
	public static void severe(String message) {
		FMLRelaunchLog.log(Constants.MOD_NAME, Level.ERROR, message);
	}
}
