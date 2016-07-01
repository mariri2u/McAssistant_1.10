package mariri.mcassistant;

import mariri.mcassistant.handler.BlockBreakEventHandler;
import mariri.mcassistant.handler.EntityInteractHandler;
import mariri.mcassistant.handler.EntityJoinWorldHandler;
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
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;


@Mod(modid = McAssistant.MODID, version = McAssistant.VERSION, acceptableRemoteVersions = "*")
public class McAssistant {

        public static final String MODID = "McAssistant";
        public static final String VERSION = "1.10.0-1.0";
        
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

        private static final String CATEGORY_MISC = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "misc";

        private static final String CATEGORY_ITEM_REGISTER = "ItemRegister";
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
        private static final String CATEGORY_ORE_DICTIONARY = CATEGORY_ITEM_REGISTER + Configuration.CATEGORY_SPLITTER + "oreDictionary";

//        private static final String COMMENT_BOOLEAN = "true / false";
//        private static final String COMMENT_0_INTEGER = "0:disable, 1 or over";
//        private static final String COMMENT_INTEGER = "1 or over";
        private static final String COMMENT_ID_LV = "ID:MinLv";
        private static final String COMMENT_ID_LV_TIME = "ID:Lv:Time (,PotionID:Lv:Time,...)";
        private static final String COMMENT_MIN_MAX = "0:disable, MinLv(:MaxLv)";
        private static final String COMMENT_HUNGER = "between 0 to 20";
        
        private String[] registOreDictionaryList;
        
