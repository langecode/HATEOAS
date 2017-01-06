package dk.nykredit.api.capabilities;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SelectTest {


    @Test
    public void testNoSelection(){
        List<Select> selections = Select.getSelections(null);
        assertEquals(0, selections.size());
    }

    @Test
    public void testEmptySelection(){
        List<Select> selections = Select.getSelections("");
        assertEquals(0, selections.size());
    }

    @Test
    public void testWrongSelection(){
        List<Select> selections = Select.getSelections("::");
        assertEquals(0, selections.size());
        selections = Select.getSelections("|");
        assertEquals(0, selections.size());
    }

    @Test
    public void testIllegalSelection(){
        List<Select> selections = Select.getSelections("?::4");
        assertEquals(0, selections.size());
        selections = Select.getSelections("3::4");
        assertEquals(0, selections.size());
        selections = Select.getSelections("|?::4");
        assertEquals(0, selections.size());
        selections = Select.getSelections("attributeWithNoValue");
        assertEquals(0, selections.size());
        selections = Select.getSelections("| | :: |");
        assertEquals(0, selections.size());
    }

    @Test
    public void testSimpleSelection(){
        List<Select> selections = Select.getSelections("u::4");
        assertEquals(1, selections.size());
        selections = Select.getSelections("attribute::value");
        assertEquals(1, selections.size());
        selections = Select.getSelections("a::value");
        assertEquals(1, selections.size());
    }

    @Test
    public void testSelectionWithWhiteSpaceAttribute(){
        List<Select> selections = Select.getSelections("attribute ::value");
        assertEquals(0, selections.size());
        selections = Select.getSelections("attribute ::value");
        assertEquals(0, selections.size());
        selections = Select.getSelections(" attribute::value");
        assertEquals(0, selections.size());
        selections = Select.getSelections("attribute:: value");
        assertEquals(0, selections.size());
        selections = Select.getSelections("attribute:: value ");
        assertEquals(0, selections.size());
        selections = Select.getSelections("attribute::value ");
        assertEquals(0, selections.size());
    }

    @Test
    public void testSelectionWithSuspiciousCharacters(){
        List<Select> selections = Select.getSelections("attribute or '1' = '1::value");
        assertEquals(0, selections.size());
        selections = Select.getSelections("attribute or '1' = '1::value");
        assertEquals(0, selections.size());
        selections = Select.getSelections(" attribute::value or '1' = '1");
        assertEquals(0, selections.size());
        selections = Select.getSelections(" attribute:: value or '1' = '1");
        assertEquals(0, selections.size());
        selections = Select.getSelections("attribute:: value or '1' = '1");
        assertEquals(0, selections.size());
        selections = Select.getSelections("attribute:: value or '1' = '1");
        assertEquals(0, selections.size());
        selections = Select.getSelections("attribute :: value' or '1' = '1");
        assertEquals(0, selections.size());
        selections = Select.getSelections("attribute :: value'  or '1'='1");
        assertEquals(0, selections.size());
    }

    @Test
    public void testSelectionCriteria(){
        List<Select> selections = Select.getSelections("attribute::value|otherAttribute::otherValue");
        assertEquals(2, selections.size());
        boolean attributeValueObserved = false;
        boolean otherAttributeValueObserved = false;
        for (Select selection : selections) {
            if ("attribute".equals(selection.getAttribute())) {
                attributeValueObserved = "value".equals(selection.getValue());
            }
            if ("otherAttribute".equals(selection.getAttribute())) {
                otherAttributeValueObserved = "otherValue".equals(selection.getValue());
            }
        }
        assertEquals(true, attributeValueObserved);
        assertEquals(true, otherAttributeValueObserved);
        selections = Select.getSelections("a3::4|v::g");
        assertEquals(2, selections.size());
        selections = Select.getSelections("b3::4|v::g");
        assertEquals(2, selections.size());
    }

    @Test
    public void testSelectionHavingMultipleCriteria(){
        List<Select> selections = Select.getSelections("attribute::value|otherAttribute::otherValue");
        assertEquals(2, selections.size());
        boolean attributeValueObserved = false;
        boolean otherAttributeValueObserved = false;
        for (Select selection : selections) {
            if ("attribute".equals(selection.getAttribute())) {
                attributeValueObserved = "value".equals(selection.getValue());
            }
            if ("otherAttribute".equals(selection.getAttribute())) {
                otherAttributeValueObserved = "otherValue".equals(selection.getValue());
            }
        }
        assertEquals(true, attributeValueObserved);
        assertEquals(true, otherAttributeValueObserved);

        selections = Select.getSelections("attribute::value|otherAttribute::otherValue|i::4");
        assertEquals(3, selections.size());
        attributeValueObserved = false;
        otherAttributeValueObserved = false;
        boolean thirdAttributeValueObserved = false;
        for (Select selection : selections) {
            if ("attribute".equals(selection.getAttribute())) {
                attributeValueObserved = "value".equals(selection.getValue());
            }
            if ("otherAttribute".equals(selection.getAttribute())) {
                otherAttributeValueObserved = "otherValue".equals(selection.getValue());
            }
            if ("i".equals(selection.getAttribute())) {
                thirdAttributeValueObserved = "4".equals(selection.getValue());
            }
        }
        assertEquals(true, attributeValueObserved);
        assertEquals(true, otherAttributeValueObserved);
        assertEquals(true, thirdAttributeValueObserved);

        selections = Select.getSelections("v::g|f::j|u::iio|b3::4");
        assertEquals(4, selections.size());
        selections = Select.getSelections("d::4|v::g|f::j|u::iio|c3::yy");
        assertEquals(5, selections.size());
        selections = Select.getSelections("d::4|v::g|f::j|u::iio|d3::yy|u::99");
        assertEquals(6, selections.size());
    }

    @Test
    public void testIllegalSelections(){
        List<Select> selections = Select.getSelections("?::4|a::b");
        assertEquals(0, selections.size());
        selections = Select.getSelections("?::4|a::b");
        assertEquals(0, selections.size());
        selections = Select.getSelections("3::4|v::g|f::j|u::iio|3::yy|u::99|");
        assertEquals(0, selections.size());
        selections = Select.getSelections("v::g|f::j|u::iio|3::4");
        assertEquals(0, selections.size());
    }

}
