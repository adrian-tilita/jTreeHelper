package JTreeHelper;

import javax.swing.SwingWorker;
import JTreeHelper.DataRetrieval.DataRetrieval;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Swing worker for data-retrieval process
 * @author Adrian Tilita
 * @email <adrian@tilita.ro>
 * @todo  Better organize setters and dependencies
 */
public class Worker extends SwingWorker<Integer,Integer> {
    /**
     * The Observer that should notify
     */
    private Observable parent = null;

    /**
     * DataRetrieval to be called
     */
    private DataRetrieval dataRetrieval = null;

    /**
     * The node to be processed
     */
    private DefaultMutableTreeNode node = null;

    /**
     * Level parameter to be used in dataRetrieval call
     */
    private int level = 1;

    /**
     * Constructor
     * @param parent
     * @param dataRetrieval 
     */
    public Worker(Observable parent, DataRetrieval dataRetrieval) {
        this.parent = parent;
        this.dataRetrieval = dataRetrieval;
    }

    /**
     * Set data for dataretrival call
     * @param currentNode
     * @param level 
     */
    public void setData(DefaultMutableTreeNode currentNode, int level) {
        this.node = currentNode;
        this.level = level;
    }

    @Override
    public Integer doInBackground() {
        this.parent.dispatchEvent(Event.START, this.node);
        this.dataRetrieval.addChildren(this.node, 0, this.level);
        return 1;
    }

    @Override
    protected void done() {
        this.parent.dispatchEvent(Event.COMPLETE, this.node);
        ((Service)this.parent).updateFrame();
    }
}