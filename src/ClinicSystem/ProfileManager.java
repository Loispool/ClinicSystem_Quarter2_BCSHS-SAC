package ClinicSystem;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Helper class to manage the user's profile picture.
 * This is NOT a Swing UI class; it handles the "behind the scenes" file saving and loading.
 */
public class ProfileManager {

    // We will save the profile picture to the user's home directory to ensure it persists
    // even if we rebuild the project.
    private static final String PROFILE_DIR = System.getProperty("user.home") + File.separator + ".clinic_system";
    private static final String PROFILE_FILE_NAME = "user_profile.png";

    /**
     * Saves the selected file as the new profile picture.
     * @param sourceFile The file selected by the user.
     * @return true if successful, false otherwise.
     */
    public static boolean saveProfilePicture(File sourceFile) {
        try {
            // 1. Create the directory if it doesn't exist
            File dir = new File(PROFILE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2. Define the destination file
            File destFile = new File(dir, PROFILE_FILE_NAME);

            // 3. Copy the file
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads the profile picture.
     * @return An ImageIcon of the profile picture, or null if none exists.
     */
    public static ImageIcon getProfilePicture() {
        File file = new File(PROFILE_DIR, PROFILE_FILE_NAME);
        if (file.exists()) {
            try {
                // Read the image
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    // Resize it to fit standard icon size (e.g., 50x50 or 100x100) if needed
                    // For now, we return the full image, let the UI scale it
                    return new ImageIcon(img);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null; // Return null if no custom picture found
    }
    
    /**
     * Helper to get a scaled version of the profile picture for buttons/labels.
     * @param width Desired width
     * @param height Desired height
     * @return Scaled ImageIcon
     */
    public static ImageIcon getScaledProfilePicture(int width, int height) {
        ImageIcon icon = getProfilePicture();
        if (icon != null) {
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(newImg);
        }
        return null;
    }

    /**
     * Helper to get a CIRCULAR scaled version of the profile picture.
     * @param width Desired width
     * @param height Desired height
     * @return Circular ImageIcon
     */
    public static ImageIcon getCircularProfilePicture(int width, int height) {
        ImageIcon icon = getProfilePicture();
        if (icon != null) {
            // 1. Scale the image first
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            
            // 2. Create a buffered image to draw the circle
            BufferedImage circleBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2 = circleBuffer.createGraphics();
            
            // 3. Enable anti-aliasing for smooth edges
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 4. Create the circular clip
            g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, width, height));
            
            // 5. Draw the image inside the clip
            g2.drawImage(scaledImg, 0, 0, width, height, null);
            g2.dispose();
            
            return new ImageIcon(circleBuffer);
        }
        return null;
    }
}
