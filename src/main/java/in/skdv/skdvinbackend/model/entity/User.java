package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractUser;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Document
public class User extends AbstractUser {

    @Id
    private ObjectId objectId;

    @NotNull
    @NotEmpty
    private String password;

    private boolean enabled = false;
    private VerificationToken verificationToken;
    private VerificationToken passwordResetToken;

    public User() {}

    public User(String username, String password, String email, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public VerificationToken getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(VerificationToken verificationToken) {
        this.verificationToken = verificationToken;
    }

    public VerificationToken getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(VerificationToken passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }
}
