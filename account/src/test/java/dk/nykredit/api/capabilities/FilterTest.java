package dk.nykredit.api.capabilities;


import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class FilterTest {


    @Test
    public void testNoFilter () {
        List<Filter> filter = Filter.getFilter(null);
        assertEquals(0, filter.size());
        filter = Filter.getFilter("");
        assertEquals(0, filter.size());
    }

    @Test
    public void testIllegal(){
        List<Filter> filter = Filter.getFilter("| | :: |");
        assertEquals(0, filter.size());
        filter = Filter.getFilter(":: ::||:|:+");
        assertEquals(0, filter.size());
        filter = Filter.getFilter("attribute|");
        assertEquals(0, filter.size());
    }

    @Test
    public void testSimplel(){
        List<Filter> filter = Filter.getFilter("attribute");
        assertEquals(1, filter.size());
        assertEquals("attribute",filter.get(0).getAttribute());
        assertEquals(Inclusion.INC,filter.get(0).getInclusion());

        filter = Filter.getFilter("attribute|otherAttribute");
        assertEquals(2, filter.size());
        assertEquals("attribute",filter.get(0).getAttribute());
        assertEquals(Inclusion.INC,filter.get(1).getInclusion());
        assertEquals("otherAttribute",filter.get(1).getAttribute());
        assertEquals(Inclusion.INC,filter.get(0).getInclusion());

        filter = Filter.getFilter("attribute::|otherAttribute::");
        assertEquals(2, filter.size());
        assertEquals("attribute",filter.get(0).getAttribute());
        assertEquals(Inclusion.INC,filter.get(1).getInclusion());
        assertEquals("otherAttribute",filter.get(1).getAttribute());
        assertEquals(Inclusion.INC,filter.get(0).getInclusion());

        filter = Filter.getFilter("attribute|otherAttribute::");
        assertEquals(2, filter.size());
        assertEquals("attribute",filter.get(0).getAttribute());
        assertEquals(Inclusion.INC,filter.get(1).getInclusion());
        assertEquals("otherAttribute",filter.get(1).getAttribute());
        assertEquals(Inclusion.INC,filter.get(0).getInclusion());

        filter = Filter.getFilter("attribute::-|otherAttribute::-");
        assertEquals(2, filter.size());
        assertEquals("attribute",filter.get(0).getAttribute());
        assertEquals(Inclusion.EXC,filter.get(1).getInclusion());
        assertEquals("otherAttribute",filter.get(1).getAttribute());
        assertEquals(Inclusion.EXC,filter.get(0).getInclusion());
    }

}
