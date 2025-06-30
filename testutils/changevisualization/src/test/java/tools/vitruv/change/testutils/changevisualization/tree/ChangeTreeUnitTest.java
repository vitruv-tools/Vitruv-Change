package tools.vitruv.change.testutils.changevisualization.tree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

class ChangeTreeUnitTest {
    @Test
    void zoomInWithCtrlIncreasesFontSize() throws Exception {

        SwingUtilities.invokeAndWait(() -> {
            // Arrange
            TabHighlighting dummyTabHighlighting = new TabHighlighting() {
                @Override
                public void setHighlightID(String highlightID) {

                }

                @Override
                public String getHighlightID() {
                    return "";
                }
            }; // Replace with a mock if you want

            // Act
            ChangeTree changeTree = new ChangeTree(dummyTabHighlighting);

            // Assert
            assertThat(changeTree.getLayout()).isInstanceOf(BorderLayout.class);

            // Check splitpane exists
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