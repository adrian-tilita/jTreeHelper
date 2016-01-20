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
                this.label.setText("Loading.... ");
                break;
            case(JTreeHelper.Event.COMPLETE):
                this.label.setText("loaded ");
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
