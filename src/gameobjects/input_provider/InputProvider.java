package gameobjects.input_provider;

import gameobjects.input_provider.middleware.InputMiddleware;

import java.util.List;

public class InputProvider {

	private List<InputMiddleware> middlewares;

	public InputProvider(InputMiddleware... middlewares) {
		this.middlewares = List.of(middlewares);
	}

	public InputState getInputState() {
		InputState out = new InputState();
		middlewares.forEach(e -> e.change(out));
		return out;
	}
}
