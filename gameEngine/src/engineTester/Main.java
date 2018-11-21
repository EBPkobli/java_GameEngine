package engineTester;


import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;


import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TextureModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrain.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class Main {

	public static void main(String args[])
	{
		DisplayManager.createDisplay();
		Loader myLoader = new Loader();
		
		//------------DRAGON--------------\\
		ModelData dragonData = OBJFileLoader.loadOBJ("dragon"); //LOAD DRAGON VERTICES TO DATA
		RawModel dragonModel = myLoader.loadToVAO(dragonData.getVertices(), dragonData.getTextureCoords(), dragonData.getNormals(), dragonData.getIndices()); //DATA TO KEEPER
		ModelTexture modelTex = new ModelTexture(myLoader.loadTexture("white")); //DRAGON TEXTURE(COLOR,PICTURE ETC) TO DATA
		modelTex.setReflectivity(1);
		modelTex.setShineDamper(10);
		TextureModel dragonSelf = new TextureModel(dragonModel,modelTex); //VERTICES AND TEXTURE TOGETHER IT MEANS IT'S READY TO DRAW
		Entity entity = new Entity(dragonSelf, new Vector3f(0,-8,-100), 0,0,0,1); //DRAW WITH POSITION , SCALE , ROTATION
		//------------DRAGON--------------\\
		
		//-----------------LIGHT-----------------
		Light light = new Light(new Vector3f(3000,2000,2000),new Vector3f(1,1,1));
		//-----------------LIGHT-----------------
		
		//-----------------TERRAIN------------
		TerrainTexture backgroundTexture = new TerrainTexture(myLoader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(myLoader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(myLoader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(myLoader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(myLoader.loadTexture("blendMap"));
		
		Terrain terrain = new Terrain(0, -1, myLoader, texturePack, blendMap);
		Terrain terrain2 = new Terrain(-1, -1, myLoader, texturePack, blendMap);
		//-----------------TERRAIN-----------------
		

		//-----------------TREE-----------------
		ModelData treeData = OBJFileLoader.loadOBJ("tree");
		RawModel treeModel = myLoader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
		ModelTexture treeTex = new ModelTexture(myLoader.loadTexture("tree"));
		treeTex.setReflectivity(0);
		treeTex.setShineDamper(10);
		TextureModel treeSelf = new TextureModel(treeModel,treeTex);
		//-----------------TREE-----------------
		
		//-----------------GRASS--------------
		List<Entity> natureEntity = new ArrayList<Entity>();
		TextureModel grass = new TextureModel(OBJLoader.loadObjModel("grassModel", myLoader), new ModelTexture(myLoader.loadTexture("grassTexture")));
		TextureModel fern = new TextureModel(OBJLoader.loadObjModel("fern", myLoader), new ModelTexture(myLoader.loadTexture("fern")));
		TextureModel flower = new TextureModel(OBJLoader.loadObjModel("grassModel", myLoader), new ModelTexture(myLoader.loadTexture("flower")));
		grass.getTexture().setHasTransparency(true); //BECAUSE IT'S BLACK BACKGROUND
		grass.getTexture().setUseFakeLighting(true); //AND LIGHT FUCKED UP SO NORMALIZE IT TO Y +1 IT MEANS TO UP FROM SHADER
		fern.getTexture().setHasTransparency(true);
		fern.getTexture().setUseFakeLighting(false);
		fern.getTexture().setReflectivity(1);
		fern.getTexture().setShineDamper(10);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		//-----------------GRASS-----------------
		
		//-----------------SIMPLE AREA MAKER COULD DELETE ANY MOMENT-----------------
		int k=1;
		for(int i = 0; i < 500; i++)
		{
			if(i>250) k=-1;
			float xPos = (float) Math.random() * 400 * k;
			float zPos = (float) Math.random() * -800;
			natureEntity.add(new Entity(treeSelf,new Vector3f(xPos,-10,zPos),0,0,0,5));
			xPos = (float) Math.random() * 400 * k;
			zPos = (float) Math.random() * -800;
			natureEntity.add(new Entity(grass,new Vector3f(xPos,-10,zPos),0,0,0,0.5f));
			xPos = (float) Math.random() * 400 * k;
			zPos = (float) Math.random() * -800;
			natureEntity.add(new Entity(fern,new Vector3f(xPos,-10,zPos),0,0,0,0.5f));
			
			if(i<300) {
			xPos = (float) Math.random() * 400 * k;
			zPos = (float) Math.random() * -800;
			natureEntity.add(new Entity(flower,new Vector3f(xPos,-10,zPos),0,0,0,2f));
			}
		}
		//-----------------SIMPLE AREA MAKER COULD DELETE ANY MOMENT-----------------

		Camera cam = new Camera();
		MasterRenderer renderer = new MasterRenderer();
		
		RawModel bunnyModel = OBJLoader.loadObjModel("bunny", myLoader);
		TextureModel standordModel = new TextureModel(bunnyModel, new ModelTexture(myLoader.loadTexture("white")));
		Player player = new Player(standordModel,new Vector3f(0,-8,-100),0,0,0,1);
		while(!Display.isCloseRequested())
		{
			entity.increaseRotation(0, 0.5f, 0);
			cam.move();
			player.move();
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			for(Entity doga:natureEntity)
			{
				renderer.processEntity(doga);
			}
			renderer.processEntity(entity);
			renderer.render(light, cam);
			//oyun mantýðý burada
			DisplayManager.updateDisplay();
		}
		renderer.cleanUP();
		myLoader.cleanUP();
		DisplayManager.closeDisplay();
	}
}