        @EventHandler
        public void preInit(FMLPreInitializationEvent event) {
            Configuration config = new Configuration(event.getSuggestedConfigurationFile());
	        config.load();
	        
	        Property prop;
	        
	        // CutdownAssist
	        prop = config.get(Configuration.CATEGORY_GENERAL, "cutdownEnable", true);
	        BlockBreakEventHandler.CUTDOWN_ENABLE = prop.getBoolean(true);
	        prop = config.get(Configuration.CATEGORY_GENERAL, "cutdownBreakFromTopEnable", true);
	        BlockBreakEventHandler.CUTDOWN_FROM_TOP_ENABLE = prop.getBoolean(true);
	        prop = config.get(Configuration.CATEGORY_GENERAL, "cutdownChainEnable", true);
	        BlockBreakEventHandler.CUTDOWN_CHAIN = prop.getBoolean(true);
	        prop = config.get(CATEGORY_CUTDOWN, "maxRadius", 30);
	        BlockBreakEventHandler.CUTDOWN_MAX_DISTANCE = prop.getInt();
	        prop = config.get(CATEGORY_CUTDOWN, "breakBelow", false);
	        BlockBreakEventHandler.CUTDOWN_BELOW =  prop.getBoolean(false);
	        prop = config.get(CATEGORY_CUTDOWN, "onlyRoot", true);
	        BlockBreakEventHandler.CUTDOWN_ONLY_ROOT = prop.getBoolean(true);
	        prop = config.get(CATEGORY_CUTDOWN_CHAIN, "requirePotionLevel", "");
	        prop.setComment(COMMENT_ID_LV);
	        BlockBreakEventHandler.CUTDOWN_CHAIN_REQUIRE_POTION_LEVEL = Lib.stringToInt(prop.getString(), ":");
	        prop = config.get(CATEGORY_CUTDOWN_CHAIN, "affectPotion", "");
	        prop.setComment(COMMENT_ID_LV_TIME);
	        BlockBreakEventHandler.CUTDOWN_CHAIN_AFFECT_POTION = Lib.stringToInt(prop.getString(), ",", ":");
	        prop = config.get(CATEGORY_CUTDOWN_CHAIN, "requireHunger", 0);
	        prop.setComment(COMMENT_HUNGER);
	        BlockBreakEventHandler.CUTDOWN_CHAIN_REQUIRE_HUNGER = prop.getInt();
	        prop = config.get(CATEGORY_CUTDOWN_CHAIN, "requireToolLevel", "2");
	        BlockBreakEventHandler.CUTDOWN_CHAIN_REQUIRE_TOOL_LEVEL = Lib.stringToInt(prop.getString(), ":");
	        prop = config.get(CATEGORY_CUTDOWN_CHAIN, "requireEnchantLevel", "32:1");
	        prop.setComment(COMMENT_ID_LV);
	        BlockBreakEventHandler.CUTDOWN_CHAIN_REQUIRE_ENCHANT_LEVEL = Lib.stringToInt(prop.getString(), ":");
	        prop = config.get(CATEGORY_CUTDOWN_CHAIN, "breakLeaves", true);
	        BlockBreakEventHandler.CUTDOWN_CHAIN_BREAK_LEAVES = prop.getBoolean(true);
	        prop = config.get(CATEGORY_CUTDOWN_CHAIN, "autoReplant", true);
	        BlockBreakEventHandler.CUTDOWN_CHAIN_REPLANT = prop.getBoolean(true);
	        prop = config.get(CATEGORY_CUTDOWN_CHAIN, "maxHorizonalRadius", 2);
	        BlockBreakEventHandler.CUTDOWN_CHAIN_MAX_HORIZONAL_DISTANCE = prop.getInt();

	        // MineAssist
	        prop = config.get(Configuration.CATEGORY_GENERAL, "mineassistEnable", true);
	        BlockBreakEventHandler.MINEASSIST_ENABLE = prop.getBoolean(true);
	        prop = config.get(CATEGORY_MINEASSIST, "maxRadius", 10);
	        BlockBreakEventHandler.MINEASSIST_MAX_DISTANCE = prop.getInt();
	        prop = config.get(CATEGORY_MINEASSIST, "requirePotionLevel", "");
	        prop.setComment(COMMENT_ID_LV);
	        BlockBreakEventHandler.MINEASSIST_REQUIRE_POTION_LEVEL = Lib.stringToInt(prop.getString(), ":");
	        prop = config.get(CATEGORY_MINEASSIST, "affectPotion", "17:1:15");
	        prop.setComment(COMMENT_ID_LV_TIME);
	        BlockBreakEventHandler.MINEASSIST_AFFECT_POTION = Lib.stringToInt(prop.getString(), ",", ":");
	        prop = config.get(CATEGORY_MINEASSIST, "requireHunger", 15);
	        prop.setComment(COMMENT_HUNGER);
	        BlockBreakEventHandler.MINEASSIST_REQUIRE_HUNGER = prop.getInt();
	        prop = config.get(CATEGORY_MINEASSIST, "requireToolLevel", "2");
	        prop.setComment(COMMENT_MIN_MAX);
	        BlockBreakEventHandler.MINEASSIST_REQUIRE_TOOL_LEVEL = Lib.stringToInt(prop.getString(), ":");
	        prop = config.get(CATEGORY_MINEASSIST, "requireEnchantLevel", "");
	        prop.setComment(COMMENT_ID_LV);
	        BlockBreakEventHandler.MINEASSIST_REQUIRE_ENCHANT_LEVEL = Lib.stringToInt(prop.getString(), ":");

	        // FlatAssist
	        prop = config.get(Configuration.CATEGORY_GENERAL, "flatassistEnable", true);
	        BlockBreakEventHandler.FLATASSIST_ENABLE = prop.getBoolean(true);
	        
	        prop = config.get(CATEGORY_FLATASSIST, "flatassistHarvestableEnable", true);
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_ENABLE = prop.getBoolean(true);
	        prop = config.get(CATEGORY_FLATASSIST_HARVESTABLE, "requirePotionId", 3);
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_REQUIRE_POTION_ID = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_HARVESTABLE, "affectPotion", "");
	        prop.setComment(COMMENT_ID_LV_TIME);
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_AFFECT_POTION = Lib.stringToInt(prop.getString(), ",", ":");
	        prop = config.get(CATEGORY_FLATASSIST_HARVESTABLE, "requireHunger", 0);
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_REQUIRE_HUNGER = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_HARVESTABLE, "requireToolLevel", "2");
	        prop.setComment(COMMENT_MIN_MAX);
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_REQUIRE_TOOL_LEVEL = Lib.stringToInt(prop.getString(), ":");
	        prop = config.get(CATEGORY_FLATASSIST_HARVESTABLE, "requireEnchantId", 0);
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_REQUIRE_ENCHANT_ID = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_HARVESTABLE, "breakBelow", false);
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_BELOW = prop.getBoolean(false);
	        prop = config.get(CATEGORY_FLATASSIST_HARVESTABLE, "maxRadius", 0);
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_MAX_RADIUS = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_HARVESTABLE, "breakAnything", false);
	        BlockBreakEventHandler.FLATASSIST_HARVESTABLE_BREAK_ANYTHING = prop.getBoolean(false);

	        prop = config.get(CATEGORY_FLATASSIST, "flatassistDirtEnable", false);
	        BlockBreakEventHandler.FLATASSIST_DIRT_ENABLE = prop.getBoolean(false);
	        prop = config.get(CATEGORY_FLATASSIST_DIRT, "requirePotionId", 3);
	        BlockBreakEventHandler.FLATASSIST_DIRT_REQUIRE_POTION_ID = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_DIRT, "affectPotion", "");
	        prop.setComment(COMMENT_ID_LV_TIME);
	        BlockBreakEventHandler.FLATASSIST_DIRT_AFFECT_POTION = Lib.stringToInt(prop.getString(), ",", ":");
	        prop = config.get(CATEGORY_FLATASSIST_DIRT, "requireHunger", 0);
	        BlockBreakEventHandler.FLATASSIST_DIRT_REQUIRE_HUNGER = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_DIRT, "requireToolLevel", "2");
	        prop.setComment(COMMENT_MIN_MAX);
	        BlockBreakEventHandler.FLATASSIST_DIRT_REQUIRE_TOOL_LEVEL = Lib.stringToInt(prop.getString(), ":");
	        prop = config.get(CATEGORY_FLATASSIST_DIRT, "requireEnchantId", 0);
	        BlockBreakEventHandler.FLATASSIST_DIRT_REQUIRE_ENCHANT_ID = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_DIRT, "breakBelow", false);
	        BlockBreakEventHandler.FLATASSIST_DIRT_BELOW = prop.getBoolean(false);
	        prop = config.get(CATEGORY_FLATASSIST_DIRT, "maxRadius", 0);
	        BlockBreakEventHandler.FLATASSIST_DIRT_MAX_RADIUS = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_DIRT, "breakAnything", false);
	        BlockBreakEventHandler.FLATASSIST_DIRT_BREAK_ANYTHING = prop.getBoolean(false);
	        
	        prop = config.get(CATEGORY_FLATASSIST, "flatassistStoneEnable", false);
	        BlockBreakEventHandler.FLATASSIST_STONE_ENABLE = prop.getBoolean(false);
	        prop = config.get(CATEGORY_FLATASSIST_STONE, "requirePotionId", 3);
	        BlockBreakEventHandler.FLATASSIST_STONE_REQUIRE_POTION_ID = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_STONE, "affectPotion", "");
	        prop.setComment(COMMENT_ID_LV_TIME);
	        BlockBreakEventHandler.FLATASSIST_STONE_AFFECT_POTION = Lib.stringToInt(prop.getString(), ",", ":");
	        prop = config.get(CATEGORY_FLATASSIST_STONE, "requireHunger", 0);
	        BlockBreakEventHandler.FLATASSIST_STONE_REQUIRE_HUNGER = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_STONE, "requireToolLevel", "2");
	        prop.setComment(COMMENT_MIN_MAX);
	        BlockBreakEventHandler.FLATASSIST_STONE_REQUIRE_TOOL_LEVEL = Lib.stringToInt(prop.getString(), ":");
	        prop = config.get(CATEGORY_FLATASSIST_STONE, "requireEnchantId", 0);
	        BlockBreakEventHandler.FLATASSIST_STONE_REQUIRE_ENCHANT_ID = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_STONE, "breakBelow", false);
	        BlockBreakEventHandler.FLATASSIST_STONE_BELOW = prop.getBoolean(false);
	        prop = config.get(CATEGORY_FLATASSIST_STONE, "maxRadius", 0);
	        BlockBreakEventHandler.FLATASSIST_STONE_MAX_RADIUS = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_STONE, "breakAnything", false);
	        BlockBreakEventHandler.FLATASSIST_STONE_BREAK_ANYTHING = prop.getBoolean(false);
	      
	        prop = config.get(CATEGORY_FLATASSIST, "flatassistWoodEnable", false);
	        BlockBreakEventHandler.FLATASSIST_WOOD_ENABLE = prop.getBoolean(false);
	        prop = config.get(CATEGORY_FLATASSIST_WOOD, "requirePotionId", 3);
	        BlockBreakEventHandler.FLATASSIST_WOOD_REQUIRE_POTION_ID = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_WOOD, "affectPotion", "");
	        prop.setComment(COMMENT_ID_LV_TIME);
	        BlockBreakEventHandler.FLATASSIST_WOOD_AFFECT_POTION = Lib.stringToInt(prop.getString(), ",", ":");
	        prop = config.get(CATEGORY_FLATASSIST_WOOD, "requireHunger", 0);
	        BlockBreakEventHandler.FLATASSIST_WOOD_REQUIRE_HUNGER = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_WOOD, "requireToolLevel", "2:10");
	        BlockBreakEventHandler.FLATASSIST_WOOD_REQUIRE_TOOL_LEVEL = Lib.stringToInt(prop.getString(), ":");
	        prop = config.get(CATEGORY_FLATASSIST_WOOD, "requireEnchantId", 0);
	        BlockBreakEventHandler.FLATASSIST_WOOD_REQUIRE_ENCHANT_ID = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_WOOD, "breakBelow", false);
	        BlockBreakEventHandler.FLATASSIST_WOOD_BELOW = prop.getBoolean(false);
	        prop = config.get(CATEGORY_FLATASSIST_WOOD, "maxRadius", 0);
	        BlockBreakEventHandler.FLATASSIST_WOOD_MAX_RADIUS = prop.getInt();
	        prop = config.get(CATEGORY_FLATASSIST_WOOD, "breakAnything", false);
	        BlockBreakEventHandler.FLATASSIST_WOOD_BREAK_ANYTHING = prop.getBoolean(false);

	        // CropAssist
	        prop = config.get(Configuration.CATEGORY_GENERAL, "cropassistEnable", true);
	        PlayerClickHandler.CROPASSIST_ENABLE = prop.getBoolean(true);
	        prop = config.get(CATEGORY_CROPASSIST, "requireToolLevel", 0);
	        PlayerClickHandler.CROPASSIST_REQUIRE_TOOL_LEVEL = prop.getInt();
	        prop = config.get(CATEGORY_CROPASSIST_AREA, "areaEnable", true);
	        PlayerClickHandler.CROPASSIST_AREA_ENABLE = prop.getBoolean(true);
	        prop = config.get(CATEGORY_CROPASSIST_AREA, "requireToolLevel", "2");
	        prop.setComment(COMMENT_MIN_MAX);
	        PlayerClickHandler.CROPASSIST_AREA_REQUIRE_TOOL_LEVEL = Lib.stringToInt(prop.getString(), ":");
	        prop = config.get(CATEGORY_CROPASSIST, "affectPotion", "");
	        prop.setComment(COMMENT_ID_LV_TIME);
	        PlayerClickHandler.CROPASSIST_AREA_AFFECT_POTION = Lib.stringToInt(prop.getString(), ",", ":");
	        prop = config.get(CATEGORY_CROPASSIST, "suplyFromInventory", true);
	        CropReplanter.CROPASSIST_SUPLY = prop.getBoolean(true);
	        prop = config.get(CATEGORY_CROPASSIST, "autoCraft", true);
	        CropReplanter.CROPASSIST_AUTOCRAFT = prop.getBoolean(true);
	        prop = config.get(CATEGORY_CROPASSIST_AREAPLUS, "areaPlusEnable", true);
	        PlayerClickHandler.CROPASSIST_AREAPLUS_ENABLE =prop.getBoolean(true);
	        prop = config.get(CATEGORY_CROPASSIST_AREAPLUS, "requireToolLevel", "3");
	        prop.setComment(COMMENT_MIN_MAX);
	        PlayerClickHandler.CROPASSIST_AREAPLUS_REQUIRE_TOOL_LEVEL = Lib.stringToInt(prop.getString(), ":");
	        
	        // TorchAssist
	        prop = config.get(Configuration.CATEGORY_GENERAL, "torchassistEnable", false);
	        PlayerClickHandler.TORCHASSIST_ENABLE = prop.getBoolean(false);
	        
	        // LeaveAssist
	        prop = config.get(Configuration.CATEGORY_GENERAL, "leaveassistEnable", true);
	        PlayerClickHandler.LEAVEASSIST_ENABLE = prop.getBoolean(true);
	        prop = config.get(CATEGORY_LEAVEASSIST, "affectPotion", "");
	        prop.setComment(COMMENT_ID_LV_TIME);
	        PlayerClickHandler.LEAVEASSIST_AFFECT_POTION = Lib.stringToInt(prop.getString(), ",", ":");
	        prop = config.get(CATEGORY_LEAVEASSIST_AREAPLUS, "areaPlusEnable", true);
	        PlayerClickHandler.LEAVEASSIST_AREAPLUS_ENABLE = prop.getBoolean(true);
	        prop = config.get(CATEGORY_LEAVEASSIST_AREAPLUS, "requireToolLevel", "3");
	        prop.setComment(COMMENT_MIN_MAX);
	        PlayerClickHandler.LEAVEASSIST_AREAPLUS_REQUIRE_TOOL_LEVEL = Lib.stringToInt(prop.getString(), ":");

	        // BedAssist
	        prop = config.get(Configuration.CATEGORY_GENERAL, "bedassistEnable", true);
	        PlayerClickHandler.BEDASSIST_ENABLE = prop.getBoolean(true);
	        prop = config.get(CATEGORY_BEDASSIST, "setRespawnAnytimeEnable", true);
	        PlayerClickHandler.BEDASSIST_SET_RESPAWN_ANYTIME = prop.getBoolean(true);
	        prop = config.get(CATEGORY_BEDASSIST, "setRespawnAnytimeMessage", "Set Respawn!!");
	        PlayerClickHandler.BEDASSIST_SET_RESPAWN_MESSAGE = prop.getString();
	        prop = config.get(CATEGORY_BEDASSIST, "noSleepEnable", false);
	        PlayerClickHandler.BEDASSIST_NO_SLEEP = prop.getBoolean(false);
	        prop = config.get(CATEGORY_BEDASSIST, "noSleepMessage", "You can't sleep!!");
	        PlayerClickHandler.BEDASSIST_NO_SLEEP_MESSAGE = prop.getString();
     
	        // BreedAssist
	        prop = config.get(Configuration.CATEGORY_GENERAL, "breedassistEnable", true);
	        EntityInteractHandler.BREEDASSIST_ENABLE = prop.getBoolean(true);
	        prop = config.get(CATEGORY_BREEDASSIST, "maxRadius", 2);
	        EntityInteractHandler.BREEDASSIST_RADIUS = prop.getInt();
	        prop = config.get(CATEGORY_BREEDASSIST, "affectPotion", "");
	        prop.setComment(COMMENT_ID_LV_TIME);
	        EntityInteractHandler.BREEDASSIST_AFFECT_POTION = Lib.stringToInt(prop.getString(), ",", ":");
	        
	        // CultivateAssist
	        prop = config.get(Configuration.CATEGORY_GENERAL, "cultivateassistEnable", true);
	        PlayerClickHandler.CULTIVATEASSIST_ENABLE = prop.getBoolean(true);
	        prop = config.get(CATEGORY_CULTIVATEASSIST, "affectPotion", "");
	        prop.setComment(COMMENT_ID_LV_TIME);
	        PlayerClickHandler.CULTIVATEASSIST_AFFECT_POTION = Lib.stringToInt(prop.getString(), ",", ":");
	        prop = config.get(CATEGORY_CULTIVATEASSIST, "requireToolLevel", "2");
	        prop.setComment(COMMENT_MIN_MAX);
	        PlayerClickHandler.CULTIVATEASSIST_REQUIRE_TOOL_LEVEL = Lib.stringToInt(prop.getString(), ":");
	        prop = config.get(CATEGORY_CULTIVATEASSIST_AREAPLUS, "areaPlusEnable", true);
	        PlayerClickHandler.CULTIVATEASSIST_AREAPLUS_ENABLE = prop.getBoolean(true);
	        prop = config.get(CATEGORY_CULTIVATEASSIST_AREAPLUS, "requireToolLevel", "3");
	        prop.setComment(COMMENT_MIN_MAX);
	        PlayerClickHandler.CULTIVATEASSIST_AREAPLUS_REQUIRE_TOOL_LEVEL = Lib.stringToInt(prop.getString(), ":");
   
	        // Converter
	        prop = config.get(Configuration.CATEGORY_GENERAL, "autounifyEnable", true);
	        EntityJoinWorldHandler.UNIFY_ENEBLE = prop.getBoolean(true);
	        
	        // misc
	        prop = config.get(CATEGORY_MISC, "compareToolClass", true);
	        Lib.COMPARE_TOOL_CLASSS = prop.getBoolean(true);	        
	        prop = config.get(CATEGORY_MISC, "compareIsHarvestable", true);
	        Lib.COMPARE_IS_HARVESTABLE = prop.getBoolean(true);
	        prop = config.get(CATEGORY_MISC, "sneakInvertOnBlockBreak", false);
	        prop.setComment("Cutdown, Mineassist, Flatassist");
	        BlockBreakEventHandler.SNEAK_INVERT = prop.getBoolean(false);
	        prop = config.get(CATEGORY_MISC, "sneakInvertOnClick", false);
	        prop.setComment("Bedassist, Cropassist, Cultivateassist, Leaveassist, Torchassist");
	        PlayerClickHandler.SNEAK_INVERT = prop.getBoolean(false);
	        prop = config.get(CATEGORY_MISC, "sneakInvertOnInteract", false);
	        prop.setComment("Breedassist");
	        EntityInteractHandler.SNEAK_INVERT = prop.getBoolean(false);	        
	        
	        // RegisterItem
	        Comparator.UNIFY.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_UNIFY, "oreDictionary", "ore.*,").getString(), ","));
	        Comparator.AXE.registerName(Lib.splitAndTrim(config.get(CATEGORY_AXE, "names", "").getString(), ","));
	        Comparator.AXE.registerClass(Lib.splitAndTrim(config.get(CATEGORY_AXE, "classes", ".*ItemAxe.*").getString(), ","));
	        Comparator.AXE.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_AXE, "oreDictionary", "").getString(), ","));
	        Comparator.AXE.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_AXE, "disallow", "").getString(), ","));
	        Comparator.PICKAXE.registerName(Lib.splitAndTrim(config.get(CATEGORY_PICKAXE, "names", "").getString(), ","));
	        Comparator.PICKAXE.registerClass(Lib.splitAndTrim(config.get(CATEGORY_PICKAXE, "classes", ".*ItemPickaxe.*").getString(), ","));
	        Comparator.PICKAXE.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_PICKAXE, "oreDictionary", "").getString(), ","));
	        Comparator.PICKAXE.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_PICKAXE, "disallow", "").getString(), ","));
	        Comparator.SHOVEL.registerName(Lib.splitAndTrim(config.get(CATEGORY_SHOVEL, "names", "").getString(), ","));
	        Comparator.SHOVEL.registerClass(Lib.splitAndTrim(config.get(CATEGORY_SHOVEL, "classes", ".*ItemSpade.*").getString(), ","));
	        Comparator.SHOVEL.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_SHOVEL, "oreDictionary", "").getString(), ","));
	        Comparator.SHOVEL.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_SHOVEL, "disallow", "").getString(), ","));
	        Comparator.HOE.registerName(Lib.splitAndTrim(config.get(CATEGORY_HOE, "names", ".*Hoe.*, .*Tool.*").getString(), ","));
	        Comparator.HOE.registerClass(Lib.splitAndTrim(config.get(CATEGORY_HOE, "classes", ".*ItemHoe.*").getString(), ","));
	        Comparator.HOE.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_HOE, "oreDictionary", "").getString(), ","));
	        Comparator.HOE.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_HOE, "disallow", "").getString(), ","));
	        Comparator.LOG.registerName(Lib.splitAndTrim(config.get(CATEGORY_LOG, "names", ".*Mushroom.*, .*log.*").getString(), ","));
	        Comparator.LOG.registerClass(Lib.splitAndTrim(config.get(CATEGORY_LOG, "classes", ".*Log.*").getString(), ","));
	        Comparator.LOG.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_LOG, "oreDictionary", "logWood").getString(), ","));
	        Comparator.LOG.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_LOG, "disallow", "").getString(), ","));
	        Comparator.SAPLING.registerName(Lib.splitAndTrim(config.get(CATEGORY_SAPLING, "names", ".*Sapling.*").getString(), ","));
	        Comparator.SAPLING.registerClass(Lib.splitAndTrim(config.get(CATEGORY_SAPLING, "classes", ".*Sapling.*").getString(), ","));
	        Comparator.SAPLING.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_SAPLING, "oreDictionary", "").getString(), ","));
	        Comparator.SAPLING.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_SAPLING, "disallow", "").getString(), ","));
	        Comparator.ORE.registerName(Lib.splitAndTrim(config.get(CATEGORY_ORE, "names", "").getString(), ","));
	        Comparator.ORE.registerClass(Lib.splitAndTrim(config.get(CATEGORY_ORE, "classes", ".*BlockOre.*, .*BlockRedstoneOre.*, .*BlockGlowstone.*, .*BlockObsidian.*").getString(), ","));
	        Comparator.ORE.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_ORE, "oreDictionary", "ore.*").getString(), ","));
	        Comparator.ORE.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_ORE, "disallow", "").getString(), ","));
	        Comparator.DIRT.registerName(Lib.splitAndTrim(config.get(CATEGORY_DIRT, "names", ".*Grass.*, .*Dirt.*").getString(), ","));
	        Comparator.DIRT.registerClass(Lib.splitAndTrim(config.get(CATEGORY_DIRT, "classes", ".*Grass.*, .*Dirt.*, .*Mycelium.*, .*Sand, .*Clay.*, .*Gravel.*").getString(), ","));
	        Comparator.DIRT.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_DIRT, "oreDictionary", "").getString(), ","));
	        Comparator.DIRT.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_DIRT, "disallow", "").getString(), ","));
	        Comparator.STONE.registerName(Lib.splitAndTrim(config.get(CATEGORY_STONE, "names", ".*Stone.*, .*Brick.*, .*Clay.*, .*Fence.*, .*Wall.*, .*Iron.*").getString(), ","));
	        Comparator.STONE.registerClass(Lib.splitAndTrim(config.get(CATEGORY_STONE, "classes", ".*Stone.*, .*Netherrack.*, .*SilverFish.*").getString(), ","));
	        Comparator.STONE.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_STONE, "oreDictionary", "").getString(), ","));
	        Comparator.STONE.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_STONE, "disallow", "").getString(), ","));
	        Comparator.WOOD.registerName(Lib.splitAndTrim(config.get(CATEGORY_WOOD, "names", ".*Wood.*, .*Plank.*").getString(), ","));
	        Comparator.WOOD.registerClass(Lib.splitAndTrim(config.get(CATEGORY_WOOD, "classes", ".*Wood.*, .*Plank.*, .*BlockFence.*").getString(), ","));
	        Comparator.WOOD.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_WOOD, "oreDictionary", "plankWood").getString(), ","));
	        Comparator.WOOD.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_WOOD, "disallow", "").getString(), ","));
	        Comparator.CROP.registerName(Lib.splitAndTrim(config.get(CATEGORY_CROP, "names", ".*Crop.*").getString(), ","));
	        Comparator.CROP.registerClass(Lib.splitAndTrim(config.get(CATEGORY_CROP, "classes", ".*Crop.*, .*Bush.*").getString(), ","));
	        Comparator.CROP.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_CROP, "oreDictionary", "").getString(), ","));
	        Comparator.CROP.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_CROP, "disallow", "").getString(), ","));
	        Comparator.SEED.registerName(Lib.splitAndTrim(config.get(CATEGORY_SEED, "names", ".*Seed.*").getString(), ","));
	        Comparator.SEED.registerClass(Lib.splitAndTrim(config.get(CATEGORY_SEED, "classes", ".*IPlantable.*, .*Seed.*").getString(), ","));
	        Comparator.SEED.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_SEED, "oreDictionary", "").getString(), ","));
	        Comparator.SEED.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_SEED, "disallow", "").getString(), ","));
	        Comparator.LEAVE.registerName(Lib.splitAndTrim(config.get(CATEGORY_LEAVE, "names", ".*Leave.*").getString(), ","));
	        Comparator.LEAVE.registerClass(Lib.splitAndTrim(config.get(CATEGORY_LEAVE, "classes", ".*Leave.*").getString(), ","));
	        Comparator.LEAVE.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_LEAVE, "oreDictionary", "").getString(), ","));
	        Comparator.LEAVE.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_LEAVE, "disallow", "").getString(), ","));
	        Comparator.FEED.registerName(Lib.splitAndTrim(config.get(CATEGORY_FEED, "names", ".*wheat.*, .*seeds.*, .*carrot.*, .*potato.*").getString(), ","));
	        Comparator.FEED.registerClass(Lib.splitAndTrim(config.get(CATEGORY_FEED, "classes", "").getString(), ","));
	        Comparator.FEED.registerOreDict(Lib.splitAndTrim(config.get(CATEGORY_FEED, "oreDictionary", "").getString(), ","));
	        Comparator.FEED.registerDisallow(Lib.splitAndTrim(config.get(CATEGORY_FEED, "disallow", "").getString(), ","));
	        registOreDictionaryList = config.get(CATEGORY_ORE_DICTIONARY, "values", new String[] { "" } ).getStringList();
	        
	        config.save();
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
                    	
        	// Unifier
        	if(EntityJoinWorldHandler.isEventEnable()){
        		MinecraftForge.EVENT_BUS.register(EntityJoinWorldHandler.INSTANCE);
        	}
        	
        	// TorchAssist, Bedassist, Cropassist, Leaveassist
        	if(PlayerClickHandler.isEventEnable()){
        		MinecraftForge.EVENT_BUS.register(PlayerClickHandler.INSTANCE);
        	}
        	
        	// BreedAssist
        	if(EntityInteractHandler.isEventEnable()){
        		MinecraftForge.EVENT_BUS.register(EntityInteractHandler.INSTANCE);
        	}
        	
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
}
