public class LoadingObserver implements JTreeHelper.Observer {
    /**
     * JLabel
     */
    private javax.swing.JLabel label;

    @Override
    /**
     * An example of implementing the JTreeHeper.Observer
     */
    public void catchEvent(JTreeHelper.Event event) {
        switch (event.getType()) {
            case(JTreeHelper.Event.START):
                System.out.print("Loading node " + event.getTarget().toString() + "\n");
                this.label.setText("Loading.... ");
                System.out.print(this.label.getText() + "\n");
                break;
            case(JTreeHelper.Event.COMPLETE):
                System.out.print("Finished loading node " + event.getTarget().toString() + "\n");
                this.label.setText("loaded ");
                System.out.print(this.label.getText() + "\n");
                break;
        }
    }

    /**
     * The item to be modified upon event notification
     * @param   label
     */
    public void setLoader(javax.swing.JLabel label) {
        this.label = label;
    }
}
