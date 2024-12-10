package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import Panels.*;
import Tower.Tower;
import enemy.*;

public class TowerDefenseGame extends JPanel implements ActionListener, MouseListener {
    private TileMap tileMap;
    private MapPanel mapPanel;
    private List<Tower> towers = new ArrayList<>();;
    private List<Enemy> enemies = new ArrayList<>();;
    private Timer timer;

    // 遊戲狀態
    private boolean isRunning = false;
    private int playerHealth = 100;
    private double playerMoney = 500.0;
    private int currentWave = 1;

    // UI 組件
    private PlayerInfoPanel playerInfoPanel;
    private TowerDataPanel towerDataPanel;
    private ControlButtonsPanel controlButtonsPanel;

    // 選中的塔
    private Tower selectedTower = null;
    private boolean selectedTowerSpot;

    private int selectedTileX = -1;
    private int selectedTileY = -1;

    public TowerDefenseGame() {
        setLayout(new BorderLayout());

        try {
            tileMap = new TileMap("asset/map/level1.tmj");
            mapPanel = new MapPanel(tileMap, "asset/map/level1.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 初始化地圖面板
        add(new JScrollPane(mapPanel), BorderLayout.CENTER);

        mapPanel.addMouseListener(this);

        // 初始化側邊欄組件
        playerInfoPanel = new PlayerInfoPanel(playerHealth, playerMoney, currentWave);
        towerDataPanel = new TowerDataPanel();
        controlButtonsPanel = new ControlButtonsPanel(
                e -> toggleGameState(),
                e -> handleBuyUpgrade(),
                e -> handleSell()
        );

        // 組合側邊欄
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0)); // 固定寬度，根據需要調整
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        sidebar.add(playerInfoPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // 間距
        sidebar.add(towerDataPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20))); // 間距
        sidebar.add(controlButtonsPanel);

        add(sidebar, BorderLayout.EAST);

        this.enemies.add(new Enemy(tileMap));
        mapPanel.setEnemies(enemies);

        // 初始化計時器
        timer = new Timer(16, this); // 約60 FPS
        timer.start();
        repaint();

    }

    /**
     * 切換遊戲的運行狀態（開始/暫停）
     */
    private void toggleGameState() {
        isRunning = !isRunning;
        if (isRunning) {
            controlButtonsPanel.setTimeControlButtonText("暫停");

        } else {
            controlButtonsPanel.setTimeControlButtonText("開始");

        }
    }

    /**
     * 處理購買或升級塔的邏輯
     */
    private void handleBuyUpgrade() {
        if (selectedTower == null && selectedTowerSpot && !checkTowers(selectedTileX, selectedTileY)) {
            towers.add(new Tower(selectedTileX,selectedTileY,mapPanel.getTileWidth(),mapPanel.getTileHeight(),"asset/tower/tower1.png"));
            System.out.println("已建造塔");
            controlButtonsPanel.setBuyUpgradeButtonText("升級");

        }
        mapPanel.setTowers(towers);
    }

    /**
     * 處理賣出塔的邏輯
     */
    private void handleSell() {

    }

    /**
     * 更新側邊欄的玩家資訊和塔的數據
     */
    private void updateSidebar() {
        playerInfoPanel.updateInfo(playerHealth, playerMoney, currentWave);
        towerDataPanel.updateTowerData(selectedTower);
    }

    private boolean checkTowers(int selectedTileX, int selectedTileY) {
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


    @Override
    public void mouseClicked(MouseEvent e) {

        this.selectedTileX = e.getX()/ mapPanel.getTileWidth();
        this.selectedTileY = e.getY()/ mapPanel.getTileHeight();

        if (mapPanel.clickTower(selectedTileX, selectedTileY)) {

            selectedTowerSpot = true;

            if (checkTowers(selectedTileX, selectedTileY)){
                controlButtonsPanel.setBuyUpgradeButtonText("升級");
            }else {
                System.out.println("這裡是塔格但沒塔");
                towerDataPanel.choseToBuild();
                controlButtonsPanel.setBuyUpgradeButtonText("購買");
            }

        }else{
            selectedTowerSpot = false;
            System.out.println("這裡沒塔格");
            towerDataPanel.updateTowerData(null);
        }
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("塔防遊戲");
        TowerDefenseGame game = new TowerDefenseGame();
        frame.add(game);
        frame.setSize(1200, 1000); // 擴展窗口大小以容納側邊欄
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Enemy enemy: enemies) {
            enemy.update();
            repaint();
        }
//        System.out.println("AA");


    }
}