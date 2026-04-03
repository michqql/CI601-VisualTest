package me.mp1282.visualtesttesting.system.diagram;

import me.mp1282.visualtest.system.diagram.Diagram;

import java.lang.reflect.Field;

public class TestDiagram {

    /**
     * Test 1: testing the number of fields in the Diagram class
     */
    public void test1() {
        Field[] fields = Diagram.class.getDeclaredFields();
        assert fields.length == 5;
    }

    /**
     * Test 2: testing the initial state of the Diagram after instantiation.
     */
    public void test2() {
        Diagram diagram = new Diagram();
    }
}
