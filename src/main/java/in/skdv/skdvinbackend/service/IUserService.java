package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.dto.UserDTO;

import java.util.List;

public interface IUserService {
    List<UserDTO> getUsers();
}
