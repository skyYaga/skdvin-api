package in.skdv.skdvinbackend.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SlotQuery {

    private int tandem;
    private int picOrVid;
    private int picAndVid;
    private int handcam;

    public boolean isValid() {
        return tandem >= picOrVid + picAndVid + handcam;
    }

}
