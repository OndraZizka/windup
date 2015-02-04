package org.jboss.windup.testutils.rulefilters;

import org.jboss.forge.furnace.util.Predicate;
import org.jboss.windup.config.WindupRuleProvider;


/**
 * Convenience type with better name.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public interface RuleFilter extends Predicate<WindupRuleProvider>
{

}