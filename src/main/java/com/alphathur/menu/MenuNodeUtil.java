package com.alphathur.menu;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import java.util.*;
import java.util.stream.Collectors;

public class MenuNodeUtil {

    private static MenuNode current(List<MenuNode> menuNodes, String code) {
        return menuNodes.stream ().filter ( e -> e.getCode ().equals ( code ) ).findFirst ().orElse ( null );
    }

    public static void cutAndPaste(List<MenuNode> menuNodes, String source, String target, String type) {
        if (StrUtil.isBlank ( source ) || StrUtil.isBlank ( target ) || StrUtil
                .isBlank ( type )) {
            throw new RuntimeException ( "INVALID PARAM" );
        }
        if (type.equals ( "before" ) || type.equals ( "after" )) {
            cutHorizontal ( source, target, type, menuNodes );
        } else if (type.equals ( "inner_before" ) || type.equals ( "inner_after" )) {
            cutVerticalNew ( source, target, type, menuNodes );
        } else {
            throw new RuntimeException ( "INVALID PARAM" );
        }
    }

    private static void cutHorizontal(String source, String target, String type, List<MenuNode> sourceNodes) {
        MenuNode sourceNode = current ( sourceNodes, source );
        MenuNode targetNode = current ( sourceNodes, target );
        if (sourceNode == null || targetNode == null) {
            throw new RuntimeException ( "INVALID PARAM" );
        }
        String sourceCutPosition = null;
        String cutSource = null;
        if (type.equals ( "before" )) {
            cutSource = cutTargetBehind ( sourceNodes, source, target );
            sourceCutPosition = target;
        } else if (type.equals ( "after" )) {
            MenuNode elderBrother = getElderBrothers ( sourceNodes, target ).stream ().findFirst ()
                    .orElse ( null );
            if (elderBrother != null) {
                if (elderBrother != sourceNode) {
                    cutSource = cutTargetBehind ( sourceNodes, source, elderBrother.getCode () );
                    sourceCutPosition = elderBrother.getCode ();
                }
            } else {
                sourceCutPosition = generateNewCode ( target, false );
            }
        } else {
            throw new RuntimeException ( "INVALID PARAM" );
        }
        if (StrUtil.isBlank ( cutSource )) {
            cutSource = source;
        }
        if (sourceCutPosition != null) {
            cutSourceToTarget ( sourceNodes, cutSource, sourceCutPosition );
        }
    }

    private static void cutVerticalNew(String source, String target, String type, List<MenuNode> menuNodes) {
        MenuNode sourceNode = current ( menuNodes, source );
        MenuNode targetNode = current ( menuNodes, target );
        if (sourceNode == null || targetNode == null) {
            throw new RuntimeException ( "INVALID PARAM" );
        }
        List<MenuNode> sonNodes = getChildren ( menuNodes, target );
        List<MenuNode> closestSons = getClosestChild ( sonNodes, target );
        if (type.equals ( "inner_before" )) {
            //get the eldest brother
            String elderBrotherCode = generateNewCodeBeforeClosestChildren ( target, closestSons );
            MenuNode elderBrother = current ( menuNodes, elderBrotherCode );
            String sourceCutPosition;
            if (elderBrother != null) {
                sourceCutPosition = elderBrother.getCode ();
                String cutSource = cutTargetBehind ( closestSons, source, elderBrother.getCode () );
                if (StrUtil.isBlank ( cutSource )) {
                    cutSource = source;
                }
                //cut source and its children nodes to target position
                cutSourceToTarget ( menuNodes, cutSource, sourceCutPosition );
            } else {
                //cut source and its children nodes to target position
                String newCode = generateNewCodeAfterClosestChildren ( target, closestSons );
                //cut source and its children nodes to target position : newCode
                cutSourceToTarget ( menuNodes, source, newCode );
            }
        } else if (type.equals ( "inner_after" )) {
            String newCode = generateNewCodeAfterClosestChildren ( target, closestSons );
            //cut source and its children nodes to target position : newCode
            cutSourceToTarget ( menuNodes, source, newCode );
        } else {
            throw new RuntimeException ( "INVALID PARAM" );
        }
    }

