package top.codemc.common.util.singleton;

public class BookList {

    private String number;
    private String libName;
    private String libRenewDate;
    private String barcode;
    private boolean isRenew;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLibName() {
        return libName;
    }

    public void setLibName(String libName) {
        this.libName = libName;
    }

    public String getLibRenewDate() {
        return libRenewDate;
    }

    public void setLibRenewDate(String libRenewDate) {
        this.libRenewDate = libRenewDate;
    }

    public boolean isRenew() {
        return isRenew;
    }

    public void setRenew(boolean isRenew) {
        this.isRenew = isRenew;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }


}
