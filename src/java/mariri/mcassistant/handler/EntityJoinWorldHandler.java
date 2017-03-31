package mariri.mcassistant.handler;

import java.util.ArrayList;
import java.util.List;

import mariri.mcassistant.helper.Comparator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityJoinWorldHandler {

	public static EntityJoinWorldHandler INSTANCE = new EntityJoinWorldHandler();

	public static boolean UNIFY_ENEBLE;
	public static boolean MOUNTASSIST_ENABLE;

	private static List<Entity> isProcessing = new ArrayList<Entity>();

	private EntityJoinWorldHandler(){}

	public static boolean SNEAK_INVERT;

	@SubscribeEvent
	public void doEvent(EntityJoinWorldEvent e){
		if(!isProcessing.contains(e.getEntity()) && !e.getWorld().isRemote){
			isProcessing.add(e.getEntity());
			// 鉱石辞書変換機能
			if(UNIFY_ENEBLE && e.getEntity() instanceof EntityItem){
				ItemStack dropItem = (ItemStack)((EntityItem)e.getEntity()).getEntityItem();
				List<ItemStack> oredict = Comparator.UNIFY.findOreDict(dropItem);
				if(oredict != null && oredict.size() > 0 && !Comparator.UNIFY.compareDisallow(dropItem.getItem())){
					// ドロップアイテムの書き換え
					for(ItemStack i : oredict){
						Item replace = i.getItem();
						if(!Comparator.UNIFY.compareDisallow(replace)){
				    		dropItem.setItem(replace);
							dropItem.setItemDamage(i.getItemDamage());
							break;
						}
					}
				}
			}
			// 乗り物補助機能 - 乗る方
			if(MOUNTASSIST_ENABLE && Comparator.MOUNT.compareEntity(e.getEntity())){
				Entity mount = e.getEntity();
				World world = e.getWorld();
				List<EntityPlayer> players =
						world.getEntitiesWithinAABB(EntityPlayer.class,
								new AxisAlignedBB(mount.posX - 5, mount.posY - 5, mount.posZ - 5, mount.posX + 5, mount.posY + 5, mount.posZ + 5));
				if(players.size() >= 1){
					EntityPlayer player = players.get(0);
					if(player.isSneaking() == SNEAK_INVERT){
						player.startRiding(mount);
					}
				}
			}
			isProcessing.remove(e.getEntity());
		}
	}

	public static boolean isEventEnable(){
		return UNIFY_ENEBLE || MOUNTASSIST_ENABLE;
	}
}
