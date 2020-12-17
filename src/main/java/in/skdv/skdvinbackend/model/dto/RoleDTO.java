package in.skdv.skdvinbackend.model.dto;

import java.util.Objects;

public class RoleDTO {

    private String id;
    private String name;

    public RoleDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleDTO roleDTO = (RoleDTO) o;
        return id.equals(roleDTO.id) &&
                name.equals(roleDTO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
