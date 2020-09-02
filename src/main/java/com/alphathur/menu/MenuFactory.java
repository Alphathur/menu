package com.alphathur.menu;

import java.util.*;
import java.util.stream.Collectors;

public class MenuFactory {

    public static final Menu fromMenuNodes(List<MenuNode> menuNodes) {
        menuNodes = menuNodes.stream ()
                .sorted ( Comparator.comparingInt ( o -> MenuNodeUtil.code2Int ( o.getCode () ) ) )
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
                Set<MenuNode> boardSet = firMap.computeIfAbsent ( firKey, k -> new LinkedHashSet<> () );
                boardSet.add ( menuNode );
            }
            if (arr.length == 3) {
                String secKey = arr[0] + "_" + arr[1];
                Set<MenuNode> boardSet = secMap.computeIfAbsent ( secKey, k -> new LinkedHashSet<> () );
                boardSet.add ( menuNode );
            }
            if (arr.length == 4) {
                String thrKey = arr[0] + "_" + arr[1] + "_" + arr[2];
                Set<MenuNode> boardSet = thrMap.computeIfAbsent ( thrKey, k -> new LinkedHashSet<> () );
                boardSet.add ( menuNode );
            }
        }
        if (root == null) {
            throw new RuntimeException ( "NO ROOT" );
        }
        List<Menu> results = new ArrayList<> ();
        Menu rootDashBoard = buildFromBoard ( root );
        results.add ( rootDashBoard );

        Set<MenuNode> firSet = firMap.get ( rootDashBoard.getCode () );
        List<Menu> firBoards = buildFromBoards ( firSet );
        rootDashBoard.setChildren ( firBoards );

        List<Menu> allSecBoards = new ArrayList<> ();

        firBoards.forEach ( firBoard -> {
            Set<MenuNode> secSet = secMap.get ( firBoard.getCode () );
            List<Menu> secBoards = buildFromBoards ( secSet );
            firBoard.setChildren ( secBoards );
            allSecBoards.addAll ( secBoards );
        } );

        allSecBoards.forEach ( secBoard -> {
            Set<MenuNode> thrSet = thrMap.get ( secBoard.getCode () );
            List<Menu> thrBoards = buildFromBoards ( thrSet );
            secBoard.setChildren ( thrBoards );
        } );

        return rootDashBoard;
    }

    private static List<Menu> buildFromBoards(Set<MenuNode> set) {
        List<Menu> dashboards = new ArrayList<> ();
        if (set == null || set.isEmpty ()) {
            return dashboards;
        }
        set.forEach ( menuNode -> {
            Menu dashboard = buildFromBoard ( menuNode );
            dashboards.add ( dashboard );
        } );
        return dashboards;
    }

    private static Menu buildFromBoard(MenuNode board) {
        Menu menu = new Menu ();
        menu.setCode ( board.getCode () );
        menu.setName ( board.getName () );
        menu.setData ( board.getData () );
        return menu;
    }
}
