package JTreeHelper.DataRetrieval;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Adrian
 */
public class DirectoryDataRetrieval extends AbstractDataRetrieval implements DataRetrieval {
    /**
     * Default root node text
     */
    protected String rootTextValue = "Local computer";

    /**
     * Return the defined rootTextValue
     * @return  String
     */
    @Override
    public String getRootTextValue() {
        return this.rootTextValue;
    }

    /**
     * Add main root items, for example Windows partitions C:\, D:\
     * @param   rootNode 
     */
    @Override
    public void addRootNodes(DefaultMutableTreeNode rootNode) {
        ArrayList<String> rootItems = this.getRootDirectories();
        rootItems.stream().forEach((item) -> {
            DefaultMutableTreeNode tmp_item = new DefaultMutableTreeNode(item);
            rootNode.add(tmp_item);
        });
    }

    /**
     * Add children to node
     * @param   treeNode
     * @param   startIndex
     * @param   depth 
     */
    @Override
    public void addChildren(DefaultMutableTreeNode treeNode, int startIndex, int depth) {
        // stop when we reach the chunk size
        if (startIndex == depth) {
            return;
        }
        ArrayList<String> items = new ArrayList<>();
        try {
            ArrayList<String> temp = this.scanDir(this.getNodeValue(treeNode));
            temp.stream().forEach((item) -> {
                items.add(item);
            });
        } catch (Exception e) {
            // if we do not have permissions to scan the dir
            // or the dir is not readable no mather the reason
            // we ignore it
            return;
        }
        if (items.isEmpty()) {
            return;
        }
        // remove all previous children if they were just empty labels
        ((DefaultMutableTreeNode)treeNode).removeAllChildren();
        items.stream().forEach((item) -> {
            DefaultMutableTreeNode tmp_item = new DefaultMutableTreeNode(item);
            ((DefaultMutableTreeNode)treeNode).add(tmp_item);
            this.addChildren(tmp_item, startIndex+1, depth);
        });
    }

    /**
     * Get the full path from a node concatenated with DirectorySeparator
     * @param   node
     * @return  String
     */
    @Override
    public String getNodeValue(DefaultMutableTreeNode node) {
        String builderPath = "";
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
        if (parent == null) {
            return builderPath;
        }
        ArrayList<String> nodes = new ArrayList<String>();
        DefaultMutableTreeNode currentNode;
        currentNode = node;
        while (currentNode.getParent() != null) {
            nodes.add(currentNode.toString());
            currentNode = (DefaultMutableTreeNode)currentNode.getParent();
        }
        Collections.reverse(nodes);
        for (String item : nodes) {
            if (item.substring(item.length() - 1).equals(File.separator)) {
                item = item.substring(0, item.length() - 1);
            }
            builderPath += item + File.separator;
        }
        return builderPath;
    }

    /**
     * Retrieve the main root "directories".
     * Example Windows partitions C:\, D:\ etc.
     * @return  ArrayList 
     */
    private ArrayList<String> getRootDirectories() {
        ArrayList<String> rootDirectories = new ArrayList<String>();
        File[] list = File.listRoots();
        if (list.length != 0) {
            for (File item : list) {
                rootDirectories.add(item.getAbsolutePath());
            }
        }
        return rootDirectories;
    }

    /**
     * Return a list of files and directories in a given path
     * @param   path
     * @return  ArrayList<String> 
     */
    private ArrayList<String> scanDir(String path) {
        ArrayList<String> result = new ArrayList<>();
        File dir_path = new File(path);
        File[] list = dir_path.listFiles();
        if (list.length == 0) {
            return result;
        }
        for (File file:list) {
            if (file.isDirectory() == false || file.canRead() == false) {
                continue;
            }
            result.add(file.getName());
        }
        return result;
    }
}
