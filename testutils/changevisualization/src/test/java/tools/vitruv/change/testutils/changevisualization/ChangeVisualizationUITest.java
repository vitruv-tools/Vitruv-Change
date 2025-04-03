package tools.vitruv.change.testutils.changevisualization;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Field;
import javax.swing.JTextArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChangeVisualizationUITest {
    private MouseWheelListener mwl;
    private JTextArea textArea;

    @BeforeEach
    void setUp() throws Exception {
        // Create an instance of ChangeVisualizationUI with a mock data model
        ChangeVisualizationUI ui = new ChangeVisualizationUI(new ChangeVisualizationDataModel());

        // Use reflection to access the private MouseWheelListener field
        Field field = ChangeVisualizationUI.class.getDeclaredField("mwl");
        field.setAccessible(true);
        mwl = (MouseWheelListener) field.get(ui);

        // Create a JTextArea and attach the listener
        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.addMouseWheelListener(mwl);
    }

    @Test
    void testMouseWheelZoomIn() {
        int originalSize = textArea.getFont().getSize();

        // Simulate mouse wheel event for zooming in (negative rotation)
        MouseWheelEvent event = new MouseWheelEvent(
                textArea, MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(),
                InputEvent.CTRL_DOWN_MASK, 0, 0, 0, false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, -1
        );

        mwl.mouseWheelMoved(event);
        assertThat(textArea.getFont().getSize()).isEqualTo(originalSize + 2);
    }

    @Test
    void testMouseWheelZoomOut() {
        int originalSize = textArea.getFont().getSize();

        // Simulate mouse wheel event for zooming out (positive rotation)
        MouseWheelEvent event = new MouseWheelEvent(
                textArea, MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(),
                InputEvent.CTRL_DOWN_MASK, 0, 0, 0, false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, 1
        );

        mwl.mouseWheelMoved(event);
        assertThat(textArea.getFont().getSize()).isEqualTo(originalSize - 2);
    }

    @Test
    void testMouseWheelZoomUpperLimit() {
        textArea.setFont(new Font("Arial", Font.PLAIN, 30));

        // Try zooming in beyond 30 (should be capped)
        MouseWheelEvent event = new MouseWheelEvent(
                textArea, MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(),
                InputEvent.CTRL_DOWN_MASK, 0, 0, 0, false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, -1
        );

        mwl.mouseWheelMoved(event);
        assertThat(textArea.getFont().getSize()).isEqualTo(30);
    }

    @Test
    void testMouseWheelZoomLowerLimit() {
        textArea.setFont(new Font("Arial", Font.PLAIN, 5));

        // Try zooming out beyond 5 (should be capped)
        MouseWheelEvent event = new MouseWheelEvent(
                textArea, MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(),
                InputEvent.CTRL_DOWN_MASK, 0, 0, 0, false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, 1
        );

        mwl.mouseWheelMoved(event);
        assertThat(textArea.getFont().getSize()).isEqualTo(5);
    }

    @Test
    void testMouseWheelWithoutCtrlKeyDoesNothing() {
        int originalSize = textArea.getFont().getSize();

        // Simulate a wheel event without the CTRL key
        MouseWheelEvent event = new MouseWheelEvent(
                textArea, MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(),
                0, 0, 0, 0, false,
                MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, -1
        );

        mwl.mouseWheelMoved(event);
        assertThat(textArea.getFont().getSize()).isEqualTo(originalSize);
    }
}
