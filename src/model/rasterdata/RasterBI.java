package model.rasterdata;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RasterBI implements Raster {

    private final BufferedImage img;
    private final Color backgroundColor = new Color(0x16161D);

    public RasterBI(int width, int height) {
        this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void clear() {
        Graphics g = img.getGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
        g.dispose();
    }

    @Override
    public void repaint(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

    @Override
    public int getWidth() {
        // TODO
        return img.getWidth();
    }

    @Override
    public int getHeight() {
        // TODO
        return img.getHeight();
    }

    @Override
    public int getPixel(int x, int y) {
        // TODO
        if (x < 0 || y < 0 || x >= img.getWidth() || y >= img.getHeight()) {
            return 0; //pixel mimo plátno! vrací černou
        }
        return img.getRGB(x, y);
    }

    @Override
    public void setPixel(int x, int y, int color) {
        // TODO
        if (x >= 0 && y >= 0 && x < img.getWidth() && y < img.getHeight()) {
            img.setRGB(x, y, color); //pixel je na plátnu, jinak nedělá nic
        }
    }

    public BufferedImage getImg() {
        return img;
    }
}
