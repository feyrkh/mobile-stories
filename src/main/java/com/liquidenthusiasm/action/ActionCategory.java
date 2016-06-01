package com.liquidenthusiasm.action;

import java.util.*;

import com.googlecode.jctree.ArrayListTree;
import com.googlecode.jctree.NodeNotFoundException;
import com.googlecode.jctree.Tree;

public enum ActionCategory {
    World(0l), Atelier(1l), Quarters(2l), Library(3l
    ), Ledgers(4l);

    private final String label;

    private long id;

    private static Map<Long, ActionCategory> byId = new HashMap<>();

    public static Tree<ActionCategory> world = new ArrayListTree<>();

    public static List<ActionCategory> rootList = new ArrayList<>();

    static {
        rootList.add(World);
        world.add(World);
        try {
            world.add(World, Atelier);
            world.add(Atelier, Quarters);
            world.add(Atelier, Library);
            world.add(Atelier, Ledgers);
        } catch (NodeNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        for (ActionCategory category : world) {
            byId.put(category.id, category);
        }
    }

    public static ActionCategory getParent(ActionCategory current) {
        try {
            return world.parent(current);
        } catch (NodeNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Collection<ActionCategory> getChildren(ActionCategory current) {
        try {
            return world.children(current);
        } catch (NodeNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Collection<ActionCategory> getSiblings(ActionCategory current) {
        try {
            ActionCategory parent = getParent(current);
            if (parent == null) {
                return ActionCategory.rootList;
            }
            return world.children(parent);
        } catch (NodeNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    ActionCategory(long id) {
        this.id = id;
        this.label = this.toString();
    }

    public static ActionCategory getById(Long id) {
        if (byId.containsKey(id)) {
            return byId.get(id);
        } else {
            return Atelier;
        }
    }

    public long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
