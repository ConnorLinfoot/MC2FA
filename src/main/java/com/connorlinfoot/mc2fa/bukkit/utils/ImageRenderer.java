package com.connorlinfoot.mc2fa.bukkit.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;

public class ImageRenderer extends MapRenderer {
    private SoftReference<BufferedImage> cacheImage;
    private boolean hasRendered = false;

    public ImageRenderer(String url) throws IOException {
        this.cacheImage = new SoftReference<>(this.getImage(url));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        if (this.hasRendered) {
            return;
        }

        if (this.cacheImage.get() != null) {
            canvas.drawImage(0, 0, this.cacheImage.get());
            this.hasRendered = true;
        } else {
            player.sendMessage(ChatColor.RED + "Attempted to render the image, but the cached image was null!");
            this.hasRendered = true;
        }
    }

    private BufferedImage getImage(String url) throws IOException {
        boolean useCache = ImageIO.getUseCache();
        ImageIO.setUseCache(false);
        BufferedImage image = resize(new URL(url), new Dimension(128, 128));
        ImageIO.setUseCache(useCache);
        return image;
    }

    private BufferedImage resize(final URL url, final Dimension size) throws IOException {
        final BufferedImage image = ImageIO.read(url);
        final BufferedImage resized = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = resized.createGraphics();
        g.drawImage(image, 0, 0, size.width, size.height, null);
        g.dispose();
        return resized;
    }

}