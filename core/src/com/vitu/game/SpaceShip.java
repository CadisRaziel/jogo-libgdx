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


import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;

public class SpaceShip extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img, tNave, tMissile, tEnemy;
    private Sprite nave, missile;
    private float posX, posY, velocity, xMissile, yMissile;
    private boolean attack, gameover;
    private Array<Rectangle> enemies;
    private long lastEnemyTime;
    private int score, life, numEnemies;

    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont bitmap;

    @Override
    public void create () {
        ///aqui ele renderiza de acordo com a ordem que está aqui dentro
        batch = new SpriteBatch();
        img = new Texture("bg.png");
        tNave = new Texture("spaceship.png");
        nave = new Sprite(tNave);
        posX = 0;
        posY = 0;
        velocity = 10;

        tMissile = new Texture("missile.png");
        missile  = new Sprite(tMissile);
        xMissile = posX;
        yMissile = posY;
        attack = false;

        tEnemy = new Texture("enemy.png");
        enemies = new Array<Rectangle>();
        lastEnemyTime = 0;

        score = 0;
        life = 3;
        numEnemies = 799999999;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 30;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.WHITE;

        //somente depois de definir os parametros é que podemos gerar a fonte com o bitmap
        bitmap = generator.generateFont(parameter);

        gameover = false;
    }

    @Override
    public void render () {

        this.moveNave();
        this.moveMissile();
        this.moveEnemies();

        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
        batch.draw(img, 0, 0);

        if(!gameover){
            if(attack){
                ///para exibir o missel só quando tive atacando
                batch.draw(missile, xMissile, yMissile);
            }
            batch.draw(nave, posX, posY);

            for(Rectangle enemy : enemies ){
                batch.draw(tEnemy, enemy.x, enemy.y);
            }
            bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
            bitmap.draw(
                    batch, "Life: " + life,
                    Gdx.graphics.getWidth() - 150,
                    Gdx.graphics.getHeight() - 20
            );
        }else{
            bitmap.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 20);
            bitmap.draw(
                    batch, "GAME OVER",
                    Gdx.graphics.getWidth() - 150,
                    Gdx.graphics.getHeight() - 20
            );


            ///Quando da game over o jogador pressionando ENTER ele reinicia o jogo
            if( Gdx.input.isKeyPressed(Input.Keys.ENTER) ){
                score = 0;
                life = 3;
                posX = 0;
                posY = 0;
                gameover = false;
            }
        }


        batch.end();
    }

    @Override
    public void dispose () {
        batch.dispose();
        img.dispose();
        tNave.dispose();
    }

    private void moveNave(){
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            if( posX < Gdx.graphics.getWidth() - nave.getWidth() ){
                posX += velocity;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            if( posX > 0 ){
                posX -= velocity;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            if( posY < Gdx.graphics.getHeight() - nave.getHeight() ){
                posY += velocity;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)){
            if( posY > 0 ){
                posY -= velocity;
            }
        }
        //		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) { // verifica se a tecla espaço foi pressionada
//			if (!isJumping) { // verifica se a nave não está pulando
//				isJumping = true;
//				jumpHeight = 0;
//			}
//		}
    }

    private void moveMissile(){
        if( Gdx.input.isKeyPressed(Input.Keys.SPACE) && !attack ){
            ///se aperta o espaço ele entra no if abaixo
            attack = true;
            yMissile = posY  + nave.getHeight() / 2 - 12;
        }

        if(attack){
            ///aqui toda hora que eu aperta espaço vai lança um missel
            if( xMissile < Gdx.graphics.getWidth() ){
                xMissile += 40;
                /// para o missel nao passar da tela
            }else{
                ///depois que o missel for lançado ele volta pra nava
                xMissile = posX + nave.getWidth() / 2;
                attack = false;
            }
        }else{
            ///aqui o missel volta pra posição da nave
            xMissile = posX + nave.getWidth() / 2;
            yMissile = posY  + nave.getHeight() / 2 - 12;
        }
    }

    private void spawnEnemies(){
        Rectangle enemy = new Rectangle( Gdx.graphics.getWidth(), MathUtils.random(0, Gdx.graphics.getHeight() - tEnemy.getHeight()), tEnemy.getWidth(), tEnemy.getHeight());
        enemies.add(enemy);
        lastEnemyTime = TimeUtils.nanoTime();
    }

    private void moveEnemies(){

        if( TimeUtils.nanoTime() - lastEnemyTime > numEnemies ){
            this.spawnEnemies();
        }

        ///vamos itegarir em cada inimigo mais precisamos ir passando por isso o hasNext
        for( Iterator<Rectangle> iter = enemies.iterator(); iter.hasNext(); ){
            Rectangle enemy = iter.next();
            enemy.x -= 400 * Gdx.graphics.getDeltaTime(); ///velocidade

            // Colisão com o míssel
            if( collide(enemy.x, enemy.y, enemy.width, enemy.height, xMissile, yMissile, missile.getWidth(), missile.getHeight()) && attack ){
                ++score;

                //quanto mais inimigos for derrotado mais inimigos apareceram na tela
                if( score % 10 == 0 ){
                    numEnemies -= 100;
                }
                //System.out.println("Score: " + ++score);
                attack = false;
                iter.remove();
                // Colisão com a nave

                ///se gameover for true ele nao gera mais pontuação e para o jogo
            }else if( collide(enemy.x, enemy.y, enemy.width, enemy.height, posX, posY, nave.getWidth(), nave.getHeight()) && !gameover ){
                --life;
                if( life <= 0 ){
                    gameover = true;
                }
                iter.remove();
            }

            if(enemy.x + tEnemy.getWidth() < 0){
                iter.remove();
            }
        }
    }

    private boolean collide(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2){
        if( x1 + w1 > x2 && x1 < x2 + w2 && y1 + h1 > y2 && y1 < y2 + h2 ){
            return true;
        }
        return false;
    }
}