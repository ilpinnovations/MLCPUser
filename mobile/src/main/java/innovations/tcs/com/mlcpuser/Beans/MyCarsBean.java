package innovations.tcs.com.mlcpuser.Beans;

public class MyCarsBean {
    String myCarsID, myCarsName;

    public MyCarsBean() {
    }

    public MyCarsBean(String parkingStatsID, String parkingInfoName) {
        this.myCarsID = parkingStatsID;
        this.myCarsName = parkingInfoName;
    }

    public String getMyCarsID() {
        return myCarsID;
    }

    public String getMyCarsName() {
        return myCarsName;
    }

    public void setMyCarsID(String myCarsID) {
        this.myCarsID = myCarsID;
    }

    public void setMyCarsName(String myCarsName) {
        this.myCarsName = myCarsName;
    }
}
