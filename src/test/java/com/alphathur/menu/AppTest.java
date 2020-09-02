package com.alphathur.menu;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue ( true );
    }

    @Test
    public void testCreate() {
        MenuNode<String> root = new MenuNode<> ();
        root.setCode ( "a" );
        root.setData ( "this is a root menu" );
        root.setName ( "root menu" );


        MenuNode<String> node1 = new MenuNode<> ();
        node1.setCode ( "a_1" );
        node1.setData ( "this is a first level menu" );
        node1.setName ( "menu 1" );

        MenuNode<String> node2 = new MenuNode<> ();
        node2.setCode ( "a_2" );
        node2.setData ( "this is a first level menu" );
        node2.setName ( "menu 2" );

        MenuNode<String> node3 = new MenuNode<> ();
        node3.setCode ( "a_2_1" );
        node3.setData ( "this is a second level menu" );
        node3.setName ( "menu 21" );

        List<MenuNode> list = new ArrayList<MenuNode> ();
        list.add ( node1 );
        list.add ( node2 );
        list.add ( node3 );
        list.add ( root );

        MenuNodeOperator.insertSonBefore ( list, "a_2", "new node a20" );
        Menu<String> menu = MenuFactory.fromMenuNodes ( list );
        System.out.println ( menu );


        MenuNodeOperator.insertSonBehind ( list, "a_2", "new node a21" );
        Menu<String> menu0 = MenuFactory.fromMenuNodes ( list );
        System.out.println ( menu0 );


        MenuNodeOperator.insertBrotherBehind ( list, "a_2", "new node 23" );
        Menu<String> menu1 = MenuFactory.fromMenuNodes ( list );
        System.out.println ( menu1 );

        MenuNodeOperator.insertBrotherBehind ( list, "a_1", "new node 2" );
        Menu<String> menu11 = MenuFactory.fromMenuNodes ( list );
        System.out.println ( menu11 );

        MenuNodeOperator.insertBrotherBefore ( list, "a_1", "new node 222" );
        Menu<String> menu2 = MenuFactory.fromMenuNodes ( list );
        System.out.println ( menu2 );

        MenuNodeOperator.deleteNode ( list, "a_4" );
        Menu<String> menu3 = MenuFactory.fromMenuNodes ( list );
        System.out.println ( menu3 );

        MenuNodeOperator.cutAndPaste ( list, "a_3", "a_2", "before" );
        Menu<String> menu4 = MenuFactory.fromMenuNodes ( list );
        System.out.println ( menu4 );

        MenuNodeOperator.cutAndPaste ( list, "a_3", "a_2", "after" );
        Menu<String> menu5 = MenuFactory.fromMenuNodes ( list );
        System.out.println ( menu5 );

        MenuNodeOperator.cutAndPaste ( list, "a_4", "a_2", "inner_before" );
        Menu<String> menu6 = MenuFactory.fromMenuNodes ( list );
        System.out.println ( menu6 );

    }
}
