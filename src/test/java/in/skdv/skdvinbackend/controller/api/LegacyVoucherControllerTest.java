package in.skdv.skdvinbackend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.repository.LegacyVoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static in.skdv.skdvinbackend.config.Authorities.READ_VOUCHERS;
import static in.skdv.skdvinbackend.config.Authorities.UPDATE_VOUCHERS;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LegacyVoucherControllerTest extends AbstractSkdvinTest {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LegacyVoucherRepository legacyVoucherRepository;

    @BeforeEach
    void setup() {
        legacyVoucherRepository.deleteAll();
        legacyVoucherRepository.save(ModelMockHelper.createVoucherDocument());
    }

    @Test
    void testGetVoucher() throws Exception {
        mockMvc.perform(get("/api/legacy-voucher/10000")
                .header("Authorization", MockJwtDecoder.addHeader(READ_VOUCHERS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.id", is("10000")));
    }

    @Test
    void testGetVoucher_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/legacy-voucher/10000")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRedeemVoucher() throws Exception {
        mockMvc.perform(patch("/api/legacy-voucher/10000/redeem")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_VOUCHERS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.id", is("10000")))
                .andExpect(jsonPath("$.payload.redeemed", is(true)));
    }

    @Test
    void testRedeemVoucher_Unauthorized() throws Exception {
        mockMvc.perform(patch("/api/legacy-voucher/10000/redeem")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    private String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }
}
