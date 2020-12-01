import java.util.ArrayList;
import java.util.List;

public class PlayerSubject {

    private static PlayerSubject instance;
    private List<LiLyPlayER> observers;

    /** Private default constructor for singleton pattern */
    private PlayerSubject() {
        observers = new ArrayList<>();
    }

    /**
     * This method is to get the singleton instance for this class.
     * 
     * @return singleton instance for PlayerSubject
     */
    public static PlayerSubject getInstance() {
        if (instance == null) {
            synchronized (Repository.class) {
                if (instance == null) {
                    instance = new PlayerSubject();
                }
            }
        }
        return instance;
    }

    /**
     * Add a new observer to the list.
     */
    public void register(LiLyPlayER player) {
        observers.add(player);
    }

    /**
     * Notify all observers.
     */
    public void update() {
        observers.forEach(observer -> observer.refreshTable());
    }
    
}
