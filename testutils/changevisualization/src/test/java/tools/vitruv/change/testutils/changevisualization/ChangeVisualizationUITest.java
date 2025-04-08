package tools.vitruv.change.testutils.changevisualization;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Font;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import tools.vitruv.change.testutils.changevisualization.ChangeVisualizationUI;

public class ChangeVisualizationUITest {

    @Test
    public void testCreateFontWithNullKeyFallsBackToDefault() throws Exception {
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
    public void testCreateFontWithInvalidKeyUsesDefaultFont() throws Exception {
        Method createFontMethod = ChangeVisualizationUI.class.getDeclaredMethod(
                "createFont", String.class, float.class, int.class);
        createFontMethod.setAccessible(true);

        Font font = (Font) createFontMethod.invoke(null, "NonExistentKey", 14f, Font.BOLD);

        assertNotNull(font, "Font should not be null for invalid key fallback");
        assertEquals(14, font.getSize(), "Font size should be 14");
        assertEquals(Font.BOLD, font.getStyle(), "Font style should be bold");
    }

    @Test
    public void testCreateFontWithValidKey() throws Exception {
        Method createFontMethod = ChangeVisualizationUI.class.getDeclaredMethod(
                "createFont", String.class, float.class, int.class);
        createFontMethod.setAccessible(true);

        Font font = (Font) createFontMethod.invoke(null, "Button.font", 18f, Font.ITALIC);

        assertNotNull(font, "Font should be created with valid key");
        assertEquals(18, font.getSize(), "Font size should be 18");
        assertEquals(Font.ITALIC, font.getStyle(), "Font style should be italic");
    }
}

