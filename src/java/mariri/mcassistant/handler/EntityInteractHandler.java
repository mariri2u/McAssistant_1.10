package mariri.mcassistant.handler;

import java.util.ArrayList;
import java.util.List;

import mariri.mcassistant.helper.Comparator;
import mariri.mcassistant.helper.Lib;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityInteractHandler {

	public static final EntityInteractHandler INSTANCE = new EntityInteractHandler();
	
	public static boolean BREEDASSIST_ENABLE;
	public static int BREEDASSIST_RADIUS;
	public static int[][] BREEDASSIST_AFFECT_POTION;
	
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
