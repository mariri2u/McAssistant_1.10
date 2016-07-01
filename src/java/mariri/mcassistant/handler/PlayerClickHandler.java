package mariri.mcassistant.handler;

import java.util.ArrayList;
import java.util.List;

import mariri.mcassistant.helper.Comparator;
import mariri.mcassistant.helper.CropReplanter;
import mariri.mcassistant.helper.Lib;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerClickHandler {
	
	public static PlayerClickHandler INSTANCE = new PlayerClickHandler();
	
	public static boolean TORCHASSIST_ENABLE;
	
	public static boolean CROPASSIST_ENABLE = true;
	public static int CROPASSIST_REQUIRE_TOOL_LEVEL;
	public static boolean CROPASSIST_AREA_ENABLE;
	public static int[] CROPASSIST_AREA_REQUIRE_TOOL_LEVEL;
	public static int[][] CROPASSIST_AREA_AFFECT_POTION;
	public static boolean CROPASSIST_AREAPLUS_ENABLE;
	public static int[] CROPASSIST_AREAPLUS_REQUIRE_TOOL_LEVEL;
	
	public static boolean LEAVEASSIST_ENABLE;
	public static int[][] LEAVEASSIST_AFFECT_POTION;
	public static boolean LEAVEASSIST_AREAPLUS_ENABLE;
	public static int[] LEAVEASSIST_AREAPLUS_REQUIRE_TOOL_LEVEL;
	
	public static boolean BEDASSIST_ENABLE;
	public static boolean BEDASSIST_SET_RESPAWN_ANYTIME;
	public static String BEDASSIST_SET_RESPAWN_MESSAGE;
	public static boolean BEDASSIST_NO_SLEEP;
	public static String BEDASSIST_NO_SLEEP_MESSAGE;
	
	public static boolean CULTIVATEASSIST_ENABLE;
	public static int[] CULTIVATEASSIST_REQUIRE_TOOL_LEVEL;
	public static int[][] CULTIVATEASSIST_AFFECT_POTION;
	public static boolean CULTIVATEASSIST_AREAPLUS_ENABLE;
	public static int[] CULTIVATEASSIST_AREAPLUS_REQUIRE_TOOL_LEVEL;
	
	public static boolean SNEAK_INVERT;
	
	private static List<EntityPlayer> isProcessing = new ArrayList<EntityPlayer>();
	
	private PlayerClickHandler(){}

	@SubscribeEvent
	public void onPlayerRightClick(PlayerInteractEvent.RightClickBlock e){
		if(!isProcessing.contains(e.getEntityPlayer()) && !e.getEntityPlayer().worldObj.isRemote && e.getEntityPlayer().isSneaking() == SNEAK_INVERT){
			isProcessing.add(e.getEntityPlayer());
			onRightClickBlock(e);
			isProcessing.remove(e.getEntityPlayer());
		}
	}

	@SubscribeEvent
	public void onPlayerLeftClick(PlayerInteractEvent.LeftClickBlock e){
		if(!isProcessing.contains(e.getEntityPlayer()) && !e.getEntityPlayer().worldObj.isRemote && e.getEntityPlayer().isSneaking() == SNEAK_INVERT){
			isProcessing.add(e.getEntityPlayer());
			onLeftClickBlock(e);
			isProcessing.remove(e.getEntityPlayer());
		}
	}

	private void onRightClickBlock(PlayerInteractEvent e){
		World world = e.getEntityPlayer().worldObj;
		IBlockState state = world.getBlockState(e.getPos());
		Block block = state.getBlock();
		int meta = block.getMetaFromState(state);
		EnumHand hand = EnumHand.MAIN_HAND;
		// ベッド補助機能
		if(BEDASSIST_ENABLE && block == Blocks.BED){
			// いつでもリスポーンセット
			if(BEDASSIST_SET_RESPAWN_ANYTIME){
//	        	ChunkCoordinates respawn = new ChunkCoordinates(e.x, e.y, e.z);
	            if (	world.provider.canRespawnHere() &&
	            		world.getBiomeGenForCoords(e.getPos()) != Biomes.HELL &&
	            		world.provider.isSurfaceWorld() &&
	            		e.getEntityPlayer().isEntityAlive() &&
	            		world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(e.getPos().getX() - 8, e.getPos().getY() - 5, e.getPos().getZ() - 8, e.getPos().getX() + 8, e.getPos().getY() + 5, e.getPos().getZ() + 8)).isEmpty()){
	                e.getEntityPlayer().setSpawnChunk(e.getPos(), false, e.getEntityPlayer().dimension);
	                e.getEntityPlayer().addChatComponentMessage(new TextComponentString(BEDASSIST_SET_RESPAWN_MESSAGE));
	            }
			}
			// 寝るの禁止
			if(BEDASSIST_NO_SLEEP){
                e.getEntityPlayer().addChatComponentMessage(new TextComponentString(BEDASSIST_NO_SLEEP_MESSAGE));
                e.setCanceled(true);
			}
		}
		// 農業補助機能
		else if(		CROPASSIST_ENABLE && !world.isAirBlock(e.getPos()) &&
				Comparator.CROP.compareBlock(state) &&
				Comparator.HOE.compareCurrentItem(e.getEntityPlayer()) &&
				Lib.compareCurrentToolLevel(e.getEntityPlayer(), CROPASSIST_REQUIRE_TOOL_LEVEL)){
			if(CROPASSIST_AREA_ENABLE && Lib.compareCurrentToolLevel(e.getEntityPlayer(), CROPASSIST_AREA_REQUIRE_TOOL_LEVEL)){
				int count = 0;
				int area = (CROPASSIST_AREAPLUS_ENABLE && Lib.compareCurrentToolLevel(e.getEntityPlayer(), CROPASSIST_AREAPLUS_REQUIRE_TOOL_LEVEL)) ? 2 : 1;
				for(int xi = -1 * area; xi <= area; xi++){
					for(int zi = -1 * area; zi <= area; zi++){
						BlockPos newpos = new BlockPos(e.getPos().getX() + xi, e.getPos().getY(), e.getPos().getZ() + zi);
						IBlockState s = world.getBlockState(newpos);
						Block b = s.getBlock();
						if(block == b){
							b.onBlockActivated(world, newpos, s, e.getEntityPlayer(), hand, e.getEntityPlayer().inventory.getCurrentItem(), e.getFace(), 0, 0, 0);
							count++;
						}
					}
				}
				ItemStack citem = e.getEntityPlayer().inventory.getCurrentItem();
				if(citem.getItem() instanceof ItemHoe){
					if(e.getEntityPlayer().inventory.getCurrentItem().attemptDamageItem(1, e.getEntityPlayer().getRNG())){
						e.getEntityPlayer().inventory.deleteStack(e.getEntityPlayer().inventory.getCurrentItem());
						world.playSound(e.getEntityPlayer(), e.getPos(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
				}else{
					citem.getItem().onBlockDestroyed(citem, world, Blocks.FARMLAND.getDefaultState(), e.getPos(), e.getEntityPlayer());
					if(citem.stackSize <= 0){
						e.getEntityPlayer().inventory.deleteStack(e.getEntityPlayer().inventory.getCurrentItem());
						world.playSound(e.getEntityPlayer(), e.getPos(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
				}
				Lib.affectPotionEffect(e.getEntityPlayer(), CROPASSIST_AREA_AFFECT_POTION, count);
				e.setCanceled(true);
			}
		}
		// 耕地化補助機能
		else if(	CULTIVATEASSIST_ENABLE &&
					(block == Blocks.DIRT || block == Blocks.GRASS) &&
					Comparator.HOE.compareCurrentItem(e.getEntityPlayer()) &&
					Lib.compareCurrentToolLevel(e.getEntityPlayer(), CULTIVATEASSIST_REQUIRE_TOOL_LEVEL)){
			int count = 0;
			int area = (CULTIVATEASSIST_AREAPLUS_ENABLE && Lib.compareCurrentToolLevel(e.getEntityPlayer(), CULTIVATEASSIST_AREAPLUS_REQUIRE_TOOL_LEVEL)) ? 2 : 1;
			for(int xi = -1 * area; xi <= area; xi++){
				for(int zi = -1 * area; zi <= area; zi++){
					ItemStack current = e.getEntityPlayer().inventory.getCurrentItem();
					if(current.getItem().onItemUse(current, e.getEntityPlayer(), world, new BlockPos(e.getPos().getX() + xi, e.getPos().getY(), e.getPos().getZ() + zi), hand, e.getFace(), 0, 0, 0) == EnumActionResult.SUCCESS){
						count++;
					}
				}
			}
			Lib.affectPotionEffect(e.getEntityPlayer(), CULTIVATEASSIST_AFFECT_POTION, count);
			e.setCanceled(true);
		}
		// 葉っぱ破壊補助機能
		else if(		LEAVEASSIST_ENABLE && !world.isAirBlock(e.getPos()) &&
				Comparator.LEAVE.compareBlock(state) &&
				Lib.isAxeOnEquip(e.getEntityPlayer()) ){
			int count = 0;
			int area = (LEAVEASSIST_AREAPLUS_ENABLE && Lib.compareCurrentToolLevel(e.getEntityPlayer(), LEAVEASSIST_AREAPLUS_REQUIRE_TOOL_LEVEL)) ? 2 : 1;
			for(int x = e.getPos().getX() - area; x <= e.getPos().getX() + area; x++){
				for(int y = e.getPos().getY() - area; y <= e.getPos().getY() + area; y++){
					for(int z = e.getPos().getZ() - area; z <= e.getPos().getZ() + area; z++){
						BlockPos pos = new BlockPos(x, y, z);
						IBlockState s = world.getBlockState(pos);
						Block b = s.getBlock();
						int m = b.getMetaFromState(s);
						if(Comparator.LEAVE.compareBlock(s)){
							b.dropBlockAsItem(world, pos, s, 0);
							world.setBlockToAir(pos);
							count++;
						}
					}
				}
			}
			world.playSound(e.getEntityPlayer(), e.getPos(), SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            
			ItemStack citem = e.getEntityPlayer().inventory.getCurrentItem();
			citem.getItem().onBlockDestroyed(citem, world, state, e.getPos(), e.getEntityPlayer());
			if(citem.stackSize <= 0){
				e.getEntityPlayer().inventory.deleteStack(e.getEntityPlayer().inventory.getCurrentItem());
				world.playSound(e.getEntityPlayer(), e.getPos(), new SoundEvent(new ResourceLocation("random.break")), SoundCategory.PLAYERS, 1.0F, 1.0F);
			}            
			Lib.affectPotionEffect(e.getEntityPlayer(), LEAVEASSIST_AFFECT_POTION, count);
			e.setCanceled(true);
		}
		// トーチ補助機能
		else if(		TORCHASSIST_ENABLE &&
				(Lib.isPickaxeOnEquip(e.getEntityPlayer()) || Lib.isShovelOnEquip(e.getEntityPlayer())) ){
			
			ItemStack current = e.getEntityPlayer().inventory.getCurrentItem();
			ItemStack torch = new ItemStack(Blocks.TORCH, 1);
			// トーチを持っている場合
			int index = -1;
			for(int i = 0; i < e.getEntityPlayer().inventory.mainInventory.length; i++){
				ItemStack itemstack = e.getEntityPlayer().inventory.mainInventory[i];
				if(itemstack != null && itemstack.getItem() instanceof ItemBlock){
					if(((ItemBlock)itemstack.getItem()).block == Blocks.TORCH){
						index = i;
					}
				}
			}
			if(index >= 0){
				// トーチを設置できた場合
				if(		!world.getBlockState(e.getPos()).getBlock().onBlockActivated(world, e.getPos(), world.getBlockState(e.getPos()), e.getEntityPlayer(), EnumHand.MAIN_HAND, e.getEntityPlayer().inventory.getCurrentItem(), e.getFace(), 0, 0, 0) &&
						current.getItem().onItemUse(current, e.getEntityPlayer(), world, e.getPos(), EnumHand.MAIN_HAND, e.getFace(), 0, 0, 0) == EnumActionResult.PASS &&
						torch.getItem().onItemUse(torch, e.getEntityPlayer(), world, e.getPos(), EnumHand.MAIN_HAND, e.getFace(), 0, 0, 0) == EnumActionResult.SUCCESS){
					e.getEntityPlayer().inventory.decrStackSize(index, 1);
					// トーチの使用をクライアントに通知
					e.getEntityPlayer().onUpdate();
					world.playSound(e.getEntityPlayer(), e.getPos(), SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}
				// 対象ブロックに対する右クリック処理をキャンセル
				e.setCanceled(true);
			}
		}
	}
	
	private void onLeftClickBlock(PlayerInteractEvent e){
		World world = e.getEntityPlayer().worldObj;
		IBlockState state = world.getBlockState(e.getPos());
		Block block = state.getBlock();
		int meta = block.getMetaFromState(state);
		// 農業補助機能
		if(		CROPASSIST_ENABLE && !world.isAirBlock(e.getPos()) &&
				Comparator.CROP.compareBlock(state) &&
				Comparator.HOE.compareCurrentItem(e.getEntityPlayer()) &&
				Lib.compareCurrentToolLevel(e.getEntityPlayer(), CROPASSIST_REQUIRE_TOOL_LEVEL)){
			if(CROPASSIST_AREA_ENABLE && Lib.compareCurrentToolLevel(e.getEntityPlayer(), CROPASSIST_AREA_REQUIRE_TOOL_LEVEL)){
				int count = 0;
				int area = (CROPASSIST_AREAPLUS_ENABLE && Lib.compareCurrentToolLevel(e.getEntityPlayer(), CROPASSIST_AREAPLUS_REQUIRE_TOOL_LEVEL)) ? 2 : 1;
				for(int xi = -1 * area; xi <= area; xi++){
					for(int zi = -1 * area; zi <= area; zi++){
						BlockPos p = new BlockPos(e.getPos().getX() + xi, e.getPos().getY(), e.getPos().getZ() + zi);
						IBlockState s = world.getBlockState(p);
						Block b = s.getBlock();
						int m = b.getMetaFromState(s);
						if(block == b && meta == m && (b instanceof BlockContainer || m > 0)){
							CropReplanter harvester = new CropReplanter(world, e.getEntityPlayer(), p, s);
							harvester.setAffectToolDamage(xi == 0 && zi == 0);
							b.harvestBlock(world, e.getEntityPlayer(), p, s, world.getTileEntity(p), e.getEntityPlayer().inventory.getCurrentItem());
							world.setBlockToAir(p);
							harvester.findDrops();
							harvester.harvestCrop();
							count++;
						}
					}
				}
				world.playSound(e.getEntityPlayer(), e.getPos(), SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
				Lib.affectPotionEffect(e.getEntityPlayer(), CROPASSIST_AREA_AFFECT_POTION, count);
			}else{
				// 収穫後の連続クリック対策（MOD独自の方法で成長を管理している場合は対象外）
				if(block instanceof BlockContainer || meta > 0){
					CropReplanter harvester = new CropReplanter(world, e.getEntityPlayer(), e.getPos(), state);
					block.harvestBlock(world, e.getEntityPlayer(), e.getPos(), state, world.getTileEntity(e.getPos()), e.getEntityPlayer().inventory.getCurrentItem());
					world.setBlockToAir(e.getPos());
					harvester.findDrops();
					harvester.harvestCrop();
					world.playSound(e.getEntityPlayer(), e.getPos(), SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}
			}
			e.setCanceled(true);
		}
	}
	
	public static boolean isEventEnable(){
		return BEDASSIST_ENABLE || CROPASSIST_ENABLE || LEAVEASSIST_ENABLE || TORCHASSIST_ENABLE;
	}
}
