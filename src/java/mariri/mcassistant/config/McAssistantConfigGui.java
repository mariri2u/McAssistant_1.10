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
//		List<IConfigElement> list = Lists.newArrayList();
//
//
//		return list;

		List<IConfigElement> list = new ArrayList<IConfigElement>();
//		list.add(new DummyCategoryElement("general", McAssistant.CONFIG_LANG, GeneralConfigEntry.class));
		list.add(new DummyCategoryElement("itemRegister", McAssistant.MODID + ".itemRegister", RegisterConfigEntry.class));

//		ConfigElement elm = new ConfigElement(McAssistant.CONFIG.getCategory(Configuration.CATEGORY_GENERAL));
//
//
//		for(String category : McAssistant.CONFIG.getCategoryNames()){
//			list.addAll(new ConfigElement(McAssistant.CONFIG.getCategory(category)).getChildElements());
//		}

		for(IConfigElement elm :  new ConfigElement(McAssistant.CONFIG.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements()){
			list.add(elm);
		}
//
//		for(IConfigElement elm :  new ConfigElement(McAssistant.config.getCategory(McAssistant.CATEGORY_ITEM_REGISTER)).getChildElements()){
//			list.add(elm);
//		}

		return list;
	}
}
