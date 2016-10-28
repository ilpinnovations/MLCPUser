package innovations.tcs.com.mlcpuser.Beans;

public class ParkingInfoBean {
    String parkingInfoID, parkingInfoName;

    public ParkingInfoBean() {
    }

    public ParkingInfoBean(String parkingInfoID, String parkingInfoName) {
        this.parkingInfoID = parkingInfoID;
        this.parkingInfoName = parkingInfoName;
    }

    public String getParkingInfoID() {
        return parkingInfoID;
    }

    public String getParkingInfoName() {
        return parkingInfoName;
    }

    public void setParkingInfoID(String parkingInfoID) {
        this.parkingInfoID = parkingInfoID;
    }

    public void setParkingInfoName(String parkingInfoName) {
        this.parkingInfoName = parkingInfoName;
    }
}
