package mariri.mcassistant.handler;

import java.util.ArrayList;
import java.util.List;

import mariri.mcassistant.helper.Comparator;
import mariri.mcassistant.helper.Lib;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityInteractHandler {

	public static final EntityInteractHandler INSTANCE = new EntityInteractHandler();

	public static boolean BREEDASSIST_ENABLE;
	public static int BREEDASSIST_RADIUS;
	public static int[][] BREEDASSIST_AFFECT_POTION;

	public static boolean SHEARASSIST_ENABLE;
	public static int SHEARASSIST_RADIUS;
	public static int [][] SHEARASSIST_AFFECT_POTION;

	public static boolean SNEAK_INVERT;

	private static List<EntityPlayer> isProcessing = new ArrayList<EntityPlayer>();

	private EntityInteractHandler(){}

	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteract e){
		EntityPlayer player = e.getEntityPlayer();
		World world = player.worldObj;
		EnumHand hand = e.getHand();
		if(!isProcessing.contains(player) && !world.isRemote && player.isSneaking() == SNEAK_INVERT){
			isProcessing.add(player);
			// ShearAssist
			if(SHEARASSIST_ENABLE && e.getTarget() instanceof EntitySheep){
				EntitySheep target = (EntitySheep)e.getTarget();
				ItemStack current = player.inventory.getCurrentItem();
				if(current != null && Comparator.SHEAR.compareItem(current) && target.isShearable(current, e.getWorld(), e.getPos())){
					int shearCount = 0;
					List<EntitySheep> list = world.getEntitiesWithinAABB(target.getClass(),
							new AxisAlignedBB(
									target.posX - SHEARASSIST_RADIUS, target.posY - SHEARASSIST_RADIUS, target.posZ - SHEARASSIST_RADIUS,
									target.posX + SHEARASSIST_RADIUS, target.posY + SHEARASSIST_RADIUS, target.posZ + SHEARASSIST_RADIUS));
					for(EntitySheep sheep : list){
						if(current != null && sheep.isShearable(current, e.getWorld(), sheep.getPosition())){
							List<ItemStack> drops = sheep.onSheared(current, e.getWorld(), sheep.getPosition(), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, current));
							if(drops.size() > 0){
								shearCount++;
								// ハサミにダメージ
								if(current != null && current.getItem() instanceof ItemShears){
									if(player.inventory.getCurrentItem().attemptDamageItem(1, player.getRNG())){
										player.inventory.deleteStack(player.inventory.getCurrentItem());
										world.playSound(player, player.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
									}
								}else{
									if(current.stackSize <= 0){
										player.inventory.deleteStack(player.inventory.getCurrentItem());
										world.playSound(player, player.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
									}
								}
								Lib.spawnItem(world, sheep.posX, sheep.posY, sheep.posZ, drops);
							}
						}
					}
					Lib.affectPotionEffect(player, BREEDASSIST_AFFECT_POTION, shearCount);
					e.setCanceled(true);
				}
			}
			// BreedAssist
			if(BREEDASSIST_ENABLE && e.getTarget() instanceof EntityAnimal){
				EntityAnimal target = (EntityAnimal)e.getTarget();
				ItemStack current = player.inventory.getCurrentItem();
				if(current != null && Comparator.FEED.compareItem(current) && target.isBreedingItem(current)){
					int breedCount = 0;
					List<EntityAnimal> list = world.getEntitiesWithinAABB(target.getClass(),
							new AxisAlignedBB(
									target.posX - BREEDASSIST_RADIUS, target.posY - BREEDASSIST_RADIUS, target.posZ - BREEDASSIST_RADIUS,
									target.posX + BREEDASSIST_RADIUS, target.posY + BREEDASSIST_RADIUS, target.posZ + BREEDASSIST_RADIUS));
					for(EntityAnimal animal : list){
						if(animal.processInteract(player, hand, player.inventory.getCurrentItem())){
							breedCount++;
						}
						if(current.stackSize == 0){
							break;
						}
					}
					Lib.affectPotionEffect(player, BREEDASSIST_AFFECT_POTION, breedCount);
					e.setCanceled(true);
				}
			}
			isProcessing.remove(player);
		}
	}

	public static boolean isEventEnable(){
		return BREEDASSIST_ENABLE;
	}
}
