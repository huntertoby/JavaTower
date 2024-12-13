package enemy;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.ArrayList;

import static enemy.StatusType.*;

public class Enemy {

    private double maxHealth;
    private double health;
    // 敵人的位置（以像素計）
    private double x;
    private double y;
    // 基礎速度(像素/更新tick)
    private double baseSpeed = 0.1;
    private double currentSpeed;



    // 路徑：存放敵人要行走的路徑點，每個點為(tileX, tileY)
    private List<int[]> path;
    private int currentTargetIndex; // 目前目標路徑點索引

    // 敵人尺寸（示意用，用來繪製在畫面上的大小）
    private int width = 32;
    private int height = 32;

    // 狀態效果管理
    private List<StatusEffect> activeStatusEffects;
    private boolean isFrozen = false;
    private List<Double> speedModifiers; // 用於累積多個減速效果



    public Enemy(int health) {
        this.health = health;
        maxHealth = health;
        path = new ArrayList<>();
        activeStatusEffects = new ArrayList<>();
        speedModifiers = new ArrayList<>();
        currentSpeed = baseSpeed;
    }

    public void update() {
        // 更新狀態效果
        updateStatusEffects(0.0166);

        // 若被冰凍，則不移動
        if (isFrozen) {
            return;
        }

        // 若路線已到最後一個目標點，就不再移動
        if (currentTargetIndex >= path.size()) {
            return;
        }

        // 取得目前目標點的像素位置（該tile的中心）
        double targetX  = (double) (path.get(currentTargetIndex)[0]);
        double targetY = (double) (path.get(currentTargetIndex)[1]);

        // 計算移動方向
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx*dx + dy*dy);

        if (dist <= 8) {
            // 已抵達目標點，切換至下一個路徑點
            currentTargetIndex++;
        } else {
            // 根據距離計算單位方向並前進
            double ux = dx / dist;
            double uy = dy / dist;

            x += ux * currentSpeed;
            y += uy * currentSpeed;
        }
    }

    private void updateStatusEffects(double deltaTime) {

        // 重置狀態
        isFrozen = false;
        speedModifiers.clear();
        double totalDamage = 0.0;

        // 遍歷所有狀態效果
        List<StatusEffect> toRemove = new ArrayList<>();
        for (StatusEffect effect : activeStatusEffects) {
            // 應用效果
            effect.applyEffect(this);

            // 減少持續時間
            effect.setDuration(effect.getDuration() - deltaTime);
            if (effect.getDuration() <= 0) {
                // 效果結束，標記為移除
                toRemove.add(effect);
                effect.removeEffect(this);
            }
        }

        // 移除已過期的效果
        activeStatusEffects.removeAll(toRemove);

        // 計算總減速效果
        for (StatusEffect effect : activeStatusEffects) {
            if (effect.getType() == SLOW) {
                speedModifiers.add(effect.getMagnitude());
            }
            if (effect.getType() == FREEZE) {
                isFrozen = true;
            }
        }

        // 計算當前速度
        currentSpeed = baseSpeed;
        for (double modifier : speedModifiers) {
            currentSpeed += modifier; // 這裡假設 modifier 是負數
        }

        if (currentSpeed < 0) {
            currentSpeed = 0;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }
    public boolean isEnd() {
        if (currentTargetIndex >= path.size()) return true;
        return false;
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        float renderX = (float) (this.x - width / 2);
        float renderY = (float) (this.y - height / 2);

        Ellipse2D.Float ellipse = new Ellipse2D.Float(renderX, renderY, width, height);
        g2d.setColor(Color.RED);
        g2d.fill(ellipse);

        // 繪製健康條
        g2d.setColor(Color.GRAY);
        g2d.fillRect((int)(x - width / 2), (int)(y - height / 2 - 10), width, 5);
        g2d.setColor(Color.GREEN);
        int healthBarWidth = (int)(width * (health / maxHealth));
        g2d.fillRect((int)(x - width / 2), (int)(y - height / 2 - 10), healthBarWidth, 5);

        // 繪製狀態欄
        renderStatusEffects(g2d);
    }

    private void renderStatusEffects(Graphics2D g2d) {
        int barWidth = 50;
        int barHeight = 10;
        int offsetY = 20;

        // 繪製狀態效果背景
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect((int)(x - width / 2), (int)(y - height / 2 - offsetY), width, barHeight);

        // 繪製當前狀態效果
        int effectCount = activeStatusEffects.size();
        int iconSize = barHeight;
        int padding = 2;

        for (int i = 0; i < effectCount && i < 5; i++) { // 最多顯示5個狀態
            StatusEffect effect = activeStatusEffects.get(i);
            switch (effect.getType()) {
                case SLOW:
                    g2d.setColor(Color.CYAN);
                    break;
                case FREEZE:
                    g2d.setColor(Color.BLUE);
                    break;
                case BURN:
                    g2d.setColor(Color.ORANGE);
                    break;
                case POISON:
                    g2d.setColor(Color.MAGENTA);
                    break;
            }
            g2d.fillOval((int)(x - width / 2 + i * (iconSize + padding)), (int)(y - height / 2 - offsetY), iconSize, iconSize);
        }
    }

    public void setPath(List<int[]> newPath) {
        this.path = newPath;

        this.x = newPath.get(0)[0];
        this.y = newPath.get(0)[1];

        this.currentTargetIndex = 0;
    }

    public void takeDamage(double damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
        }
    }

    public void applyStatusEffect(StatusEffect effect) {
        // 如果是緩速，可以堆疊
        if (effect.getType() == SLOW) {
            activeStatusEffects.add(effect);
        }
        // 如果是冰凍、燃燒或中毒，不同的處理方式
        else if (effect.getType() == FREEZE ||
                effect.getType() == BURN ||
                effect.getType() == StatusType.POISON) {
            activeStatusEffects.add(effect);
        }
    }

    // 速度修改管理
    public void addSpeedModifier(double modifier) {
        // 累計速度修改
        currentSpeed += modifier;
    }

    public void removeSpeedModifier(double modifier) {
        currentSpeed -= modifier;
    }

    // 冰凍狀態管理
    public void setFrozen(boolean frozen) {
        this.isFrozen = frozen;
    }

    // 取得敵人位置(像素為單位)
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setHealth(double health) { this.health = health; }
    public double getHealth() { return health; }


    // 若需要檢查敵人是否走到終點，可新增方法：
    public boolean hasReachedEnd() {
        return currentTargetIndex >= path.size();
    }
}
