package tools.vitruv.change.composite;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;

/** Tests {@link MetamodelDescriptor#overlaps(MetamodelDescriptor)}. */
class MetamodelDescriptorTest {
  private static final MetamodelDescriptor A = MetamodelDescriptor.with("a");
  private static final MetamodelDescriptor A_AND_B = MetamodelDescriptor.with(Set.of("a", "b"));
  private static final MetamodelDescriptor B_AND_C = MetamodelDescriptor.with(Set.of("b", "c"));
  private static final MetamodelDescriptor C = MetamodelDescriptor.with("c");

  @Test
  void descriptorsWithCommonNamespaceOverlap() {
    assertTrue(A.overlaps(A));
    assertTrue(A_AND_B.overlaps(B_AND_C));
    assertTrue(B_AND_C.overlaps(A_AND_B));
  }

  @Test
  void containedDescriptorsOverlapInBothDirections() {
    assertTrue(A.overlaps(A_AND_B));
    assertTrue(A_AND_B.overlaps(A));
  }

  @Test
  void descriptorsWithoutCommonNamespaceDoNotOverlap() {
    assertFalse(A.overlaps(C));
    assertFalse(A_AND_B.overlaps(C));
    assertFalse(C.overlaps(A_AND_B));
  }
}
