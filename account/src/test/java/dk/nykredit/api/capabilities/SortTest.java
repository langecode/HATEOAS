package dk.nykredit.api.capabilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

public class SortTest {

    @Test
    public void testNoSorting() {
        List<Sort> sortings = Sort.getSortings(null);
        assertEquals(0, sortings.size());
        sortings = Sort.getSortings("");
        assertEquals(0, sortings.size());

    }

    @Test
    public void testIllegalSorting() {
        List<Sort> sortings = Sort.getSortings("?");
        assertEquals(0, sortings.size());
        sortings = Sort.getSortings("?::+");
        assertEquals(0, sortings.size());
        sortings = Sort.getSortings("3");
        assertEquals(0, sortings.size());
        sortings = Sort.getSortings("3::");
        assertEquals(0, sortings.size());
        sortings = Sort.getSortings("3::+");
        assertEquals(0, sortings.size());
        sortings = Sort.getSortings("|::u3::+|");
        assertEquals(0, sortings.size());
        sortings = Sort.getSortings("| | :: |");
        assertEquals(0, sortings.size());
        sortings = Sort.getSortings("attribute::-|");
        assertEquals(0, sortings.size());
        sortings = Sort.getSortings("|attribute::-|");
        assertEquals(0, sortings.size());
        sortings = Sort.getSortings("|t::+");
        assertEquals(0, sortings.size());
        sortings = Sort.getSortings("attribute::  ");
        assertEquals(0, sortings.size());
    }

    @Test
    public void testMatches () {
        assertTrue("attribute::+".matches("^(([a-zA-Z_0-9]+)?::(-|\\+)?)?((\\|[a-zA-Z_0-9]+)?::(-|\\+)?)*"));
        assertTrue("attribute::+|other::-".matches("^(([a-zA-Z_0-9]+)?::(-|\\+)?)?((\\|[a-zA-Z_0-9]+)?::(-|\\+)?)*"));
        assertTrue("attribute|otherattribute|thirdAttribute".matches("^(([a-zA-Z_0-9]+)?((\\|[a-zA-Z_0-9]+)*|::)(-|\\+)?)?((\\|[a-zA-Z_0-9]+)?::(-|\\+)?)*"));
        assertTrue("attribute::+|other::-".matches("^(([a-zA-Z_0-9]+)?((\\|[a-zA-Z_0-9]+)*|::)(-|\\+)?)?((\\|[a-zA-Z_0-9]+)?::(-|\\+)?)*"));
    }

    @Test
    public void testSingleSorting(){
        List<Sort> sortings = Sort.getSortings("attribute");
        assertEquals(1, sortings.size());
        assertEquals(Direction.ASC, sortings.get(0).getDirection());

        sortings = Sort.getSortings("attribute::+");
        assertEquals(1, sortings.size());
        assertEquals("attribute", sortings.get(0).getAttribute());
        assertEquals(Direction.ASC, sortings.get(0).getDirection());

        sortings = Sort.getSortings("attribute:: ");
        assertEquals(1, sortings.size());
        assertEquals("attribute", sortings.get(0).getAttribute());
        assertEquals(Direction.ASC, sortings.get(0).getDirection());

        sortings = Sort.getSortings("attribute::-");
        assertEquals(1, sortings.size());
        assertEquals(Direction.DESC, sortings.get(0).getDirection());

        sortings = Sort.getSortings("a::-");
        assertEquals(1, sortings.size());
        assertEquals(Direction.DESC, sortings.get(0).getDirection());

        sortings = Sort.getSortings("a::+");
        assertEquals(1, sortings.size());
        assertEquals(Direction.ASC, sortings.get(0).getDirection());

        sortings = Sort.getSortings("amount::+");
        assertEquals(1, sortings.size());
        assertEquals(Direction.ASC, sortings.get(0).getDirection());

    }

    @Test
    public void testMultipleSorting(){
        List<Sort> sortings = Sort.getSortings("attribute|otherattribute");
        assertEquals(2, sortings.size());
        assertEquals("attribute", sortings.get(0).getAttribute());
        assertEquals(Direction.ASC, sortings.get(0).getDirection());
        assertEquals("otherattribute", sortings.get(1).getAttribute());
        assertEquals(Direction.ASC, sortings.get(1).getDirection());

        sortings = Sort.getSortings("attribute::+|otherattribute::+");
        assertEquals(2, sortings.size());
        assertEquals(Direction.ASC, sortings.get(0).getDirection());
        assertEquals(Direction.ASC, sortings.get(1).getDirection());

        sortings = Sort.getSortings("attribute::+|otherattribute::+");
        assertEquals(2, sortings.size());
        assertEquals(Direction.ASC, sortings.get(0).getDirection());
        assertEquals(Direction.ASC, sortings.get(1).getDirection());

        sortings = Sort.getSortings("attribute|otherattribute::-");
        assertEquals(2, sortings.size());
        assertEquals(Direction.ASC, sortings.get(0).getDirection());
        assertEquals(Direction.DESC, sortings.get(1).getDirection());

        sortings = Sort.getSortings("attribute::-|otherattribute");
        assertEquals(2, sortings.size());
        assertEquals(Direction.DESC, sortings.get(0).getDirection());
        assertEquals(Direction.ASC, sortings.get(1).getDirection());

        sortings = Sort.getSortings("attribute::-|otherattribute::-");
        assertEquals(2, sortings.size());
        assertEquals(Direction.DESC, sortings.get(0).getDirection());
        assertEquals(Direction.DESC, sortings.get(1).getDirection());

        sortings = Sort.getSortings("attribute::-|otherattribute::+");
        assertEquals(2, sortings.size());
        assertEquals(Direction.DESC, sortings.get(0).getDirection());
        assertEquals(Direction.ASC, sortings.get(1).getDirection());

        sortings = Sort.getSortings("attribute|otherattribute");
        assertEquals(2, sortings.size());
        assertEquals(Direction.ASC, sortings.get(0).getDirection());
        assertEquals(Direction.ASC, sortings.get(1).getDirection());

        sortings = Sort.getSortings("attribute::-|otherattribute::+|third::+");
        assertEquals(3, sortings.size());
        assertEquals(Direction.DESC, sortings.get(0).getDirection());
        assertEquals(Direction.ASC, sortings.get(1).getDirection());
        assertEquals(Direction.ASC, sortings.get(2).getDirection());

        sortings = Sort.getSortings("attribute|otherattribute|third");
        assertEquals(3, sortings.size());
        assertEquals(Direction.ASC, sortings.get(0).getDirection());
        assertEquals(Direction.ASC, sortings.get(1).getDirection());
        assertEquals(Direction.ASC, sortings.get(2).getDirection());

        sortings = Sort.getSortings("attribute::-|otherattribute::+|third::+");
        assertEquals(3, sortings.size());
        assertEquals(Direction.DESC, sortings.get(0).getDirection());
        assertEquals(Direction.ASC, sortings.get(1).getDirection());
        assertEquals(Direction.ASC, sortings.get(2).getDirection());

        sortings = Sort.getSortings("attribute::-|otherattribute::+|third::-");
        assertEquals(3, sortings.size());
        assertEquals(Direction.DESC, sortings.get(0).getDirection());
        assertEquals(Direction.ASC, sortings.get(1).getDirection());
        assertEquals(Direction.DESC, sortings.get(2).getDirection());
    }

}
