package dev.lbuddyboy.samurai.custom.shop;

import dev.lbuddyboy.samurai.util.CC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/12/2021 / 9:51 AM
 * SteelHCF-main / com.steelpvp.hcf.shop
 */

@AllArgsConstructor
@Getter
public enum ShopCategory {

	CONCRETE_SHOP("Concrete Shop", CC.getCustomHead(CC.translate("&gConcrete Shop"), 1, "f477f44389362c4c764c847a9739bc78c32467eab4e3830ae4c8beac3442ef9"), 43),
	GEM("Shard Shop", CC.getCustomHead(CC.translate("&6Shard Shop"), 1, "96eff9299cffd23baa8d590e19dbd8df38aeb204a86e7e09e5a2916d95eb25ae"), 37),
	SELL("Sell Shop", CC.getCustomHead(CC.translate("&cSell Shop"), 1, "df4dc3c3753bf5b0b7f081cdb49b83d37428a12e4187f6346dec06fac54ce"), 14),
	GLASS_SHOP("Glass Shop", CC.getCustomHead(CC.translate("&gGlass Shop"), 1, "d52efad1be84e0889bb2a581a5e665aa070622260fbf6c1eb8d931a1267a54ba"), 30),
	WOOL_SHOP("Wool Shop", CC.getCustomHead(CC.translate("&gWool Shop"), 1, "463a4e9882f189233f9bf1121b96b799bb3fd377054f39dd19a5d83be01ff6"), 31),
	DECORATION_SHOP("Decoration Shop", CC.getCustomHead(CC.translate("&gDecoration Shop"), 1, "c5a35b5ca15268685c4660535e5883d21a5ec57c55d397234269acb5dc2954f"), 32),
	CLAY_SHOP("Clay Shop", CC.getCustomHead(CC.translate("&gClay Shop"), 1, "daa55391ef89b2a534a58d0150f1621175310ea87735601aebe8d6b1d7c568bb"), 40),
	BUY("Buy Shop", CC.getCustomHead(CC.translate("&aBuy Shop"), 1, "8e9b27fccd80921bd263c91dc511d09e9a746555e6c9cad52e8562ed0182a2f"), 12);

	private String menuTitle;
	private ItemStack buttonItem;
	private int menuSlot;

}
