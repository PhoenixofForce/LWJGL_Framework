package rendering;

import org.lwjgl.opengl.GL46;
import utils.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL46.*;

public class ShaderHandler {

	private static Map<ShaderType, Integer> shaderMap;
	public enum ShaderType {
		DEFAULT("default_vertexShader", "default_fragmentShader"),
		PARTICLE("particle_vertexShader", "particle_fragmentShader"),
		;

		private final String vertexFileName, fragmentFileName;
		ShaderType(String vertexFileName, String fragmentFileName) {
			this.vertexFileName = vertexFileName;
			this.fragmentFileName = fragmentFileName;
		}

		public String getVertexFileName() {
			return vertexFileName;
		}

		public String getFragmentFileName() {
			return fragmentFileName;
		}

		public int get() {
			return getShader(this);
		}
	}

	public static void initShader() {
		shaderMap = new HashMap<>();
		int[] res = new int[1];

		for(ShaderType st: ShaderType.values()) {
			try {
				int shader = glCreateProgram();

				String vertexShaderCode = getShaderCode(String.format(Constants.SHADER_PATH + "%s.glsl", st.getVertexFileName()), true);
				String fragmentShaderCode = getShaderCode(String.format(Constants.SHADER_PATH + "%s.glsl", st.getFragmentFileName()), true);

				createShader(st, shader, GL_VERTEX_SHADER, vertexShaderCode, res);
				createShader(st, shader, GL_FRAGMENT_SHADER, fragmentShaderCode, res);

				glLinkProgram(shader);
				shaderMap.put(st, shader);
			} catch (IOException e) {
				System.err.println("Error while reading files of shaders:\r\n" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private static String getShaderCode(String filePath, boolean isFirstlevel) throws IOException {
		String wholeCode  = Files.readString(Paths.get(filePath));

		if(isFirstlevel) {
			String[] lines = wholeCode.split("\r\n");

			for(int i = 0; i < lines.length; i++) {
				String s = lines[i];
				if(s.trim().startsWith("//import ")) {
					String fileName = s.trim().split(" ")[1];
					lines[i] = getShaderCode(String.format(Constants.SHADER_PATH + "utils/%s.glsl", fileName), false);
				}
			}
			wholeCode = String.join("\r\n", lines);
		}

		return wholeCode;
	}

	public static int getShader(ShaderType st) {
		return shaderMap.get(st);
	}

	private static int createShader(ShaderType st, int shader, int type, String shaderCode, int[] res) {
		int out = glCreateShader(type);
		glShaderSource(out, shaderCode);
		glCompileShader(out);
		glAttachShader(shader, out);

		glGetShaderiv(out, GL_COMPILE_STATUS, res);
		if (res[0] == 0) {
			System.err.println("Compile error at " + st.toString().toLowerCase() + " " + (type == GL_VERTEX_SHADER ? "vertex" : (type == GL_GEOMETRY_SHADER ? "geometry" : "fragment")) + " shader: " + glGetShaderInfoLog(out));
		}

		return out;
	}

	public static void cleanup() {
		shaderMap.values().forEach(GL46::glDeleteProgram);
	}
}