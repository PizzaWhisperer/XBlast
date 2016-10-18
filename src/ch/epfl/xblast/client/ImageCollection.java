package ch.epfl.xblast.client;

import java.awt.Image;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;

/**
 * <Code> ImageCollection </Code> is a collection of images
 * 
 * @author Mathilde Raynal (259176)
 * @author Richard Roubaty (260549)
 */
public final class ImageCollection {

	private File imagesRepository;

	private Map<Integer, Image> imagesMap = new HashMap<>();

	/**
	 * @param directory
	 * @throw throws Error if <Code> URISyntaxException </Code> 
	 */
	public ImageCollection(String directory){
		try {
			this.imagesRepository = new File(ImageCollection.class.getClassLoader().getResource(directory).toURI());
	
		for (File f : imagesRepository.listFiles()) {
			String name = f.getName();
			// 0 and 3 are the beginning and ending indexes of the files
			// numbers.
			int numberOfImage = Integer.parseInt(name.substring(0, 3));
			try {
				imagesMap.put(numberOfImage, ImageIO.read(f));
			} catch (Exception e) {
				// do nothing : we ignore exception here, we continue reading.
			}
		}
		}catch (URISyntaxException e){
			throw new Error();
		}

	}

	/**
	 * @param index
	 * @return the image corresponding to that index in the current image
	 *         collection
	 * @throws <Code> NoSuchElementException </Code> if there is not any image
	 *             corresponding to the index.
	 */
	public Image image(int index) {
		Image im = imagesMap.get(index);
		if (im.equals(null))
			throw new NoSuchElementException("No such image could be found");
		else
			return im;
	}

	/**
	 * @param index
	 * @return the image corresponding to the index, or null.
	 */
	public Image imageOrNull(int index) {
		return imagesMap.get(index);
	}
}