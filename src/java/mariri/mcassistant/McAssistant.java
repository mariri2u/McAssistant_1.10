package mariri.mcassistant;

import mariri.mcassistant.config.ConfigGuiHandler;
import mariri.mcassistant.handler.BlockBreakEventHandler;
import mariri.mcassistant.handler.EntityInteractHandler;
import mariri.mcassistant.handler.EntityJoinWorldHandler;
import mariri.mcassistant.handler.EntityMountHandler;
import mariri.mcassistant.handler.PlayerClickHandler;
import mariri.mcassistant.helper.Comparator;
import mariri.mcassistant.helper.CropReplanter;
import mariri.mcassistant.helper.Lib;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;


@Mod(
		modid = McAssistant.MODID,
		version = McAssistant.VERSION,
		acceptableRemoteVersions = "*",
		guiFactory = "mariri.mcassistant.config.McAssistantGuiFactory"
)
public class McAssistant {

        public static final String MODID = "McAssistant";
        public static final String VERSION = "1.10.2-1.2";
        public static final String CONFIG_LANG = "mcassistant.config";
        public static Configuration CONFIG;

        @Metadata(MODID)
        public static ModMetadata MOD_METADATA;

        @Instance(MODID)
        public static McAssistant INSTANCE;

        private static final String CATEGORY_BREEDASSIST = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "breedassist";
        private static final String CATEGORY_CROPASSIST = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "cropassist";
        private static final String CATEGORY_CROPASSIST_AREA = CATEGORY_CROPASSIST + Configuration.CATEGORY_SPLITTER + "area";
        private static final String CATEGORY_CROPASSIST_AREAPLUS = CATEGORY_CROPASSIST + Configuration.CATEGORY_SPLITTER + "areaplus";
        private static final String CATEGORY_CUTDOWN = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "cutdown";
        private static final String CATEGORY_CUTDOWN_CHAIN = CATEGORY_CUTDOWN + Configuration.CATEGORY_SPLITTER + "chain";
        private static final String CATEGORY_FLATASSIST = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "flatassist";
        private static final String CATEGORY_FLATASSIST_HARVESTABLE = CATEGORY_FLATASSIST + Configuration.CATEGORY_SPLITTER + "harvestable";
        private static final String CATEGORY_FLATASSIST_DIRT = CATEGORY_FLATASSIST + Configuration.CATEGORY_SPLITTER + "dirt";
        private static final String CATEGORY_FLATASSIST_STONE = CATEGORY_FLATASSIST + Configuration.CATEGORY_SPLITTER + "stone";
        private static final String CATEGORY_FLATASSIST_WOOD = CATEGORY_FLATASSIST + Configuration.CATEGORY_SPLITTER + "wood";
        private static final String CATEGORY_LEAVEASSIST = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "leaveassist";
        private static final String CATEGORY_LEAVEASSIST_AREAPLUS = CATEGORY_LEAVEASSIST + Configuration.CATEGORY_SPLITTER + "areaplus";
        private static final String CATEGORY_MINEASSIST = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "mineassist";
        private static final String CATEGORY_BEDASSIST = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "bedassist";
        private static final String CATEGORY_CULTIVATEASSIST = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "cultivateassist";
        private static final String CATEGORY_CULTIVATEASSIST_AREAPLUS = CATEGORY_CULTIVATEASSIST + Configuration.CATEGORY_SPLITTER + "areaplus";
        private static final String CATEGORY_SHEARASSIST = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "shearassist";

        private static final String CATEGORY_MISC = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "misc";

        public static final String CATEGORY_ITEM_REGISTER = "ItemRegister";
        private static final String CATEGORY_AXE = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "axe";
        private static final String CATEGORY_CROP = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "crop";
        private static final String CATEGORY_DIRT = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "dirt";
        private static final String CATEGORY_HOE = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "hoe";
        private static final String CATEGORY_LOG = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "log";
        private static final String CATEGORY_ORE = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "ore";
        private static final String CATEGORY_PICKAXE = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "pickaxe";
        private static final String CATEGORY_SEED = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "seed";
        private static final String CATEGORY_SHOVEL = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "shovel";
        private static final String CATEGORY_STONE = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "stone";
        private static final String CATEGORY_UNIFY = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "unify";
        private static final String CATEGORY_WOOD = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "wood";
        private static final String CATEGORY_SAPLING = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "sapling";
        private static final String CATEGORY_LEAVE = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "leave";
        private static final String CATEGORY_FEED = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "feed";
        private static final String CATEGORY_SHEAR = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "shear";
        private static final String CATEGORY_MOUNT = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "mount";
        private static final String CATEGORY_ORE_DICTIONARY = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "oreDictionary";

