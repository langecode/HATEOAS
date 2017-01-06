package dk.nykredit.api.capabilities;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class ElementTest {

    @Test
    public void testNoElements() {
        Optional<Element> element = Element.getElement(null);
        assertEquals(Optional.empty(), element);
        element = Element.getElement("");
        assertEquals(Optional.empty(), element);
    }

    @Test
    public void testNonValidInput(){
        Optional<Element> element = Element.getElement("3");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("0|4");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("|0");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("|1");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("0|0");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("-1|0");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("1|+1");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("4|4");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("4|3");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("501|501");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("1|501");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("501|500");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("1501|1500");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("| | :: 0|");
        assertEquals(Optional.empty(), element);
        element = Element.getElement("| | :: 0|::");
        assertEquals(Optional.empty(), element);
    }

    @Test
    public void testValidInput(){
        Optional<Element> element = Element.getElement("3|4");
        assertEquals(3, element.isPresent() ? element.get().getStart() : Optional.empty());
        assertEquals(4, element.isPresent() ? element.get().getEnd() : Optional.empty());
        element = Element.getElement("10|14");
        assertEquals(10, element.isPresent() ? element.get().getStart() : Optional.empty());
        assertEquals(14, element.isPresent() ? element.get().getEnd() : Optional.empty());
        element = Element.getElement("1|500");
        assertEquals(1, element.isPresent() ? element.get().getStart() : Optional.empty());
        assertEquals(500, element.isPresent() ? element.get().getEnd() : Optional.empty());
        element = Element.getElement("4|30");
        assertEquals(4, element.isPresent() ? element.get().getStart() : Optional.empty());
        assertEquals(30, element.isPresent() ? element.get().getEnd() : Optional.empty());
        element = Element.getElement("501|502");
        assertEquals(501, element.isPresent() ? element.get().getStart() : Optional.empty());
        assertEquals(502, element.isPresent() ? element.get().getEnd() : Optional.empty());
    }

}
