package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Panels.*;
import Tower.*;
import enemy.*;


public class TowerDefenseGame extends JPanel implements ActionListener, MouseListener {
    private TileMap tileMap;
    private MapPanel mapPanel;
    public static List<Tower> towers = new ArrayList<>();;
    public static List<Enemy> enemies = new ArrayList<>();;
    public static List<Bullet> bullets = new ArrayList<>();

    private Timer timer;

    private data data;

    // 遊戲狀態
    private boolean isRunning = false;
    private int playerHealth = 100;
    private double playerMoney = 500.0;
    private int currentWave = 1;

    // UI 組件
    private PlayerInfoPanel playerInfoPanel;
    private TowerDataPanel towerDataPanel;
    private ControlButtonsPanel controlButtonsPanel;
    private TowerTypeButtonPanel towerTypeButtonPanel;

    // 選中的塔
    static public Tower selectedTower = null;
    private boolean selectedTowerSpot;
    private int selectedTileX = -1;
    private int selectedTileY = -1;

    // 生成相關屬性
    private int enemiesToSpawn;
    private double spawnInterval;
    private double spawnTimer;


    public TowerDefenseGame(){
        setLayout(new BorderLayout());

        try {
            tileMap = new TileMap("asset/map/level1.tmj");
            mapPanel = new MapPanel(tileMap, "asset/map/level.png");
            data = new data();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 初始化地圖面板
        JScrollPane scrollPane = new JScrollPane(mapPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        add(scrollPane, BorderLayout.CENTER);

        mapPanel.addMouseListener(this);

        // 初始化側邊欄組件
        playerInfoPanel = new PlayerInfoPanel(playerHealth, playerMoney, currentWave);
        towerDataPanel = new TowerDataPanel();
        controlButtonsPanel = new ControlButtonsPanel(
                e -> toggleGameState(),
                e -> handleBuyUpgrade(),
                e -> handleSell()
        );
        towerTypeButtonPanel = new TowerTypeButtonPanel(data.getTowers(),this);

        // 組合側邊欄
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(300, 0)); // 固定寬度，根據需要調整
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        sidebar.add(playerInfoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // 間距
        sidebar.add(towerDataPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // 間距
        sidebar.add(towerTypeButtonPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // 間距
        sidebar.add(controlButtonsPanel);

        add(sidebar, BorderLayout.EAST);

        // 初始化計時器
        timer = new Timer(16, this); // 約60 FPS
        timer.stop();
        repaint();

        startWave();
    }

    /**
     * 切換遊戲的運行狀態（開始/暫停）
     */
    private void toggleGameState() {



        this.isRunning = !this.isRunning;

        if (!isRunning) {
            controlButtonsPanel.setTimeControlButtonText("開始");
            timer.stop();
        } else {
            controlButtonsPanel.setTimeControlButtonText("暫停");
            if (enemiesToSpawn == 0) {
                currentWave++;
                startWave();
            }
            timer.start();
        }
    }


    private void startWave() {
        enemiesToSpawn = 5 + (currentWave - 1) * 2; // 每波增加2隻敵人，第一波5隻
        spawnInterval = 1.0; // 每隔1秒生成一隻敵人
        spawnTimer = 0.0;
        System.out.println("開始第 " + currentWave + " 波敵人！");
    }


    private void handleBuyUpgrade() {
        if (selectedTower == null && towerTypeButtonPanel.getSelectedTowerName() == null) {
            JOptionPane.showMessageDialog(null, "沒有選擇砲塔類型", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedTower == null && selectedTowerSpot && !checkTowers(selectedTileX, selectedTileY)) {
            Tower tower = data.createTower(towerTypeButtonPanel.getSelectedTowerName(),1,selectedTileX,selectedTileY);
            towers.add(tower);
            selectedTower = tower;
            controlButtonsPanel.setBuyUpgradeButtonText("升級");
            towerDataPanel.updateTowerData(selectedTower);
        }else if (selectedTower != null) {

            if (selectedTower.getMaxLevel() == selectedTower.getLevel()) {

                return;
            }

            Tower upgradedTower = data.createTower(
                    towerTypeButtonPanel.getSelectedTowerName(),
                    selectedTower.getLevel() + 1,
                    selectedTileX,
                    selectedTileY
            );


            towers.remove(selectedTower);

            towers.add(upgradedTower);
            selectedTower = upgradedTower;
            towerDataPanel.updateTowerData(selectedTower);
        }
        repaint();
    }


    private void handleSell() {


    }

    private void updateSidebar() {
        playerInfoPanel.updateInfo(playerHealth, playerMoney, currentWave);
        towerDataPanel.updateTowerData(selectedTower);
    }

    public boolean checkTowers(int selectedTileX, int selectedTileY) {
        for (Tower tower : towers) {
            if( tower.getTileX() == selectedTileX && tower.getTileY() == selectedTileY) {
                towerDataPanel.updateTowerData(tower);
                System.out.println("這裡有塔");
                selectedTower = tower;
                return true;
            }
        }
        selectedTower = null;
        return false;
    }

    public void repaintGame()
    {
        this.repaint();
    }


    @Override
    public void mouseClicked(MouseEvent e) {

        this.selectedTileX = e.getX()/ MapPanel.tileWidth;
        this.selectedTileY = e.getY()/ MapPanel.tileHeight;

        if (mapPanel.clickTower(selectedTileX, selectedTileY)) {

            selectedTowerSpot = true;

            if (checkTowers(selectedTileX, selectedTileY)){
                controlButtonsPanel.setBuyUpgradeButtonText("升級");
            }else {
                towerDataPanel.choseToBuild();
                controlButtonsPanel.setBuyUpgradeButtonText("購買");
            }

        }else{
            selectedTowerSpot = false;
            towerDataPanel.updateTowerData(null);
        }

        repaint();
    }

    // 其他MouseListener方法
    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args){
        JFrame frame = new JFrame("塔防遊戲");
        TowerDefenseGame game = new TowerDefenseGame();
        frame.add(game);
        frame.setSize(1280, 1010); // 擴展窗口大小以容納側邊欄
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        double deltaTime = 0.016;


        for (Enemy enemy: enemies) {
            enemy.update();

        }
        for (Tower tower: towers) {
            tower.update(0.017,enemies);
        }

        // 更新子彈
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();
            if (bullet.isUsed()) {
                bullets.remove(i);
            }
        }

        enemies.removeIf(Enemy::isDead);



        if (isRunning) {

            if (enemiesToSpawn > 0) {
                spawnTimer += deltaTime;
                if (spawnTimer >= spawnInterval) {
                    Enemy enemy = new Enemy();
                    enemy.setPath(tileMap.getSpot());
                    enemies.add(enemy);
                    enemiesToSpawn--;
                    spawnTimer = 0.0;
                    System.out.println("生成一隻敵人，剩餘：" + enemiesToSpawn);
                }
            }
        }



        if(enemies.isEmpty() && isRunning && enemiesToSpawn == 0) {
            System.out.println("enemies.isEmpty() && isRunning");
            timer.stop();
            isRunning = false;
            controlButtonsPanel.setTimeControlButtonText("開始");
            playerInfoPanel.updateInfo(playerHealth,currentWave,currentWave);
            bullets.clear();
        }


        repaint();
    }
}