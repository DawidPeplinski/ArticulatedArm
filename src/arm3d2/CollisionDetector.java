package arm3d2;

import com.sun.j3d.utils.geometry.Sphere;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.vecmath.Point3d;


public class CollisionDetector extends Behavior
{
    public static boolean inCollision = false;
    private WakeupOnCollisionEntry wEnter;
    private WakeupOnCollisionExit wExit;
    Sphere prym;
    
    public CollisionDetector(Sphere s)
    {
        inCollision = false;
        prym = s;
        prym.setCollisionBounds(new BoundingSphere(new Point3d(), 0.1d));
    }

    @Override
    public void initialize() 
    {
        wEnter = new WakeupOnCollisionEntry(prym);
        wExit = new WakeupOnCollisionExit(prym);
        wakeupOn(wEnter);
    }

    @Override
    public void processStimulus(Enumeration criteria) 
    {
        inCollision = !inCollision;
        if(inCollision)
        {
            System.out.println("in");
            wakeupOn(wExit);
        }
        else
        {
            System.out.println("out");
            wakeupOn(wEnter);
        }
    }
    
}
