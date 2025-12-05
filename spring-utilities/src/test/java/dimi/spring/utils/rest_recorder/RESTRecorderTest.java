package dimi.spring.utils.rest_recorder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RESTRecorderTest
{
    @Autowired MockMvc mockMvc;
    @Autowired RESTCallRecorder recorder;


    @BeforeEach
    void beforeEach()
    {
        recorder.clear();
        assertThat(recorder.getAll()).isEmpty();
    }


    @Test
    void recorderCapturesGetRequest() throws Exception
    {
        mockMvc.perform(get("/api/ping"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("pong"));
        List<RESTCall> last = recorder.getLast(5);
        assertThat(last).isNotEmpty();
        RESTCall call = last.get(0);
        assertThat(call.method()).isEqualTo("GET");
        assertThat(call.path()).isEqualTo("/api/ping");
        assertThat(call.responseStatus()).isEqualTo(200);
        // ping has no request body, so requestBody is null or empty
        assertThat(call.requestBody()).satisfiesAnyOf(
                        s -> assertThat(s).isNull(),
                        s -> assertThat(s).isEmpty()
        );
        assertThat(call.durationMillis()).isGreaterThanOrEqualTo(0);
    }


    @Test
    void recorderCapturesPostRequestWithBodyAndStatus() throws Exception
    {
        String payload = "{\"name\":\"alice\"}";
        mockMvc.perform(post("/api/echo")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(payload))
                        .andExpect(status().isOk())
                        .andExpect(content().string("echo:" + payload));
        // also test a non-200 status endpoint
        mockMvc.perform(get("/api/status/418"))
                        .andExpect(status().isIAmATeapot());
        List<RESTCall> last3 = recorder.getLast(10);
        assertThat(last3).hasSizeGreaterThanOrEqualTo(2);
        // the most recent should be the status/418 call
        RESTCall mostRecent = last3.get(0);
        assertThat(mostRecent.path()).isEqualTo("/api/status/418");
        assertThat(mostRecent.responseStatus()).isEqualTo(418);
        // second-most recent is the POST /api/echo
        RESTCall postCall = last3.get(1);
        assertThat(postCall.method()).isEqualTo("POST");
        assertThat(postCall.path()).isEqualTo("/api/echo");
        assertThat(postCall.requestBody()).contains("\"name\":\"alice\"");
        assertThat(postCall.responseBody()).contains("echo:" + payload);
    }
}
