package gameobjects.input_provider.middleware;

import gameobjects.input_provider.InputState;

public interface InputMiddleware {

    void change(InputState state);

}
