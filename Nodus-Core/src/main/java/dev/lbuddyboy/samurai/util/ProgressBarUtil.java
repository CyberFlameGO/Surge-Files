package dev.lbuddyboy.samurai.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/01/2022 / 9:14 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.util
 */
public class ProgressBarUtil {

	public static String getProgressBar(int one, int two) {
		float percent = (float) one / two;
		int progressBars = (int) (10 * percent);
		return StringUtils.repeat("&a" + SymbolUtil.BLOCK, progressBars) + StringUtils.repeat("&7" + SymbolUtil.BLOCK, 10 - progressBars);
	}

	public static String getProgressBar(long one, long two) {
		float percent = (float) one / two;
		int progressBars = (int) (10 * percent);
		return StringUtils.repeat("&a" + SymbolUtil.BLOCK, progressBars) + StringUtils.repeat("&7" + SymbolUtil.BLOCK, 10 - progressBars);
	}

}
