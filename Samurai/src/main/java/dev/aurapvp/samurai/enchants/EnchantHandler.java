package dev.aurapvp.samurai.enchants;

import de.tr7zw.nbtapi.NBTItem;
import dev.aurapvp.samurai.enchants.command.ArmorSetsCommand;
import dev.aurapvp.samurai.enchants.command.EnchantItemsCommand;
import dev.aurapvp.samurai.enchants.command.EnchanterCommand;
import dev.aurapvp.samurai.enchants.command.EnchantsCommand;
import dev.aurapvp.samurai.enchants.command.completion.ArmorSetCompletion;
import dev.aurapvp.samurai.enchants.command.context.ArmorSetContext;
import dev.aurapvp.samurai.enchants.impl.*;
import dev.aurapvp.samurai.enchants.listener.CustomEnchantListener;
import dev.aurapvp.samurai.enchants.listener.EnchantBookListener;
import dev.aurapvp.samurai.enchants.lore.CustomLoreComparator;
import dev.aurapvp.samurai.enchants.lore.CustomLoreType;
import dev.aurapvp.samurai.enchants.model.BlackScroll;
import dev.aurapvp.samurai.enchants.model.CustomLoreLine;
import dev.aurapvp.samurai.enchants.rarity.Rarity;
import dev.aurapvp.samurai.enchants.set.ArmorSet;
import dev.aurapvp.samurai.enchants.set.impl.GladiatorArmorSet;
import dev.aurapvp.samurai.enchants.thread.EnchantUpdateThread;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.*;
import dev.aurapvp.samurai.util.event.ArmorListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class EnchantHandler implements IModule {

    @Getter private static final String ENCHANT_TAG = "PCORE_CUSTOM_ENCHANT_";
    @Getter private static final String PROTECTED_TAG = "PCORE_PROTECTED";
    @Getter private static final String EXTRA_ENCHANT_TAG = "PCORE_EXTRA_ENCHANT";
    @Getter private static final String HOLY_PROTECTED_TAG = "PCORE_HOLY_PROTECTED";
    @Getter private static final String ARMOR_SET_TAG = "PCORE_ARMOR_SET";
    @Getter private static final String EXTRA_ENCHANT = "&f&lEXTRA ENCHANT";

    private final Config configFile;
    private final List<Rarity> rarities;
    private final List<CustomEnchant> enchants;
    private final ConcurrentHashMap<Player, Tasks.Callback> enchantCalls;
    private File enchantDirectory;
    private ItemStack WHITE_SCROLL, HOLY_WHITESCROLL, TRANSMOG_SCROLL, ARMOR_ORB, WEAPON_ORB;
    private int maxEnchants, maxExtraEnchants;
    private String WHITE_SCROLL_LORE, HOLY_WHITE_SCROLL_LORE;
    private final List<String> VALID_APPLICABLE;
    private final List<String> GEAR;
    private final List<String> NON_GEAR;
    private final List<String> TOOLS;

    public EnchantHandler() {
        this.configFile = new Config(Samurai.getInstance(), "enchants");
        this.rarities = new ArrayList<>();
        this.enchants = new ArrayList<>();
        this.enchantCalls = new ConcurrentHashMap<>();
        this.VALID_APPLICABLE = Arrays.asList("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS", "AXE", "SWORD", "PICKAXE", "SPADE", "HOE", "FISHING_ROD", "BOW");
        this.GEAR = Arrays.asList("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS");
        this.NON_GEAR = Arrays.asList("FISHING_ROD", "BOW");
        this.TOOLS = Arrays.asList("AXE", "SWORD", "PICKAXE", "SPADE", "HOE");
    }

    @Override
    public String getId() {
        return "custom-enchants";
    }

    @Override
    public void load(Samurai plugin) {
        this.loadListeners();
        this.loadCommands();
        this.reload();

        Bukkit.getScheduler().runTaskTimer(Samurai.getInstance(), () -> {
            for (Map.Entry<Player, Tasks.Callback> entry : enchantCalls.entrySet()) {
                Player player = entry.getKey();

                entry.getValue().call(player);
            }
        }, 20, 20);

        new EnchantUpdateThread().start();
    }

    @Override
    public void unload(Samurai plugin) {

    }

    @Override
    public void reload() {
        this.loadDirectories();
        this.loadRarities();
        this.loadEnchants();
        this.WHITE_SCROLL = ItemUtils.itemStackFromConfigSect("white-scroll", getConfig());
        this.HOLY_WHITESCROLL = ItemUtils.itemStackFromConfigSect("holy-white-scroll", getConfig());
        this.TRANSMOG_SCROLL = ItemUtils.itemStackFromConfigSect("transmog-scroll", getConfig());
        this.ARMOR_ORB = ItemUtils.itemStackFromConfigSect("armor-orb", getConfig());
        this.WEAPON_ORB = ItemUtils.itemStackFromConfigSect("weapon-orb", getConfig());
        this.maxEnchants = getConfig().getInt("max-enchants", 9);
        this.maxExtraEnchants = getConfig().getInt("extra-enchants", 1);
        this.WHITE_SCROLL_LORE = getConfig().getString("white-scroll.lore-addition");
        this.HOLY_WHITE_SCROLL_LORE = getConfig().getString("holy-white-scroll.lore-addition");
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new ArmorListener(Collections.emptyList()), Samurai.getInstance());
        Bukkit.getPluginManager().registerEvents(new CustomEnchantListener(), Samurai.getInstance());
        Bukkit.getPluginManager().registerEvents(new EnchantBookListener(), Samurai.getInstance());
    }

    private void loadCommands() {
        Samurai.getInstance().getCommandManager().getCommandCompletions().registerCompletion("rarities", s -> rarities.stream().map(Rarity::getName).collect(Collectors.toList()));
        Samurai.getInstance().getCommandManager().registerCommand(new EnchanterCommand());
        Samurai.getInstance().getCommandManager().registerCommand(new EnchantItemsCommand());
        Samurai.getInstance().getCommandManager().registerCommand(new EnchantsCommand());
    }

    private void loadDirectories() {
        this.enchantDirectory = new File(Samurai.getInstance().getDataFolder(), "enchants");
        if (!enchantDirectory.exists()) enchantDirectory.mkdirs();
    }

    private void loadRarities() {
        for (String key : getConfig().getConfigurationSection("rarities").getKeys(false)) {
            String[] successParts = getConfig().getString("rarities." + key + ".successRange").split("-");
            String[] destroyParts = getConfig().getString("rarities." + key + ".destroyRange").split("-");

            this.rarities.add(new Rarity(key,
                    getConfig().getString("rarities." + key + ".displayName"),
                    getConfig().getString("rarities." + key + ".color"),
                    ItemUtils.itemStackFromConfigSect("rarities." + key + ".openItem", getConfig()),
                    getConfig().getInt("rarities." + key + ".weight"),
                    getConfig().getInt("rarities." + key + ".xpNeeded"),
                    new IntRange(Integer.parseInt(successParts[0]), Integer.parseInt(successParts[1])),
                    new IntRange(Integer.parseInt(destroyParts[0]), Integer.parseInt(destroyParts[1]))
            ));
        }
    }

    private void loadEnchants() {
        Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
            this.enchants.add(new Bat());
            this.enchants.add(new Headless());
            this.enchants.add(new JellyLegs());
            this.enchants.add(new Saturation());
            this.enchants.add(new Strength());
            this.enchants.add(new Swift());
        });
        Bukkit.getConsoleSender().sendMessage("[Enchant Handler] Loaded " + this.enchants.size() + " custom enchants.");
    }

    public Optional<Rarity> getRarity(String name) {
        return this.rarities.stream().filter(rarity -> rarity.getName().equalsIgnoreCase(name)).findFirst();
    }

    public Optional<CustomEnchant> getCustomEnchant(String name) {
        return this.enchants.stream().filter(enchant -> enchant.getName().equalsIgnoreCase(name)).findFirst();
    }

    public Optional<CustomEnchant> getCustomEnchantByDisplay(String name) {
        return this.enchants.stream().filter(enchant -> enchant.getDisplayName().contains(name)).findFirst();
    }

    public Map<CustomEnchant, Integer> getCustomEnchants(ItemStack stack) {
        Map<CustomEnchant, Integer> enchants = new HashMap<>();

        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return enchants;

        NBTItem item = new NBTItem(stack);

        for (String key : item.getKeys()) {
            if (!key.startsWith(ENCHANT_TAG)) continue;
            String[] parts = item.getString(key).split(":");
            String name = parts[0];
            int level = Integer.parseInt(parts[1]);
            Optional<CustomEnchant> enchantOpt = getCustomEnchant(name);

            if (!enchantOpt.isPresent()) continue;

            enchants.put(enchantOpt.get(), level);
        }

        return enchants;
    }

    public ItemStack setEnchant(ItemStack stack, CustomEnchant enchant, int level) {
        String line = CC.translate(enchant.getColoredName() + " " + level) + " " + convertToInvisibleString(CustomLoreType.CUSTOM_ENCHANT.getInvisibleId());
        CustomLoreLine customLine = new CustomLoreLine(line, CustomLoreType.CUSTOM_ENCHANT, -1, CustomLoreType.CUSTOM_ENCHANT.getPriority(enchant));

        customLine.setEnchant(enchant);
        customLine.setLevel(level);
        stack = createLine(stack, customLine);

        NBTItem item = new NBTItem(stack);
        CustomLoreLine enchantLine = getCustomEnchantLoreLine(stack, enchant);

        if (enchantLine == null) {
            Bukkit.getLogger().info("Enchants: Item cannot add enchant because it can't find a lore line.");
            return stack;
        }

        item.setString(ENCHANT_TAG + enchant.getName(), enchant.getName() + ":" + level + ":" + enchantLine.getIndex());
        stack = item.getItem();

        return stack;
    }

    public ItemStack removeEnchant(ItemStack stack, CustomEnchant enchant) {
        CustomLoreLine enchantLine = getCustomEnchantLoreLine(stack, enchant);

        if (enchantLine == null) {
            Bukkit.getLogger().info("Enchants: Item cannot remove enchant because it can't find a lore line.");
            return stack;
        }

        NBTItem item = new NBTItem(stack);
        item.removeKey(ENCHANT_TAG + enchant.getName());
        stack = item.getItem();
        stack = removeLine(stack, enchantLine.getIndex());

        return stack;
    }

    public ItemStack applyBlackScroll(Player player, BlackScroll blackScroll) {
        ItemStack stack = blackScroll.getStack();
        if (!stack.hasItemMeta()) return stack;

        ItemMeta meta = stack.getItemMeta();
        Map<CustomEnchant, Integer> enchants = this.getCustomEnchants(stack);

        if (enchants.size() == 0 || !meta.hasLore()) {
            player.sendMessage(CC.translate("&cThat item has no enchants applied."));
            return null;
        }

        List<CustomEnchant> customEnchants = new ArrayList<>(enchants.keySet());
        CustomEnchant enchant = customEnchants.get(ThreadLocalRandom.current().nextInt(0, customEnchants.size()));

        ItemUtils.tryFit(player, enchant.getBook(enchants.get(enchant), blackScroll.getSuccess(), blackScroll.getDestroy()), false);

        return removeEnchant(stack, enchant);
    }

    public ItemStack getBlackScroll(int success, int destroy) {
        YamlConfiguration config = Samurai.getInstance().getEnchantHandler().getConfigFile();
        ItemBuilder builder = new ItemBuilder(Material.getMaterial(config.getString("black-scroll.material")));

        builder.setName(config.getString("black-scroll.name"));
        builder.setLore(config.getStringList("black-scroll.lore"), "%success%", success, "%destroy%", destroy);

        for (String key : config.getStringList("black-scroll.enchants")) {
            String[] args = key.split(":");
            builder.addEnchantment(Enchantment.getByName(args[0].toUpperCase()), Integer.parseInt(args[1]));
        }

        for (String key : config.getStringList("black-scroll.item-flags")) {
            builder.addItemFlag(ItemFlag.valueOf(key));
        }

        NBTItem item = new NBTItem(builder.create());

        item.setBoolean("black-scroll", true);
        item.setInteger("success", success);
        item.setInteger("destroy", destroy);

        return item.getItem();
    }

    public boolean isBlackScroll(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return false;

        NBTItem item = new NBTItem(stack);
        return item.hasTag("black-scroll") && item.getBoolean("black-scroll");
    }

    public ItemStack setArmorSet(ItemStack stack, ArmorSet set) {
        List<CustomLoreLine> lines = new ArrayList<>();

        for (String line : set.getDescription()) {
            String formattedLine = CC.translate(line) + " " + convertToInvisibleString(CustomLoreType.ARMOR_SET.getInvisibleId());
            CustomLoreLine customLine = new CustomLoreLine(formattedLine, CustomLoreType.ARMOR_SET, -1, CustomLoreType.ARMOR_SET.getPriority());
            customLine.setArmorSet(true);
            lines.add(customLine);
        }

        stack = createLines(stack, lines);

        NBTItem item = new NBTItem(stack);
        CustomLoreLine setLine = getLoreLine(stack, CustomLoreType.ARMOR_SET);

        if (setLine == null) {
            Bukkit.getLogger().info("Enchants: Item cannot add armor set because it can't find a lore line.");
            return stack;
        }

        item.setString(ARMOR_SET_TAG, set.getName());
        stack = item.getItem();

        return stack;
    }

    public ItemStack setProtected(ItemStack stack) {
        String line = CC.translate(this.WHITE_SCROLL_LORE) + " " + convertToInvisibleString(CustomLoreType.PROTECTED.getInvisibleId());
        CustomLoreLine customLine = new CustomLoreLine(line, CustomLoreType.PROTECTED, -1, CustomLoreType.PROTECTED.getPriority());

        customLine.setWhiteScroll(true);
        stack = createLine(stack, customLine);

        NBTItem item = new NBTItem(stack);
        CustomLoreLine protectedLine = getLoreLine(stack, CustomLoreType.PROTECTED);

        if (protectedLine == null) {
            Bukkit.getLogger().info("Enchants: Item cannot set protected because it can't find a lore line.");
            return stack;
        }

        item.setBoolean(PROTECTED_TAG, true);
        stack = item.getItem();

        return stack;
    }

    public ItemStack removeProtected(ItemStack stack) {
        NBTItem item = new NBTItem(stack);
        CustomLoreLine protectedLine = getLoreLine(stack, CustomLoreType.PROTECTED);

        if (protectedLine == null) {
            Bukkit.getLogger().info("Enchants: Item cannot remove protected because it can't find a lore line.");
            return stack;
        }

        item.removeKey(PROTECTED_TAG);
        stack = item.getItem();
        stack = removeLine(stack, protectedLine.getIndex());

        return stack;
    }

    public ItemStack setExtraEnchant(ItemStack stack) {
        String line = CC.translate(EXTRA_ENCHANT) + " " + convertToInvisibleString(CustomLoreType.EXTRA_ENCHANT.getInvisibleId());
        CustomLoreLine customLine = new CustomLoreLine(line, CustomLoreType.EXTRA_ENCHANT, -1, CustomLoreType.EXTRA_ENCHANT.getPriority());

        customLine.setExtraEnchant(true);
        stack = createLine(stack, customLine);

        NBTItem item = new NBTItem(stack);
        CustomLoreLine protectedLine = getLoreLine(stack, CustomLoreType.EXTRA_ENCHANT);

        if (protectedLine == null) {
            Bukkit.getLogger().info("Enchants: Item cannot set extra enchant because it can't find a lore line.");
            return stack;
        }

        item.setBoolean(EXTRA_ENCHANT_TAG, true);
        stack = item.getItem();

        return stack;
    }

    public ItemStack removeExtraEnchant(ItemStack stack) {
        NBTItem item = new NBTItem(stack);
        CustomLoreLine extraEnchantLoreLine = getLoreLine(stack, CustomLoreType.EXTRA_ENCHANT);

        if (extraEnchantLoreLine == null) {
            Bukkit.getLogger().info("Enchants: Item cannot remove extra enchant because it can't find a lore line.");
            return stack;
        }

        item.removeKey(EXTRA_ENCHANT_TAG);
        stack = item.getItem();
        stack = removeLine(stack, extraEnchantLoreLine.getIndex());

        return stack;
    }

    public ItemStack setHolyProtected(ItemStack stack) {
        String line = CC.translate(this.HOLY_WHITE_SCROLL_LORE) + " " + convertToInvisibleString(CustomLoreType.HOLY_PROTECTED.getInvisibleId());
        CustomLoreLine customLine = new CustomLoreLine(line, CustomLoreType.HOLY_PROTECTED, -1, CustomLoreType.HOLY_PROTECTED.getPriority());

        customLine.setHolyWhiteScroll(true);
        stack = createLine(stack, customLine);

        NBTItem item = new NBTItem(stack);
        CustomLoreLine protectedLine = getHolyProtectedLoreLine(stack);

        if (protectedLine == null) {
            Bukkit.getLogger().info("Enchants: Item cannot set holy protected because it can't find a lore line.");
            return stack;
        }

        item.setBoolean(HOLY_PROTECTED_TAG, true);
        stack = item.getItem();

        return stack;
    }

    public ItemStack removeHolyProtected(ItemStack stack) {
        NBTItem item = new NBTItem(stack);
        CustomLoreLine protectedLine = getHolyProtectedLoreLine(stack);

        if (protectedLine == null) {
            Bukkit.getLogger().info("Enchants: Item cannot remove holy protected because it can't find a lore line.");
            return stack;
        }

        item.removeKey(HOLY_PROTECTED_TAG);
        stack = item.getItem();
        stack = removeLine(stack, protectedLine.getIndex());

        return stack;
    }

    public boolean isProtected(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return false;
        NBTItem item = new NBTItem(stack);

        return item.hasTag(PROTECTED_TAG) && item.getBoolean(PROTECTED_TAG);
    }

    public boolean isHolyProtected(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return false;
        NBTItem item = new NBTItem(stack);

        return item.hasTag(HOLY_PROTECTED_TAG) && item.getBoolean(HOLY_PROTECTED_TAG);
    }

    public boolean hasExtraEnchant(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return false;
        NBTItem item = new NBTItem(stack);

        return item.hasTag(EXTRA_ENCHANT_TAG) && item.getBoolean(EXTRA_ENCHANT_TAG);
    }

    public ItemStack createLine(ItemStack stack, CustomLoreLine customLine) {
        return createLines(stack, Collections.singletonList(customLine));
    }

    public ItemStack createLines(ItemStack stack, List<CustomLoreLine> customLines) {
        ItemMeta meta = stack.getItemMeta();
        List<CustomLoreLine> lines = getLore(stack);
        lines.addAll(customLines);
        lines = lines.stream().sorted(new CustomLoreComparator().reversed()).collect(Collectors.toList());

        meta.setLore(organizeLore(lines, false));
        stack.setItemMeta(meta);

        return stack;
    }

    public ItemStack removeLine(ItemStack stack, int index) {
        ItemMeta meta = stack.getItemMeta();
        List<CustomLoreLine> lines = getLore(stack);
        lines.remove(index);
        lines = lines.stream().sorted(new CustomLoreComparator().reversed()).collect(Collectors.toList());

        meta.setLore(organizeLore(lines, false));
        stack.setItemMeta(meta);

        return stack;
    }

    public ItemStack applyTransMog(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        List<CustomLoreLine> lines = getLore(stack);

        meta.setLore(organizeLore(lines, true));
        stack.setItemMeta(meta);
        return stack;
    }

    public List<String> organizeLore(List<CustomLoreLine> lines, boolean enchants) {
        List<String> lore = new ArrayList<>();

        for (CustomLoreLine line : lines.stream().sorted(new CustomLoreComparator(enchants).reversed()).collect(Collectors.toList())) {
            lore.add(line.getValue());
        }

        return lore;
    }

    public List<CustomLoreLine> getLore(ItemStack stack) {
        List<String> lore = ItemUtils.getLore(stack);
        List<CustomLoreLine> lines = new ArrayList<>();

        int i = 0;
        for (String string : lore) {
            CustomLoreType type = getCustomLoreType(string);
            CustomLoreLine line = new CustomLoreLine(string, type, i++, type == null ? -1 : type.getPriority());
            String normalLine = line.getValue();
            String replacedLine = CC.translate(normalLine);
            String strippedLine = ChatColor.stripColor(replacedLine);

            for (Map.Entry<CustomEnchant, Integer> entry : getCustomEnchants(stack).entrySet()) {
                if (!strippedLine.startsWith(ChatColor.stripColor(CC.translate(entry.getKey().getColoredName())) + " ")) continue;

                line.setEnchant(entry.getKey());
                line.setLevel(entry.getValue());
            }

            if (strippedLine.startsWith(ChatColor.stripColor(CC.translate(WHITE_SCROLL_LORE)) + " ")) line.setWhiteScroll(true);
            if (strippedLine.startsWith(ChatColor.stripColor(CC.translate(HOLY_WHITE_SCROLL_LORE)) + " ")) line.setHolyWhiteScroll(true);
            if (strippedLine.startsWith(ChatColor.stripColor(CC.translate(EXTRA_ENCHANT)) + " ")) line.setExtraEnchant(true);
            if (strippedLine.endsWith(ChatColor.stripColor(CustomLoreType.ARMOR_SET.getInvisibleId()))) line.setArmorSet(true);


            lines.add(line);
        }

        return lines;
    }

    public CustomLoreLine getCustomEnchantLoreLine(ItemStack stack, CustomEnchant enchant) {
        for (CustomLoreLine line : getLore(stack)) {
            String normalLine = line.getValue();
            String replacedLine = CC.translate(normalLine);
            String strippedLine = ChatColor.stripColor(replacedLine);

            if (strippedLine.startsWith(ChatColor.stripColor(CC.translate(enchant.getColoredName())) + " ")) return line;
        }

        return null;
    }

    public CustomLoreLine getLoreLine(ItemStack stack, CustomLoreType type) {
        for (CustomLoreLine line : getLore(stack)) {
            if (line.getType() == type) return line;
        }

        return null;
    }

    public CustomLoreLine getHolyProtectedLoreLine(ItemStack stack) {
        for (CustomLoreLine line : getLore(stack)) {
            String normalLine = line.getValue();
            String replacedLine = CC.translate(normalLine);
            String strippedLine = ChatColor.stripColor(replacedLine);

            if (strippedLine.startsWith(ChatColor.stripColor(CC.translate(this.HOLY_WHITE_SCROLL_LORE)) + " ")) return line;
        }

        return null;
    }

    public CustomLoreType getCustomLoreType(String line) {
        int i = 0;
        for (String s : line.split(" ")) {
            CustomLoreType type = CustomLoreType.getByInvisibleId(s.replaceAll("§", ""));
            if (type != null) return type;
        }

        return null;
    }

    private YamlConfiguration getConfig() {
        return this.configFile;
    }

    public static String convertToInvisibleString(String s) {
        StringBuilder hidden = new StringBuilder();

        for (char c : s.toCharArray()) hidden.append(ChatColor.COLOR_CHAR).append(c);

        return hidden.toString();
    }

}