        private static final String BR = System.getProperty("line.separator");
        private static final String COMMNET_REQUIRE_TOOL_LEVEL =
        		"Enable the function only for tools greater the specified mining level" + BR +
        		"Wood and Gold: 0, Stone: 1, Iron: 2, Diamond: 3" + BR +
        		"(You can specify lower and upper limits separated by commas.)";
        private static final String COMMENT_AFFECT_POTION =
        		"When of collective destruction, what potion effects given to player" + BR +
        		"(Multiple potion effects are possible, separated by commas)" + BR +
        		"StatusID:Level:Duration (,StatusID:Level:Duration,...)";
        private static final String COMMENT_REQUIRE_HUNGER = "Threshold of satiety to enable collective destruction (0-20)";
        private static final String COMMENT_REQUIRE_POTION_LEVEL = "Potion effect and level enabling collective destruction" + BR + "StatusID:Level";
        private static final String COMMENT_REQUIRE_POTION_ID =
        		"Potion effect is enabling to collective destruction" + BR +
        		"As the effect level rised the destruction range expands" + BR +
        		"(1: 3*3, 2: 5*5, ...)";
        private static final String COMMENT_REQUIRE_ENCHANT_LEVEL = "Enchant effect and level enabling collective destruction" + BR + "EnchantID:Level";
        private static final String COMMENT_REQUIRE_ENCHANT_ID =
        		"Enchant effect is enabling to collective destruction" + BR +
        		"As the effect level rised the destruction range expands" + BR +
        		"(1: 3*3, 2: 5*5, ...)";
        private static final String COMMENT_MAX_RADIUS = "It limits the maximum range of collective destruction" + BR + "(1: 3*3, 2: 5*5, ...)";
        private static final String COMMENT_BREAK_BELOW = "Whether to break below blocks on the target by collective destruction";
        private static final String COMMENT_BREAK_ANYTHING = "Whether or not to break another type blocks too";


        private String[] registOreDictionaryList;

        @EventHandler
        public void preInit(FMLPreInitializationEvent event) {
        	loadInfo(MOD_METADATA);

            CONFIG = new Configuration(event.getSuggestedConfigurationFile());
	        CONFIG.load();

	        syncConfig();
        }

        @EventHandler
        public void postInit(FMLPostInitializationEvent event) {
        }

        @EventHandler
        public void init(FMLInitializationEvent event) {

        	// Cutdown, Mineassist, Flatassist
        	if(BlockBreakEventHandler.isEventEnable()){
        		MinecraftForge.EVENT_BUS.register(BlockBreakEventHandler.INSTANCE);
        	}

        	// Unifier, MountAssist
        	if(EntityJoinWorldHandler.isEventEnable()){
        		MinecraftForge.EVENT_BUS.register(EntityJoinWorldHandler.INSTANCE);
        	}

        	// MountAssist
        	if(EntityMountHandler.isEventEnable()){
        		MinecraftForge.EVENT_BUS.register(EntityMountHandler.INSTANCE);
        	}

        	// TorchAssist, Bedassist, Cropassist, Leaveassist
        	if(PlayerClickHandler.isEventEnable()){
        		MinecraftForge.EVENT_BUS.register(PlayerClickHandler.INSTANCE);
        	}

        	// BreedAssist
        	if(EntityInteractHandler.isEventEnable()){
        		MinecraftForge.EVENT_BUS.register(EntityInteractHandler.INSTANCE);
        	}

        	registOreDictionary();

        	// Config GUI
        	MinecraftForge.EVENT_BUS.register(ConfigGuiHandler.INSTANCE);
        }

        public void registOreDictionary(){
        	// OreDictionary Regist
        	try{
	        	for(String value : registOreDictionaryList){
	    			if(!"".equals(value)){
	        			String[] s = value.split(":");
	        			String key = s[0];
	        			String modid = s[1];
	        			String name = s[2];
	        			int meta = Integer.parseInt(s[3]);
	        			ItemStack item = new ItemStack(GameRegistry.findItem(modid, name));
	        			item.setItemDamage(meta);
	        			OreDictionary.registerOre(key, item);
	        		}
	        	}
        	}catch(NullPointerException e){
        		e.printStackTrace();
        	}
        }

