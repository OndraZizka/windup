package org.jboss.windup.rules.apps.mavenize;

import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.map.LinkedMap;

/**
 * A simplified POM model - just G:A:V:C:P and the dependencies.
 *
 *  @author Ondrej Zizka, zizka at seznam.cz
 */
public class Pom implements Dependency
{
    MavenCoord coord = new MavenCoord();
    Pom parent;
    MavenCoord bom;
    String name;
    String description;
    Set<Dependency> dependencies = new LinkedHashSet<>();
    Set<Pom> localDependencies = new LinkedHashSet<>();
    OrderedMap<String, Pom> submodules = new LinkedMap<>();
    boolean root = false;


    enum ModuleRole { PARENT, BOM, NORMAL }
    ModuleRole role = ModuleRole.NORMAL;


    Pom(MavenCoord coord){
        this.coord = coord;
    }

    public Pom getParent(){
        return parent;
    }

    public Pom setParent(Pom parent){
        this.parent = parent;
        return this;
    }

    public String getName(){
        return name;
    }

    public Pom setName(String name){
        this.name = name;
        return this;
    }

    public String getDescription(){
        return description;
    }

    public Pom setDescription(String description){
        this.description = description;
        return this;
    }

    /**
     * Third-party library dependencies.
     */
    public Set<Dependency> getDependencies(){
        return dependencies;
    }

    public Pom setDependencies(Set<Dependency> dependencies){
        this.dependencies = dependencies;
        return this;
    }

    /**
     * Dependencies on other modules of the project.
     */
    public Set<Pom> getLocalDependencies(){
        return localDependencies;
    }

    public Pom setLocalDependencies(Set<Pom> localDependencies){
        this.localDependencies = localDependencies;
        return this;
    }

    public OrderedMap<String, Pom> getSubmodules(){
        return submodules;
    }

    public Pom setSubmodules(OrderedMap<String, Pom> submodules){
        this.submodules = submodules;
        return this;
    }

    /**
     * The root pom of the project.
     */
    public boolean isRoot(){
        return root;
    }

    public Pom setRoot(boolean root){
        this.root = root;
        return this;
    }


    public MavenCoord getCoord(){
        return coord;
    }

    public Pom setCoord(MavenCoord coord){
        this.coord = coord;
        return this;
    }

    public MavenCoord getBom(){
        return bom;
    }

    public Pom setBom(MavenCoord bom){
        this.bom = bom;
        return this;
    }

    @Override
    public String toString(){
        return "Pom{" + role + " " + coord + ", parent=" + (parent == null ? "" : parent.coord) + ", " + "name=" + name + /*", desc=" + description +*/ ", " + "dependencies=" + CollectionUtils.size(dependencies) + ", " + "localDependencies=" + CollectionUtils.size(localDependencies) + ", " + "submodules=" + CollectionUtils.size(submodules) + '}';
    }

    @Override
    public Role getRole()
    {
        return Role.MODULE;
    }
}
