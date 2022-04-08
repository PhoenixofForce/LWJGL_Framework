package assets;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Optional;

class WaveAudio {
	private int format;
	private int sampleRate;
	private int totalBytes;
	private ByteBuffer dataBuffer;

	private AudioInputStream stream;
	private byte[] dataArray;

	int buffer;

	private WaveAudio(AudioInputStream stream) {
		this.stream = stream;

		AudioFormat audioFormat = stream.getFormat();
		format = getOpenAlFormat(audioFormat.getChannels(), audioFormat.getSampleSizeInBits());
		this.sampleRate = (int) audioFormat.getSampleRate();
		int bytesPerFrame = audioFormat.getFrameSize();
		this.totalBytes = (int) (stream.getFrameLength() * bytesPerFrame);
		this.dataBuffer = BufferUtils.createByteBuffer(totalBytes);
		this.dataArray = new byte[totalBytes];

		loadData();

		buffer = AL10.alGenBuffers();
		AL10.alBufferData(buffer, this.getFormat(), this.getData(), this.getSampleRate());
		this.dispose();
	}

	public static Optional<WaveAudio> create(String file) {
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException ignored) { }

		if (stream == null) {
			System.err.println("Error while loading audio: Stream is null, File not found: " + file);
			return Optional.empty();
		}

		InputStream bufferedInput = new BufferedInputStream(stream);
		AudioInputStream audioStream = null;
		try {
			audioStream = AudioSystem.getAudioInputStream(bufferedInput);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.of(new WaveAudio(audioStream));
	}

	private static int getOpenAlFormat(int channels, int bitsPerSample) {
		if (channels == 1) {
			return bitsPerSample == 8 ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_MONO16;
		} else {
			return bitsPerSample == 8 ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_STEREO16;
		}
	}

	protected void dispose() {
		try {
			stream.close();
			dataBuffer.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadData() {
		try {
			int bytesRead = stream.read(dataArray, 0, totalBytes);
			dataBuffer.clear();
			dataBuffer.put(dataArray, 0, bytesRead);
			dataBuffer.flip();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ByteBuffer getData() {
		return dataBuffer;
	}

	public int getFormat() {
		return format;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public int getBuffer() {
		return buffer;
	}

}