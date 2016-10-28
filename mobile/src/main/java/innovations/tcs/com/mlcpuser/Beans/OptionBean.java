package innovations.tcs.com.mlcpuser.Beans;

public class OptionBean {
    String optionID, optionName;

    public OptionBean() {
    }

    public OptionBean(String optionID, String optionName) {
        this.optionID = optionID;
        this.optionName = optionName;
    }

    public String getOptionID() {
        return optionID;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionID(String optionID) {
        this.optionID = optionID;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
}
