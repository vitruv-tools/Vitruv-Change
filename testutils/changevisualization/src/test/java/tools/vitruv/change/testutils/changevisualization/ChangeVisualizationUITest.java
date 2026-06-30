package tools.vitruv.change.testutils.changevisualization;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class ChangeVisualizationUITest {

  private ChangeVisualizationUI ui;

  @BeforeEach
  void setUp() throws Exception {
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

    ChangeVisualizationDataModel mockModel = new ChangeVisualizationDataModel();
    if (!GraphicsEnvironment.isHeadless()) {
      ui = new ChangeVisualizationUI(mockModel);
    }
  }

  @Test
  void testInitializeWindowProperties() {
    // Check close operation
    assumeTrue(ui != null, "Skipping UI test in headless environment.");
    assertEquals(WindowConstants.DISPOSE_ON_CLOSE, ui.getDefaultCloseOperation(), 
                  "Window should dispose on close");

    // Check size is within screen bounds
    Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    assertTrue(ui.getWidth() <= screenSize.width - 30, "Width should fit within screen");
    assertTrue(ui.getHeight() <= screenSize.height - 60, "Height should fit within screen");

    // Check window is centered
    Dimension frameSize = ui.getSize();
    int expectedX = (screenSize.width - frameSize.width) / 2;
    int expectedY = (screenSize.height - frameSize.height) / 2;

    assertEquals(expectedX, ui.getX(), 30, "Window X position should be centered (±30)");
    assertEquals(expectedY, ui.getY(), 30, "Window Y position should be centered (±30)");
  }

  @Test
  void testCreateFontWithNullKeyFallsBackToDefault() throws Exception {
    // Use reflection to access the private method
    Method createFontMethod = ChangeVisualizationUI.class.getDeclaredMethod(
            "createFont", String.class, float.class, int.class);
    createFontMethod.setAccessible(true);

    // Call with null key
    Font font = (Font) createFontMethod.invoke(null, null, 16f, Font.PLAIN);

    assertNotNull(font, "Font should not be null when null fontKey is passed");
    assertEquals(16, font.getSize(), "Font size should be 16");
    assertEquals(Font.PLAIN, font.getStyle(), "Font style should be plain");
  }

  @Test
  void testCreateFontWithInvalidKeyUsesDefaultFont() throws Exception {
    Method createFontMethod = ChangeVisualizationUI.class.getDeclaredMethod(
            "createFont", String.class, float.class, int.class);
    createFontMethod.setAccessible(true);

    Font font = (Font) createFontMethod.invoke(null, "NonExistentKey", 14f, Font.BOLD);

    assertNotNull(font, "Font should not be null for invalid key fallback");
    assertEquals(14, font.getSize(), "Font size should be 14");
    assertEquals(Font.BOLD, font.getStyle(), "Font style should be bold");
  }

  @Test
  void testCreateFontWithValidKey() throws Exception {
    Method createFontMethod = ChangeVisualizationUI.class.getDeclaredMethod(
            "createFont", String.class, float.class, int.class);
    createFontMethod.setAccessible(true);

    Font defaultFont = UIManager.getFont("Button.font");
    if (defaultFont == null) {
      System.out.println("Skipping testCreateFontWithValidKey: "
              + "'Button.font' not available on this platform.");
      return; // or use Assumptions.assumeTrue to skip
    }

    assumeTrue(defaultFont != null, "'Button.font' is not available in this environment.");

    Font font = (Font) createFontMethod.invoke(null, "Button.font", 18f, Font.ITALIC);

    assertNotNull(font, "Font should be created with valid key");
    assertEquals(18, font.getSize(), "Font size should be 18");
    assertEquals(Font.ITALIC, font.getStyle(), "Font style should be italic");
  }

  @Test
  void testMouseWheelListenerFieldIsStatic() throws Exception {
    Field mwlField = ChangeVisualizationUI.class.getDeclaredField("mwl");

    assertTrue(Modifier.isStatic(mwlField.getModifiers()),
        "mwl is a stateless MouseWheelListener and should be static so it is excluded "
            + "from instance serialization without needing transient (SonarCloud java:S1948)");
  }

  @Test
  void testMouseWheelListenerZoomsTextAreaFontOnCtrlScroll() throws Exception {
    Field mwlField = ChangeVisualizationUI.class.getDeclaredField("mwl");
    mwlField.setAccessible(true);
    MouseWheelListener listener = (MouseWheelListener) mwlField.get(null);
    assertNotNull(listener, "static mwl listener should be initialized on class load");

    JTextArea area = new JTextArea();
    area.setFont(area.getFont().deriveFont(16f));

    listener.mouseWheelMoved(ctrlWheelEvent(area, -1));
    assertEquals(18, area.getFont().getSize(), "Ctrl+scroll up should increase font size");

    listener.mouseWheelMoved(ctrlWheelEvent(area, 1));
    assertEquals(16, area.getFont().getSize(), "Ctrl+scroll down should decrease font size");
  }

  @Test
  void testMouseWheelListenerIgnoresScrollWithoutCtrl() throws Exception {
    Field mwlField = ChangeVisualizationUI.class.getDeclaredField("mwl");
    mwlField.setAccessible(true);
    MouseWheelListener listener = (MouseWheelListener) mwlField.get(null);

    JTextArea area = new JTextArea();
    area.setFont(area.getFont().deriveFont(16f));

    MouseWheelEvent plainScroll =
        new MouseWheelEvent(
            area,
            MouseEvent.MOUSE_WHEEL,
            0,
            0,
            0,
            0,
            0,
            false,
            MouseWheelEvent.WHEEL_UNIT_SCROLL,
            1,
            -1);
    listener.mouseWheelMoved(plainScroll);

    assertEquals(16, area.getFont().getSize(), "Scroll without Ctrl should not change font size");
  }

  @Test
  void testMouseWheelListenerIgnoresNonTextAreaSource() throws Exception {
    Field mwlField = ChangeVisualizationUI.class.getDeclaredField("mwl");
    mwlField.setAccessible(true);
    MouseWheelListener listener = (MouseWheelListener) mwlField.get(null);

    JLabel label = new JLabel();
    MouseWheelEvent event =
        new MouseWheelEvent(
            label,
            MouseEvent.MOUSE_WHEEL,
            0,
            InputEvent.CTRL_DOWN_MASK,
            0,
            0,
            0,
            false,
            MouseWheelEvent.WHEEL_UNIT_SCROLL,
            1,
            -1);

    // Should simply return without throwing for a non-JTextArea source.
    assertDoesNotThrow(
        () -> listener.mouseWheelMoved(event),
        "A non-JTextArea wheel source should be ignored without throwing");
  }

  private static MouseWheelEvent ctrlWheelEvent(JTextArea source, int rotation) {
    return new MouseWheelEvent(
        source,
        MouseEvent.MOUSE_WHEEL,
        0,
        InputEvent.CTRL_DOWN_MASK,
        0,
        0,
        0,
        false,
        MouseWheelEvent.WHEEL_UNIT_SCROLL,
        1,
        rotation);
  }
}

