package utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import window.Window;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

public class Screenshot {

	public static void screenShot(){
		int width = (int) Window.INSTANCE.getWidth();
		int height = (int) Window.INSTANCE.getHeight();

		ByteBuffer fb = BufferUtils.createByteBuffer(width * height * 4);
		GL46.glReadPixels(0, 0, width, height, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, fb);

		BufferedImage imageIn = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
		for (int x = imageIn.getWidth() - 1; x >= 0; x--) {
			for (int y = imageIn.getHeight() - 1; y >= 0; y--) {
				int i = (x + imageIn.getWidth() * y) * 4;
				imageIn.setRGB(x, imageIn.getHeight() - 1 - y, (((fb.get(i) & 0xFF) & 0x0ff) << 16) | (((fb.get(i + 1) & 0xFF) & 0x0ff) << 8) | ((fb.get(i + 2) & 0xFF) & 0x0ff));
			}
		}

		try {//Try to screate image, else show exception.
			String timestamp = TimeUtils.getTime() + "";
			File f = new File("./screenshots/" + timestamp + ".png");
			if(!f.getParentFile().exists()) f.getParentFile().mkdir();
			ImageIO.write(imageIn, "png" , f);

			TransferableImage trans = new TransferableImage(imageIn);
			Toolkit.getDefaultToolkit()
					.getSystemClipboard()
					.setContents(trans, null);
		}
		catch (Exception e) {
			System.out.println("ScreenShot() exception: " + e);
		}
	}

	private record TransferableImage(BufferedImage image) implements Transferable {

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[]{DataFlavor.imageFlavor};
		}

		@Override
		public boolean isDataFlavorSupported(final DataFlavor flavor) {
			return DataFlavor.imageFlavor.equals(flavor);
		}

		@Override
		public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
			if (isDataFlavorSupported(flavor)) {
				return image;
			}

			throw new UnsupportedFlavorException(flavor);
		}
	}
}
