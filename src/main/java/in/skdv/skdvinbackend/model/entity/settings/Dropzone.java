package in.skdv.skdvinbackend.model.entity.settings;

public class Dropzone {

    private String name;
    private String email;
    private String priceListUrl;
    private String transportationAgreementUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPriceListUrl() {
        return priceListUrl;
    }

    public void setPriceListUrl(String priceListUrl) {
        this.priceListUrl = priceListUrl;
    }

    public String getTransportationAgreementUrl() {
        return transportationAgreementUrl;
    }

    public void setTransportationAgreementUrl(String transportationAgreementUrl) {
        this.transportationAgreementUrl = transportationAgreementUrl;
    }
}
