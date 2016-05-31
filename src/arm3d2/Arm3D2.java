package arm3d2;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.transform.Transform;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Geometry;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Material;
import javax.media.j3d.SceneGraphPath;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

public class Arm3D2 extends JFrame implements ActionListener, KeyListener
{
    private TransformGroup myTransformGroup, myTransformGroup1, myTransformGroup2, mainTransformGroup, baseTransformGroup, cubeTransformGroup, sferaTG;
    private TransformGroup wholeTransformGroup, objRotate;
    private Transform3D myTransform = new Transform3D();
    private Transform3D myTransform1 = new Transform3D();
    private Transform3D myTransform2 = new Transform3D();
    private Transform3D mainTransform = new Transform3D();
    private Transform3D baseTransform = new Transform3D();
    private Transform3D cubeTransform = new Transform3D();
    private BranchGroup mySceneBranch;
    private CollisionDetector myColSphere;
    private Sphere mySphere;
    private float x2, y2, x1, y1, x, y, x3, y3, rot1, rot2, rot, rot3;
    private float myAngle = (float) (Math.PI/72);
    private boolean     klawisze[];
    private Timer zegar;
    
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
         
        klawisze        = new boolean[9];
        for(int i=0; i<9; i++) klawisze[i] = false;
        
        zegar = new Timer();
        zegar.scheduleAtFixedRate(new Zadanie(),0,20);
        
        BranchGroup myScene = createMyScene();
        myScene.compile();
        
        SimpleUniverse simpleU = new SimpleUniverse(myCanvas);
        
