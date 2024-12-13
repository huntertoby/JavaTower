package Panels;

import javax.swing.*;
import java.awt.*;


public class PlayerInfoPanel extends JPanel {
    private JLabel healthLabel;
    private JLabel moneyLabel;
    private JLabel waveLabel;

    public PlayerInfoPanel(int playerHealth, double playerMoney, int currentWave) {
        setLayout(new GridLayout(3, 1, 5, 5));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        healthLabel = new JLabel("血量: " + playerHealth);
        healthLabel.setFont(new Font("微軟正黑體",Font.PLAIN,20));
        moneyLabel = new JLabel("金錢: $" + playerMoney);
        moneyLabel.setFont(new Font("微軟正黑體",Font.PLAIN,20));
        waveLabel = new JLabel("波數: " + currentWave);
        waveLabel.setFont(new Font("微軟正黑體",Font.PLAIN,20));

        add(healthLabel);
        add(moneyLabel);
        add(waveLabel);
    }

    public void updateInfo(int playerHealth, double playerMoney, int currentWave) {
        healthLabel.setText("血量: " + playerHealth);
        moneyLabel.setText("金錢: $" + playerMoney);
        waveLabel.setText("波數: " + currentWave);
    }
}
