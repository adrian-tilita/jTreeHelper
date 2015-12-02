package JTreeHelper;

/**
 * A Prototype event object
 * @author Adrian Tilita
 * @email <adrian@tilita.ro>
 */
public class Event {
    /**
     * Event type START
     */
    public static final String START = "STARTED";

    /**
     * Event type COMPLETE
     */
    public static final String COMPLETE = "COMPLETED";

    /**
     * Event type
     */
    private String type = null;

    /**
     * Object that triggered the event/the object affected
     */
    private Object target = null;

    /**
     * The time when the event occurred
     */
    private long time = 0; 

    /**
     * Set the event data
     * @param   type
     * @param   target
     * @return  Event
     */
    public Event setEventData(String type, Object target) {
        this.type = type;
        this.target = target;
        this.time = System.nanoTime();
        return this;
    }

    /**
     * Clone the Event
     * @return  Event
     */
    @Override
    public Event clone() {
        Object clone = null;
        try {
            clone = super.clone();
            ((Event)clone).reset();
        } catch (Exception e) {
            // if in some situatia the clone is not supported,
            // we instantiate a new event object
        } finally {
            clone = (Object)new Event();
        }
        return (Event)clone;
    }

    /**
     * Reset the event details
     */
    public void reset() {
        this.type = null;
        this.target = null;
        this.time = System.nanoTime();
    }

    /**
     * Return the Event Type
     * @return  String
     */
    public String getType() {
        return this.type;
    }

    /**
     * Return the event Target
     * @return  Object
     */
    public Object getTarget() {
        return this.target;
    }

    /**
     * Return the time when the event occurred in nanoTime
     * @return  long
     */
    public long getTime() {
        return this.time;
    }
}
