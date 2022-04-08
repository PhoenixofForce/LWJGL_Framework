package gameobjects.component_system.components.rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import gameobjects.Entity;
import gameobjects.component_system.components.RenderingComponent;
import assets.ObjHandler;
import rendering.Renderer;
import rendering.ShaderHandler;
import rendering.MassUniform;

public class ModelRenderComponent extends RenderingComponent {

	private MassUniform uniform;
	private static final Matrix4f transformation = new Matrix4f(
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1);
	
	private String model;
	
	public ModelRenderComponent(Entity e, String model) {
		super(e);
		this.model = model;

		uniform = new MassUniform();
	}

	@Override
	public void render(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
		uniform.setMatrices(projectionMatrix, viewMatrix, pc != null? pc.transformationMatrix(): transformation);
		uniform.setVector3fs(new Vector3f(1, 0, 1));
		Renderer.render(ShaderHandler.ShaderType.DEFAULT, ObjHandler.loadOBJ(model), uniform);
	}

}
