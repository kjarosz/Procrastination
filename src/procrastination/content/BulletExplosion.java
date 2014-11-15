package procrastination.content;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;

public class BulletExplosion extends Entity {
    private static final double growthRate = 400;
    private static final double existTime = .25;
    
    private double radius;
    private double timeLiving;
    private double mTime;
    
    public BulletExplosion(Point2D.Double position) {
        setPosition(position);
        setType(Entity.objectTypes.BULLET_EXPLOSION);
        radius = 5;
        setBBox(radius * 2, radius * 2);
        mTime = System.currentTimeMillis();
        timeLiving = 0;
    }

    @Override
    public void update(Level level) {
        double deltaTime = (System.currentTimeMillis() - mTime) / 1000.0;
        timeLiving += deltaTime;
        if(timeLiving >= existTime){
            level.deleteEntity(this);
        }
        animate(deltaTime);
        mTime = System.currentTimeMillis();
    }

    private void animate(double deltaTime) {
        radius += deltaTime * growthRate;
        setBBox(2 * radius, 2 * radius);
    }

    @Override
    public void draw(Graphics g, int xOffset, int yOffset) {
        drawBBox(g, Color.MAGENTA, xOffset, yOffset);
    }

    @Override
    public void collision(Entity other, Level level) {
        
    }
}