        public void syncConfig(){
	        Property prop;

	        // CutdownAssist
	        BlockBreakEventHandler.CUTDOWN_ENABLE = CONFIG.get(Configuration.CATEGORY_GENERAL, "cutdownEnable", true,
	        		"Assist to lumberjack by any axes").getBoolean(true);
	        BlockBreakEventHandler.CUTDOWN_FROM_TOP_ENABLE =
	        		CONFIG.get(Configuration.CATEGORY_GENERAL, "cutdownBreakFromTopEnable", true,
	        				"Whether enable to break wood in order from the top").getBoolean(true);
	        BlockBreakEventHandler.CUTDOWN_CHAIN = CONFIG.get(Configuration.CATEGORY_GENERAL, "cutdownChainEnable", true,
	        		"Axe that given the efficiency enchant is enable to collective destruction").getBoolean(true);
	        CONFIG.addCustomCategoryComment(CATEGORY_CUTDOWN, "Assist to lumberjack by any axes");
	        BlockBreakEventHandler.CUTDOWN_MAX_DISTANCE = CONFIG.get(CATEGORY_CUTDOWN, "maxRadius", 30,
	        		COMMENT_MAX_RADIUS).getInt();
	        BlockBreakEventHandler.CUTDOWN_BELOW =  CONFIG.get(CATEGORY_CUTDOWN, "breakBelow", false,
	        		COMMENT_BREAK_BELOW).getBoolean(false);
	        BlockBreakEventHandler.CUTDOWN_ONLY_ROOT = CONFIG.get(CATEGORY_CUTDOWN, "onlyRoot", true,
	        		"Enable to assist lumberjack only time of breaking root log (on DIRT kind of  block only)").getBoolean(true);
	        CONFIG.addCustomCategoryComment(CATEGORY_CUTDOWN_CHAIN, "Enchanted efficiency axes effect wide range");
	        BlockBreakEventHandler.CUTDOWN_CHAIN_REQUIRE_POTION_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_CUTDOWN_CHAIN, "requirePotionLevel", "",
	        		COMMENT_REQUIRE_POTION_LEVEL).getString(), ":");
	        BlockBreakEventHandler.CUTDOWN_CHAIN_AFFECT_POTION = Lib.stringToInt(CONFIG.get(CATEGORY_CUTDOWN_CHAIN, "affectPotion", "",
	        		COMMENT_AFFECT_POTION).getString(), ",", ":");
	        BlockBreakEventHandler.CUTDOWN_CHAIN_REQUIRE_HUNGER = CONFIG.get(CATEGORY_CUTDOWN_CHAIN, "requireHunger", 0,
	        		COMMENT_REQUIRE_HUNGER).getInt();
	        BlockBreakEventHandler.CUTDOWN_CHAIN_REQUIRE_TOOL_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_CUTDOWN_CHAIN, "requireToolLevel", "2",
	        		COMMNET_REQUIRE_TOOL_LEVEL).getString(), ":");
	        BlockBreakEventHandler.CUTDOWN_CHAIN_REQUIRE_ENCHANT_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_CUTDOWN_CHAIN, "requireEnchantLevel", "32:1",
	        		COMMENT_REQUIRE_ENCHANT_LEVEL).getString(), ":");
	        BlockBreakEventHandler.CUTDOWN_CHAIN_BREAK_LEAVES = CONFIG.get(CATEGORY_CUTDOWN_CHAIN, "breakLeaves", true,
	        		"Whether the leaf blocks also to be destroying too").getBoolean(true);
	        BlockBreakEventHandler.CUTDOWN_CHAIN_REPLANT = CONFIG.get(CATEGORY_CUTDOWN_CHAIN, "autoReplant", true,
	        		"Whether or not to replant the dropped sapling automatically when the leaf block are harvested").getBoolean(true);
	        BlockBreakEventHandler.CUTDOWN_CHAIN_MAX_HORIZONAL_DISTANCE = CONFIG.get(CATEGORY_CUTDOWN_CHAIN, "maxHorizonalRadius", 2,
	        		"Specify the horizonal limit distance when leaf block breaking").getInt();

	        // MineAssist
	        BlockBreakEventHandler.MINEASSIST_ENABLE = CONFIG.get(Configuration.CATEGORY_GENERAL, "mineassistEnable", true,
	        		"Collective destruct many ores (Require greater than iron pickaxe)").getBoolean(true);
	        CONFIG.addCustomCategoryComment(CATEGORY_MINEASSIST, "Collective destruct many ores (Require greater than iron pickaxe)");
	        BlockBreakEventHandler.MINEASSIST_MAX_DISTANCE = CONFIG.get(CATEGORY_MINEASSIST, "maxRadius", 10,
	        		COMMENT_MAX_RADIUS).getInt();
	        BlockBreakEventHandler.MINEASSIST_REQUIRE_POTION_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_MINEASSIST, "requirePotionLevel", "",
	        		COMMENT_REQUIRE_POTION_LEVEL).getString(), ":");
	        BlockBreakEventHandler.MINEASSIST_AFFECT_POTION = Lib.stringToInt(CONFIG.get(CATEGORY_MINEASSIST, "affectPotion", "17:1:15",
	        		COMMENT_AFFECT_POTION).getString(), ",", ":");
	        BlockBreakEventHandler.MINEASSIST_REQUIRE_HUNGER = CONFIG.get(CATEGORY_MINEASSIST, "requireHunger", 15,
	        		COMMENT_REQUIRE_HUNGER).getInt();
	        BlockBreakEventHandler.MINEASSIST_REQUIRE_TOOL_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_MINEASSIST, "requireToolLevel", "2",
	        		COMMNET_REQUIRE_TOOL_LEVEL).getString(), ":");
	        BlockBreakEventHandler.MINEASSIST_REQUIRE_ENCHANT_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_MINEASSIST, "requireEnchantLevel", "",
	        		COMMENT_REQUIRE_ENCHANT_LEVEL).getString(), ":");

	        // FlatAssist
	        CONFIG.addCustomCategoryComment(CATEGORY_FLATASSIST, "Break any blocks in wide range when given haste potion effect");
	        BlockBreakEventHandler.FLATASSIST_ENABLE = CONFIG.get(Configuration.CATEGORY_GENERAL, "flatassistEnable", true,
	        		"Break any blocks in wide range when given haste potion effect").getBoolean(true);

	        CONFIG.addCustomCategoryComment(CATEGORY_FLATASSIST_HARVESTABLE, "For all harvestable blocks");
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_ENABLE = CONFIG.get(CATEGORY_FLATASSIST, "flatassistHarvestableEnable", true,
	        		"For all harvestable blocks").getBoolean(true);
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_REQUIRE_POTION_ID = CONFIG.get(CATEGORY_FLATASSIST_HARVESTABLE, "requirePotionId", 3,
	        		COMMENT_REQUIRE_POTION_ID).getInt();
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_AFFECT_POTION = Lib.stringToInt(CONFIG.get(CATEGORY_FLATASSIST_HARVESTABLE, "affectPotion", "",
	        		COMMENT_AFFECT_POTION).getString(), ",", ":");
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_REQUIRE_HUNGER = CONFIG.get(CATEGORY_FLATASSIST_HARVESTABLE, "requireHunger", 0,
	        		COMMENT_REQUIRE_HUNGER).getInt();
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_REQUIRE_TOOL_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_FLATASSIST_HARVESTABLE, "requireToolLevel", "2",
	        		COMMNET_REQUIRE_TOOL_LEVEL).getString(), ":");
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_REQUIRE_ENCHANT_ID = CONFIG.get(CATEGORY_FLATASSIST_HARVESTABLE, "requireEnchantId", 0,
	        		COMMENT_REQUIRE_ENCHANT_ID).getInt();
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_BELOW = CONFIG.get(CATEGORY_FLATASSIST_HARVESTABLE, "breakBelow", false,
	        		COMMENT_BREAK_BELOW).getBoolean(false);
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_MAX_RADIUS = CONFIG.get(CATEGORY_FLATASSIST_HARVESTABLE, "maxRadius", 0,
	        		COMMENT_MAX_RADIUS).getInt();
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_BREAK_ANYTHING = CONFIG.get(CATEGORY_FLATASSIST_HARVESTABLE, "breakAnything", false,
	        		COMMENT_BREAK_ANYTHING).getBoolean(false);

	        CONFIG.addCustomCategoryComment(CATEGORY_FLATASSIST_DIRT, "Only DIRT category blocks");
	        BlockBreakEventHandler.FLATASSIST_DIRT_ENABLE = CONFIG.get(CATEGORY_FLATASSIST, "flatassistDirtEnable", false,
	        		"Only DIRT category blocks").getBoolean(false);
	        BlockBreakEventHandler.FLATASSIST_DIRT_REQUIRE_POTION_ID = CONFIG.get(CATEGORY_FLATASSIST_DIRT, "requirePotionId", 3,
	        		COMMENT_REQUIRE_POTION_ID).getInt();
	        BlockBreakEventHandler.FLATASSIST_DIRT_AFFECT_POTION = Lib.stringToInt(CONFIG.get(CATEGORY_FLATASSIST_DIRT, "affectPotion", "",
	        		COMMENT_AFFECT_POTION).getString(), ",", ":");
	        BlockBreakEventHandler.FLATASSIST_DIRT_REQUIRE_HUNGER = CONFIG.get(CATEGORY_FLATASSIST_DIRT, "requireHunger", 0,
	        		COMMENT_REQUIRE_HUNGER).getInt();
	        BlockBreakEventHandler.FLATASSIST_DIRT_REQUIRE_TOOL_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_FLATASSIST_DIRT, "requireToolLevel", "2",
	        		COMMNET_REQUIRE_TOOL_LEVEL).getString(), ":");
	        BlockBreakEventHandler.FLATASSIST_DIRT_REQUIRE_ENCHANT_ID = CONFIG.get(CATEGORY_FLATASSIST_DIRT, "requireEnchantId", 0,
	        		COMMENT_REQUIRE_ENCHANT_ID).getInt();
	        BlockBreakEventHandler.FLATASSIST_DIRT_BELOW = CONFIG.get(CATEGORY_FLATASSIST_DIRT, "breakBelow", false,
	        		COMMENT_BREAK_BELOW).getBoolean(false);
	        BlockBreakEventHandler.FLATASSIST_DIRT_MAX_RADIUS = CONFIG.get(CATEGORY_FLATASSIST_DIRT, "maxRadius", 0,
	        		COMMENT_MAX_RADIUS).getInt();
	        BlockBreakEventHandler.FLATASSIST_DIRT_BREAK_ANYTHING = CONFIG.get(CATEGORY_FLATASSIST_DIRT, "breakAnything", false,
	        		COMMENT_BREAK_ANYTHING).getBoolean(false);

	        CONFIG.addCustomCategoryComment(CATEGORY_FLATASSIST_STONE, "Only STONE category blocks");
	        BlockBreakEventHandler.FLATASSIST_STONE_ENABLE = CONFIG.get(CATEGORY_FLATASSIST, "flatassistStoneEnable", false,
	        		"Only STONE category blocks").getBoolean(false);
	        BlockBreakEventHandler.FLATASSIST_STONE_REQUIRE_POTION_ID = CONFIG.get(CATEGORY_FLATASSIST_STONE, "requirePotionId", 3,
	        		COMMENT_REQUIRE_POTION_ID).getInt();
	        BlockBreakEventHandler.FLATASSIST_STONE_AFFECT_POTION = Lib.stringToInt(CONFIG.get(CATEGORY_FLATASSIST_STONE, "affectPotion", "",
	        		COMMENT_AFFECT_POTION).getString(), ",", ":");
	        BlockBreakEventHandler.FLATASSIST_STONE_REQUIRE_HUNGER = CONFIG.get(CATEGORY_FLATASSIST_STONE, "requireHunger", 0,
	        		COMMENT_REQUIRE_HUNGER).getInt();
	        BlockBreakEventHandler.FLATASSIST_STONE_REQUIRE_TOOL_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_FLATASSIST_STONE, "requireToolLevel", "2",
	        		COMMNET_REQUIRE_TOOL_LEVEL).getString(), ":");
	        BlockBreakEventHandler.FLATASSIST_STONE_REQUIRE_ENCHANT_ID = CONFIG.get(CATEGORY_FLATASSIST_STONE, "requireEnchantId", 0,
	        		COMMENT_REQUIRE_ENCHANT_ID).getInt();;
	        BlockBreakEventHandler.FLATASSIST_STONE_BELOW = CONFIG.get(CATEGORY_FLATASSIST_STONE, "breakBelow", false,
	        		COMMENT_BREAK_BELOW).getBoolean(false);
	        BlockBreakEventHandler.FLATASSIST_STONE_MAX_RADIUS = CONFIG.get(CATEGORY_FLATASSIST_STONE, "maxRadius", 0,
	        		COMMENT_MAX_RADIUS).getInt();
	        BlockBreakEventHandler.FLATASSIST_STONE_BREAK_ANYTHING = CONFIG.get(CATEGORY_FLATASSIST_STONE, "breakAnything", false,
	        		COMMENT_BREAK_ANYTHING).getBoolean(false);

	        CONFIG.addCustomCategoryComment(CATEGORY_FLATASSIST_WOOD, "Only WOOD category blocks");
	        BlockBreakEventHandler.FLATASSIST_WOOD_ENABLE = CONFIG.get(CATEGORY_FLATASSIST, "flatassistWoodEnable", false,
	        		"Only WOOD category blocks").getBoolean(false);
	        BlockBreakEventHandler.FLATASSIST_WOOD_REQUIRE_POTION_ID = CONFIG.get(CATEGORY_FLATASSIST_WOOD, "requirePotionId", 3,
	        		COMMENT_REQUIRE_POTION_ID).getInt();
	        BlockBreakEventHandler.FLATASSIST_WOOD_AFFECT_POTION = Lib.stringToInt(CONFIG.get(CATEGORY_FLATASSIST_WOOD, "affectPotion", "",
	        		COMMENT_AFFECT_POTION).getString(), ",", ":");
	        BlockBreakEventHandler.FLATASSIST_WOOD_REQUIRE_HUNGER = CONFIG.get(CATEGORY_FLATASSIST_WOOD, "requireHunger", 0,
	        		COMMENT_REQUIRE_HUNGER).getInt();
	        BlockBreakEventHandler.FLATASSIST_WOOD_REQUIRE_TOOL_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_FLATASSIST_WOOD, "requireToolLevel", "2:10",
	        		COMMNET_REQUIRE_TOOL_LEVEL).getString(), ":");
	        BlockBreakEventHandler.FLATASSIST_WOOD_REQUIRE_ENCHANT_ID = CONFIG.get(CATEGORY_FLATASSIST_WOOD, "requireEnchantId", 0,
	        		COMMENT_REQUIRE_ENCHANT_ID).getInt();
	        BlockBreakEventHandler.FLATASSIST_WOOD_BELOW = CONFIG.get(CATEGORY_FLATASSIST_WOOD, "breakBelow", false,
	        		COMMENT_BREAK_BELOW).getBoolean(false);
	        BlockBreakEventHandler.FLATASSIST_WOOD_MAX_RADIUS = CONFIG.get(CATEGORY_FLATASSIST_WOOD, "maxRadius", 0,
	        		COMMENT_MAX_RADIUS).getInt();
	        BlockBreakEventHandler.FLATASSIST_WOOD_BREAK_ANYTHING = CONFIG.get(CATEGORY_FLATASSIST_WOOD, "breakAnything", false,
	        		COMMENT_BREAK_ANYTHING).getBoolean(false);

	        // CropAssist
	        PlayerClickHandler.CROPASSIST_ENABLE = CONFIG.get(Configuration.CATEGORY_GENERAL, "cropassistEnable", true,
	        		"Auto replanting crops breaked by hoe").getBoolean(true);
	        CONFIG.addCustomCategoryComment(CATEGORY_CROPASSIST, "Auto replanting crops breaked by hoe");
	        PlayerClickHandler.CROPASSIST_REQUIRE_TOOL_LEVEL = CONFIG.get(CATEGORY_CROPASSIST, "requireToolLevel", 0,
	        		COMMNET_REQUIRE_TOOL_LEVEL).getInt();
	        CONFIG.addCustomCategoryComment(CATEGORY_CROPASSIST_AREA, "Greater than iron hoes effect wide range");
	        PlayerClickHandler.CROPASSIST_AREA_ENABLE = CONFIG.get(CATEGORY_CROPASSIST_AREA, "areaEnable", true,
	        		"Greater than iron hoes effect wide range").getBoolean(true);
	        PlayerClickHandler.CROPASSIST_AREA_REQUIRE_TOOL_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_CROPASSIST_AREA, "requireToolLevel", "2",
	        		COMMNET_REQUIRE_TOOL_LEVEL).getString(), ":");
	        PlayerClickHandler.CROPASSIST_AREA_AFFECT_POTION = Lib.stringToInt(CONFIG.get(CATEGORY_CROPASSIST, "affectPotion", "",
	        		COMMENT_AFFECT_POTION).getString(), ",", ":");
	        CropReplanter.CROPASSIST_SUPLY = CONFIG.get(CATEGORY_CROPASSIST, "suplyFromInventory", true,
	        		"Whether to use a seed in the inventory when not dropped seeds (Wheat only)").getBoolean(true);
	        CropReplanter.CROPASSIST_AUTOCRAFT = CONFIG.get(CATEGORY_CROPASSIST, "autoCraft", true,
	        		"Whether to do automatic craft from crop to seed one time for replanting").getBoolean(true);
	        PlayerClickHandler.CROPASSIST_AREAPLUS_ENABLE =CONFIG.get(CATEGORY_CROPASSIST_AREAPLUS, "areaPlusEnable", true,
	        		"Greater than diamond hoes effect wide range").getBoolean(true);
	        PlayerClickHandler.CROPASSIST_AREAPLUS_REQUIRE_TOOL_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_CROPASSIST_AREAPLUS, "requireToolLevel", "3",
	        		COMMNET_REQUIRE_TOOL_LEVEL).getString(), ":");

	        // TorchAssist
	        PlayerClickHandler.TORCHASSIST_ENABLE = CONFIG.get(Configuration.CATEGORY_GENERAL, "torchassistEnable", false,
	        		"Place torch by pickaxe and shovel on right click").getBoolean(false);

	        // LeaveAssist
	        PlayerClickHandler.LEAVEASSIST_ENABLE = CONFIG.get(Configuration.CATEGORY_GENERAL, "leaveassistEnable", true,
	        		"Remove wide range leaves by axe on left click").getBoolean(true);
	        CONFIG.addCustomCategoryComment(CATEGORY_LEAVEASSIST, "Remove wide range leaves by axe on left click");
	        PlayerClickHandler.LEAVEASSIST_AFFECT_POTION = Lib.stringToInt(CONFIG.get(CATEGORY_LEAVEASSIST, "affectPotion", "",
	        		COMMENT_AFFECT_POTION).getString(), ",", ":");
	        CONFIG.addCustomCategoryComment(CATEGORY_LEAVEASSIST_AREAPLUS, "Greater than diamond axes effect more wide range");
	        PlayerClickHandler.LEAVEASSIST_AREAPLUS_ENABLE = CONFIG.get(CATEGORY_LEAVEASSIST_AREAPLUS, "areaPlusEnable", true,
	        		"Greater than diamond axes effect more wide range").getBoolean(true);
	        PlayerClickHandler.LEAVEASSIST_AREAPLUS_REQUIRE_TOOL_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_LEAVEASSIST_AREAPLUS, "requireToolLevel", "3",
	        		COMMNET_REQUIRE_TOOL_LEVEL).getString(), ":");

	        // BedAssist
	        PlayerClickHandler.BEDASSIST_ENABLE = CONFIG.get(Configuration.CATEGORY_GENERAL, "bedassistEnable", true,
	        		"Set respawn point by bed in anytime").getBoolean(true);
	        CONFIG.addCustomCategoryComment(CATEGORY_BEDASSIST, "Set respawn point by bed in anytime");
	        PlayerClickHandler.BEDASSIST_SET_RESPAWN_ANYTIME = CONFIG.get(CATEGORY_BEDASSIST, "setRespawnAnytimeEnable", true,
	        		"Set respawn point by bed in anytime").getBoolean(true);
	        PlayerClickHandler.BEDASSIST_NO_SLEEP = CONFIG.get(CATEGORY_BEDASSIST, "noSleepEnable", false,
	        		"Sleepless in bed").getBoolean(false);

	        // BreedAssist
	        EntityInteractHandler.BREEDASSIST_ENABLE = CONFIG.get(Configuration.CATEGORY_GENERAL, "breedassistEnable", true,
	        		"Animal breeding can wide range on right click by feeds").getBoolean(true);
	        CONFIG.addCustomCategoryComment(CATEGORY_BREEDASSIST, "Animal breeding can  wide range on right click by feeds");
	        EntityInteractHandler.BREEDASSIST_RADIUS = CONFIG.get(CATEGORY_BREEDASSIST, "maxRadius", 2,
	        		COMMENT_MAX_RADIUS).getInt();
	        EntityInteractHandler.BREEDASSIST_AFFECT_POTION = Lib.stringToInt(CONFIG.get(CATEGORY_BREEDASSIST, "affectPotion", "",
	        		COMMENT_AFFECT_POTION).getString(), ",", ":");

	        // ShearAssist
	        EntityInteractHandler.SHEARASSIST_ENABLE = CONFIG.get(Configuration.CATEGORY_GENERAL, "shearassistEnable", true,
	        		"Sheep shearing can wide range on right click by shear ").getBoolean(true);
	        CONFIG.addCustomCategoryComment(CATEGORY_SHEARASSIST, "Sheep shearing can  wide range on right click by shear ");
	        EntityInteractHandler.SHEARASSIST_RADIUS = CONFIG.get(CATEGORY_SHEARASSIST, "maxRadius", 2,
	        		COMMENT_MAX_RADIUS).getInt();
	        EntityInteractHandler.SHEARASSIST_AFFECT_POTION = Lib.stringToInt(CONFIG.get(CATEGORY_SHEARASSIST, "affectPotion", "",
	        		COMMENT_AFFECT_POTION).getString(), ",", ":");

	        // CultivateAssist
	        PlayerClickHandler.CULTIVATEASSIST_ENABLE = CONFIG.get(Configuration.CATEGORY_GENERAL, "cultivateassistEnable", true,
	        		"Greater than iron hoes are able to cultivate wide range").getBoolean(true);
	        CONFIG.addCustomCategoryComment(CATEGORY_CULTIVATEASSIST, "Greater than iron hoes are able to cultivate wide range");
	        PlayerClickHandler.CULTIVATEASSIST_AFFECT_POTION = Lib.stringToInt(CONFIG.get(CATEGORY_CULTIVATEASSIST, "affectPotion", "",
	        		COMMENT_AFFECT_POTION).getString(), ",", ":");
	        PlayerClickHandler.CULTIVATEASSIST_REQUIRE_TOOL_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_CULTIVATEASSIST, "requireToolLevel", "2",
	        		COMMNET_REQUIRE_TOOL_LEVEL).getString(), ":");
	        CONFIG.addCustomCategoryComment(CATEGORY_CULTIVATEASSIST_AREAPLUS, "Greater than diamond hoes effect more wide range");
	        PlayerClickHandler.CULTIVATEASSIST_AREAPLUS_ENABLE = CONFIG.get(CATEGORY_CULTIVATEASSIST_AREAPLUS, "areaPlusEnable", true,
	        		"Greater than diamond hoes are able to cultivate more wide range").getBoolean(true);
	        PlayerClickHandler.CULTIVATEASSIST_AREAPLUS_REQUIRE_TOOL_LEVEL = Lib.stringToInt(CONFIG.get(CATEGORY_CULTIVATEASSIST_AREAPLUS, "requireToolLevel", "3",
	        		COMMNET_REQUIRE_TOOL_LEVEL).getString(), ":");


	        // MountAssist
	        EntityJoinWorldHandler.MOUNTASSIST_ENABLE = CONFIG.get(Configuration.CATEGORY_GENERAL, "mountassistEnable", true,
	        		"Mounting automatically when you place a vehicles, and returning to inventory it when you dismount.").getBoolean(true);
	        EntityMountHandler.MOUNTASSIST_ENABLE = EntityJoinWorldHandler.MOUNTASSIST_ENABLE;

	        // Converter
	        EntityJoinWorldHandler.UNIFY_ENEBLE = CONFIG.get(Configuration.CATEGORY_GENERAL, "autounifyEnable", true,
	        		"Auto unify many ores by Ore Dictionary").getBoolean(true);

	        // misc
	        CONFIG.addCustomCategoryComment(CATEGORY_MISC, "miscellaneous");
	        Lib.COMPARE_TOOL_CLASSS = CONFIG.get(CATEGORY_MISC, "compareToolClass", true,
	        		"Please set it to false when conflict occurs").getBoolean(true);
	        Lib.COMPARE_IS_HARVESTABLE = CONFIG.get(CATEGORY_MISC, "compareIsHarvestable", true,
	        		"Please set it to false when conflict occurs").getBoolean(true);
	        BlockBreakEventHandler.SNEAK_INVERT = CONFIG.get(CATEGORY_MISC, "sneakInvert", false,
	        		"In default, some of the functions are disabled when sneaking, but setting this to true makes it enable only when you are sneaked").getBoolean(false);
	        PlayerClickHandler.SNEAK_INVERT =  BlockBreakEventHandler.SNEAK_INVERT;
	        EntityInteractHandler.SNEAK_INVERT =  BlockBreakEventHandler.SNEAK_INVERT;
	        EntityJoinWorldHandler.SNEAK_INVERT =  BlockBreakEventHandler.SNEAK_INVERT;

	        // RegisterItem
	        Comparator.UNIFY.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_UNIFY, "oreDictionary", "ore.*,").getString(), ","));
	        Comparator.UNIFY.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_UNIFY, "disallow", "").getString(), ","));
	        Comparator.AXE.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_AXE, "names", "").getString(), ","));
	        Comparator.AXE.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_AXE, "classes", ".*ItemAxe.*").getString(), ","));
	        Comparator.AXE.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_AXE, "oreDictionary", "").getString(), ","));
	        Comparator.AXE.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_AXE, "disallow", "").getString(), ","));
	        Comparator.PICKAXE.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_PICKAXE, "names", "").getString(), ","));
	        Comparator.PICKAXE.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_PICKAXE, "classes", ".*ItemPickaxe.*").getString(), ","));
	        Comparator.PICKAXE.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_PICKAXE, "oreDictionary", "").getString(), ","));
	        Comparator.PICKAXE.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_PICKAXE, "disallow", "").getString(), ","));
	        Comparator.SHOVEL.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_SHOVEL, "names", "").getString(), ","));
	        Comparator.SHOVEL.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_SHOVEL, "classes", ".*ItemSpade.*").getString(), ","));
	        Comparator.SHOVEL.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_SHOVEL, "oreDictionary", "").getString(), ","));
	        Comparator.SHOVEL.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_SHOVEL, "disallow", "").getString(), ","));
	        Comparator.HOE.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_HOE, "names", ".*Hoe.*, .*Tool.*").getString(), ","));
	        Comparator.HOE.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_HOE, "classes", ".*ItemHoe.*").getString(), ","));
	        Comparator.HOE.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_HOE, "oreDictionary", "").getString(), ","));
	        Comparator.HOE.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_HOE, "disallow", "").getString(), ","));
	        Comparator.LOG.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_LOG, "names", ".*Mushroom.*, .*log.*").getString(), ","));
	        Comparator.LOG.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_LOG, "classes", ".*Log.*").getString(), ","));
	        Comparator.LOG.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_LOG, "oreDictionary", "logWood").getString(), ","));
	        Comparator.LOG.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_LOG, "disallow", "").getString(), ","));
	        Comparator.SAPLING.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_SAPLING, "names", ".*Sapling.*").getString(), ","));
	        Comparator.SAPLING.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_SAPLING, "classes", ".*Sapling.*").getString(), ","));
	        Comparator.SAPLING.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_SAPLING, "oreDictionary", "").getString(), ","));
	        Comparator.SAPLING.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_SAPLING, "disallow", "").getString(), ","));
	        Comparator.ORE.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_ORE, "names", "").getString(), ","));
	        Comparator.ORE.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_ORE, "classes", ".*BlockOre.*, .*BlockRedstoneOre.*, .*BlockGlowstone.*, .*BlockObsidian.*").getString(), ","));
	        Comparator.ORE.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_ORE, "oreDictionary", "ore.*").getString(), ","));
	        Comparator.ORE.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_ORE, "disallow", "").getString(), ","));
	        Comparator.DIRT.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_DIRT, "names", ".*Grass.*, .*Dirt.*").getString(), ","));
	        Comparator.DIRT.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_DIRT, "classes", ".*Grass.*, .*Dirt.*, .*Mycelium.*, .*Sand, .*Clay.*, .*Gravel.*").getString(), ","));
	        Comparator.DIRT.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_DIRT, "oreDictionary", "").getString(), ","));
	        Comparator.DIRT.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_DIRT, "disallow", "").getString(), ","));
	        Comparator.STONE.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_STONE, "names", ".*Stone.*, .*Brick.*, .*Clay.*, .*Fence.*, .*Wall.*, .*Iron.*").getString(), ","));
	        Comparator.STONE.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_STONE, "classes", ".*Stone.*, .*Netherrack.*, .*SilverFish.*").getString(), ","));
	        Comparator.STONE.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_STONE, "oreDictionary", "").getString(), ","));
	        Comparator.STONE.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_STONE, "disallow", "").getString(), ","));
	        Comparator.WOOD.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_WOOD, "names", ".*Wood.*, .*Plank.*").getString(), ","));
	        Comparator.WOOD.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_WOOD, "classes", ".*Wood.*, .*Plank.*, .*BlockFence.*").getString(), ","));
	        Comparator.WOOD.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_WOOD, "oreDictionary", "plankWood").getString(), ","));
	        Comparator.WOOD.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_WOOD, "disallow", "").getString(), ","));
	        Comparator.CROP.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_CROP, "names", ".*Crop.*").getString(), ","));
	        Comparator.CROP.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_CROP, "classes", ".*Crop.*, .*Bush.*").getString(), ","));
	        Comparator.CROP.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_CROP, "oreDictionary", "").getString(), ","));
	        Comparator.CROP.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_CROP, "disallow", "").getString(), ","));
	        Comparator.SEED.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_SEED, "names", ".*Seed.*").getString(), ","));
	        Comparator.SEED.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_SEED, "classes", ".*IPlantable.*, .*Seed.*").getString(), ","));
	        Comparator.SEED.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_SEED, "oreDictionary", "").getString(), ","));
	        Comparator.SEED.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_SEED, "disallow", "").getString(), ","));
	        Comparator.LEAVE.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_LEAVE, "names", ".*Leave.*").getString(), ","));
	        Comparator.LEAVE.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_LEAVE, "classes", ".*Leave.*").getString(), ","));
	        Comparator.LEAVE.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_LEAVE, "oreDictionary", "").getString(), ","));
	        Comparator.LEAVE.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_LEAVE, "disallow", "").getString(), ","));
	        Comparator.FEED.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_FEED, "names", ".*wheat.*, .*seeds.*, .*carrot.*, .*potato.*").getString(), ","));
	        Comparator.FEED.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_FEED, "classes", "").getString(), ","));
	        Comparator.FEED.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_FEED, "oreDictionary", "").getString(), ","));
	        Comparator.FEED.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_FEED, "disallow", "").getString(), ","));
	        Comparator.SHEAR.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_SHEAR, "names", ".*shear.*").getString(), ","));
	        Comparator.SHEAR.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_SHEAR, "classes", "").getString(), ","));
	        Comparator.SHEAR.registerOreDict(Lib.splitAndTrim(CONFIG.get(CATEGORY_SHEAR, "oreDictionary", "").getString(), ","));
	        Comparator.SHEAR.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_SHEAR, "disallow", "").getString(), ","));
	        Comparator.MOUNT.registerName(Lib.splitAndTrim(CONFIG.get(CATEGORY_MOUNT, "names", "").getString(), ","));
	        Comparator.MOUNT.registerClass(Lib.splitAndTrim(CONFIG.get(CATEGORY_MOUNT, "classes", ".*EntityMinecartEmpty.*, .*EntityBoat.*").getString(), ","));
	        Comparator.MOUNT.registerDisallow(Lib.splitAndTrim(CONFIG.get(CATEGORY_MOUNT, "disallow", "").getString(), ","));
	        registOreDictionaryList = CONFIG.get(CATEGORY_ORE_DICTIONARY, "values", new String[] { "" },
	        		"To regist specific items to ore dictionary, entering one item per line by following format" + BR +
	        		"OreDictionaryName:ModID:ItemName").getStringList();

	        CONFIG.save();
        }

        private void loadInfo(ModMetadata meta){
    		meta.modId = McAssistant.MODID;
    		meta.name = McAssistant.MODID;
    		meta.description = "Assist your playing of minecraft by many functions";
    		meta.version = McAssistant.VERSION;
    		meta.url = "https://minecraft.curseforge.com/projects/mcassistant";
    		meta.authorList.add("mariri");
    		meta.credits = "";
    		meta.logoFile = "";
    		meta.autogenerated = false;
    	}

}
