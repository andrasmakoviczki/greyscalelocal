/**
 * Created by AMakoviczki on 2018. 05. 05..
 */

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;


public class GreyScale {

    public static void main(String[] args) {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("JPEG");
            while (readers.hasNext()) {
                System.out.println("reader: " + readers.next());
            }

            File folder = new File(args[0]);
            File[] listOfFiles = folder.listFiles();
            File dir = new File(args[1]);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            ArrayList<String> nullImages = new ArrayList<String>();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    File input = file;

                    // Create input stream
                    ImageInputStream inputIO = ImageIO.createImageInputStream(file);

                    try {
                        // Get the reader
                        Iterator<ImageReader> readers = ImageIO.getImageReaders(inputIO);

                        if (!readers.hasNext()) {
                            throw new IllegalArgumentException("No reader for: " + file);
                        }

                        ImageReader reader = readers.next();



                        try {
                            reader.setInput(inputIO);


                            ImageReadParam param = reader.getDefaultReadParam();


                            // Finally read the image, using settings from param
                            BufferedImage image = reader.read(0, param);

                            // Optionally, read thumbnails, meta data, etc...
                            int numThumbs = reader.getNumThumbnails(0);
                            // ...
                        }
                        finally {
                            // Dispose reader in finally block to avoid memory leaks
                            reader.dispose();
                        }
                    }
                    finally {
                        // Close stream in finally block to avoid resource leaks
                        inputIO.close();
                    }

                    BufferedImage image = ImageIO.read(input);

                    System.out.println(image.getClass().getName());

                    if (input == null) {
                        System.out.println("input null");
                    }

                    if (image != null) {
                        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
                        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
                        mat.put(0, 0, data);

                        Mat mat1 = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC1);
                        Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_RGB2GRAY);

                        byte[] data1 = new byte[mat1.rows() * mat1.cols() * (int) (mat1.elemSize())];
                        mat1.get(0, 0, data1);
                        BufferedImage image1 = new BufferedImage(mat1.cols(), mat1.rows(), BufferedImage.TYPE_BYTE_GRAY);
                        image1.getRaster().setDataElements(0, 0, mat1.cols(), mat1.rows(), data1);


                        File ouptut = new File(dir + "/grayscale_" + file.getName());
                        ImageIO.write(image1, "jpg", ouptut);
                    } else {
                        nullImages.add(file.getName());
                    }
                }
            }

            for (String item : nullImages
                    ) {
                System.out.println(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
