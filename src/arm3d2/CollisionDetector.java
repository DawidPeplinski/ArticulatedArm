package arm3d2;

import com.sun.j3d.utils.geometry.Sphere;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.vecmath.Point3d;


public class CollisionDetector extends Behavior
{
    public static boolean inCollision = false;
    private WakeupOnCollisionEntry wEnter;
    private WakeupOnCollisionExit wExit;
    private  TransformGroup ref_doTG = new TransformGroup();
   
    
    public boolean getinCollision()
    {
        return inCollision;
    }
    
    public TransformGroup getref_doTG()
    {
        return ref_doTG;
    }
    
    public void setref_doTG(Transform3D buf)
    {
        ref_doTG.setTransform(buf);
    }
    
    public CollisionDetector(TransformGroup s, Bounds colBounds)
    {
       inCollision = false;
        this.ref_doTG = s;
        s.setCollisionBounds(new BoundingSphere(new Point3d(), 0.01d));
        ref_doTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
    }

    @Override
    public void initialize() 
    {
        wEnter = new WakeupOnCollisionEntry(ref_doTG);
        wExit = new WakeupOnCollisionExit(ref_doTG);
        wakeupOn(wEnter);
        System.out.println("init");
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