    private static String cutTargetBehind(List<MenuNode> sourceNodes, String source, String target) {
        //move target and its all brother's nodes behind
        Map<String, String> targetBroPositionMap = buildBroPositionMap ( sourceNodes, target, false, false );
        String targetNewKey = generateNewCode ( target, false );
        targetBroPositionMap.put ( target, targetNewKey );
        sortByMap ( targetBroPositionMap, sourceNodes );
        //exclude source node if source node is target's brother node
        return targetBroPositionMap.get ( source );
    }

    private static void cutSourceToTarget(List<MenuNode> sourceNodes, String source, String sourceCutPosition) {
        //cut source and its children nodes to target position
        List<MenuNode> sourceSons = getChildren ( sourceNodes, source );
        Map<String, String> sourcePositionMap = buildSpecificPosition ( sourceSons, source,
                sourceCutPosition );
        sourcePositionMap.put ( source, sourceCutPosition );
        sortByMap ( sourcePositionMap, sourceNodes );

        //move source and its brother nodes forward
        Map<String, String> sourceBroPositionMap = buildBroPositionMap ( sourceNodes, source, true, false );
        sortByMap ( sourceBroPositionMap, sourceNodes );
    }

    private static Map<String, String> buildSpecificPosition(List<MenuNode> sourceNodes, String source,
                                                             String target) {
        Map<String, String> positionMap = new LinkedHashMap<> ();
        sourceNodes.forEach ( sourceSon -> {
            String oldKey = sourceSon.getCode ();
            String newKey = oldKey.replace ( source, target );
            positionMap.put ( oldKey, newKey );
        } );
        return positionMap;
    }

    public static void deleteNode(List<MenuNode> menuNodes, String code) {
        MenuNode menuNode = current ( menuNodes, code );
        if (menuNode == null) {
            throw new RuntimeException ( "CODE INVALID" );
        }
        //delete current node and its children nodes
        List<MenuNode> sonMenuNodes = getChildren ( menuNodes, code );
        sonMenuNodes.add ( menuNode );
        menuNodes.removeIf ( sonMenuNodes::contains );
        //move current node and its brother nodes forward
        Map<String, String> broPositionMap = buildBroPositionMap ( menuNodes, code, true, false );
        sortByMap ( broPositionMap, menuNodes );
    }

    public static String insertBrotherBefore(List<MenuNode> menuNodes, String code, String name) {
        MenuNode menuNode = current ( menuNodes, code );
        if (menuNode == null) {
            throw new RuntimeException ( "CODE INVALID" );
        }
        String previous = generateNewCode ( code, true );
        MenuNode previousNode = current ( menuNodes, previous );
        String newCode;
        if (previousNode != null) {
            return insertBrotherBehind ( menuNodes, previous, name );
        } else {
            //move brother nodes and children behind
            newCode = code;
            Map<String, String> broPositionMap = buildBroPositionMap ( menuNodes, code, false, true );
            sortByMap ( broPositionMap, menuNodes );
        }
        //insert node to new position
        insertNewNode ( newCode, name, menuNodes );
        return newCode;
    }

    public static String insertBrotherBehind(List<MenuNode> menuNodes, String code, String name) {
        MenuNode menuNode = current ( menuNodes, code );
        if (menuNode == null) {
            throw new RuntimeException ( "CODE INVALID" );
        }
        // //insert node before current node
        String newCode = generateNewCode ( code, false );
        MenuNode nextMenuNode = current ( menuNodes, newCode );
        if (nextMenuNode != null) {
            //move brother nodes and children behind
            Map<String, String> broPositionMap = buildBroPositionMap ( menuNodes, code, false, false );
            sortByMap ( broPositionMap, menuNodes );
        }
        //insert node to new position
        insertNewNode ( newCode, name, menuNodes );
        return newCode;
    }

