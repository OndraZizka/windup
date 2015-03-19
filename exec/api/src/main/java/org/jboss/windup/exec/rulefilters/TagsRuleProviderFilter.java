package org.jboss.windup.exec.rulefilters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.jboss.forge.furnace.util.Predicate;
import org.jboss.windup.config.RuleProvider;

/**
 * Accepts the given provider if it has any or all of requested include tags,
 * or has not all or any of the requested exclude tags.
 *
 * @author Ondrej Zizka, ozizka@redhat.com
 */
public class TagsRuleProviderFilter implements Predicate<RuleProvider>
{
    private static Logger log = Logger.getLogger(TagsRuleProviderFilter.class.getName());

    private final Set<String> includeTags;
    private final Set<String> excludeTags;
    private boolean requireAllIncludeTags = false;
    private boolean requireAllExcludeTags = false;


    public TagsRuleProviderFilter(Set<String> includeTags, Set<String> excludeTags)
    {
        this.includeTags = new HashSet(Arrays.asList(includeTags));
        this.excludeTags = new HashSet(Arrays.asList(excludeTags));
    }


    public TagsRuleProviderFilter setRequireAllIncludeTags(boolean requireAll)
    {
        this.requireAllIncludeTags = requireAll;
        return this;
    }

    public TagsRuleProviderFilter setRequireAllExcludeTags(boolean requireAll)
    {
        this.requireAllExcludeTags = requireAll;
        return this;
    }



    @Override
    public boolean accept(RuleProvider provider)
    {
        Set<String> tags = provider.getMetadata().getTags();

        boolean includeMatches = this.requireAllIncludeTags
                ? tags.containsAll(this.includeTags)
                : CollectionUtils.containsAny(tags, this.includeTags);

        boolean excludeMatches = this.requireAllExcludeTags
                ? !tags.containsAll(this.excludeTags)
                : !CollectionUtils.containsAny(tags, this.excludeTags);

        return includeMatches && excludeMatches;
    }

}