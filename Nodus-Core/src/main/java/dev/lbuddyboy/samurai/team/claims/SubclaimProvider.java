package dev.lbuddyboy.samurai.team.claims;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;

public class SubclaimProvider implements ContextResolver<Subclaim, BukkitCommandExecutionContext> {
    @Override
    public Subclaim getContext(BukkitCommandExecutionContext bukkitCommandExecutionContext) throws InvalidCommandArgument {
        return null;
    }
}
