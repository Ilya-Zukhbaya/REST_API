package entities;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BaseContext {
    private Map<String, String> authTokens  = new HashMap<>();
    private Map<String, String> authMap = new HashMap<>();
    private Map<String, String> userMap = new HashMap<>();
}
