
public class App {

    private static final Repository repository = Repository.getInstance();
    public static void main(String[] args) throws Exception {
        System.out.println(repository.getAllPersonNames());
        
        // Test branch stuff
        
        LiLyPlayER player = new LiLyPlayER();
		player.setVisible(true);
		
    }
}
