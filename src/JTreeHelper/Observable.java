package JTreeHelper;

import java.util.ArrayList;

/**
 * Extend the Service capabilities by making it Observable
 * for clients. The state is limited to DataRetrievers process status
 * @author Adrian Tilita
 * @email <adrian@tilita.ro>
 */
abstract public class Observable {
    /**
     * Event holder
     */
    final private Event eventPrototype = new Event();

    /**
     * Attached observers
     */
    final private ArrayList<Observer> observers = new ArrayList<Observer>();

    /**
     * Inject observer
     * @param   observer
     * @return  Observable
     */
    public Observable addObserver(Observer observer) {
        // to avoid duplicates we remove it first if it is allready added
        this.removeObserver(observer);
        this.observers.add(observer);
        return this;
    }

    /**
     * Remove an observer
     * @param   observer 
     * @return  Observable
     */
    public Observable removeObserver(Observer observer) {
        if (this.observers.isEmpty() == true) {
            return this;
        }
        int observerCount = this.observers.size();
        for (int i = 0; i < observerCount; i++) {
            if (this.observers.get(i) == observer) {
                this.observers.remove(i);
                break;
            }
        }
        return this;
    }

    /**
     * Notify all injected observers about the current state change
     */
    private void notify(Event event) {
        // stop notify if no injected observers present
        if (this.observers.isEmpty() == true) {
            return;
        }
        this.observers.stream().forEach((observer) -> {
            observer.catchEvent(event);
        });
    }

    /**
     * Builds a new event to be dispatched
     * @param   type
     * @param   target
     */
    protected void dispatchEvent(String type, Object target) {
        Event event = eventPrototype.clone();
        event.setEventData(type, target);
        this.notify(event);
    }
}
