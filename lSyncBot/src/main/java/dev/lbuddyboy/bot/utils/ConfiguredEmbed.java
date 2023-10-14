package dev.lbuddyboy.bot.utils;

import dev.lbuddyboy.bot.config.impl.MessageConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public class ConfiguredEmbed {

    private boolean embed;
    private String message;
    private EmbedBuilder builder;
    
    public EmbedBuilder format(Member member, Object... objects) {

        List<Object> os = new ArrayList<>(Arrays.asList(objects));
        
        if (member != null) os.addAll(Arrays.asList(MessageConfiguration.MEMBER_PLACEHOLDERS(member)));
        
        MessageEmbed embed = this.builder.build();
        EmbedBuilder builder = new EmbedBuilder(this.builder);
        
        if (embed.getTitle() != null) builder.setTitle(CC.format(embed.getTitle(), os.toArray()));
        if (embed.getDescription() != null) builder.setDescription(CC.format(embed.getDescription(), os.toArray()));
        if (embed.getAuthor() != null) {
            if (member != null) {
                builder.setAuthor(CC.format(embed.getAuthor().getName(), os.toArray()), member.getAvatarUrl());
            } else {
                builder.setAuthor(CC.format(embed.getAuthor().getName(), os.toArray()));
            }
        }
        if (embed.getColor() != null) builder.setColor(embed.getColor());
        if (embed.getFooter() != null) {
            if (embed.getFooter().getIconUrl() == null) {
                builder.setFooter(CC.format(embed.getFooter().getText(), os.toArray()));
            } else {
                builder.setFooter(CC.format(embed.getFooter().getText(), os.toArray()), embed.getFooter().getIconUrl());
            }
        }
        if (embed.getImage() != null) builder.setImage(embed.getImage().getUrl());
        if (embed.getThumbnail() != null) builder.setThumbnail(embed.getThumbnail().getUrl());
        
        builder.setTimestamp(OffsetDateTime.now());
                
        return builder;
    }

    public EmbedBuilder format(User user, Object... objects) {

        List<Object> os = new ArrayList<>(Arrays.asList(objects));

        if (user != null) os.addAll(Arrays.asList(MessageConfiguration.USER_PLACEHOLDERS(user)));

        MessageEmbed embed = this.builder.build();
        EmbedBuilder builder = new EmbedBuilder(this.builder);

        if (embed.getTitle() != null) builder.setTitle(CC.format(embed.getTitle(), os.toArray()));
        if (embed.getDescription() != null) builder.setDescription(CC.format(embed.getDescription(), os.toArray()));
        if (embed.getAuthor() != null) {
            if (user != null) {
                builder.setAuthor(CC.format(embed.getAuthor().getName(), os.toArray()), user.getAvatarUrl());
            } else {
                builder.setAuthor(CC.format(embed.getAuthor().getName(), os.toArray()));
            }
        }
        if (embed.getColor() != null) builder.setColor(embed.getColor());
        if (embed.getFooter() != null) {
            if (embed.getFooter().getIconUrl() == null) {
                builder.setFooter(CC.format(embed.getFooter().getText(), os.toArray()));
            } else {
                builder.setFooter(CC.format(embed.getFooter().getText(), os.toArray()), embed.getFooter().getIconUrl());
            }
        }
        if (embed.getImage() != null) builder.setImage(embed.getImage().getUrl());
        if (embed.getThumbnail() != null) builder.setThumbnail(embed.getThumbnail().getUrl());

        builder.setTimestamp(OffsetDateTime.now());

        return builder;
    }

}
