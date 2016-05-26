package arm3d2;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import javafx.scene.transform.Transform;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Geometry;
import javax.media.j3d.Material;
import javax.media.j3d.SceneGraphPath;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.Timer;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public class Arm3D2 extends JFrame implements ActionListener, KeyListener
{
    private TransformGroup myTransformGroup, myTransformGroup1, myTransformGroup2, mainTransformGroup, baseTransformGroup, cubeTransformGroup;
    private TransformGroup wholeTransformGroup, objRotate;
    private Transform3D myTransform = new Transform3D();
    private Transform3D myTransform1 = new Transform3D();
    private Transform3D myTransform2 = new Transform3D();
    private Transform3D mainTransform = new Transform3D();
    private Transform3D baseTransform = new Transform3D();
    private Transform3D cubeTransform = new Transform3D();
    private CollisionDetector myColSphere;
    private Sphere mySphere;
    private float x2, y2, x1, y1, x, y, x3, y3, rot1, rot2, rot, rot3;
    private float myAngle = (float) (Math.PI/36);
    private Appearance armApp, armApp2;
    private Shape3D armPartsTab[] = new Shape3D[112];
    private int number;
    
    public Arm3D2()
    {
        super("Articulated Arm");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        GraphicsConfiguration myConfig =
                SimpleUniverse.getPreferredConfiguration();
        Canvas3D myCanvas = new Canvas3D(myConfig);
        myCanvas.setPreferredSize(new Dimension(1200,1000));
        
        myCanvas.addKeyListener(this);
        
        add(myCanvas);
        pack();
        setVisible(true);
        
        BranchGroup myScene = createMyScene();
        myScene.compile();
        
        SimpleUniverse simpleU = new SimpleUniverse(myCanvas);
        
        Transform3D observerTrans = new Transform3D();
        observerTrans.set(new Vector3f(0.0f, 0.0f, 2.5f));
        
        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(observerTrans);
        simpleU.addBranchGraph(myScene);
    }
    
    BranchGroup createMyScene()
    {
        BranchGroup mySceneBranch = new BranchGroup();
        
        baseTransformGroup = new TransformGroup();
        baseTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      //  mySceneBranch.addChild(baseTransformGroup);
        
        mainTransformGroup = new TransformGroup();
        mainTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        cubeTransformGroup = new TransformGroup();
        cubeTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        myTransformGroup = new TransformGroup();
        myTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        myTransformGroup1 = new TransformGroup();
        myTransformGroup1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        myTransformGroup2 = new TransformGroup();
        myTransformGroup2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        wholeTransformGroup = new TransformGroup();
        wholeTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
      //  mySceneBranch.addChild(wholeTransformGroup);
        
        BoundingSphere bounds = new BoundingSphere();
        // obracanie kamery        
        objRotate = new TransformGroup();
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
         
        objRotate.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        objRotate.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        objRotate.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

        mySceneBranch.addChild(objRotate);
        
       objRotate.addChild(wholeTransformGroup);

        MouseRotate myMouseRotate = new MouseRotate();
        
        myMouseRotate.setTransformGroup(objRotate);
        myMouseRotate.setSchedulingBounds(new BoundingSphere());
        
        mySceneBranch.addChild(myMouseRotate);
        
        // LIGHTS
        AmbientLight lightA = new AmbientLight();
        lightA.setInfluencingBounds(bounds);
        wholeTransformGroup.addChild(lightA);
        
        DirectionalLight lightD = new DirectionalLight();
        lightD.setInfluencingBounds(bounds);
        lightD.setDirection(new Vector3f(0.0f, 0.0f, -1.0f));
        lightD.setColor(new Color3f(1.0f, 1.0f, 1.0f));
        wholeTransformGroup.addChild(lightD);
        // ładuję model
        Scene baseScene = null;
        Scene mainScene = null;
        Scene Scene0 = null;
        Scene Scene1 = null;
        Scene Scene2 = null;
        try {
            ObjectFile f = new ObjectFile();
            //f.setFlags ( ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
            f.setFlags(ObjectFile.LOAD_BACKGROUND_NODES | ObjectFile.STRIPIFY | ObjectFile.RESIZE);
            baseScene = f.load("resources/base.obj");
            mainScene = f.load("resources/main.obj");
            Scene0 = f.load("resources/0.obj");
            Scene1 = f.load("resources/1.obj");
            Scene2 = f.load("resources/2.obj");
        } catch (java.io.FileNotFoundException ex) {
        }
        
        baseTransformGroup.addChild(baseScene.getSceneGroup());
        mainTransformGroup.addChild(mainScene.getSceneGroup());
        myTransformGroup.addChild(Scene0.getSceneGroup());
        myTransformGroup1.addChild(Scene1.getSceneGroup());
        myTransformGroup2.addChild(Scene2.getSceneGroup());
        
        baseTransform.setScale(0.2);
        mainTransform.setScale(0.3);
       
        //wyglad i materiał
        armApp = new Appearance();
        Material armMat = new Material(new Color3f(0.0f, 0.1f,0.0f), new Color3f(0.0f,0.0f,0.3f),
                                             new Color3f(0.5f, 0.5f, 0.5f), new Color3f(1.0f, 1.0f, 1.0f), 80.0f);
         ColoringAttributes armColor = new ColoringAttributes();
         armColor.setShadeModel(ColoringAttributes.SHADE_GOURAUD);
         armApp.setMaterial(armMat);
         armApp.setColoringAttributes(armColor);
         
         Shape3D sasa = (Shape3D)Scene2.getSceneGroup().getChild(0);
         sasa.setAppearance(armApp);
         
                 
         //Podstawa ramienia
        Cylinder podstawa = new Cylinder(1.0f,0.05f,armApp);
        Transform3D p_podstawa = new Transform3D();
        p_podstawa.set(new Vector3f(0.0f, -0.55f, 0.0f));
        TransformGroup podstawa_transGroup = new TransformGroup(p_podstawa);
        podstawa_transGroup.addChild(podstawa);
        wholeTransformGroup.addChild(podstawa_transGroup);
        
        // Sfera
        mySphere = new Sphere(0.1f, armApp);
        myColSphere = new CollisionDetector(mySphere);
        Transform3D p_sfera = new Transform3D();
        p_sfera.set(new Vector3f(0.5f, -0.4f, 0.0f));
        TransformGroup sferaTG = new TransformGroup(p_sfera);
        sferaTG.addChild(mySphere);
        wholeTransformGroup.addChild(sferaTG);
     //   sferaTG.setCapability(TransformGroup.ALLOW_COLLISION_BOUNDS_READ);
        
        
        myTransform2.setTranslation(new Vector3f(1.8f, 0.1f, 0.0f));
        myTransformGroup2.setTransform(myTransform2);
        
        myTransformGroup1.addChild(myTransformGroup2);
        myTransform1.setTranslation(new Vector3f(1.75f, 0.0f, 0.0f));
        myTransformGroup1.setTransform(myTransform1);
        
        myTransformGroup.addChild(myTransformGroup1);
        myTransform.setTranslation(new Vector3f(1.0f, 1.0f, 0.0f));
        myTransformGroup.setTransform(myTransform);
        
        cubeTransformGroup.addChild(myTransformGroup);
        cubeTransform.setTranslation(new Vector3f(-0.1f, -0.1f, 0.0f));
        cubeTransformGroup.setTransform(cubeTransform);
        
        mainTransform.setTranslation(new Vector3f(0.0f, 0.8f, 0.0f));    
        mainTransformGroup.setTransform(mainTransform);
        
        baseTransformGroup.addChild(cubeTransformGroup);
        baseTransformGroup.addChild(mainTransformGroup);
        baseTransform.setTranslation(new Vector3f(0.0f, -0.4f, 0.0f));
        baseTransformGroup.setTransform(baseTransform);
        wholeTransformGroup.addChild(baseTransformGroup);
         
        return mySceneBranch;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyTyped(KeyEvent e) 
    {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar()=='q')
        {        
        Transform3D  tmp_rot      = new Transform3D();
        tmp_rot.rotZ(myAngle);
        myTransform.mul(tmp_rot);
        rot = (float) (rot + myAngle);
        x = (float) (1.0f*(Math.cos(rot)));
        y = (float) (1.0f*(Math.sin(rot)));
        myTransform.setTranslation(new Vector3f(x, y + 1.0f, 0.0f));
        myTransformGroup.setTransform(myTransform);
        }
        if(e.getKeyChar()=='a')
        {
        Transform3D  tmp_rot      = new Transform3D();
        tmp_rot.rotZ(-myAngle);
        myTransform.mul(tmp_rot);
        rot = (float) (rot - myAngle);
        x = (float) (1.0f*(Math.cos(rot)));
        y = (float) (1.0f*(Math.sin(rot)));
        myTransform.setTranslation(new Vector3f(x, y + 1.0f, 0.0f));
        myTransformGroup.setTransform(myTransform);
        }
        if(e.getKeyChar()=='w')
        {        
        Transform3D  tmp_rot      = new Transform3D();
        tmp_rot.rotZ(myAngle);
        myTransform1.mul(tmp_rot);
        rot1 = (float) (rot1 + myAngle);
        x1 = (float) (0.9f + 0.85f*(Math.cos(rot1)));
        y1 = (float) (0.85f*(Math.sin(rot1)));
        myTransform1.setTranslation(new Vector3f(x1, y1, 0.0f));
        myTransformGroup1.setTransform(myTransform1);
        }
        if(e.getKeyChar()=='s')
        {
        Transform3D  tmp_rot      = new Transform3D();
        tmp_rot.rotZ(-myAngle);
        myTransform1.mul(tmp_rot);
        rot1 = (float) (rot1 - myAngle);
        x1 = (float) (0.9f + 0.85f*(Math.cos(rot1)));
        y1 = (float) (0.85f*(Math.sin(rot1)));
        myTransform1.setTranslation(new Vector3f(x1, y1, 0.0f));
        myTransformGroup1.setTransform(myTransform1);
        }
        if(e.getKeyChar()=='e')
        {        
        Transform3D  tmp_rot      = new Transform3D();
        tmp_rot.rotZ(myAngle);
        myTransform2.mul(tmp_rot);
        rot2 = (float) (rot2 + myAngle);
        x2 = (float) (0.9f + 0.9f*(Math.cos(rot2)));
        y2 = (float) (0.1f + 0.9f*(Math.sin(rot2)));
        myTransform2.setTranslation(new Vector3f(x2 , y2, 0.0f));
        myTransformGroup2.setTransform(myTransform2);
        }
        if(e.getKeyChar()=='d')
        {
        Transform3D  tmp_rot      = new Transform3D();
        tmp_rot.rotZ(-myAngle);
        myTransform2.mul(tmp_rot);
        rot2 = (float) (rot2 - myAngle);
        x2 = (float) (0.9f + 0.9f*(Math.cos(rot2)));
        y2 = (float) (0.1f + 0.9f*(Math.sin(rot2)));
        myTransform2.setTranslation(new Vector3f(x2, y2, 0.0f));
        myTransformGroup2.setTransform(myTransform2);
        }
        if(e.getKeyChar() == 'z')
        {
         Transform3D  tmp_rot      = new Transform3D();
        tmp_rot.rotY(myAngle);
        mainTransform.mul(tmp_rot);
        mainTransformGroup.setTransform(mainTransform);
        cubeTransform.mul(tmp_rot);
        rot3 = (float) (rot3 + myAngle);
        x3 = (float) (-0.1f*(Math.cos(rot3)));
        y3 = (float) (-0.1f*(Math.sin(rot3)));
        cubeTransform.setTranslation(new Vector3f(x3, -0.1f, 0.0f));
        cubeTransformGroup.setTransform(cubeTransform);
        }
        if(e.getKeyChar() == 'x')
        {
        Transform3D  tmp_rot      = new Transform3D();
        tmp_rot.rotY(-myAngle);
        mainTransform.mul(tmp_rot);
        mainTransformGroup.setTransform(mainTransform);
        cubeTransform.mul(tmp_rot);
        rot3 = (float) (rot3 - myAngle);
        x3 = (float) (-0.1f*(Math.cos(rot3)));
        y3 = (float) (-0.1f*(Math.sin(rot3)));
        cubeTransform.setTranslation(new Vector3f(x3, -0.1f, 0.0f));
        cubeTransformGroup.setTransform(cubeTransform);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) 
    {
        
    }
    
    public static void main(String[] args)
    {
        Arm3D2 mainScene = new Arm3D2();
        mainScene.addKeyListener(mainScene);
    }
    
}
