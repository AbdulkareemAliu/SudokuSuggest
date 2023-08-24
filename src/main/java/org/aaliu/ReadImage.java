package org.aaliu;

import net.sourceforge.tess4j.*;
import net.sourceforge.tess4j.util.LoadLibs;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;


public class ReadImage {

    public static BufferedImage getBlackWhiteImage(BufferedImage image) {
        // Create a new black and white image with the same dimensions as the original image.
        BufferedImage blackAndWhiteImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

        // Iterate over the pixels of the original image and set the corresponding pixel in the new image to black or white.
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                int brightness = (rgb >> 16) & 0xFF;
                if (brightness > 127) {
                    blackAndWhiteImage.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    blackAndWhiteImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        return blackAndWhiteImage;
    }

    public static BufferedImage getCroppedImage(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();

        int topLeftX = 0; int topLeftY = 0;
        while (image.getRGB(topLeftX, topLeftY) != Color.BLACK.getRGB()){
            if (topLeftX < width - 1){
                topLeftX ++;
            } else {
                topLeftX = 0;
                topLeftY ++;
            }
        }

        int bottomRightX = width - 1; int bottomRightY = height - 1;
        while (image.getRGB(bottomRightX, bottomRightY) != Color.BLACK.getRGB()){
            if (bottomRightX > 0){
                bottomRightX --;
            } else {
                bottomRightX = width - 1;
                bottomRightY --;
            }
        }

        return image.getSubimage(topLeftX, topLeftY, bottomRightX - topLeftX, bottomRightY - topLeftY);

    }


    
    public static void main(String[] args) {
        try{
            BufferedImage sudokuImage = ImageIO.read(new File("src/main/java/org/aaliu/testboard.png"));

            BufferedImage blackWhiteImage = getBlackWhiteImage(sudokuImage);

            BufferedImage croppedImage = getCroppedImage(blackWhiteImage);

            int width = sudokuImage.getWidth();
            int height = sudokuImage.getHeight();

            int widthIncrement = width / 9;
            int heightIncrement = height / 9;

            

            // BufferedImage singleGrid = croppedImage.getSubimage(0, 0, widthIncrement, heightIncrement);

            // Specifics of OCR implementation are yet to be implemented

        } catch (IOException e) {
            e.printStackTrace();
        }
        

    }
}
