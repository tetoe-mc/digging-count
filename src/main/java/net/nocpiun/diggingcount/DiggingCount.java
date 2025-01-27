package net.nocpiun.diggingcount;

import net.fabricmc.api.ModInitializer;

import net.nocpiun.diggingcount.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiggingCount implements ModInitializer {
	public static final String MOD_ID = "digging-count";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static DiggingCountPlugin plugin;

	@Override
	public void onInitialize() {
		Log.setLogger(LOGGER);
		plugin = new DiggingCountPlugin();

		Log.info("Digging Count Plugin initialized.");
	}
}
