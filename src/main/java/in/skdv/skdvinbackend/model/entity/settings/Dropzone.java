package in.skdv.skdvinbackend.model.entity.settings;

public class Dropzone {

    private String name;
    private String email;
    private String priceListUrl;

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
}
