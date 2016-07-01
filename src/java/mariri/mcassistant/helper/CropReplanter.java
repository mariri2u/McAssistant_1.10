package mariri.mcassistant.helper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CropReplanter {

	public static boolean CROPASSIST_SUPLY = true;
	public static boolean CROPASSIST_AUTOCRAFT;
	
	protected World world;
	protected EntityPlayer player;
	protected BlockPos pos;
	protected int x;
	protected int y;
	protected int z;
	protected IBlockState state;
	protected Block block;
	protected int metadata;
	protected boolean isAffectToolDamage;
	
	private List<ItemStack> drops;
	
	public CropReplanter(World world, EntityPlayer player, BlockPos pos, IBlockState state){
		this.player = player;
		this.world = world;
		this.pos = pos;
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.state = state;
		this.block = state.getBlock();
		this.metadata = block.getMetaFromState(state);
		this.isAffectToolDamage = true;
		drops = new ArrayList<ItemStack>();
	}
	
	public CropReplanter setAffectToolDamage(boolean value){
		this.isAffectToolDamage = value;
		return this;
	}
	
	public void cancelHarvest(){
		// ドロップアイテムがある場合削除
		EntityItem entity = null;
		List<EntityItem> entityList =
				world.getEntitiesWithinAABB(EntityItem.class,
				new AxisAlignedBB(x - 1, y - 1, z - 1, x + 2, y + 2, z + 2));
		for(EntityItem item : entityList){
			if(Comparator.SEED.compareItem(item.getEntityItem().getItem())){
				entity = item;
			}
		}
		
		if(entity != null){
			((EntityItem)entity).setDead();
		}
		
		// 植え直し
		world.setBlockState(pos, state, 4);
	}
	
	public void findDrops(){
		List<EntityItem> entityList = world.getEntitiesWithinAABB(EntityItem.class,
				new AxisAlignedBB(x - 1, y - 1, z - 1, x + 2, y + 2, z + 2));
		for(EntityItem item : entityList){
			drops.add(item.getEntityItem());
		}
	}
	
	public void harvestCrop(){
		ItemStack seed = null;
		
		// ドロップアイテムから種を探す
		for(ItemStack itemstack : drops){
			if(Comparator.SEED.compareItem(itemstack.getItem())){
				seed = itemstack;
			}
		}
		
		// 種がない場合自動クラフト
		if(CROPASSIST_AUTOCRAFT && seed == null){
			ItemStack product = null;
			ItemStack material = null;
			for(ItemStack m : drops){
				InventoryCrafting recipe = new InventoryCrafting(player.inventoryContainer, 1, 1);
				recipe.setInventorySlotContents(0, m);
				ItemStack p = CraftingManager.getInstance().findMatchingRecipe(recipe, world);
				if(p != null && Comparator.SEED.compareItem(p.getItem())){
					product = p;
					material = m;
				}
			}
			if(product != null){
				// クラフト結果がインベントリにあったとき
				int index = -1;
				for(int i = 0; i < player.inventory.mainInventory.length; i++){
					ItemStack itemstack = player.inventory.mainInventory[i];
					if(itemstack.getItem() == product.getItem()){
						index = i;
					}
				}
				if(CROPASSIST_SUPLY && index >= 0){
					player.inventory.decrStackSize(index, 1);
					seed = new ItemStack(product.getItem(), 1);
				}else if(product.stackSize == 1){
					seed = product;
					material.stackSize--;
				}else if(product.stackSize > 1){
					seed = new ItemStack(product.getItem(), 1);
					product.stackSize--;
					material.stackSize--;
					Lib.spawnItem(world, x, y, z, product);
				}
			}
		}
		
		// インベントリから補充（小麦だけ）
		if(seed == null && CROPASSIST_SUPLY){
			int index = -1;
			for(int i = 0; i < player.inventory.mainInventory.length; i++){
				ItemStack itemstack = player.inventory.mainInventory[i];
				if(itemstack != null && itemstack.getItem() == Items.WHEAT_SEEDS){
					index = i;
				}
			}
			if(block.equals(Blocks.WHEAT) && index >= 0){
				player.inventory.decrStackSize(index, 1);
				seed = new ItemStack(Items.WHEAT_SEEDS, 1);
			}
		}
		
		if(seed != null){
			// 植え直しできた場合
			if(seed.getItem().onItemUse(seed, player, world, new BlockPos(x, y - 1, z), EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0) == EnumActionResult.SUCCESS){
				
				if(isAffectToolDamage){
					ItemStack citem = player.inventory.getCurrentItem();
					if(citem != null && citem.getItem() instanceof ItemHoe){
						if(player.inventory.getCurrentItem().attemptDamageItem(1, player.getRNG())){
							player.inventory.deleteStack(player.inventory.getCurrentItem());
							
							world.playSound(player, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
						}
					}else{
						citem.getItem().onBlockDestroyed(citem, world, Blocks.FARMLAND.getDefaultState(), pos, player);
						if(citem.stackSize <= 0){
							player.inventory.deleteStack(player.inventory.getCurrentItem());
							world.playSound(player, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
						}
					}
				}
			}
		}
	}
}