        Transform3D observerTrans = new Transform3D();
        observerTrans.set(new Vector3f(x, 0.0f, 2.5f));
        
        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(observerTrans);
        simpleU.addBranchGraph(myScene);
    }
    
    BranchGroup createMyScene()
    {
        mySceneBranch = new BranchGroup();
        
        baseTransformGroup = new TransformGroup();
        baseTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
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
        lightD.setDirection(new Vector3f(0.0f, -0.5f, -1.0f));
        lightD.setColor(new Color3f(1.0f, 1.0f, 1.0f));
        wholeTransformGroup.addChild(lightD);
        
        DirectionalLight lightE = new DirectionalLight();
        lightE.setInfluencingBounds(bounds);
        lightE.setDirection(new Vector3f(0.0f, 0.5f, 1.0f));
        lightE.setColor(new Color3f(1.0f, 1.0f, 1.0f));
        wholeTransformGroup.addChild(lightE);
        
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
                         
         // textura podłoża
        Appearance ground_app = new Appearance(); 
        TextureLoader loader = new TextureLoader("resources/ground.png",null);
        ImageComponent2D image = loader.getImage();
        Texture2D ground = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                        image.getWidth(), image.getHeight());
        ground.setImage(0, image);
        ground.setBoundaryModeS(Texture.WRAP);
        ground.setBoundaryModeT(Texture.WRAP);
        ground_app.setTexture(ground);
        
        //textura sfery
        Appearance sphere_app = new Appearance();
        loader = new TextureLoader("resources/pattern.jpg",this);
        image = loader.getImage();
        Texture2D sphere_tex = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                        image.getWidth(), image.getHeight());
        sphere_tex.setImage(0, image);
        sphere_tex.setBoundaryModeS(Texture.WRAP);
        sphere_tex.setBoundaryModeT(Texture.WRAP);
        sphere_app.setTexture(sphere_tex);
        
        //wygląd podstawy robota
        Appearance base_app = new Appearance();
        Material baseMat = new Material(new Color3f(0.0f, 0.1f,0.0f), new Color3f(0.0f,0.0f,0.3f),
                                             new Color3f(0.459f, 0.518f, 0.237f), new Color3f(1.0f, 1.0f, 1.0f), 80.0f);
         ColoringAttributes baseColor = new ColoringAttributes();
         baseColor.setShadeModel(ColoringAttributes.SHADE_GOURAUD);
         base_app.setMaterial(baseMat);
         base_app.setColoringAttributes(baseColor);
         
         Shape3D base_shape = (Shape3D)baseScene.getSceneGroup().getChild(0);
         base_shape.setAppearance(base_app);
         
         //wygląd ramion robota
        Appearance arms_app = new Appearance();
        Material armsMat = new Material(new Color3f(0.0f, 0.1f,0.0f), new Color3f(0.0f,0.0f,0.3f),
                                             new Color3f(0.9f, 0.9f, 0.4f), new Color3f(1.0f, 1.0f, 1.0f), 80.0f);
         ColoringAttributes armsColor = new ColoringAttributes();
         armsColor.setShadeModel(ColoringAttributes.SHADE_GOURAUD);
         arms_app.setMaterial(armsMat);
         arms_app.setColoringAttributes(armsColor);
         
         Shape3D Scene0_shape = (Shape3D)Scene0.getSceneGroup().getChild(0);
         Scene0_shape.setAppearance(arms_app);
         
         Shape3D Scene1_shape = (Shape3D)Scene1.getSceneGroup().getChild(0);
         Scene1_shape.setAppearance(arms_app);
         
        // wygląd maina i chwytaka             
         Appearance main_app = new Appearance();
        Material main_mat = new Material(new Color3f(0.0f, 0.1f,0.0f), new Color3f(0.0f,0.0f,0.3f),
                                             new Color3f(0.7f, 0.7f, 0.4f), new Color3f(1.0f, 1.0f, 1.0f), 80.0f);
         ColoringAttributes mainColor = new ColoringAttributes();
         mainColor.setShadeModel(ColoringAttributes.SHADE_GOURAUD);
         main_app.setMaterial(main_mat);
         main_app.setColoringAttributes(mainColor);
         
         Shape3D Main_shape = (Shape3D)mainScene.getSceneGroup().getChild(0);
         Main_shape.setAppearance(main_app);
         Shape3D Scene2_shape = (Shape3D)Scene2.getSceneGroup().getChild(0);
         Scene2_shape.setAppearance(main_app);
         
         //Podstawa ramienia
        Cylinder podstawa = new Cylinder(1.0f,0.05f,Cylinder.GENERATE_NORMALS| Cylinder.GENERATE_TEXTURE_COORDS, ground_app);
        
        podstawa.setAppearance(ground_app);
        Transform3D p_podstawa = new Transform3D();
        p_podstawa.set(new Vector3f(0.0f, -0.535f, 0.0f));
        TransformGroup podstawa_transGroup = new TransformGroup(p_podstawa);
        podstawa_transGroup.addChild(podstawa);
        wholeTransformGroup.addChild(podstawa_transGroup);
        
        // Sfera
        mySphere = new Sphere(0.05f,Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS, sphere_app);
        Transform3D p_sfera = new Transform3D();
        p_sfera.set(new Vector3f(0.5f, -0.2f, 0.0f));
        sferaTG = new TransformGroup(p_sfera);
        sferaTG.addChild(mySphere);
        
        wholeTransformGroup.addChild(sferaTG);

        sferaTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
                                                                                                                            //////////////////////////
        myColSphere = new CollisionDetector(sferaTG, mySphere.getBounds());
        myColSphere.setSchedulingBounds(new BoundingSphere());
        wholeTransformGroup.addChild(myColSphere);
        
        
    
        
        
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
    public void keyPressed(KeyEvent e) 
    {
        switch(e.getKeyCode()){
                    case KeyEvent.VK_Q:      klawisze[0] = true; break;
                    case KeyEvent.VK_A:    klawisze[1] = true; break;
                    case KeyEvent.VK_W:    klawisze[2] = true; break;
                    case KeyEvent.VK_S:   klawisze[3] = true; break;
                    case KeyEvent.VK_E:      klawisze[4] = true; break;
                    case KeyEvent.VK_D:    klawisze[5] = true; break;
                    case KeyEvent.VK_Z:    klawisze[6] = true; break;
                    case KeyEvent.VK_X:   klawisze[7] = true; break;
                    case KeyEvent.VK_C:   klawisze[8] = true; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) 
    {
         switch(e.getKeyCode()){
                    case KeyEvent.VK_Q:      klawisze[0] = false; break;
                    case KeyEvent.VK_A:    klawisze[1] = false; break;
                    case KeyEvent.VK_W:    klawisze[2] = false; break;
                    case KeyEvent.VK_S:   klawisze[3] = false; break;
                    case KeyEvent.VK_E:      klawisze[4] = false; break;
                    case KeyEvent.VK_D:    klawisze[5] = false; break;
                    case KeyEvent.VK_Z:    klawisze[6] = false; break;
                    case KeyEvent.VK_X:   klawisze[7] = false; break;
                    case KeyEvent.VK_C:   klawisze[8] = false; break;
         }
    }
 
    class Zadanie extends TimerTask{

        @Override
        public void run()
        {

            if(klawisze[0])
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
            if(klawisze[1])
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
            if(klawisze[2])
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
            if(klawisze[3]) 
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
            if(klawisze[4])
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
            if(klawisze[5])
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
            if(klawisze[6])
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
               
               // System.out.println("" + y2 + " " + rot2);
            }
            if(klawisze[7]) 
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
            if(klawisze[8]) 
            {
             /*   Transform3D sferaTrans = new Transform3D();
                sferaTrans.set(new Vector3f(0.0f, 1.0f, 0.0f));
                sferaTG.setTransform(sferaTrans); */
            }
           
           }
  }
    
    public static void main(String[] args)
    {
        Arm3D2 mainScene = new Arm3D2();
        mainScene.addKeyListener(mainScene);
    }
    
}
