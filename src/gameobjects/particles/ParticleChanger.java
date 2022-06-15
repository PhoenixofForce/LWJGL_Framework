package gameobjects.particles;

public interface ParticleChanger<ParticleAtrribute> {

	ParticleAtrribute calculateAttribute(ParticleAtrribute startValue, ParticleAtrribute currentValue, float timeLeftToLife, float lifetime, float dts);

}
