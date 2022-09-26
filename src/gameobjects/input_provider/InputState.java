package gameobjects.input_provider;

import java.util.HashMap;
import java.util.Map;

public class InputState {

    private final Map<String, Float> states;
    public InputState() {
        this.states = new HashMap<>();
    }

    public void setValue(String name, float state) {
        states.put(name, state);
    }

    public void setValue(Object name, float state) {
        states.put(name.toString(), state);
    }

    public float getValue(String name) {
        return states.getOrDefault(name, 0.0f);
    }

    public float getValue(Object name) {
        return states.getOrDefault(name.toString(), 0.0f);
    }
}
