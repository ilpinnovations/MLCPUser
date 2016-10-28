package innovations.tcs.com.mlcpuser.Beans;

public class MySlotBean {
    String mySlotID, mySlotName;

    public MySlotBean() {
    }

    public MySlotBean(String mySlotID, String mySlotName) {
        this.mySlotID = mySlotID;
        this.mySlotName = mySlotName;
    }

    public String getMySlotID() {
        return mySlotID;
    }

    public String getMySlotName() {
        return mySlotName;
    }

    public void setMySlotID(String mySlotID) {
        this.mySlotID = mySlotID;
    }

    public void setMySlotName(String mySlotName) {
        this.mySlotName = mySlotName;
    }
}
