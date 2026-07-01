package tools.vitruv.change.testutils.changevisualization.tree;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.testutils.changevisualization.ui.TabColours;

class ChangeTreeNodeRendererTest {

  private static TabHighlighting highlightingFor(String id) {
    return new TabHighlighting() {
      @Override
      public void setHighlightID(String highlightID) {
        // Not needed for this test.
      }

      @Override
      public String getHighlightID() {
        return id;
      }
    };
  }

  private static DefaultMutableTreeNode featureNode(String featureName) {
    return new DefaultMutableTreeNode(new FeatureNode(featureName, "value", null, null, null));
  }

  @Test
  void highlightedNodeIsRenderedWithHighlightColour() throws Exception {
    SwingUtilities.invokeAndWait(
        () -> {
          ChangeTreeNodeRenderer renderer = new ChangeTreeNodeRenderer(highlightingFor("HL"));
          renderer.setActive(true);

          Component comp =
              renderer.getTreeCellRendererComponent(
                  new JTree(), featureNode("HL"), false, false, true, 0, false);

          assertThat(comp.getForeground()).isEqualTo(TabColours.HIGHLIGHT_COLOR);
        });
  }

  @Test
  void nonMatchingNodeIsNotHighlighted() throws Exception {
    SwingUtilities.invokeAndWait(
        () -> {
          ChangeTreeNodeRenderer renderer = new ChangeTreeNodeRenderer(highlightingFor("HL"));
          renderer.setActive(true);

          Component comp =
              renderer.getTreeCellRendererComponent(
                  new JTree(), featureNode("unrelated"), false, false, true, 0, false);

          assertThat(comp.getForeground()).isNotEqualTo(TabColours.HIGHLIGHT_COLOR);
        });
  }
}
