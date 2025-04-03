package tools.vitruv.change.testutils.changevisualization;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JTextArea;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChangeVisualizationUITest {

    private JTextArea textArea;
    private ChangeVisualizationUI changeVisualizationUI;

    @BeforeEach
    void setUp() {
        // Create an instance of ChangeVisualizationUI
        changeVisualizationUI = new ChangeVisualizationUI(new ChangeVisualizationDataModel());

        // Create a JTextArea and set the default font
        textArea = new JTextArea();
        textArea.setFont(ChangeVisualizationUI.DEFAULT_TEXTAREA_FONT);

        // Attach the MouseWheelListener
        textArea.addMouseWheelListener(changeVisualizationUI.mwl);
    }

    @Test
    void testMouseWheelZoomIn() {
        int originalSize = textArea.getFont().getSize();

        // Simulate a MouseWheelEvent with CTRL pressed (Zoom In)
        MouseWheelEvent event = new MouseWheelEvent(
                textArea, MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(),
                InputEvent.CTRL_DOWN_MASK, 0, 0, 0, false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, -1); // -1 for zoom in

        // Trigger the event
        textArea.dispatchEvent(event);

        // Verify the font size increased
        assertTrue(textArea.getFont().getSize() > originalSize, "Font size should increase on zoom in.");
    }

    @Test
    void testMouseWheelZoomOut() {
        int originalSize = textArea.getFont().getSize();

        // Simulate a MouseWheelEvent with CTRL pressed (Zoom Out)
        MouseWheelEvent event = new MouseWheelEvent(
                textArea, MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(),
                InputEvent.CTRL_DOWN_MASK, 0, 0, 0, false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, 1); // 1 for zoom out

        // Trigger the event
        textArea.dispatchEvent(event);

        // Verify the font size decreased
        assertTrue(textArea.getFont().getSize() < originalSize, "Font size should decrease on zoom out.");
    }

    @Test
    void testMouseWheelNoCtrl_NoZoom() {
        int originalSize = textArea.getFont().getSize();

        // Simulate a MouseWheelEvent WITHOUT CTRL pressed
        MouseWheelEvent event = new MouseWheelEvent(
                textArea, MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(),
                0, 0, 0, 0, false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, -1);

        // Trigger the event
        textArea.dispatchEvent(event);

        // Verify the font size remains unchanged
        assertEquals(originalSize, textArea.getFont().getSize(), "Font size should not change if CTRL is not pressed.");
    }
}
