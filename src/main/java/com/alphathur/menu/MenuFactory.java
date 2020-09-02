package com.alphathur.menu;

import java.util.*;
import java.util.stream.Collectors;

/**
 * a factory can use menu-node to build a menu tree
 */
public class MenuFactory {

    public static final Menu fromMenuNodes(List<MenuNode> menuNodes) {
        menuNodes = menuNodes.stream ()
                .sorted ( Comparator.comparingInt ( o -> MenuNodeOperator.code2Int ( o.getCode () ) ) )
                .collect ( Collectors.toList () );
        if (menuNodes == null || menuNodes.isEmpty ()) {
            return null;
        }
        MenuNode root = null;
        Map<String, Set<MenuNode>> firMap = new LinkedHashMap<> ();
        Map<String, Set<MenuNode>> secMap = new LinkedHashMap<> ();
        Map<String, Set<MenuNode>> thrMap = new LinkedHashMap<> ();

        for (MenuNode menuNode : menuNodes) {
            String[] arr = menuNode.getCode ().split ( "_" );
            if (arr.length == 1) {
                root = menuNode;
            }
            if (arr.length == 2) {
                String firKey = arr[0];
                Set<MenuNode> nodeSet = firMap.computeIfAbsent ( firKey, k -> new LinkedHashSet<> () );
                nodeSet.add ( menuNode );
            }
            if (arr.length == 3) {
                String secKey = arr[0] + "_" + arr[1];
                Set<MenuNode> nodeSet = secMap.computeIfAbsent ( secKey, k -> new LinkedHashSet<> () );
                nodeSet.add ( menuNode );
            }
            if (arr.length == 4) {
                String thrKey = arr[0] + "_" + arr[1] + "_" + arr[2];
                Set<MenuNode> nodeSet = thrMap.computeIfAbsent ( thrKey, k -> new LinkedHashSet<> () );
                nodeSet.add ( menuNode );
            }
        }
        if (root == null) {
            throw new RuntimeException ( "NO ROOT" );
        }
        List<Menu> results = new ArrayList<> ();
        Menu rootNode = buildFromNode ( root );
        results.add ( rootNode );

        Set<MenuNode> firSet = firMap.get ( rootNode.getCode () );
        List<Menu> firNodes = buildFromNodes ( firSet );
        rootNode.setChildren ( firNodes );

        List<Menu> allSecNodes = new ArrayList<> ();

        firNodes.forEach ( firNode -> {
            Set<MenuNode> secSet = secMap.get ( firNode.getCode () );
            List<Menu> secNodes = buildFromNodes ( secSet );
            firNode.setChildren ( secNodes );
            allSecNodes.addAll ( secNodes );
        } );

        allSecNodes.forEach ( secNode -> {
            Set<MenuNode> thrSet = thrMap.get ( secNode.getCode () );
            List<Menu> thrNodes = buildFromNodes ( thrSet );
            secNode.setChildren ( thrNodes );
        } );

        return rootNode;
    }

    private static List<Menu> buildFromNodes(Set<MenuNode> set) {
        List<Menu> menus = new ArrayList<> ();
        if (set == null || set.isEmpty ()) {
            return menus;
        }
        set.forEach ( menuNode -> {
            Menu menu = buildFromNode ( menuNode );
            menus.add ( menu );
        } );
        return menus;
    }

    private static Menu buildFromNode(MenuNode node) {
        Menu menu = new Menu ();
        menu.setCode ( node.getCode () );
        menu.setName ( node.getName () );
        menu.setData ( node.getData () );
        return menu;
    }
}
