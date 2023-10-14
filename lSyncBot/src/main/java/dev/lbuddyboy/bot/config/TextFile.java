package dev.lbuddyboy.bot.config;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.simpleyaml.configuration.file.YamlFile;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class TextFile {

    private final YamlFile yamlFile;
    private static final List<TextFile> list = Lists.newArrayList();
    public static final boolean UTF8_OVERRIDE;
    public static final boolean UTF_BIG;

    @SneakyThrows
    public TextFile(Path path, String fileName) {
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }

        Path configPath = path.resolve(fileName);

        if (!Files.exists(configPath)) {
            try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
                Files.copy(Objects.requireNonNull(in), configPath);
            }
        }

        yamlFile = new YamlFile(configPath.toFile());
        yamlFile.load();

        list.add(this);

    }

    public YamlFile getConfig() {return yamlFile;}

    @SneakyThrows
    public void reload() {
        yamlFile.load();
    }

    public static void reloadAll() {
        list.forEach(TextFile::reload);
    }

    static {
        final byte[] testBytes = Base64Coder.decode("ICEiIyQlJicoKSorLC0uLzAxMjM0NTY3ODk6Ozw9Pj9AQUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVpbXF1eX2BhYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ent8fX4NCg==");
        final String testString = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\r\n";
        final Charset defaultCharset = Charset.defaultCharset();
        final String resultString = new String(testBytes, defaultCharset);
        final boolean trueUTF = defaultCharset.name().contains("UTF");
        UTF8_OVERRIDE = !testString.equals(resultString) || defaultCharset.equals(Charset.forName("US-ASCII"));
        UTF_BIG = trueUTF && UTF8_OVERRIDE;
    }

}