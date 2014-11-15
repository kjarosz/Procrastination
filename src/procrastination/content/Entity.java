package procrastination.content;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class Entity {

    public static enum objectTypes {
        PLAYER, ENEMY, BULLET, BULLET_EXPLOSION, POWERUP
    };
    private objectTypes type;

    protected Point2D.Double mPosition;
    protected Point2D.Double mDirection;

    protected long mLastTime; // milliseconds

    protected Polygon bBox;
    private double cornerDistance;
    private double[] cornerAngle;

    private BufferedImage mImage;

    public Entity() {
        mPosition = new Point2D.Double();
        mDirection = new Point2D.Double();
        //bBox = new Polygon();
        mLastTime = System.currentTimeMillis();
    }

    public void setType(objectTypes oType) {
        type = oType;
    }

    public objectTypes getType() {
        return type;
    }

    protected BufferedImage[] loadSprites(File spriteSheetFile, Rectangle spriteMappings[]) {
        try {
            BufferedImage spriteSheet = ImageIO.read(spriteSheetFile);
            if (spriteSheet == null) {
                throw new RuntimeException("\"" + spriteSheetFile.getPath() + "\" sprite sheet could not be loaded.");
            }

            BufferedImage sprites[] = new BufferedImage[spriteMappings.length];
            for (int i = 0; i < spriteMappings.length; i++) {
                sprites[i] = spriteSheet.getSubimage(
                        spriteMappings[i].x,
                        spriteMappings[i].y,
                        spriteMappings[i].width - spriteMappings[i].x,
                        spriteMappings[i].height - spriteMappings[i].y);
            }
            return sprites;
        } catch (IOException ex) {
            System.out.println("Could not read sprite sheet.");
            throw new RuntimeException("\"" + spriteSheetFile.getPath() + "\" sprite sheet could not be loaded.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new RuntimeException("\"" + spriteSheetFile.getPath() + "\" sprite sheet could not be loaded.");
        }
    }

    public void setPosition(Point2D.Double position) {
        mPosition.x = position.x;
        mPosition.y = position.y;
    }

    public Point2D.Double getPosition() {
        return mPosition;
    }

    public void setDirection(Point2D.Double direction) {
        double length = Math.sqrt(direction.x * direction.x + direction.y * direction.y);
        mDirection.x = direction.x / length;
        mDirection.y = direction.y / length;
    }

    public Point2D.Double getDirection() {
        return mDirection;
    }

    public void setBBox(double width, double height) {
        bBox = new Polygon();
        bBox.reset();
        cornerDistance = Math.sqrt(Math.pow(width / 2, 2) + Math.pow(height / 2, 2));
        cornerAngle = new double[]{
            Math.atan2(-width / 2, -height / 2),
            Math.atan2(-width / 2, height / 2),
            Math.atan2(width / 2, height / 2),
            Math.atan2(width / 2, -height / 2)
        };
        bBox.addPoint((int) (mPosition.x - width / 2), (int) (mPosition.y - height / 2));
        bBox.addPoint((int) (mPosition.x - width / 2), (int) (mPosition.y + height / 2));
        bBox.addPoint((int) (mPosition.x + width / 2), (int) (mPosition.y + height / 2));
        bBox.addPoint((int) (mPosition.x + width / 2), (int) (mPosition.y - height / 2));
        bBox.invalidate();
    }

    public Polygon getBBox() {
        return bBox;
    }

    public boolean intersectsBBox(Polygon other) {
        Point p;
        for (int i = 0; i < other.npoints; i++) {
            p = new Point(other.xpoints[i], other.ypoints[i]);
            if (bBox.contains(p)) {
                return true;
            }
        }
        for (int i = 0; i < bBox.npoints; i++) {
            p = new Point(bBox.xpoints[i], bBox.ypoints[i]);
            if (other.contains(p)) {
                return true;
            }
        }
        return false;
    }

    public abstract void collision(Entity other, Level level);
    
    public void drawBBox(Graphics g, Color c, int xOffset, int yOffset) {
        Color oldColor = g.getColor();
        g.setColor(c);
        bBox.translate(-xOffset, -yOffset);
        g.drawPolygon(bBox);
        bBox.translate(xOffset, yOffset);
        g.setColor(oldColor);
    }

    protected void setCurrentImage(BufferedImage image) {
        mImage = image;
    }

    public abstract void update(Level level);

    protected void move(double velocity) {
        long deltaTime = System.currentTimeMillis() - mLastTime;
        mLastTime += deltaTime;

        double xChange = mDirection.x * velocity * deltaTime / 1000.0f;
        double yChange = mDirection.y * velocity * deltaTime / 1000.0f;
        mPosition.x += xChange;
        mPosition.y += yChange;

        updateBBox();
    }

    protected void move(Point2D.Double velocity) {
        long deltaTime = System.currentTimeMillis() - mLastTime;
        mLastTime += deltaTime;

        double xChange = velocity.x * deltaTime / 1000.0f;
        double yChange = velocity.y * deltaTime / 1000.0f;
        mPosition.x += xChange;
        mPosition.y += yChange;

        updateBBox();
    }

    private void updateBBox() {
        double angle = Math.atan2(mDirection.y, mDirection.x);

        bBox.xpoints[0] = (int) (mPosition.x + cornerDistance * Math.cos(angle + cornerAngle[0]));
        bBox.ypoints[0] = (int) (mPosition.y + cornerDistance * Math.sin(angle + cornerAngle[0]));

        bBox.xpoints[1] = (int) (mPosition.x + cornerDistance * Math.cos(angle + cornerAngle[1]));
        bBox.ypoints[1] = (int) (mPosition.y + cornerDistance * Math.sin(angle + cornerAngle[1]));

        bBox.xpoints[2] = (int) (mPosition.x + cornerDistance * Math.cos(angle + cornerAngle[2]));
        bBox.ypoints[2] = (int) (mPosition.y + cornerDistance * Math.sin(angle + cornerAngle[2]));

        bBox.xpoints[3] = (int) (mPosition.x + cornerDistance * Math.cos(angle + cornerAngle[3]));
        bBox.ypoints[3] = (int) (mPosition.y + cornerDistance * Math.sin(angle + cornerAngle[3]));

        bBox.invalidate();
    }

    public void draw(Graphics g, int xOffset, int yOffset) {
        draw(g, 1.0, xOffset, yOffset);
    }

    protected void draw(Graphics g, double scale, int xOffset, int yOffset) {
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(mPosition.x - xOffset, mPosition.y - yOffset);
        if (mDirection.x < 0) {
            g2.rotate(Math.atan(mDirection.y / mDirection.x) - 3.14 / 2);
        } else {
            g2.rotate(Math.atan(mDirection.y / mDirection.x) + 3.14 / 2);
        }
        g2.scale(scale, scale);
        g2.drawImage(mImage,
                -mImage.getWidth() / 2,
                -mImage.getHeight() / 2,
                mImage.getWidth(),
                mImage.getHeight(),
                null);
        g2.setTransform(oldTransform);
    }
}
