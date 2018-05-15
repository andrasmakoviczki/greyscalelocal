/**
 * Created by AMakoviczki on 2018. 05. 05..
 */
package hu.elte.bigdata.thesis;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;


public class GreyScaleLocal {

    public static void main(String[] args) {
        try {
            //Load the native library for OpenCV
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            //Input directory
            File inputDir = new File(args[0]);
            File[] listOfFiles = inputDir.listFiles();

            //Output directory
            File outputDir = new File(args[1]);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            ArrayList<String> nullImages = new ArrayList<String>();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    File input = file;

                    // Create input stream
                    ImageInputStream inputIO = ImageIO.createImageInputStream(file);
                    BufferedImage image = null;

                    try {
                        // Get the reader
                        Iterator<ImageReader> readers = ImageIO.getImageReaders(inputIO);

                        if (readers.hasNext()) {
                            ImageReader reader = readers.next();
                            try {
                                reader.setInput(inputIO);
                                ImageReadParam param = reader.getDefaultReadParam();
                                image = reader.read(0, param);
                            } finally {
                                // Dispose reader
                                reader.dispose();
                            }
                        }
                    } finally {
                        // Close stream
                        inputIO.close();
                    }

                    if (image != null) {
                        try {
                            //Get the image file
                            byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
                            Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
                            mat.put(0, 0, data);

                            //Init new greyscale image
                            Mat greyMat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC1);
                            Imgproc.cvtColor(mat, greyMat, Imgproc.COLOR_RGB2GRAY);

                            byte[] greyData = new byte[greyMat.rows() * greyMat.cols() * (int) (greyMat.elemSize())];
                            greyMat.get(0, 0, greyData);

                            //Copy the image into greyscale image
                            BufferedImage greyImage = new BufferedImage(greyMat.cols(), greyMat.rows(), BufferedImage.TYPE_BYTE_GRAY);
                            greyImage.getRaster().setDataElements(0, 0, greyMat.cols(), greyMat.rows(), greyData);


                            File ouptut = new File(outputDir + "/grayscale_" + file.getName());
                            ImageIO.write(greyImage, "jpg", ouptut);
                        } catch (UnsupportedOperationException e) {

                        } catch (NullPointerException e) {

                        }
                    } else {
                        nullImages.add(file.getName());
                    }
                }
            }

            //Check unsupported images
            for (String item : nullImages) {
                System.out.println(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
