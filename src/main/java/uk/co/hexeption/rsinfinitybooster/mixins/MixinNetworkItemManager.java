package uk.co.hexeption.rsinfinitybooster.mixins;

import java.util.Map;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.item.INetworkItem;
import com.refinedmods.refinedstorage.api.network.item.INetworkItemManager;
import com.refinedmods.refinedstorage.api.network.item.INetworkItemProvider;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.item.NetworkItemManager;
import com.refinedmods.refinedstorage.apiimpl.network.node.WirelessTransmitterNetworkNode;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.co.hexeption.rsinfinitybooster.utils.CardUtil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * MixinNetworkItemManager
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 01/03/2021 - 04:34 pm
 */
@Mixin(value = NetworkItemManager.class, remap = false)
public abstract class MixinNetworkItemManager implements INetworkItemManager {

	@Shadow
	@Final
	private INetwork network;

	@Shadow
	@Final
	private Map<PlayerEntity, INetworkItem> items;

	@Inject(method = "open", at = @At("HEAD"), cancellable = true)
	private void onOpen(PlayerEntity playerEntity, ItemStack itemStack, PlayerSlot playerSlot, CallbackInfo ci) {
		network.getNodeGraph().all().forEach(iNetworkNode -> {
			INetworkNode node = iNetworkNode.getNode();
			if (node instanceof WirelessTransmitterNetworkNode) {
				WirelessTransmitterNetworkNode transsmitter = (WirelessTransmitterNetworkNode) node;
				if (!transsmitter.isActive()) {
					return;
				}
				if (CardUtil.isDimensionCard(transsmitter.getUpgrades())) {
					INetworkItem item = ((INetworkItemProvider) itemStack.getItem()).provide(this, playerEntity, itemStack, playerSlot);
					if (item.onOpen(this.network)) {
						this.items.put(playerEntity, item);
					}
					ci.cancel();
				}
			}
		});
	}


}
