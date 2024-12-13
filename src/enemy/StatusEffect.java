package enemy;

public class StatusEffect {
    private StatusType type;
    private double duration; // 持續時間，以tick為單位
    private double magnitude; // 效果強度，例如減速百分比或每tick傷害

    public StatusEffect(StatusType type, double duration, double magnitude) {
        this.type = type;
        this.duration = duration;
        this.magnitude = magnitude;
    }

    public StatusType getType() {
        return type;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void applyEffect(Enemy enemy) {
        switch (type) {
            case SLOW:
                enemy.addSpeedModifier(-magnitude);
                break;
            case FREEZE:
                enemy.setFrozen(true);
                break;
            case BURN:
                enemy.takeDamage(magnitude);
                break;
            case POISON:
                enemy.takeDamage(magnitude);
                break;
        }
    }

    public void removeEffect(Enemy enemy) {
        switch (type) {
            case SLOW:
                enemy.removeSpeedModifier(-magnitude);
                break;
            case FREEZE:
                enemy.setFrozen(false);
                break;
            case BURN:
            case POISON:
                // 持續傷害效果不需要移除任何狀態
                break;
        }
    }
}
