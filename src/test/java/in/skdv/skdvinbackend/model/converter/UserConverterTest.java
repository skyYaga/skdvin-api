package in.skdv.skdvinbackend.model.converter;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.dto.UserDTO;
import in.skdv.skdvinbackend.model.dto.UserDtoIncoming;
import in.skdv.skdvinbackend.model.entity.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserConverterTest {

    private UserConverter converter = new UserConverter();

    @Test
    public void convertToDto() {
        User user = ModelMockHelper.createUser();

        UserDTO userDTO = converter.convertToDto(user);

        assertEquals(user.getUsername(), userDTO.getUsername());
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(user.getRoles(), userDTO.getRoles());
    }

    @Test
    public void convertToEntity() {
        User user = ModelMockHelper.createUser();
        UserDtoIncoming userDtoIncoming = new UserDtoIncoming(user.getUsername(), user.getPassword(), user.getEmail(), user.getRoles());

        user = converter.convertToEntity(userDtoIncoming);

        assertEquals(userDtoIncoming.getUsername(), user.getUsername());
        assertEquals(userDtoIncoming.getEmail(), user.getEmail());
        assertEquals(userDtoIncoming.getRoles(), user.getRoles());
        assertEquals(userDtoIncoming.getPassword(), user.getPassword());

    }

    @Test
    public void convertToDto_Null() {
        User user = null;
        assertNull(converter.convertToDto(user));
    }


    @Test
    public void convertToEntity_Null() {
        assertNull(converter.convertToEntity(null));
    }
}