package mariri.mcassistant.handler;

import java.util.ArrayList;
import java.util.List;

import mariri.mcassistant.helper.Comparator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityMountHandler {

	public static EntityMountHandler INSTANCE = new EntityMountHandler();

	public static boolean MOUNTASSIST_ENABLE;

	private static List<Entity> isProcessing = new ArrayList<Entity>();

	private EntityMountHandler(){}


	@SubscribeEvent
	public void doEvent(EntityMountEvent e){
		if(!isProcessing.contains(e.getEntity()) && !e.getWorldObj().isRemote){
			isProcessing.add(e.getEntity());
			if(e.isDismounting()){
				onDismount(e);
			}
			isProcessing.remove(e.getEntity());
		}
	}

	private void onDismount(EntityMountEvent e){
		// 乗り物補助機能 - 降りる方
		if(MOUNTASSIST_ENABLE && e.getEntityMounting() instanceof EntityPlayer && Comparator.MOUNT.compareEntity(e.getEntityBeingMounted())){
			Entity mount = e.getEntityBeingMounted();
			EntityPlayer player = (EntityPlayer)e.getEntityMounting();
			World world = e.getWorldObj();
			if(mount.attackEntityFrom(DamageSource.causePlayerDamage(player), 100.0f)){
				List<EntityItem> drops =
						world.getEntitiesWithinAABB(EntityItem.class,
								new AxisAlignedBB(mount.posX - 5, mount.posY - 5, mount.posZ - 5, mount.posX + 5, mount.posY + 5, mount.posZ + 5));
				for(EntityItem item : drops){
					player.inventory.addItemStackToInventory(item.getEntityItem());
					item.setDead();
				}
			}
		}
	}

	public static boolean isEventEnable(){
		return MOUNTASSIST_ENABLE;
	}
}
