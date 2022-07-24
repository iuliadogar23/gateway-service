package lucrare.dizertatie.gateway.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ContResponse implements Serializable {
    private long id;
    private String login;
    private String token;
}
