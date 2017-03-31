package mariri.mcassistant.config;

import mariri.mcassistant.McAssistant;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigGuiHandler {

	public static ConfigGuiHandler INSTANCE = new ConfigGuiHandler();

	private ConfigGuiHandler(){}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e){
		if(e.getModID().equals(McAssistant.MODID)){
			McAssistant.INSTANCE.syncConfig();
			McAssistant.INSTANCE.registOreDictionary();
		}
	}
}
