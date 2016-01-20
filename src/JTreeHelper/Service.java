package JTreeHelper;

import JTreeHelper.DataRetrieval.DataRetrieval;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.SwingUtilities;

/**
 * Main JTreeHelper service
 * @author Adrian Tilita
 * @email <adrian@tilita.ro>
 */
public class Service extends Observable implements TreeWillExpandListener {
    /**
     * Configurate if the JTree should have the root node visible
     */
    final private boolean VISIBLE_ROOT_NODE = true;

    /**
     * The depth of items to load on node expand - 0 for no limit
     */
    final private int LEVEL_DEPTH_LOAD = 2;

    /**
     * The actual JTree to be "helped"
     */
    private JTree JTree = null;

    /**
     * The parent frame
     */
    private JFrame frame = null;

    /**
     * RootNode Reference
     */
    private DefaultMutableTreeNode rootNode = null;

    /**
     * Data handler - the Strategy class that populate with data
     */
    private DataRetrieval dataRetrieval = null;

    /**
     * Stores the nodes already loaded
     */
    private ArrayList<String> cachedNodes = new ArrayList<String>();

    /**
     * Service constructor - class cannot be instantiated without
     * the necessary elements injected
     * 
     * @param   JTree 
     */
    public Service(JTree JTree, JFrame frame) {
        this.JTree = JTree;
        this.frame = frame;
    }

    /**
     * Injects a strategy of data retrieval
     * @param   value
     * @return  Service
     */
    public Service setDataRetrieval(DataRetrieval value) {
        this.dataRetrieval = value;
        return this;
    }

    /**
     * Return the DataRetrieval strategy
     * If not set, a default one will be instantiated
     * 
     * @return  DataRetrieval 
     */
    public DataRetrieval getDataRetrieval() {
        if (this.dataRetrieval == null) {
            this.dataRetrieval = new JTreeHelper.DataRetrieval.DirectoryDataRetrieval();
        }
        return this.dataRetrieval;
    }

    /**
     * Apply the helper service
     */
    public void apply() {
        this.emptyTree();
        this.addInitialData();
        this.attachEvents();
    }

    /**
     * Return the current selected item value
     * @return String
     */
    public String getSelectedValue() {
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)this.JTree.getLastSelectedPathComponent();
        if (currentNode == null) {
            return "";
        }
        return this.dataRetrieval.getNodeValue(currentNode);
    }

    /**
     * Empties the tree from the default data
     */
    private void emptyTree() {
        this.rootNode = new DefaultMutableTreeNode(this.getDataRetrieval().getRootTextValue());
        DefaultTreeModel treeModel = new DefaultTreeModel(this.rootNode);
        this.JTree.setModel(treeModel);
    }

    /**
     * Add The initial data to load
     */
    private void addInitialData() {
        Thread initialTreeData = new Thread(() -> {
            try {
                this.dataRetrieval.addRootNodes(this.rootNode);
                // get child count
                int childCount = this.rootNode.getChildCount();
                if (childCount != 0) {
                    ((Observable)this).dispatchEvent(Event.START, (Object)this.rootNode);
                    for (int i = 0; i < childCount; i++) {
                        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)this.rootNode.getChildAt(i);
                        this.dataRetrieval.addChildren(currentNode, 0, LEVEL_DEPTH_LOAD);
                        this.cacheNode(currentNode, 0);
                    }
                    ((Observable)this).dispatchEvent(Event.COMPLETE, (Object)this.rootNode);
                }
                this.JTree.setRootVisible(this.VISIBLE_ROOT_NODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        initialTreeData.start();            
    }

    /**
     * Attach the events
     */
    private void attachEvents() {
        this.JTree.addTreeWillExpandListener(this);
    }

    /**
     * On expand we trigger loading next nodes
     * @param   event
     * @throws  ExpandVetoException 
     */
    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)event.getPath().getLastPathComponent();
        String nodeId = this.dataRetrieval.getNodeValue(currentNode);
        boolean isCached = false;
        for (String cached:this.cachedNodes) {
            if (cached.equals(nodeId)) {
                isCached = true;
                break;
            }
        }
        if (isCached == false) {
            // decouple the processing in a Swing Worker
            Worker worker = new Worker(this, this.dataRetrieval);
            worker.setData(currentNode, LEVEL_DEPTH_LOAD);
            worker.execute();
            // we assume we can cache it, even if the process is not done
            this.cacheNode(currentNode, 0);
        }
    }

    /**
     * Empty implementation - just to be according TreeWillExpandListener interface
     * @param   tee
     * @throws  ExpandVetoException 
     */
    @Override
    public void treeWillCollapse(TreeExpansionEvent tee) throws ExpandVetoException {}

    /**
     * Cache each element so we do not retrieve it on expand
     * @param node 
     */
    private void cacheNode(DefaultMutableTreeNode node, int offset) {
        this.cachedNodes.add(this.dataRetrieval.getNodeValue(node));
        if (node.getChildCount() > 0 && this.LEVEL_DEPTH_LOAD > 2) {
            if (offset == this.LEVEL_DEPTH_LOAD - 2) {
                return;
            }
            for (int it = 0; it < node.getChildCount(); it++) {
                this.cacheNode((DefaultMutableTreeNode)node.getChildAt(it), offset+1);
            }
        }
    }

    /**
     * Update the entire frame after the TreeModel was modified in the swing worker
     */
    public void updateFrame() {
        SwingUtilities.updateComponentTreeUI(this.frame);
    }
}