    public static String insertSonBehind(List<MenuNode> menuNodes, String code, String name) {
        Objects.requireNonNull ( code );
        String[] codeArr = code.split ( "_" );
        if (codeArr.length == 4) {
            throw new RuntimeException ( "BEYOND 4 LEVEL" );
        }
        MenuNode menuNode = current ( menuNodes, code );
        if (menuNode == null) {
            throw new RuntimeException ( "CODE INVALID" );
        }
        //find current node and all children nodes
        List<MenuNode> sonMenuNodes = getChildren ( menuNodes, code );
        List<MenuNode> closestSons = getClosestChild ( sonMenuNodes, code );
        String newCode = generateNewCodeAfterClosestChildren ( code, closestSons );
        if (newCode.split ( "_" ).length > 4) throw new RuntimeException ( "BEYOND 4 LEVEL" );
        //insert node to new position
        insertNewNode ( newCode, name, menuNodes );
        return newCode;
    }

    public static String insertSonBefore(List<MenuNode> menuNodes, String code, String name) {
        Objects.requireNonNull ( code );
        String[] codeArr = code.split ( "_" );
        if (codeArr.length == 4) {
            throw new RuntimeException ( "BEYOND 4 LEVEL" );
        }
        MenuNode menuNode = current ( menuNodes, code );
        if (menuNode == null) {
            throw new RuntimeException ( "CODE INVALID" );
        }
        List<MenuNode> sonMenuNodes = getChildren ( menuNodes, code );
        List<MenuNode> closestSons = getClosestChild ( sonMenuNodes, code );
        String newCode = generateNewCodeBeforeClosestChildren ( code, closestSons );
        if (newCode.split ( "_" ).length > 4) {
            throw new RuntimeException ( "BEYOND 4 LEVEL" );
        }
        Map<String, String> positionExchangeMap = buildPositionMap ( closestSons, menuNodes, code,
                false );
        sortByMap ( positionExchangeMap, menuNodes );
        insertNewNode ( newCode, name, menuNodes );
        return newCode;
    }

    private static Map<String, String> buildBroPositionMap(List<MenuNode> sourceNodes, String code,
                                                           boolean forward, boolean includeSelf) {
        List<MenuNode> brotherMenuNodes = getElderBrothers ( sourceNodes, code );
        if (includeSelf) {
            brotherMenuNodes.add ( 0, current ( sourceNodes, code ) );
        }
        String parentCode = code.substring ( 0, code.lastIndexOf ( "_" ) );
        return buildPositionMap ( brotherMenuNodes, sourceNodes, parentCode, forward );
    }

    private static String generateNewCode(String code, boolean forward) {
        String codePrefix = code.substring ( 0, code.lastIndexOf ( "_" ) + 1 );
        Integer lastCodePos = Integer.valueOf ( code.replace ( codePrefix, "" ) );
        if (forward) {
            lastCodePos--;
        } else {
            lastCodePos++;
        }
        return codePrefix + lastCodePos;
    }

    private static List<MenuNode> getClosestChild(List<MenuNode> sonMenuNodes, String code) {
        return sonMenuNodes.stream ().filter (
                sonNode -> sonNode.getCode ().split ( "_" ).length == (code.split ( "_" ).length + 1) )
                .collect ( Collectors.toList () );
    }

    private static List<MenuNode> getChildren(List<MenuNode> sourceMenuNodes, String code) {
        return sourceMenuNodes.stream ().filter ( menuNode -> menuNode.getCode ().contains ( code )
                && menuNode.getCode ().length () > code.length ()
                && menuNode.getCode ().split ( "_" ).length > code.split ( "_" ).length )
                .sorted ( Comparator.comparingInt ( menuNode -> code2Int ( menuNode.getCode () ) ) )
                .collect ( Collectors.toList () );
    }

    private static String generateNewCodeBeforeClosestChildren(String parentCode,
                                                               List<MenuNode> closestSons) {
        String newCode;
        if (CollectionUtil.isEmpty ( closestSons )) {
            newCode = parentCode + "_1";
        } else {
            MenuNode youngestSon = closestSons.get ( 0 );
            newCode = youngestSon.getCode ();
        }
        return newCode;
    }

