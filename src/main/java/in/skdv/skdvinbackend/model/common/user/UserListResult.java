package in.skdv.skdvinbackend.model.common.user;

import in.skdv.skdvinbackend.model.dto.UserDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserListResult {

    private List<UserDTO> users = new ArrayList<>();
    private int start;
    private int total;
}
