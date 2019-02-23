package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.model.dto.UserDTO;
import in.skdv.skdvinbackend.model.dto.UserDtoIncoming;
import in.skdv.skdvinbackend.model.entity.User;
import org.modelmapper.ModelMapper;

public class UserConverter {

    private ModelMapper modelMapper = new ModelMapper();

    public UserDTO convertToDto(User user) {
        if (user == null) {
            return null;
        }
        return modelMapper.map(user, UserDTO.class);
    }

    public User convertToEntity(UserDtoIncoming userDto) {
        if (userDto == null) {
            return null;
        }
        return modelMapper.map(userDto, User.class);
    }
}
