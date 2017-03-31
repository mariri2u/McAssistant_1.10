package mariri.mcassistant.config;

import java.util.List;

import com.google.common.collect.Lists;

import mariri.mcassistant.McAssistant;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

public abstract class McAssistantCategoryEntry extends CategoryEntry {
	public McAssistantCategoryEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	protected abstract Configuration getConfig();

	protected abstract String getEntryName();

	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		for (String category : getConfig().getCategoryNames())
		{
			list.addAll(new ConfigElement(getConfig().getCategory(category)).getChildElements());
		}

		return list;
	}

	@Override
	protected GuiScreen buildChildScreen()
	{
		return new GuiConfig(owningScreen, getConfigElements(), owningScreen.modID, getEntryName(),
			configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart,
			I18n.translateToLocal(McAssistant.CONFIG_LANG + getEntryName()), GuiConfig.getAbridgedConfigPath(getConfig().toString()));
	}

	@Override
	public boolean enabled()
	{
		if (childScreen instanceof GuiConfig)
		{
			if (((GuiConfig)childScreen).configElements.isEmpty())
			{
				return false;
			}
		}

		return super.enabled();
	}
}
