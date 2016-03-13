/*
 *  Event Class
 *  int eventNum decides what event takes place
 *      0 = toggle user on/off
 *      1 = user stores data (sends it)
 *      2 = user retrives data
 */
public class Events
{
    private int eventNumber;
    private int numOfUsers;
    
    // Constructor that gets the number of users from the main class
    // Randomly generates a event to occur whenever made
    public Events(int users){
       eventNumber = (int)(Math.random() * 3);
       numOfUsers = users;
    }
    
    // Constructor that gets the number of users from the main class
    // Gets the event that the main class wants to run
    // -----USED FOR DEBUGGING-----
    public Events(int users, int event){
       eventNumber = event;
       numOfUsers = users;
    }
    
    // Returns an int array to represent the event created
    // Arrays set up as [user doing event, which event is being done]
    public int[] getEvent(){
        int activeUser = (int)(Math.random() * numOfUsers);
        int[] result = new int[2];
        result[0] = activeUser;
        result[1] = eventNumber;
        
        return result;
    }
    
    // Returns an int array to represent the event created
    // Arrays set up as [user doing event, which event is being done]
    // Given the user the event is affecting
    // -----USED FOR DEBUGGING-----
    public int[] getEvent(int user){
        int activeUser = user;
        int[] result = new int[2];
        result[0] = activeUser;
        result[1] = eventNumber;
        
        return result;
    }
}
