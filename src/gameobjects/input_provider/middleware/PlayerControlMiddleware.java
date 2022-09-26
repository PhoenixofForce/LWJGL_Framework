package gameobjects.input_provider.middleware;

import gameobjects.input_provider.InputState;
import utils.ControllableAction;

public class PlayerControlMiddleware implements InputMiddleware {

    @Override
    public void change(InputState state) {
        for(ControllableAction a: ControllableAction.values()) {
            state.setValue(a, a.anyPressed());
        }
    }
}
