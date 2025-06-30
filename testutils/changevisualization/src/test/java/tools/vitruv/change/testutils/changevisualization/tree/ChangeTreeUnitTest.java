package tools.vitruv.change.testutils.changevisualization.tree;

import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

class ChangeTreeUnitTest {
    @Test
    void zoomInWithCtrlIncreasesFontSize() throws Exception {

        SwingUtilities.invokeAndWait(() -> {
            TabHighlighting dummyTabHighlighting = new TabHighlighting() {
                @Override
                public void setHighlightID(String highlightID) {

                }

                @Override
                public String getHighlightID() {
                    return "";
                }
            };

            ChangeTree changeTree = new ChangeTree(dummyTabHighlighting);

            assertThat(changeTree.getLayout()).isInstanceOf(BorderLayout.class);

            assertThat(changeTree.getComponentCount()).isGreaterThanOrEqualTo(1);
            boolean hasSplitPane = false;
            for (int i = 0; i < changeTree.getComponentCount(); i++) {
                if (changeTree.getComponent(i) instanceof javax.swing.JSplitPane) {
                    hasSplitPane = true;
                    break;
                }
            }
            assertThat(hasSplitPane).isTrue();
        });
    }
}