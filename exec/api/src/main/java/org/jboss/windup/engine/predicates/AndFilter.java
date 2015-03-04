package org.jboss.windup.engine.predicates;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jboss.forge.furnace.util.Predicate;
import org.jboss.windup.config.WindupRuleProvider;

/**
 * AND predicate which needs all of the given predicates to accept.
 * It will stop on first false if you setStopWhenKnown(true).
 * Null predicates on input are silently dropped.
 *
 * @author Ondrej Zizka, ozizka at redhat.com
 */
public class AndFilter implements Predicate<WindupRuleProvider>
{
    protected final Set<Predicate<WindupRuleProvider>> predicates;
    protected boolean stopWhenKnown = false;


    public AndFilter(Predicate<WindupRuleProvider> ... preds)
    {
        List<Predicate<WindupRuleProvider>> list = Arrays.asList(preds);
        list.removeAll(Collections.singleton(null));
        this.predicates = new HashSet(list);
    }

    @Override
    public boolean accept(WindupRuleProvider provider)
    {
        boolean res = true;
        if (this.predicates.isEmpty())
            return false;

        for( Predicate<WindupRuleProvider> pred : this.predicates )
        {
            if (!pred.accept(provider)){
                res = false;
                if(this.stopWhenKnown)
                    return false;
            }
        }
        return res;
    }


    public void setStopWhenKnown(boolean stopWhenKnown)
    {
        this.stopWhenKnown = stopWhenKnown;
    }

}