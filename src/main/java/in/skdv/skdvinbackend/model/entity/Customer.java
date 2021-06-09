package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractCustomer;
import in.skdv.skdvinbackend.util.ValidEmail;

import javax.validation.constraints.NotNull;
import java.util.List;

public class Customer extends AbstractCustomer {
    @NotNull
    @ValidEmail
    private String email;
    @NotNull
    private List<Jumper> jumpers;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Jumper> getJumpers() {
        return jumpers;
    }

    public void setJumpers(List<Jumper> jumpers) {
        this.jumpers = jumpers;
    }

}
