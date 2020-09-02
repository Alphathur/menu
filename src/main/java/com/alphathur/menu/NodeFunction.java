package com.alphathur.menu;

import java.util.List;

/**
 * a road map
 */
public interface NodeFunction {

    boolean sonOf(MenuNode menuNode);

    boolean fatherOf(MenuNode menuNode);

    boolean elderBrotherOf(MenuNode menuNode);

    boolean littleBrotherOf(MenuNode menuNode);

    List<MenuNode> sons();

    MenuNode father();

    List<MenuNode> elderBrothers();

    List<MenuNode> littleBrothers();
}
