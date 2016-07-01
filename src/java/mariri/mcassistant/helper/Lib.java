package mariri.mcassistant.helper;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class Lib {
	
//	public static void unify(ItemStack drop){
//		List<ItemStack> oredict = Comparator.UNIFY.findOreDict(drop);
//		if(oredict != null && oredict.size() > 0){
//			// ドロップアイテムの書き換え
//			drop.func_150996_a(oredict.get(0).getItem());
//			drop.setItemDamage(oredict.get(0).getItemDamage());
//		}
//	}
	
	public static boolean COMPARE_TOOL_CLASSS;
	public static boolean COMPARE_IS_HARVESTABLE;
	
	public static boolean isPickaxeOnEquip(EntityPlayer player){
		if(player.inventory.getCurrentItem() == null){
			return false;
		}else{
			return compareToolClass(Blocks.STONE, 0, player.inventory.getCurrentItem(), Comparator.PICKAXE, "pickaxe");
		}
	}

	public static boolean isShovelOnEquip(EntityPlayer player){
		if(player.inventory.getCurrentItem() == null){
			return false;
		}else{
			return compareToolClass(Blocks.DIRT, 0, player.inventory.getCurrentItem(), Comparator.SHOVEL, "shovel");
		}
	}
	
	public static boolean isAxeOnEquip(EntityPlayer player){
		if(player.inventory.getCurrentItem() == null){
			return false;
		}else{
			return compareToolClass(Blocks.LOG, 0, player.inventory.getCurrentItem(), Comparator.AXE, "axe");
		}
	}
	
	public static boolean compareToolClass(Block block, int meta, ItemStack tool, Comparator comparator, String toolClass){
		boolean result = false;
		try{
			if(comparator.compareDisallow(tool.getItem())){
				return false;
			}
			Set<String> toolClasses = tool.getItem().getToolClasses(tool);
			if(COMPARE_TOOL_CLASSS){
				if(toolClasses.size() > 0){
					for(String tc : toolClasses){
						result |= tc == toolClass;
					}
				}else if(COMPARE_IS_HARVESTABLE){
//					result |= Lib.isHarvestable(block, meta, player.inventory.getCurrentItem());
					result |= tool.getItem().canHarvestBlock(block.getStateFromMeta(meta), tool);
				}
			}
			if(!result){
				result |= comparator.compareItem(tool);
			}
		}catch(NullPointerException e) {
			result = false;
		}
		return result;
	}
	
	public static void affectPotionEffect(EntityPlayer player, int[][] potion, int count){
		if(count > 1 && potion != null && potion.length > 0){
			for(int[] pote : potion){
				if(pote != null && pote.length == 3){
					PotionEffect effect = player.getActivePotionEffect(Potion.getPotionById(pote[0]));
					if(effect != null && effect.getAmplifier() == pote[1] - 1){
						player.addPotionEffect(new PotionEffect(Potion.getPotionById(pote[0]), effect.getDuration() + pote[2] * count, pote[1] - 1));
					}else{
						player.addPotionEffect(new PotionEffect(Potion.getPotionById(pote[0]), pote[2] * count, pote[1] - 1));
					}
				}
			}
		}
	}
	
	
	public static Item.ToolMaterial getMaterial(String material){
		Item.ToolMaterial[] marr = Item.ToolMaterial.values();
		Item.ToolMaterial mm = null;
		for(Item.ToolMaterial m : marr){
			if(m.name().equals(material)){
				mm = m;
			}
		}
		return mm;
	}
	
	public static Item.ToolMaterial getMaterial(Item item){
		Item.ToolMaterial m = null;
		if(item != null){
			if(item instanceof ItemTool){
				m = getMaterial(((ItemTool)item).getToolMaterialName());
			}else if(item instanceof ItemHoe){
				m = getMaterial(((ItemHoe)item).getMaterialName());
			}else if(item instanceof ItemSword){
				m = getMaterial(((ItemSword)item).getToolMaterialName());
			}
		}
		return m;
	}
	
	public static boolean isHarvestable(IBlockState state, ItemStack itemstack){
		Block block = state.getBlock();
		int meta = block.getMetaFromState(state);
		if(block == Blocks.AIR) { return false; }
		boolean result = true;
		try{
			result &= getHarvestLevel(itemstack) >= block.getHarvestLevel(state);
			boolean  r = false;
			Set<String> toolClasses = itemstack.getItem().getToolClasses(itemstack);
			for(String tc : toolClasses){
				r |= tc == block.getHarvestTool(state);
			}
			result &= r;
//			result |= block.getHarvestTool(metadata) == null;
			result |= itemstack.getItem().canHarvestBlock(state, itemstack);
		}catch(NullPointerException e){
			result = false;
		}
		return result;
	}
	
	public static int getHarvestLevel(ItemStack itemstack){
		Set<String> toolClasses = itemstack.getItem().getToolClasses(itemstack);
		int level = itemstack.getItem().getHarvestLevel(itemstack, "");
		for(String tc : toolClasses){
			int l = itemstack.getItem().getHarvestLevel(itemstack, tc);
			if(l > level){
				level = l;
			}
		}
		return level;
	}
	
	public static boolean compareCurrentToolLevel(EntityPlayer player, int level){
		boolean result = false;
		if(level <= 0) { return true; }
		try{
			int lv = getHarvestLevel(player.inventory.getCurrentItem());
			result = lv >= level;
			if(lv <= 0){
				Item.ToolMaterial material = getMaterial(player.inventory.getCurrentItem().getItem());
				result = material.getHarvestLevel() >= level;
			}
		}catch(NullPointerException e){}
		return result;
	}
	
	public static boolean compareCurrentToolLevel(EntityPlayer player, int min, int max){
		boolean result = false;
		try{
			int lv = getHarvestLevel(player.inventory.getCurrentItem()); 
			result = lv >= min && lv <= max;
			if(lv <= 0){
				Item.ToolMaterial material = getMaterial(player.inventory.getCurrentItem().getItem());
				result = material.getHarvestLevel() >= min && material.getHarvestLevel() <= max;
			}
		}catch(NullPointerException e){}
		return result;
	}
	
	public static boolean compareCurrentToolLevel(EntityPlayer player, int[] level){
		if(level.length == 1){
			return compareCurrentToolLevel(player, level[0]);
		}else if(level.length == 2){
			return compareCurrentToolLevel(player, level[0], level[1]);
		}else{
			return false;
		}
	}
	
	public static boolean compareCurrentToolClass(EntityPlayer player, String name){
		boolean result = false;
		try{
			ItemStack current = player.inventory.getCurrentItem();
			for(String c : current.getItem().getToolClasses(player.inventory.getCurrentItem())){
				result |= c.equals(name);
			}
		}catch(NullPointerException e){}
		return result;
	}
	
	public static int getPotionAffectedLevel(EntityLivingBase entity, int id){
		int result = 0;
		if(id <= 0){ return 0; }
		PotionEffect effect = entity.getActivePotionEffect(Potion.getPotionById(id));
		if(effect != null){
			result |= effect.getAmplifier() + 1;
		}
		return result;
	}
	
	public static boolean isPotionAffected(EntityLivingBase entity, int id, int lv){
		boolean result = false;
		if(id <= 0){ return true; }
		PotionEffect effect = entity.getActivePotionEffect(Potion.getPotionById(id));
		if(lv <= 0){
			result |= true;
		}else{
			result |= getPotionAffectedLevel(entity, id) >= lv;
		}
		return result;
	}
	
	public static boolean isEnchanted(EntityPlayer entity, int id, int lv){
		boolean result = false;
		int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(id), entity.inventory.getCurrentItem());
		result = l >= lv;
		return result;
	}
	
	public static boolean isEnchanted(EntityPlayer entity, int[] enchant){
		if(enchant == null || enchant.length != 2){
			return true;
		}else{
			return isEnchanted(entity, enchant[0], enchant[1]);
		}
	}
	
	public static int getEnchentLevel(EntityPlayer entity, int enchant){
		return EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByID(enchant), entity.inventory.getCurrentItem());
	}
	
	public static boolean isPotionAffected(EntityLivingBase entity, int[] pot){
		if(pot != null && pot.length == 2){
			return isPotionAffected(entity, pot[0], pot[1]);
		}else{
			return true;
		}
	}
	
	public static void spawnItem(World world, double x, double y, double z, ItemStack itemstack){
	    float f = 0.7F;
	    double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
	    double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
	    double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
	    EntityItem entityitem = 
	    		new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, itemstack);
	    entityitem.setDefaultPickupDelay();
	    world.spawnEntityInWorld(entityitem);
	}
	
	public static void spawnItem(World world, double x, double y, double z, List<ItemStack> itemstack){
		for(ItemStack item : itemstack){
			spawnItem(world, x, y, z, item);
		}
	}
	
    public static String[] splitAndTrim(String str, String separator){
    	return splitAndTrim(str,separator, true);
    }
    public static String[] splitAndTrim(String str, String separator, boolean lower){
        String[] aaa = lower ? str.toLowerCase().split(separator) : str.split(separator);
        String[] ids = new String[aaa.length];
        if("".equals(str)){
        	ids = null;
        }else{
	        for(int i = 0; i < aaa.length; i++){
	        	ids[i] = aaa[i].trim();
	        }
        }
        return ids;
    }

    public static int[] stringToInt(String str, String separator) throws NumberFormatException{
        String[] aaa = str.split(separator);
        int[] ids = new int[aaa.length];
        if("".equals(str)){
        	ids = null;
        }else{
	        for(int i = 0; i < aaa.length; i++){
	        	ids[i]= Integer.parseInt(aaa[i].trim());
	        }
        }
        return ids;
    }
    
    public static int[][] stringToInt(String str, String separator1, String separator2) throws NumberFormatException{
    	String[] aaa = str.split(separator1);
    	int[][] ids = new int[aaa.length][];
        if("".equals(str)){
        	ids = null;
        }else{
	        for(int i = 0; i < aaa.length; i++){
	    		ids[i] = stringToInt(aaa[i], separator2);
//	    		int[] s = stringToInt(aaa[i], separator2);
//	    		ids[i] = new int[2];
//	    		ids[i][0] = s[0];
//	    		ids[i][1] = (s.length >= 2) ? s[1] : 0;
	    	}
        }
    	return ids;
    }
}
