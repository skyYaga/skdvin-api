package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.util.ValidPassword;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PasswordDto {

    @NotNull
    @NotEmpty
    @ValidPassword
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
