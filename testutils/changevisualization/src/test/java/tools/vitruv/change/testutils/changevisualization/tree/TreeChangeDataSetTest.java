package tools.vitruv.change.testutils.changevisualization.tree;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

public class TreeChangeDataSetTest {

  private DefaultMutableTreeNode root;
  private JTree tree;

  @BeforeEach
  public void setup() {
    root = new DefaultMutableTreeNode("root");
    DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("child1");
    DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("child2");
    DefaultMutableTreeNode grandchild = new DefaultMutableTreeNode("grandchild");

    child1.add(grandchild);
    root.add(child1);
    root.add(child2);

    tree = new JTree(root);
  }

  private String invokeGetPathString(TreeNode[] path) throws Exception {
    Method method = Class.forName("tools.vitruv.change.testutils.changevisualization.tree.TreeChangeDataSet")
                          .getDeclaredMethod("getPathString", TreeNode[].class, JTree.class);
    method.setAccessible(true);
    return (String) method.invoke(null, path, tree);
  }

  @Test
  public void testValidPath() throws Exception {
    TreeNode[] path = ((DefaultMutableTreeNode) root.getChildAt(0)).getPath();
    String result = invokeGetPathString(path);
    assertEquals("0|0", result); // child1 is the 0th child of root
  }

  @Test
  public void testDeeperPath() throws Exception {
    TreeNode[] path = ((DefaultMutableTreeNode) root.getChildAt(0).getChildAt(0)).getPath();
    String result = invokeGetPathString(path);
    assertEquals("0|0|0", result); // grandchild is the 0th child of child1
  }

  @Test
  public void testEmptyPath() throws Exception {
    String result = invokeGetPathString(new TreeNode[0]);
    assertEquals("", result);
  }

  @Test
  public void testNullPath() throws Exception {
    String result = invokeGetPathString(null);
    assertEquals("", result);
  }
}