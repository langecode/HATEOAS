package dk.nykredit.api.capabilities;


import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompositionTest {

    @Test
    public void testNoComposition() {
        List<Composition> c = Composition.getEmbedded("");
        assertEquals(0, c.size());
        c = Composition.getEmbedded(null);
        assertEquals(0, c.size());
    }

    @Test
    public void testInvalidCompositions(){
        List<Composition> compositions = Composition.getEmbedded("concept+::projection|otherConcept::otherProjection");
        assertEquals(0, compositions.size());
        compositions = Composition.getEmbedded("concept::projection+|otherConcept::otherProjection-");
        assertEquals(0, compositions.size());
        compositions = Composition.getEmbedded("concept::projection|otherConcept::otherProjection-");
        assertEquals(0, compositions.size());
        compositions = Composition.getEmbedded("concept::projection|otherConcept::");
        assertEquals(0, compositions.size());
    }

    @Test
    public void testValidCompositions(){
        List<Composition> compositions = Composition.getEmbedded("concept::projection|otherConcept::otherProjection");
        assertEquals(2, compositions.size());
        boolean conceptProjectionObserved = false;
        boolean otherConceptProjectionObserved = false;
        for (Composition composition : compositions) {
            if ("concept".equals(composition.getConcept())) {
                conceptProjectionObserved = "projection".equals(composition.getProjection());
            }
            if ("otherConcept".equals(composition.getConcept())) {
                otherConceptProjectionObserved = "otherProjection".equals(composition.getProjection());
            }
        }
        assertEquals(true, conceptProjectionObserved);
        assertEquals(true, otherConceptProjectionObserved);
    }
}

