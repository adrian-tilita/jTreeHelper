package JTreeHelper.DataRetrieval;

/**
 * Data Retrieval interface
 * @author Adrian Tilita
 * @email <adrian@tilita.ro>
 */
public interface DataRetrieval {
    public void addRootNodes(javax.swing.tree.DefaultMutableTreeNode treeModel);
    public void addChildren(javax.swing.tree.DefaultMutableTreeNode treeNode, int startIndex, int depth);
    public String getRootTextValue();
    public String getNodeValue(javax.swing.tree.DefaultMutableTreeNode node);
}
