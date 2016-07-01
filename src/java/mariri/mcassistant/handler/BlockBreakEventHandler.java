package mariri.mcassistant.handler;

import java.util.ArrayList;
import java.util.List;

import mariri.mcassistant.helper.Comparator;
import mariri.mcassistant.helper.EdgeHarvester;
import mariri.mcassistant.helper.Lib;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockBreakEventHandler {
	
	public static BlockBreakEventHandler INSTANCE = new BlockBreakEventHandler();
	
	public static boolean CUTDOWN_ENABLE = true;
	public static boolean CUTDOWN_CHAIN;
	public static boolean CUTDOWN_BELOW;
	public static boolean CUTDOWN_ONLY_ROOT;
	public static boolean CUTDOWN_FROM_TOP_ENABLE;
//	public static boolean CUTDOWN_REPLANT;
	public static int CUTDOWN_MAX_DISTANCE;
	public static int[] CUTDOWN_CHAIN_REQUIRE_POTION_LEVEL;
	public static int[][] CUTDOWN_CHAIN_AFFECT_POTION;
	public static int CUTDOWN_CHAIN_REQUIRE_HUNGER;
	public static int[] CUTDOWN_CHAIN_REQUIRE_TOOL_LEVEL;
	public static int[] CUTDOWN_CHAIN_REQUIRE_ENCHANT_LEVEL;
	public static boolean CUTDOWN_CHAIN_BREAK_LEAVES;
	public static boolean CUTDOWN_CHAIN_REPLANT;
	public static int CUTDOWN_CHAIN_MAX_HORIZONAL_DISTANCE;
	
//	public static boolean CROPASSIST_ENABLE = true;
//	public static int CROPASSIST_REQUIRE_TOOL_LEVEL;
	public static boolean MINEASSIST_ENABLE = false;
	public static int MINEASSIST_MAX_DISTANCE;
	public static int[] MINEASSIST_REQUIRE_POTION_LEVEL;
	public static int[][] MINEASSIST_AFFECT_POTION;
	public static int MINEASSIST_REQUIRE_HUNGER;
	public static int[] MINEASSIST_REQUIRE_TOOL_LEVEL;
	public static int[] MINEASSIST_REQUIRE_ENCHANT_LEVEL;
	
	public static boolean FLATASSIST_ENABLE;
	
	public static boolean FLATASSIST_HARVESTABLE_ENABLE;
	public static int FLATASSIST_HARVESTABLE_REQUIRE_POTION_ID;
	public static int[] FLATASSIST_HARVESTABLE_REQUIRE_TOOL_LEVEL;
	public static int FLATASSIST_HARVESTABLE_REQUIRE_ENCHANT_ID;
	public static int[][] FLATASSIST_HARVESTABLE_AFFECT_POTION;
	public static int FLATASSIST_HARVESTABLE_REQUIRE_HUNGER;
	public static boolean FLATASSIST_HARVESTABLE_BELOW;
	public static int FLATASSIST_HARVESTABLE_MAX_RADIUS;
	public static boolean FLATASSIST_HARVESTABLE_BREAK_ANYTHING;

	
	public static boolean FLATASSIST_DIRT_ENABLE;
	public static int FLATASSIST_DIRT_REQUIRE_POTION_ID;
	public static int[] FLATASSIST_DIRT_REQUIRE_TOOL_LEVEL;
	public static int FLATASSIST_DIRT_REQUIRE_ENCHANT_ID;
	public static int[][] FLATASSIST_DIRT_AFFECT_POTION;
	public static int FLATASSIST_DIRT_REQUIRE_HUNGER;
	public static boolean FLATASSIST_DIRT_BELOW;
	public static int FLATASSIST_DIRT_MAX_RADIUS;
	public static boolean FLATASSIST_DIRT_BREAK_ANYTHING;
	
	public static boolean FLATASSIST_STONE_ENABLE;
	public static int FLATASSIST_STONE_REQUIRE_POTION_ID;
	public static int[] FLATASSIST_STONE_REQUIRE_TOOL_LEVEL;
	public static int FLATASSIST_STONE_REQUIRE_ENCHANT_ID;
	public static int[][] FLATASSIST_STONE_AFFECT_POTION;
	public static int FLATASSIST_STONE_REQUIRE_HUNGER;
	public static boolean FLATASSIST_STONE_BELOW;
	public static int FLATASSIST_STONE_MAX_RADIUS;
	public static boolean FLATASSIST_STONE_BREAK_ANYTHING;
	
	public static boolean FLATASSIST_WOOD_ENABLE;
	public static int FLATASSIST_WOOD_REQUIRE_POTION_ID;
	public static int[] FLATASSIST_WOOD_REQUIRE_TOOL_LEVEL;
	public static int FLATASSIST_WOOD_REQUIRE_ENCHANT_ID;
	public static int[][] FLATASSIST_WOOD_AFFECT_POTION;
	public static int FLATASSIST_WOOD_REQUIRE_HUNGER;
	public static boolean FLATASSIST_WOOD_BELOW;
	public static int FLATASSIST_WOOD_MAX_RADIUS;
	public static boolean FLATASSIST_WOOD_BREAK_ANYTHING;
	
	public static boolean SNEAK_INVERT;
	
	private static List<EntityPlayer> isProcessing = new ArrayList<EntityPlayer>();
	
	private BlockBreakEventHandler(){}
	
	@SubscribeEvent
	public void onPlayerHarvest(BlockEvent.BreakEvent e){
		BlockPos pos = e.getPos();
		int x = e.getPos().getX();
		int y = e.getPos().getY();
		int z = e.getPos().getZ();
		World world = e.getWorld();
		EntityPlayer player = e.getPlayer();
		IBlockState state = e.getState();
		Block block = e.getState().getBlock();
		int meta = block.getMetaFromState(state);
		
		if(!isProcessing.contains(player) && !world.isRemote && player != null && player.isSneaking() == SNEAK_INVERT){
			isProcessing.add(player);
			
			// 木こり補助機能
			if(		CUTDOWN_ENABLE && Comparator.LOG.compareBlock(state) &&
					Lib.isAxeOnEquip(player) &&
					(!CUTDOWN_ONLY_ROOT || Comparator.DIRT.compareBlock(world.getBlockState(new BlockPos(x, y - 1, z)))) ){
				EdgeHarvester harvester = new EdgeHarvester(world, player, pos, state, CUTDOWN_BELOW, CUTDOWN_MAX_DISTANCE);
				harvester.setCheckMetadata(false);
				harvester.setFindRange(2);
				// 木こり一括破壊の判定
				if(CUTDOWN_CHAIN && Lib.isPotionAffected(player, CUTDOWN_CHAIN_REQUIRE_POTION_LEVEL) &&
						player.getFoodStats().getFoodLevel() >= CUTDOWN_CHAIN_REQUIRE_HUNGER &&
						Lib.isEnchanted(player, CUTDOWN_CHAIN_REQUIRE_ENCHANT_LEVEL) &&
						Lib.compareCurrentToolLevel(player, CUTDOWN_CHAIN_REQUIRE_TOOL_LEVEL)){
					if(CUTDOWN_CHAIN_BREAK_LEAVES){
						if(block == Blocks.RED_MUSHROOM_BLOCK){
							harvester.setIdentifyBlocks(new IBlockState[] { Blocks.BROWN_MUSHROOM_BLOCK.getBlockState().getBaseState() });
						}else if(block == Blocks.BROWN_MUSHROOM_BLOCK){
							harvester.setIdentifyBlocks(new IBlockState[] { Blocks.RED_MUSHROOM_BLOCK.getBlockState().getBaseState() });
						}else{
							harvester.setHorizonalMaxOffset(CUTDOWN_CHAIN_MAX_HORIZONAL_DISTANCE);
							harvester.setIdentifyBreakTool(false);
							harvester.setReplant(CUTDOWN_CHAIN_REPLANT);
							harvester.setDropAfter(CUTDOWN_CHAIN_REPLANT);
							harvester.setIdentifyComparator(Comparator.LEAVE);
							//harvester.setCheckMetadata(true);
						}
					}
					harvester.harvestChain(CUTDOWN_CHAIN_AFFECT_POTION, false);
					e.setCanceled(true);
				}else if(CUTDOWN_FROM_TOP_ENABLE){
					harvester.harvestEdge();
					e.setCanceled(true);
				}
			}
			// 鉱石一括破壊機能
			else if(		MINEASSIST_ENABLE && Lib.compareCurrentToolLevel(player, MINEASSIST_REQUIRE_TOOL_LEVEL) &&
					Lib.isPotionAffected(player, MINEASSIST_REQUIRE_POTION_LEVEL) &&
					player.getFoodStats().getFoodLevel() >= MINEASSIST_REQUIRE_HUNGER &&
					Lib.isEnchanted(player, MINEASSIST_REQUIRE_ENCHANT_LEVEL) &&
					Comparator.ORE.compareBlock(state) && 
					Lib.isPickaxeOnEquip(player) ){
				EdgeHarvester harvester = new EdgeHarvester(world, player, pos, state, true, MINEASSIST_MAX_DISTANCE);
				harvester.setCheckMetadata(true);
				if(block == Blocks.LIT_REDSTONE_ORE){
					harvester.setIdentifyBlocks(new IBlockState[]{ Blocks.REDSTONE_ORE.getBlockState().getBaseState() });
					harvester.setCheckMetadata(false);
				}
				harvester.harvestChain(MINEASSIST_AFFECT_POTION, false);
				e.setCanceled(true);
			}
			// 整地補助機能
			else if(FLATASSIST_ENABLE){
				// ポーションレベルによって採掘範囲を変更
				int distance = 0;
				int[][] affect = null;
				EdgeHarvester harvester = null;
				
				if(		FLATASSIST_HARVESTABLE_ENABLE &&
						player.getFoodStats().getFoodLevel() >= FLATASSIST_HARVESTABLE_REQUIRE_HUNGER &&
						Lib.isHarvestable(state, player.inventory.getCurrentItem()) &&
						Lib.compareCurrentToolLevel(player, FLATASSIST_HARVESTABLE_REQUIRE_TOOL_LEVEL)){
					int plv = Lib.getPotionAffectedLevel(player, FLATASSIST_HARVESTABLE_REQUIRE_POTION_ID);
					int elv = Lib.getEnchentLevel(player, FLATASSIST_HARVESTABLE_REQUIRE_ENCHANT_ID);
					
					distance =
							(FLATASSIST_HARVESTABLE_REQUIRE_ENCHANT_ID <= 0) ? plv :
							(FLATASSIST_HARVESTABLE_REQUIRE_POTION_ID <= 0) ? elv :
							plv > elv ? elv : plv;
					distance = (FLATASSIST_HARVESTABLE_MAX_RADIUS > 0 && distance > FLATASSIST_HARVESTABLE_MAX_RADIUS) ? FLATASSIST_HARVESTABLE_MAX_RADIUS : distance;
					harvester = new EdgeHarvester(world, player, pos, state, FLATASSIST_HARVESTABLE_BELOW, distance);
					affect = FLATASSIST_HARVESTABLE_AFFECT_POTION;
					harvester.setBreakAnything(FLATASSIST_HARVESTABLE_BREAK_ANYTHING);
				}else if(		FLATASSIST_DIRT_ENABLE &&
						player.getFoodStats().getFoodLevel() >= FLATASSIST_DIRT_REQUIRE_HUNGER &&
						Comparator.DIRT.compareBlock(state) &&
						Lib.isShovelOnEquip(player) &&
						Lib.compareCurrentToolLevel(player, FLATASSIST_DIRT_REQUIRE_TOOL_LEVEL)){
					int plv = Lib.getPotionAffectedLevel(player, FLATASSIST_DIRT_REQUIRE_POTION_ID);
					int elv = Lib.getEnchentLevel(player, FLATASSIST_DIRT_REQUIRE_ENCHANT_ID);
					
					distance =
							(FLATASSIST_DIRT_REQUIRE_ENCHANT_ID <= 0) ? plv :
							(FLATASSIST_DIRT_REQUIRE_POTION_ID <= 0) ? elv :
							plv > elv ? elv : plv;
					distance = (FLATASSIST_DIRT_MAX_RADIUS > 0 && distance > FLATASSIST_DIRT_MAX_RADIUS) ? FLATASSIST_DIRT_MAX_RADIUS : distance;
					harvester = new EdgeHarvester(world, player, pos, state, FLATASSIST_DIRT_BELOW, distance);
					affect = FLATASSIST_DIRT_AFFECT_POTION;
					harvester.setBreakAnything(FLATASSIST_DIRT_BREAK_ANYTHING);
				}else if(	FLATASSIST_STONE_ENABLE &&
							player.getFoodStats().getFoodLevel() >= FLATASSIST_STONE_REQUIRE_HUNGER &&
							Comparator.STONE.compareBlock(state) &&
							Lib.isPickaxeOnEquip(player) &&
							Lib.compareCurrentToolLevel(player, FLATASSIST_STONE_REQUIRE_TOOL_LEVEL)){
					int plv = Lib.getPotionAffectedLevel(player, FLATASSIST_STONE_REQUIRE_POTION_ID);
					int elv = Lib.getEnchentLevel(player, FLATASSIST_STONE_REQUIRE_ENCHANT_ID);
					distance =
							(FLATASSIST_STONE_REQUIRE_ENCHANT_ID <= 0) ? plv :
							(FLATASSIST_STONE_REQUIRE_POTION_ID <= 0) ? elv :
							plv > elv ? elv : plv;
					distance = (FLATASSIST_STONE_MAX_RADIUS > 0 && distance > FLATASSIST_STONE_MAX_RADIUS) ? FLATASSIST_STONE_MAX_RADIUS : distance;
					harvester = new EdgeHarvester(world, player, pos, state, FLATASSIST_STONE_BELOW, distance);
					affect = FLATASSIST_STONE_AFFECT_POTION;
					harvester.setBreakAnything(FLATASSIST_STONE_BREAK_ANYTHING);
				}else if(	FLATASSIST_WOOD_ENABLE &&
							player.getFoodStats().getFoodLevel() >= FLATASSIST_WOOD_REQUIRE_HUNGER &&
							Comparator.WOOD.compareBlock(state) &&
							Lib.isAxeOnEquip(player) &&
							Lib.compareCurrentToolLevel(player, FLATASSIST_WOOD_REQUIRE_TOOL_LEVEL)){
					int plv = Lib.getPotionAffectedLevel(player, FLATASSIST_WOOD_REQUIRE_POTION_ID);
					int elv = Lib.getEnchentLevel(player, FLATASSIST_WOOD_REQUIRE_ENCHANT_ID);
					distance =
							(FLATASSIST_WOOD_REQUIRE_ENCHANT_ID <= 0) ? plv :
							(FLATASSIST_WOOD_REQUIRE_POTION_ID <= 0) ? elv :
							plv > elv ? elv : plv;
					distance = (FLATASSIST_WOOD_MAX_RADIUS > 0 && distance > FLATASSIST_WOOD_MAX_RADIUS) ? FLATASSIST_WOOD_MAX_RADIUS : distance;
					harvester = new EdgeHarvester(world, player, pos, state, FLATASSIST_WOOD_BELOW, distance);
					affect = FLATASSIST_WOOD_AFFECT_POTION;
					harvester.setBreakAnything(FLATASSIST_WOOD_BREAK_ANYTHING);
				}
				
				if(distance > 0 && harvester != null){
					// 土・草・菌糸は同一視
					if(block == Blocks.GLASS){
						harvester.setIdentifyBlocks(new IBlockState[]{ Blocks.DIRT.getBlockState().getBaseState(), Blocks.MYCELIUM.getBlockState().getBaseState() });
						harvester.setCheckMetadata(false);
					}else if(block == Blocks.DIRT){
						harvester.setIdentifyBlocks(new IBlockState[]{ Blocks.GLASS.getBlockState().getBaseState(), Blocks.MYCELIUM.getBlockState().getBaseState() });
						harvester.setCheckMetadata(false);
					}else if(block == Blocks.MYCELIUM){
						harvester.setIdentifyBlocks(new IBlockState[]{ Blocks.GLASS.getBlockState().getBaseState(), Blocks.DIRT.getBlockState().getBaseState() });
						harvester.setCheckMetadata(false);
					}
					// 石とシルバーフィッシュは同一視
					else if(block == Blocks.STONE){
						harvester.setIdentifyBlocks(new IBlockState[]{ Blocks.MONSTER_EGG.getBlockState().getBaseState() });
						harvester.setCheckMetadata(false);
					}
					// 丸石とシルバーフィッシュは同一視
					if(block == Blocks.COBBLESTONE){
						harvester.setIdentifyBlocks(new IBlockState[]{ Blocks.MONSTER_EGG.getBlockState().getBaseState() });
						harvester.setCheckMetadata(false);
					}
					// 石レンガとシルバーフィッシュは同一視
					if(block == Blocks.STONEBRICK){
						harvester.setIdentifyBlocks(new IBlockState[]{ Blocks.MONSTER_EGG.getBlockState().getBaseState() });
						harvester.setCheckMetadata(false);
					}
					// シルバーフィッシュは石系ブロックと同一視
					if(block == Blocks.MONSTER_EGG){
						harvester.setIdentifyBlocks(new IBlockState[]{ Blocks.STONE.getBlockState().getBaseState(), Blocks.COBBLESTONE.getBlockState().getBaseState(), Blocks.STONEBRICK.getBlockState().getBaseState() });
						harvester.setCheckMetadata(false);
					}
					// 光った赤石対策
					else if(block == Blocks.LIT_REDSTONE_ORE){
						harvester.setIdentifyBlocks(new IBlockState[]{ Blocks.REDSTONE_ORE.getBlockState().getBaseState() });
						harvester.setCheckMetadata(false);
					}
					
					harvester.harvestChain(affect, true);
					e.setCanceled(true);
				}
			}
			
			isProcessing.remove(player);
		}
	}
	
	public static boolean isEventEnable(){
		return CUTDOWN_ENABLE || FLATASSIST_ENABLE || MINEASSIST_ENABLE;
	}
}
