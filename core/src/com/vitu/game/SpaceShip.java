package com.vitu.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;

public class SpaceShip extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img;
    Texture tNave;
    Texture tMissile;
    Texture tEnemy;
    private Sprite nave;
    private Sprite missile;
    private Array<Rectangle> enemies;
	private long lastEnemyTime;
    private float posX;
    private float posY;
    private float velocity = 10;
    private float xtMissile;
    private float ytMissile;
    private boolean attack;


    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("bg.png");
        tNave = new Texture("spaceship.png");
        tMissile = new Texture("missile.png");
        tEnemy = new Texture("enemy.png");
        nave = new Sprite(tNave);
        missile = new Sprite(tMissile);
		enemies = new Array<Rectangle>();
		lastEnemyTime = 0;
        posX = 0;
        posY = 0;
        velocity = 10;
        xtMissile = posX;
        ytMissile = posY;
        attack = false;
    }

    @Override
    public void render() {
        ///aqui ele renderiza de acordo com a ordem que está aqui dentro
        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
        batch.draw(img, 0, 0);
		if(attack){
			///para exibir o missel só quando tive atacando
        batch.draw(missile, xtMissile + nave.getWidth() / 2, ytMissile + nave.getHeight() / 2 - 13);
		}
        batch.draw(nave, posX, posY);

		for(Rectangle enemy: enemies){
			batch.draw(tEnemy, enemy.x, enemy.y);
		}

        batch.end();

        this.moveNave();
        this.moveMissile();
		this.moveEnemies();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        tNave.dispose();
    }

    private void moveNave() {
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (posX < Gdx.graphics.getWidth() - nave.getWidth()) {
                posX += velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (posX > 0) {
                posX -= velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (posY < Gdx.graphics.getHeight() - nave.getHeight()) {
                posY += velocity;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (posY > 0) {
                posY -= velocity;
                attack = true;
            }
        }
//		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) { // verifica se a tecla espaço foi pressionada
//			if (!isJumping) { // verifica se a nave não está pulando
//				isJumping = true;
//				jumpHeight = 0;
//			}
//		}
    }

    private void moveMissile() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !attack) {
			///se aperta o espaço ele entra no if abaixo
            attack = true;
            ytMissile = posY;
        }
        if (attack) {
			///aqui toda hora que eu aperta espaço vai lança um missel
            if (attack) {
				if(xtMissile < Gdx.graphics.getWidth() - nave.getWidth()) {
					/// para o missel nao passar da tela
                xtMissile += 40;
				} else {
					///depois que o missel for lançado ele volta pra nava
					xtMissile = posX;
					attack = false;
				}
            }
        } else {
			///aqui o missel volta pra posição da nave
            xtMissile = posX;
            ytMissile = posY;
        }
    }


	private void spawnEnemies(){
		Rectangle enemy = new Rectangle(Gdx.graphics.getWidth(),MathUtils.random(0, Gdx.graphics.getHeight() - tEnemy.getHeight()), tEnemy.getWidth(), tEnemy.getHeight());
		enemies.add(enemy);
		lastEnemyTime = TimeUtils.nanoTime();
	}

	private void moveEnemies(){
		if(TimeUtils.nanoTime() - lastEnemyTime > 999999999){
		this.spawnEnemies();
		}
		///vamos itegarir em cada inimigo mais precisamos ir passando por isso o hasNext
		for (Iterator<Rectangle> iter = enemies.iterator(); iter.hasNext();) {
		  Rectangle enemy = iter.next();
		  enemy.x -= 15; ///velocidade
		  if(enemy.x + tEnemy.getWidth() < 0) {
			  iter.remove();
		  }
		}
	}
}
