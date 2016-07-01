package mariri.mcassistant.helper;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EdgeHarvester {

	private int count;
	private boolean below;
	private int maxDist;
	private IBlockState[] identifies;
	private Comparator idCompare;
	private boolean dropAfter;
	private boolean isReplant;
	private List<ItemStack> drops;
	private boolean currentIdentify;
	private boolean targetIdentify;
	private int horizonalMaxOffset;
	private Coord coreCoord;
	private boolean idBreakTool;
	private int findRange;
	private boolean breakAnything;
	
	protected World world;
	protected EntityPlayer player;
	protected IBlockState state;
	protected Block block;
	protected int metadata;
	protected LinkedList<Coord> path;

	protected boolean checkMeta;
	
	public EdgeHarvester(World world, EntityPlayer player, BlockPos pos, IBlockState state, boolean below, int dist){
		this.player = player;
		this.world = world;
		this.path = new LinkedList<Coord>();
		this.path.addLast(new Coord(pos.getX(), pos.getY(), pos.getZ()));
		this.state = state;
		this.block = state.getBlock();
		this.metadata = block.getMetaFromState(state);
		this.below = below;
		this.maxDist = dist;
		this.count = 0;
		this.checkMeta = true;
		this.horizonalMaxOffset = 0;
		this.drops = new LinkedList<ItemStack>();
		this.idBreakTool = true;
		this.findRange = 1;
		this.breakAnything = false;
	}
	
	public EdgeHarvester setIdentifyBlocks(IBlockState[] blocks){
		identifies = blocks;
		return this;
	}
	
	public EdgeHarvester setIdentifyComparator(Comparator value){
		this.idCompare = value;
		return this;
	}
	
	public EdgeHarvester setReplant(boolean value){
		this.isReplant = true;
		return this;
	}
	
	public EdgeHarvester setDropAfter(boolean value){
		this.dropAfter = value;
		return this;
	}
	
	public EdgeHarvester setCheckMetadata(boolean value){
		this.checkMeta = value;
		return this;
	}
	
	public EdgeHarvester setHorizonalMaxOffset(int value){
		this.horizonalMaxOffset = value;
		return this;
	}
	
	public EdgeHarvester setIdentifyBreakTool(boolean value){
		this.idBreakTool = value;
		return this;
	}
	
	public EdgeHarvester setFindRange(int value){
		this.findRange = value;
		return this;
	}
	
	public EdgeHarvester setBreakAnything(boolean value){
		this.breakAnything = value;
		return this;
	}
	
	private int getDistance(Coord c, boolean square){
		return getDistance(c.x, c.y, c.z, square);
	}
	
	private int getDistance(int x, int y, int z, boolean square){
		Coord target = path.getFirst();
		if(square){
			return Math.max(Math.abs(x - target.x), Math.max(Math.abs(y - target.y), Math.abs(z - target.z)));
		}else{
			return Math.abs(x - target.x) + Math.abs(y - target.y) + Math.abs(z - target.z);
		}
	}
	
	private int getHorizonalDistance(int x, int y, int z, boolean square){
		Coord target = path.getFirst();
		if(square){
			return Math.max(Math.abs(x - target.x), Math.abs(z - target.z));
		}else{
			return Math.abs(x - target.x) + Math.abs(z - target.z);
		}
	}
	
//	private void debugOutput(String prefix, Coord c, String sufix){
//		System.out.println(prefix + " (" + c.x + ", " + c.y + ", " + c.z + ") " + sufix);
//	}
	
	public int harvestChain(){
		return harvestChain(null, false);
	}
	
	public int harvestChain(int[][] potion, boolean square){
		while(player.inventory.getCurrentItem() != null && findEdge(square) >= 0){
			harvestEdge();
		}
		if(dropAfter){
			// -- 再植え付け --
			for(ItemStack items : drops){
				Coord c = path.getFirst();
				if(		Comparator.SAPLING.compareItem(items) &&
						world.isAirBlock(c.getPos()) &&
						Comparator.DIRT.compareBlock(world.getBlockState(c.getUnderPos()))){
//					items.getItem().onItemUse(items, player, world, c.x, c.y, c.z, 0, 0, 0, 0);
					world.setBlockState(c.getPos(), ((ItemBlock)items.getItem()).block.getStateFromMeta(items.getItemDamage()), 2);
					items.stackSize--;
				}
			}
			Coord target = path.getFirst();
			Lib.spawnItem(world, target.x, target.y, target.z, drops);
		}
		Lib.affectPotionEffect(player, potion, count);
		return count;
	}
	
	public int findEdge(boolean square){
		Coord edge = path.getLast().copy();
		Coord prev = edge.copy();
		int dist = getDistance(edge, square);
		for(int x = prev.x - findRange; x <= prev.x + findRange; x++){
			for(int y = prev.y + findRange; y >= prev.y - findRange; y--){
				for(int z = prev.z - findRange; z <= prev.z + findRange; z++){
					int d = getDistance(x, y, z, square);
					if(isHarvestableEdge(new BlockPos(x, y, z), edge, prev, dist, d)){
						edge.x = x;
						edge.y = y;
						edge.z = z;
						path.addLast(new Coord(x, y, z));
						dist = d;
						targetIdentify = currentIdentify;
					}
				}
			}
		}
		if(!(edge.x == prev.x && edge.y == prev.y && edge.z == prev.z) && dist <= maxDist){
			findEdge(square);
		}
		if(count > 0 && path.size() <= 1) {
			if(world.isAirBlock(path.getFirst().getPos())){
				return -1;
			}else{
				return 0;
			}
		}
		return dist;
	}
	
	private boolean isHarvestableEdge(BlockPos pos, Coord edge, Coord prev, boolean square){
		return isHarvestableEdge(pos, edge, prev, getDistance(edge, square), getDistance(prev, square));
	}
	
	private boolean isHarvestableEdge(BlockPos pos, Coord edge, Coord prev, int edgeDist, int prevDist){
		boolean result = false;
		if(		(below || pos.getY() >= edge.y) &&
				matchBlock(pos) && edgeDist <= prevDist && prevDist <= maxDist){
			if(horizonalMaxOffset > 0){
				if(currentIdentify && getHorizonalDistance(pos.getX(), pos.getY(), pos.getZ(), true) <= horizonalMaxOffset){
					result = true;
				}else if(world.getBlockState(prev.getPos()).getBlock() == block){
					result = true;
				}
			}else{
				result = true;
			}
		}
		return result;
	}
	
//	private boolean checkIdentify(int x, int y, int z){
//		boolean result = false;
//		for(ItemStack identify : identifies){
//			result |= matchBlock(x, y, z, ((ItemBlock)identify.getItem()).field_150939_a, identify.getItemDamage());
//		}
//		return result;
//	}
	
	private boolean matchBlock(BlockPos pos){
		boolean result = false;
		
		if(breakAnything){
			result |= Lib.isHarvestable(world.getBlockState(pos), player.inventory.getCurrentItem());
		}
		
		result |= matchBlock(pos, state);
		currentIdentify = false;
		if(!result && idCompare != null){
			IBlockState s = world.getBlockState(pos);
			Block b = s.getBlock();
			int m = b.getMetaFromState(s);
			if(idCompare.compareBlock(s)){
				result = true;
				currentIdentify = true;
			}
		}
		if(!result && identifies != null){
			for(IBlockState identify : identifies){
				result |= matchBlock(pos, identify);
			}
			currentIdentify = result;
		}
		return result;
	}
	
	private boolean matchBlock(BlockPos pos, IBlockState state){
		boolean result = false;
		result |= world.getBlockState(pos).getBlock() == state.getBlock();
		if(checkMeta){
			result &= world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)) == state.getBlock().getMetaFromState(state);
		}
		return result;
	}

	
	public void harvestEdge(){
		if(path.size() <= 1){
			findEdge(false);
		}
		int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.inventory.getCurrentItem());
		boolean silktouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.inventory.getCurrentItem()) > 0;
		Coord edge = path.getLast();
		BlockPos edpos = edge.getPos();
		IBlockState edst = world.getBlockState(edpos);
		Block edblk = edst.getBlock();
		int edmeta = edblk.getMetaFromState(edst);
		int exp = edblk.getExpDrop(edblk.getStateFromMeta(edmeta), world, edpos, fortune);
		world.setBlockToAir(edpos);
		edblk.onBlockDestroyedByPlayer(world, edpos, edst);
		// 葉っぱブロック破壊時はシルクタッチを無視する
		if(isSilkHarvest(edpos, edst)){
			ItemStack drop = new ItemStack(edblk, 1, edmeta);
			if(edblk == Blocks.LIT_REDSTONE_ORE){
				drop = new ItemStack(Blocks.REDSTONE_ORE);
			}
			if(dropAfter) { drops.add(drop); }
			else{ Lib.spawnItem(world, edge.x, edge.y, edge.z, drop); }
		}else{
			edblk.dropBlockAsItem(world, edpos, edst, fortune);
			if(dropAfter){
				List<EntityItem> entityList = world.getEntitiesWithinAABB(EntityItem.class,
						new AxisAlignedBB(edge.x - 1, edge.y - 1, edge.z - 1, edge.x + 2, edge.y + 2, edge.z + 2));
				for(EntityItem item : entityList){
					drops.add(item.getEntityItem().copy());
					item.getEntityItem().stackSize = 0;
				}
			}
//			List<ItemStack> drop = edblk.getDrops(world, edge.x, edge.y, edge.z, edmeta, fortune);
//			if(dropAfter && drop != null && drop.size() > 0) {
//				for(ItemStack d : drop){ drops.add(d); }
//			}
//			else { Lib.spawnItem(world, edge.x, edge.y, edge.z, edblk.getDrops(world, edge.x, edge.y, edge.z, edmeta, fortune)); }
			edblk.dropXpOnBlockBreak(world, edge.getPos(), exp);
		}
		// 武器の耐久値を減らす
		if(		player.inventory.getCurrentItem() != null && edblk != Blocks.AIR &&
				(!targetIdentify || idBreakTool) /* 葉っぱブロック破壊時は耐久消費無し */){
			ItemStack citem = player.inventory.getCurrentItem();
			citem.getItem().onBlockDestroyed(citem, world, edblk.getStateFromMeta(edmeta), edpos, player);
			if(citem.stackSize <= 0){
				player.inventory.deleteStack(player.inventory.getCurrentItem());
//	            world.playSoundAtEntity(player, "random.break", 1.0F, 1.0F);
			}
		}
		
		if(path.size() > 1){
			path.removeLast();
		}
		count++;
	}
	
	private boolean isSilkHarvest(BlockPos pos, IBlockState state){
		boolean result = false;
		boolean silktouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.inventory.getCurrentItem()) > 0;
		if(horizonalMaxOffset > 0 && targetIdentify){
			result = false;
		}else if(silktouch && block.canSilkHarvest(world, pos, state, player)){
			result = true;
		}
		return result;
	}
	
	protected class Coord {
		public int x;
		public int y;
		public int z;
		public Coord(int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public Coord(BlockPos pos){
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
		}
		
		public BlockPos getPos(){
			return new BlockPos(x, y, z);
		}
		
		public BlockPos getUnderPos(){
			return new BlockPos(x, y - 1, z);
		}
		
		@Override
		public String toString(){
			return x + ", " + y + ", " + z;
		}
		
		public Coord copy(){
			return new Coord(x, y, z);
		}
	}
}
