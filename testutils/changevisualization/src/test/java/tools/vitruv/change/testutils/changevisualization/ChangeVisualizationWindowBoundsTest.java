package tools.vitruv.change.testutils.changevisualization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests the window geometry of {@link ChangeVisualizationUI}.
 *
 * <p>These run headless. {@link ChangeVisualizationUI#computeCenteredWindowBounds} takes the screen
 * rectangle as an argument, so multi-display arrangements can be expressed as plain rectangles
 * rather than requiring displays to be attached. That matters here: the bug this covers only
 * appeared on multi-monitor machines and was therefore invisible to CI, which is headless.
 */
class ChangeVisualizationWindowBoundsTest {

  private static final int MAX_WIDTH = 1890;
  private static final int MAX_HEIGHT = 1020;

  @Test
  @DisplayName("centers on a single display")
  void centersOnSingleDisplay() {
    Rectangle bounds = ChangeVisualizationUI.computeCenteredWindowBounds(
        new Rectangle(0, 0, 2560, 1440));

    assertEquals(new Rectangle(335, 210, MAX_WIDTH, MAX_HEIGHT), bounds);
  }

  @Test
  @DisplayName("centers on the primary display only, not across the whole virtual desktop")
  void ignoresSecondaryDisplays() {
    // Regression for issue #320. Three 2560-wide monitors were reported; centering on the
    // combined desktop put the window at x=2895, on the middle monitor. Only the primary
    // display's rectangle is passed in, so the result cannot depend on the others.
    Rectangle primary = new Rectangle(0, 0, 2560, 1440);

    Rectangle bounds = ChangeVisualizationUI.computeCenteredWindowBounds(primary);

    assertEquals(335, bounds.x, "must center on the primary display, not the virtual desktop");
    assertTrue(
        primary.contains(bounds), "window must lie entirely within the primary display");
  }

  @Test
  @DisplayName("honours a primary display that does not start at the origin")
  void respectsScreenOrigin() {
    // A primary monitor sitting to the right of a secondary one starts at x>0. Centering has to
    // be relative to that origin, otherwise the window lands on the wrong screen.
    Rectangle primary = new Rectangle(2560, 0, 2560, 1440);

    Rectangle bounds = ChangeVisualizationUI.computeCenteredWindowBounds(primary);

    assertEquals(2560 + 335, bounds.x);
    assertEquals(210, bounds.y);
    assertTrue(primary.contains(bounds));
  }

  @Test
  @DisplayName("handles a display positioned above or left of the primary, giving negative origins")
  void respectsNegativeScreenOrigin() {
    Rectangle primary = new Rectangle(-1920, -1080, 1920, 1080);

    Rectangle bounds = ChangeVisualizationUI.computeCenteredWindowBounds(primary);

    assertTrue(primary.contains(bounds), "window must lie entirely within the primary display");
  }

  @Test
  @DisplayName("caps the size on a very large display")
  void capsSizeOnLargeDisplay() {
    Rectangle bounds = ChangeVisualizationUI.computeCenteredWindowBounds(
        new Rectangle(0, 0, 7680, 4320));

    assertEquals(MAX_WIDTH, bounds.width);
    assertEquals(MAX_HEIGHT, bounds.height);
  }

  @Test
  @DisplayName("shrinks to fit a display smaller than the caps, leaving a margin")
  void fitsSmallDisplay() {
    Rectangle screen = new Rectangle(0, 0, 1280, 800);

    Rectangle bounds = ChangeVisualizationUI.computeCenteredWindowBounds(screen);

    assertEquals(1280 - 30, bounds.width);
    assertEquals(800 - 60, bounds.height);
    assertTrue(screen.contains(bounds));
  }

  @Test
  @DisplayName("stays within the screen for a range of display sizes")
  void alwaysFitsWithinItsScreen() {
    int[][] screens = {
      {1280, 800}, {1500, 938}, {1920, 1080}, {2560, 1440}, {3440, 1440}, {3840, 2160}
    };
    for (int[] size : screens) {
      Rectangle screen = new Rectangle(0, 0, size[0], size[1]);
      Rectangle bounds = ChangeVisualizationUI.computeCenteredWindowBounds(screen);
      assertTrue(
          screen.contains(bounds),
          () -> "window " + bounds + " escaped screen " + screen);
    }
  }
}
