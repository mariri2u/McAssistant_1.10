package mariri.mcassistant.config;

import java.util.List;

import mariri.mcassistant.McAssistant;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RegisterConfigEntry extends CategoryEntry{
	public RegisterConfigEntry(GuiConfig config, GuiConfigEntries entryList, IConfigElement element){
		super(config, entryList, element);
	}

	protected List<IConfigElement> getConfigElements(){
		return new ConfigElement(McAssistant.CONFIG.getCategory(McAssistant.CATEGORY_ITEM_REGISTER)).getChildElements();
	}

	@Override
	protected GuiScreen buildChildScreen(){
		return new GuiConfig(
				owningScreen, getConfigElements(), owningScreen.modID, McAssistant.CATEGORY_ITEM_REGISTER,
				configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart,
				configElement.requiresMcRestart() || owningScreen.allRequireMcRestart,
				"itemRegister");
	}
}