    private static String generateNewCodeAfterClosestChildren(String parentCode,
                                                              List<MenuNode> closestSons) {
        String newCode;
        if (CollectionUtil.isEmpty ( closestSons )) {
            newCode = parentCode + "_1";
        } else {
            MenuNode youngestSon = closestSons.get ( closestSons.size () - 1 );
            String code = youngestSon.getCode ();
            String[] arr = code.split ( "_" );
            int newPos = Integer.parseInt ( arr[arr.length - 1] ) + 1;
            newCode = code.substring ( 0, code.lastIndexOf ( "_" ) ) + "_" + newPos;
        }
        return newCode;
    }

    private static void insertNewNode(String newCode, String name, List<MenuNode> menuNodes) {
        MenuNode newNode = new MenuNode ();
        newNode.setName ( name );
        newNode.setCode ( newCode );
        menuNodes.add ( newNode );
    }

    static int code2Int(String code) {
        String prefix = code.substring ( 0, 1 );
        String intStr = code.replace ( "_", "" ).replace ( prefix, "" );
        if (StrUtil.isBlank ( intStr )) {
            return 0;
        }
        return Integer.parseInt ( intStr );
    }

    private static Map<String, String> buildPositionMap(List<MenuNode> movableNodes,
                                                        List<MenuNode> sourceNodes, String parentCode, boolean forward) {
        if (!forward) {
            Collections.reverse ( movableNodes );
        }
        //build map for nodes prepared to move
        Map<String, List<MenuNode>> movableSonsMap = new LinkedHashMap<> ();
        movableNodes.forEach ( node -> {
            String key = node.getCode ();
            movableSonsMap.put ( key, getChildren ( sourceNodes, key ) );
        } );
        //calculate new position for move
        Map<String, String> updateCodeMap = new LinkedHashMap<> ();
        String targetPrefix = parentCode + "_";
        movableSonsMap.keySet ().forEach ( oldKey -> {
            Integer currLastPos = Integer.valueOf ( oldKey.replace ( targetPrefix, "" ) );
            if (forward) {
                currLastPos--;
            } else {
                currLastPos++;
            }
            String newKey = targetPrefix + currLastPos;
            updateCodeMap.put ( oldKey, newKey );
            // sons also need to add
            List<MenuNode> sons = movableSonsMap.get ( oldKey );
            sons.forEach ( son -> {
                String sonNewKey = son.getCode ().replace ( oldKey, newKey );
                updateCodeMap.put ( son.getCode (), sonNewKey );
            } );
        } );
        return updateCodeMap;
    }

    private static void sortByMap(Map<String, String> positionExchangeMap, List<MenuNode> menuNodes) {
        positionExchangeMap.forEach ( (oldKey, newKey) -> updateMenuNode ( oldKey, newKey, menuNodes ) );
    }

    private static void updateMenuNode(String oldKey, String newKey, List<MenuNode> menuNodes) {
        MenuNode menuNode = current ( menuNodes, oldKey );
        menuNode.setCode ( newKey );
    }

    private static List<MenuNode> getElderBrothers(List<MenuNode> sourceMenuNodes, String code) {
        String targetPrefix = code.substring ( 0, code.lastIndexOf ( "_" ) + 1 );
        Integer targetLastPos = Integer.valueOf ( code.replace ( targetPrefix, "" ) );
        return sourceMenuNodes.stream ().filter ( menuNode -> menuNode.getCode ().contains ( targetPrefix )
                && menuNode.getCode ().split ( "_" ).length == code.split ( "_" ).length
                && Integer.parseInt ( menuNode.getCode ().replace ( targetPrefix, "" ) ) > targetLastPos )
                .sorted ( Comparator.comparingInt ( menuNode -> code2Int ( menuNode.getCode () ) ) )
                .collect ( Collectors.toList () );
    }
}
