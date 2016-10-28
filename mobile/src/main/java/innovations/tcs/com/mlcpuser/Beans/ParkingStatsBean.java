package innovations.tcs.com.mlcpuser.Beans;

public class ParkingStatsBean {
    String parkingStatsID, parkingStatsName;

    public ParkingStatsBean() {
    }

    public ParkingStatsBean(String parkingStatsID, String parkingInfoName) {
        this.parkingStatsID = parkingStatsID;
        this.parkingStatsName = parkingInfoName;
    }

    public String getParkingStatsID() {
        return parkingStatsID;
    }

    public String getParkingStatsName() {
        return parkingStatsName;
    }

    public void setParkingStatsID(String parkingStatsID) {
        this.parkingStatsID = parkingStatsID;
    }

    public void setParkingStatsName(String parkingStatsName) {
        this.parkingStatsName = parkingStatsName;
    }
}
