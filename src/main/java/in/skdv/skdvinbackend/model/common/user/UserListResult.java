package in.skdv.skdvinbackend.model.common.user;

import in.skdv.skdvinbackend.model.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserListResult {

    private List<UserDTO> users = new ArrayList<>();
    private int start;
    private int total;
}
