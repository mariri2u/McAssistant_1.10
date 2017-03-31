package mariri.mcassistant.config;

import java.util.ArrayList;
import java.util.List;

import mariri.mcassistant.McAssistant;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class McAssistantConfigGui extends GuiConfig {
	public static boolean detailInfo = true;
	public static boolean instantFilter = true;

	public McAssistantConfigGui(GuiScreen parent){
		super(parent, getConfigElements(), McAssistant.MODID, false, false, I18n.translateToLocal(McAssistant.CONFIG_LANG));
	}

	private static List<IConfigElement> getConfigElements(){
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.add(new DummyCategoryElement("itemRegister", McAssistant.MODID + ".itemRegister", RegisterConfigEntry.class));

		for(IConfigElement elm :  new ConfigElement(McAssistant.CONFIG.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements()){
			list.add(elm);
		}

		return list;
	}
}
