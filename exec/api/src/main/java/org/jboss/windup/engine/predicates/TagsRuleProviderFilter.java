package org.jboss.windup.engine.predicates;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.jboss.forge.furnace.util.Predicate;
import org.jboss.windup.config.WindupRuleProvider;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.ocpsoft.rewrite.context.Context;
import org.ocpsoft.rewrite.context.ContextBase;

/**
 * Accepts the given provider if it has any of requested tags.
 *
 * @author Ondrej Zizka, ozizka@redhat.com
 */
public class TagsRuleProviderFilter implements Predicate<WindupRuleProvider>
{
    private static Logger log = Logger.getLogger(TagsRuleProviderFilter.class.getName());

    private Set<String> requestedTags;
    private boolean requireAllTags = false;

    public TagsRuleProviderFilter(String ... requestedTags)
    {
        this.requestedTags = new HashSet(Arrays.asList(requestedTags));
    }


    public TagsRuleProviderFilter setRequireAllTags(boolean requireAllTags)
    {
        this.requireAllTags = requireAllTags;
        return this;
    }



    @Override
    public boolean accept(WindupRuleProvider provider)
    {
        //Set<String> tags = provider.getMetadata().getTags(); // TODO Use when WINDUP-520 implemented.
        Set<String> tags = new HashSet<>();
        Context ctx = new ContextBase(){};
        provider.enhanceMetadata(ctx);
        Object value = ctx.get(RuleMetadata.TAGS);
        if (value instanceof Collection)
        {
            for( Object tag : (Collection)value )
            {
                if (tag instanceof String)
                    tags.add((String)tag);
            }
        }

        if (this.requireAllTags)
            return tags.containsAll(this.requestedTags);
        else
            return CollectionUtils.containsAny(tags, this.requestedTags);
    }

}